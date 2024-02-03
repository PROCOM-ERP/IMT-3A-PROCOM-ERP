#!/bin/bash
# Script Name : deploy.sh
# Description: Create environment variables in the current shell, and deploy the project using stack
# Author: maestro-bene (GitHub)
# Date Created: 2024-02-02
# Last Modified: 2024-02-02
# Version: 1.0
# Usage: Run the script to create secrets beforehand, and this is to solve the fact that docker stack deploy doesn't support .env placeholding

# Check if .env file exists
if [ -f .env ]; then
    echo ".env file found"
else
    echo ".env file not found"
fi

# Check if docker-compose.yml file exists
if [ -f docker-compose.yml ]; then
    echo "docker-compose.yml file found"
else
    echo "docker-compose.yml file not found"
fi

# Define the Docker Compose file
COMPOSE_FILE=docker-compose.yml

# Define your Docker registry prefix (e.g., username or organization)
DOCKER_REGISTRY_PREFIX=erp

# Build the images using docker-compose
docker-compose -f $COMPOSE_FILE build

# List all services from the Docker Compose file
SERVICES=$(docker-compose -f $COMPOSE_FILE config --services)

# Loop through all services
for SERVICE in $SERVICES; do
    # Extract the image name defined in the Docker Compose file for the service
    IMAGE_NAME=$(docker-compose -f $COMPOSE_FILE config | grep "image: $SERVICE" | awk '{print $2}')

    # If the service has an image defined, tag it with the registry prefix and push it
    if [ ! -z "$IMAGE_NAME" ]; then
        TAGGED_IMAGE="$DOCKER_REGISTRY_PREFIX/$IMAGE_NAME"
        echo "Tagging $IMAGE_NAME as $TAGGED_IMAGE"
        docker tag $IMAGE_NAME $TAGGED_IMAGE
        echo "Pushing $TAGGED_IMAGE"
        docker push $TAGGED_IMAGE
    fi
done


export $(cat ./.env) > /dev/null 2>&1; docker stack deploy -c ./docker-compose.yml ERP
