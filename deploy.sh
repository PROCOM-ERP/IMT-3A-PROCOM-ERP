#!/bin/bash
# Script Name : deploy.sh
# Description: Copy necessary files to every service, and then Create environment variables in the current shell, and deploy the project using stack
# Author: maestro-bene (GitHub)
# Date Created: 2024-02-02
# Last Modified: 2024-02-06
# Version: 1.2
# Usage: Run the script to create secrets beforehand, and this is to solve the fact that docker stack deploy doesn't support .env placeholding

security() {
    ./src/security/security_setup.sh
}

copy_system_files() {
   # Define paths to backend and frontend directories
    declare -a backend_paths=("src/backend/*")
    declare -a frontend_paths=("src/frontend/*")

    echo "Copying system files to services"
    # Copy system files to backend services
    for backend_path in "${backend_paths[@]}"; do
        # Find and iterate over directories in the backend
        for SERVICE_PATH in $backend_path; do
            if [ -d "$SERVICE_PATH" ]; then
                cp ./src/system/entrypoint.sh "${SERVICE_PATH}"
                cp ./src/system/wait-for-it.sh "${SERVICE_PATH}"
                cp ./src/system/procom-erp-truststore.jks "${SERVICE_PATH}"
                cp ./src/system/procom-erp-ca.pem "${SERVICE_PATH}"
                # Copy Maven Wrapper (mvnw) to backend services
                cp ./src/system/mvnw "${SERVICE_PATH}"
                cp -R ./src/system/.mvn "${SERVICE_PATH}/"
            fi
        done
    done

    # Copy system files to frontend services
    for frontend_path in "${frontend_paths[@]}"; do
        # Find and iterate over directories in the frontend
        for SERVICE_PATH in $frontend_path; do
            if [ -d "$SERVICE_PATH" ]; then
                cp ./src/system/procom-erp-truststore.jks "${SERVICE_PATH}"
                cp ./src/system/procom-erp-ca.pem "${SERVICE_PATH}"
            fi
        done
    done

    declare -a db_paths=("src/databases/*")
    for db_path in "${db_paths[@]}"; do
        # Find and iterate over directories in databases
        for SERVICE_PATH in $db_path; do
            if [ -d "$SERVICE_PATH" ]; then
                cp ./src/system/db_entrypoint.sh "${SERVICE_PATH}"
            fi
        done
    done
} 

# Litteral opposite of copy_system_files function
clean_system_files() {
    # Define paths to backend and frontend directories
    declare -a backend_paths=("src/backend/*")
    declare -a frontend_paths=("src/frontend/*")
    declare -a db_paths=("src/databases/*")

    echo "Cleaning up system files from services"

    # Clean up system files from backend services
    for backend_path in "${backend_paths[@]}"; do
        # Find and iterate over directories in the backend
        for SERVICE_PATH in $backend_path; do
            if [ -d "$SERVICE_PATH" ]; then
                # Remove system files and Maven Wrapper from backend services
                rm -f "${SERVICE_PATH}/entrypoint.sh"
                rm -f "${SERVICE_PATH}/wait-for-it.sh"
                rm -f "${SERVICE_PATH}/procom-erp-truststore.jks"
                rm -f "${SERVICE_PATH}/procom-erp-ca.pem"
                rm -f "${SERVICE_PATH}/mvnw"
                rm -rf "${SERVICE_PATH}/.mvn"
            fi
        done
    done

    # Clean up system files from frontend services
    for frontend_path in "${frontend_paths[@]}"; do
        # Find and iterate over directories in the frontend
        for SERVICE_PATH in $frontend_path; do
            if [ -d "$SERVICE_PATH" ]; then
                # Remove system files from frontend services
                rm -f "${SERVICE_PATH}/procom-erp-truststore.jks"
                rm -f "${SERVICE_PATH}/procom-erp-ca.pem"
            fi
        done
    done

    # Clean up system files from database services
    for db_path in "${db_paths[@]}"; do
        # Find and iterate over directories in databases
        for SERVICE_PATH in $db_path; do
            if [ -d "$SERVICE_PATH" ]; then
                # Remove system files from database services
                rm -f "${SERVICE_PATH}/db_entrypoint.sh"
            fi
        done
    done
}

is_stack_running() {
    local STACK_NAME="$1"
    local STACK_STATUS=$(docker stack ps --format "{{.Name}}" "$STACK_NAME" 2>/dev/null)
    if [ -z "$STACK_STATUS" ]; then
        return 1
    else
        return 0
    fi
}

get_image_versions(){
    # Initialize an associative array to store image names and versions
    IMAGE_NAMES=()  # Array to store image names
    IMAGE_VERSIONS=()  # Array to store image versions

    # Loop through lines in .env file
    while IFS= read -r line; do
        # Check if the line contains "IMAGE_VERSION"
        if [[ "$line" =~ IMAGE_VERSION=([^[:space:]]+) ]]; then
            IMAGE_VERSION="${BASH_REMATCH[1]}"
            IMAGE_VERSIONS+=("$IMAGE_VERSION")
        fi

        # Check if the line contains "HOSTNAME"
        if [[ "$line" =~ HOSTNAME=([^[:space:]]+) ]]; then
            IMAGE_NAME="${BASH_REMATCH[1]}"
            IMAGE_NAMES+=("$IMAGE_NAME")
        fi
    done < .env
}

