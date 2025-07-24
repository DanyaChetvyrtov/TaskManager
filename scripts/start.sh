#!/bin/bash

red=$(tput setaf 1)
green=$(tput setaf 2)
blue=$(tput setaf 4)
reset=$(tput sgr0)

# healthcheck func
check_services() {
    local compose_file=$1
    local timeout=60
    local elapsed=0

    echo "${blue}${compose_file} services check...${reset}"

    while [ $elapsed -lt $timeout ]; do
        if docker-compose -f "$compose_file" ps | grep -q "Up"; then
            echo "${green}${compose_file} services are up${reset}"
            return 0
        fi
        sleep 5
        elapsed=$((elapsed + 5))
    done

    echo "${red}Error${reset}: ${compose_file} services didn't start after ${timeout} seconds" >&2
    docker-compose -f "$compose_file" logs  # Check logs for debug
    exit 1
}

check_container_healthy() {
    local container_name=$1
    local status

    status=$(docker inspect --format='{{.State.Health.Status}}' "${container_name}" 2>/dev/null || echo "unhealthy")

    if [[ $status == "healthy" ]]; then
        echo "true"
        return 0
    else
        echo "false"
    fi
    return 1
}

core_healthcheck() {
    local timeout=300  # Max timeout
    local interval=5
    local elapsed=0
    local gateway_ready=false
    local config_ready=false

    echo "${blue}Checking core services...${reset}"
    while (( elapsed < timeout )); do
        printf "\r${blue}Waiting for services... [%3d/%3d seconds]${reset}" "$elapsed" "$timeout"

        gateway_ready=$(check_container_healthy "gateway-server-zuzex-container")
        config_ready=$(check_container_healthy "config-server-zuzex-container")

        if [[ $gateway_ready == true && $config_ready == true ]]; then
            echo "${green} All core-services up and running${reset}"
            return 0
        fi


        sleep $interval
        elapsed=$((elapsed + interval))
#        echo "Waiting... ${elapsed} seconds elapsed"
    done

    echo "\n${red}Error: services not ready after $timeout seconds${reset}"
    echo "Current status: "
    echo "  gateway-server-zuzex-container: $(docker inspect --format='{{.State.Health.Status}}' gateway-server-zuzex-container 2>/dev/null || echo 'Not ready')"
    echo "  config-server-zuzex-container: $(docker inspect --format='{{.State.Health.Status}}' config-server-zuzex-container 2>/dev/null || echo 'Not ready')"
    printf "\nTry to check logs"
    exit 1;
}


cd "../docker" || { echo "${red}Error${reset} change dir"; exit 1; }

echo "${blue}Creating network for containers${reset}"
docker network create task-manager || { echo "${red}Error${reset} creating 'task-manager' network"; return 1; }

docker_files=("infrastructure" "core" "business-logic")

for cur_file in "${docker_files[@]}"; do
  echo "${blue}Loading ${cur_file}...${reset}"
  docker-compose -f docker-compose."${cur_file}".yml up -d || echo "${red}Error${reset} starting ${cur_file}"

  if [[ ${cur_file} == "core" ]]; then
    core_healthcheck
  else
    check_services "docker-compose.${cur_file}.yml"
  fi
done

echo "${green}All services up and running${reset}"