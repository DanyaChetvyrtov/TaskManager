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
# Первый параметр - папка/модуль микросервиса в директории ./services/
# Второй параметр - название образа
build_service "config-server" "danilchet/zuzex-config-server-ms"
build_service "eureka-server" "danilchet/zuzex-eureka-server-ms"
build_service "gateway-server" "danilchet/zuzex-gateway-server-ms"

build_service "auth-ms" "danilchet/zuzex-auth-ms"
build_service "account-ms" "danilchet/zuzex-account-ms"
build_service "task-ms" "danilchet/zuzex-task-ms"

echo "All images built successfully"