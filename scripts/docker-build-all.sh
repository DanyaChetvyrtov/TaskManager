#!/usr/bin/env bash

red=$(tput setaf 1)
green=$(tput setaf 2)
blue=$(tput setaf 4)
reset=$(tput sgr0)

# Функция для сборки Docker-образа
build_service() {
    local service_name=$1
    local image_name=$2

    echo "${blue}Building ${service_name} image...${reset}"

    cd "${service_name}" || { echo "${red}Error entering ${service_name} directory${reset}"; exit 1; }
    docker build -t "${image_name}" . || { echo "${red}Error building ${service_name} image${reset}"; exit 1; }
    cd ..

    echo "${green}Successfully built ${service_name} image${reset}"
    echo "----------------------------------------"
}

# Сборка всех микросервисов
# Первый параметр - папка/модуль микросервиса в директории ./services/
# Второй параметр - название образа
cd ../services || { echo "${red}Error entering ${service_name} directory${reset}"; exit 1; }

core_ms=("config" "eureka" "gateway")

for cur_ms in "${core_ms[@]}"; do
  build_service "${cur_ms}-server" "danilchet/zuzex-${cur_ms}-server-ms"
done

business_ms=("auth" "account" "task")
for cur_ms in "${business_ms[@]}"; do
  build_service "${cur_ms}-ms" "danilchet/zuzex-${cur_ms}-ms"
done

echo "${green}All images successfully built${reset}"