#!/bin/bash
# Script Name : undeploy.sh
# Description: Undeploy the ERP application
# Author: maestro-bene (GitHub)
# Date Created: 2024-03-02
# Last Modified: 2024-03-02
# Version: 1.0

docker_path="./docker"

if docker info 2>/dev/null | grep -q "Swarm: active"; then
    echo "Swarm detected"
    docker stack rm ERP
    docker swarm leave --force
else
    cd ${docker_path}
    docker-compose down
fi

cd ../
