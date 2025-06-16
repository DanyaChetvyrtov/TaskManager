#!/usr/bin/env bash

if [ $# -eq 0 ]; then
    echo "Error: you should add arguments!"
    echo "Example: ./docker-build-specific.sh <service-folder-name> <image-name>"
    exit 1
fi

cd "$1" || { echo "Error entering $1"; return 1; }
echo "Start building $2 image"
docker build -t "danilchet/$2" . || { echo "Error building $2 image"; return 1; }
cd ..