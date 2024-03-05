#!/bin/bash
# Script Name : undeploy.sh
# Description: Undeploy the ERP application
# Author: maestro-bene (GitHub)
# Date Created: 2024-03-02
# Last Modified: 2024-03-02
# Version: 1.0

if docker info 2>/dev/null | grep -q "Swarm: active"; then
    echo "Swarm detected"
    docker stack rm ERP
    docker swarm leave --force
    echo -e "\a"
else
    $(docker compose -p erp down)
    $(docker compose -p elk down)
    echo -e "\a"
fi

