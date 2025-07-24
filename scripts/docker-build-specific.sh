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

cd ..
cd "services/$1" || { echo "${red}Error entering $1${reset}"; return 1; }
echo "${blue}Start building $2 image${reset}"
docker build -t "danilchet/zuzex-$2" . || { echo "${red}Error building $2 image${reset}"; return 1; }
echo "${green}Successfully built $2 image${reset}"
cd ..