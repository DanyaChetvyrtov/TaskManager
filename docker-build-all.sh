#!/usr/bin/env bash

# Функция для сборки Docker-образа
echo "----------------------------------------"
build_service() {
    local service_name=$1
    local image_name=$2

    echo "Building ${service_name} image..."

    cd "services/${service_name}" || { echo "Error entering ${service_name} directory"; exit 1; }
    docker build -t "${image_name}" . || { echo "Error building ${service_name} image"; exit 1; }
    cd ../..

    echo "Successfully built ${service_name} image"
    echo "----------------------------------------"
}

# Сборка всех микросервисов
build_service "config-server" "danilchet/zuzex-config-server-ms"
build_service "eureka-server" "danilchet/zuzex-eureka-server-ms"

echo "All images built successfully"