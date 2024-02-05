#!/bin/bash
# Script Name : deploy.sh
# Description: Copy necessary files to every service, and then Create environment variables in the current shell, and deploy the project using stack
# Author: maestro-bene (GitHub)
# Date Created: 2024-02-02
# Last Modified: 2024-02-02
# Version: 1.0
# Usage: Run the script to create secrets beforehand, and this is to solve the fact that docker stack deploy doesn't support .env placeholding

# Function to copy system files to each service
copy_system_files() {
    # Define paths to backend and frontend directories
    declare -a paths=("src/backend/*" "src/frontend/*")
    
    for path in "${paths[@]}"; do
        # Find and iterate over directories in backend and frontend
        for SERVICE_PATH in $path; do
            if [ -d "$SERVICE_PATH" ]; then
                echo "Copying system files to $SERVICE_PATH"
                # Copy all files from system except deploy.sh to the service's directory
                cp ./src/system/entrypoint.sh "${SERVICE_PATH}"
                cp ./src/system/procom-erp-truststore.jks "${SERVICE_PATH}"
                cp ./src/system/procom-erp-ca.pem "${SERVICE_PATH}"
           fi
        done
    done

    declare -a db_paths=("src/databases/*")
    for db_path in "${db_paths[@]}"; do
        # Find and iterate over directories in backend and frontend
        for SERVICE_PATH in $db_path; do
            if [ -d "$SERVICE_PATH" ]; then
                echo "Copying system files to $SERVICE_PATH"
                # Copy all files from system except deploy.sh to the service's directory
                cp ./src/system/db_entrypoint.sh "${SERVICE_PATH}"
            fi
        done
    done
}

# Check if .env file exists
if [ -f .env ]; then
    echo ".env file found"
else
    echo ".env file not found"
    exit 1
fi

# Check if docker-compose.yml file exists
if [ -f docker-compose.yml ]; then
    echo "docker-compose.yml file found"
else
    echo "docker-compose.yml file not found"
    exit 1
fi

# Copy system files to each service directory
copy_system_files

# Define the Docker Compose file
 COMPOSE_FILE=docker-compose.yml
# # Define your Docker registry prefix (e.g., username or organization)
# DOCKER_REGISTRY_PREFIX=gachille/erp

# Build the images using docker-compose
docker-compose -f $COMPOSE_FILE build

# # List all services from the Docker Compose file
# SERVICES=$(docker-compose -f $COMPOSE_FILE config --services)
#
# IMAGES_NAME=$(docker-compose -f $COMPOSE_FILE config --images) 
#
# # Loop through all services
# for IMAGE_NAME in $IMAGES_NAME; do
#     # Extract the image name defined in the Docker Compose file for the service
#
#     # If the service has an image defined, tag it with the registry prefix and push it
#     if [ ! -z "$IMAGE_NAME" ]; then
#         TAGGED_IMAGE="$DOCKER_REGISTRY_PREFIX/$IMAGE_NAME"
#         echo "Tagging $IMAGE_NAME as $TAGGED_IMAGE"
#         docker tag $IMAGE_NAME $TAGGED_IMAGE
#         echo "Pushing $TAGGED_IMAGE"
#         docker push $TAGGED_IMAGE
#     fi
# done


export $(cat ./.env) > /dev/null 2>&1; docker stack deploy -c ./docker-compose.yml ERP