latest_image_exists() {
    local IMAGE_NAME="$1"
    
    docker images "$IMAGE_NAME:latest" | awk 'NR>1{exit} END{if (NR == 0) exit 1}'
}

build_images() {
    docker-compose -f $COMPOSE_FILE build
}

build_and_push_images() {
    DOCKER_REGISTRY_PREFIX="$1"
    
    for ((i = 0; i < ${#IMAGE_NAMES[@]}; i++)); do
        SERVICE="${IMAGE_NAMES[i]}"
        IMAGE_VERSION="${IMAGE_VERSIONS[i]}"

        # Build the image if it doesn't exist
        if ! docker images "$DOCKER_REGISTRY:$SERVICE-$IMAGE_VERSION" | grep "$SERVICE"; then
            echo "Building $SERVICE:$IMAGE_VERSION image"
            docker-compose -f $COMPOSE_FILE build "$SERVICE"
        fi

        # Tag and push the image
        if latest_image_exists "$SERVICE"; then
            IMAGE_NAME="$DOCKER_REGISTRY:$SERVICE-$IMAGE_VERSION"
            echo "Tagging and pushing $IMAGE_NAME"
            docker tag "$SERVICE" "$IMAGE_NAME"
            docker push "$IMAGE_NAME"
        else
            echo "You need to replace Image version by latest for $SERVICE"
            echo "Then build by executing this script with the --build option"
            break
        fi
    done
}

pull_images() {
    DOCKER_REGISTRY_PREFIX="$1"
    
    SERVICES=$(docker-compose -f $COMPOSE_FILE config --services)
    
    for SERVICE in $SERVICES; do
        IMAGE_NAME="$DOCKER_REGISTRY:$SERVICE-latest"
        echo "Pulling $IMAGE_NAME"
        docker pull "$IMAGE_NAME"
    done
}

deploy(){
    if [ "$SWARM" == "true" ]; then
        docker stack deploy -c $COMPOSE_FILE ERP
    else
        docker-compose -f $COMPOSE_FILE up -d --build
    fi
}



if ! [ -f .env ]; then
    echo ".env file not found"
    exit 1
fi

if ! [ -f docker-compose.yml ]; then
    echo "docker-compose.yml file not found"
    exit 1
fi

if ! [ -f docker-compose-swarm.yml ]; then
    echo "docker-compose-swarm.yml file not found"
    exit 1
fi

SWARM=false
SEC=false
PUSH=false
PULL=false
HOT=false
COMPOSE_FILE=docker-compose.yml

while [[ $# -gt 0 ]]; do
    case $1 in
        --swarm)
            SWARM=true
            COMPOSE_FILE=docker-compose-swarm.yml
            shift
            ;;
        --sec)
            SEC=true
            shift
            ;;
        --push)
            PUSH=true
            shift
            DOCKER_REGISTRY="$1"
            if [ -z "$DOCKER_REGISTRY" ]; then
                echo "No Docker registry entered, please enter one along with the option"
                exit 1
            fi
            shift
            ;;
        --pull)
            PULL=true
            shift
            DOCKER_REGISTRY="$1"
            if [ -z "$DOCKER_REGISTRY" ]; then
                echo "No Docker registry entered, please enter one along with the option"
                exit 1
            fi
            shift
            ;;
        --hot)
            HOT=true
            shift
            ;;
        *)
            echo "Invalid option: $1"
            exit 1
            ;;
    esac
done

if [ "$SWARM" == "true" ]; then

    export $(cat ./.env) > /dev/null 2>&1

    if ! docker info 2>/dev/null | grep -q "Swarm: active"; then
        docker swarm init > /dev/null 2>&1
        echo "Swarm initialized"
        ./src/security/docker_secrets.sh > /dev/null 2>&1
    else
        echo "Swarm detected"
    fi

    # Check if the stack is running
    while is_stack_running "ERP"; do
        if [ "$HOT" == "false" ]; then
            echo "The stack is already running. Use --hot option to force redeployment."
            exit 1
        else
            echo "Hot deployment requested. Stopping the stack..."
            docker stack rm ERP
            sleep 1  # TODO: Redeploy dynamically
        fi
    done


    if [ "$HOT" == "false" ] && is_stack_running "ERP"; then
        echo "The stack is already running. Use --hot option to force redeployment."
        exit 1
    fi
fi


copy_system_files

if [ "$SEC" == "true" ]; then
    security
fi

get_image_versions


# Define your Docker registry prefix (e.g., username or organization)
#DOCKER_REGISTRY_PREFIX=gachille/erp

if [ "$PUSH" == "true" ]; then
    docker login
    build_and_push_images "$DOCKER_REGISTRY"
    deploy
    clean_system_files
    exit 0
fi

if [ "$PULL" == "true" ]; then
    docker login
    # Pull the images instead of building
    pull_images "$DOCKER_REGISTRY"
    deploy
    clean_system_files
    exit 0
fi


if [ "$PUSH" == "false" ] && [ "$PULL" == "false"  ]; then
    build_images
    deploy
    clean_system_files
    exit 0
fi


exit 1