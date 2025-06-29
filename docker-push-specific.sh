#!/usr/bin/env bash

if [ $# -eq 0 ]; then
    echo "Error: you should add arguments!"
    echo "Example: ./docker-build-specific.sh <service-folder-name> <image-name>"
    exit 1
fi

# $1 - название микросервиса
docker push "danilchet/zuzex-$1" || { echo "Error pushing $1 image"; exit 1; }