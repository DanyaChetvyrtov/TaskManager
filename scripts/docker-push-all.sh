#!/usr/bin/env bash

red=$(tput setaf 1)
green=$(tput setaf 2)
blue=$(tput setaf 4)
reset=$(tput sgr0)

# Функция для пуша Docker-образа
push_service() {
    local image_name=$1
    echo "${blue}Pushing ${image_name} image...${reset}"

    docker push "${image_name}" || { echo "${red}Error building ${image_name} image${reset}"; exit 1; }

    echo "${green}Successfully pushed ${image_name} image${reset}"
    echo "----------------------------------------"
}

core_ms=("config" "eureka" "gateway")
for cur_ms in "${core_ms[@]}"; do
    push_service "danilchet/zuzex-${cur_ms}-server-ms"
done

business_ms=("auth" "account" "task")
for cur_ms in "${business_ms[@]}"; do
    push_service "danilchet/zuzex-${cur_ms}-ms"
done

echo "${green}All images successfully pushed${reset}"