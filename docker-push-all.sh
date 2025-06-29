#!/usr/bin/env bash

# Функция для сборки Docker-образа
echo "----------------------------------------"
push_service() {
    local image_name=$1

    echo "Pushing ${image_name} image..."

    docker push "${image_name}" || { echo "Error building ${image_name} image"; exit 1; }
    cd ../..

    echo "Successfully push ${image_name} image"
    echo "----------------------------------------"
}

# Сборка всех микросервисов
# Первый параметр - папка/модуль микросервиса в директории ./services/
# Второй параметр - название образа
push_service "danilchet/zuzex-config-server-ms"
push_service "danilchet/zuzex-eureka-server-ms"
push_service "danilchet/zuzex-gateway-server-ms"

push_service "danilchet/zuzex-auth-ms"
push_service "danilchet/zuzex-account-ms"
push_service "danilchet/zuzex-task-ms"

echo "All images successfully pushed"