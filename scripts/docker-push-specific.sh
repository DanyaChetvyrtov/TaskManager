#!/usr/bin/env bash

red=$(tput setaf 1)
green=$(tput setaf 2)
blue=$(tput setaf 4)
reset=$(tput sgr0)

if [ $# -eq 0 ]; then
    echo "${red}Error: you should add arguments!${reset}"
    echo "Example: ./docker-build-specific.sh <service-folder-name> <image-name>"
    exit 1
fi

echo "${blue}Start pushing $1 image${reset}"
# $1 - название микросервиса
docker push "danilchet/zuzex-$1" || { echo "Error pushing $1 image"; exit 1; }
echo "${green}Successfully pushed $2 image${reset}"
