#!/bin/bash
# Script Name : deploy.sh
# Description: Copy necessary files to every service, and then Create environment variables in the current shell, and deploy the project using stack
# Author: maestro-bene (GitHub)
# Date Created: 2024-02-02
# Last Modified: 2024-03-02
# Version: 2.0
# Usage: use --help

# +-----------------------------------------------------------------------------+
# | System-relative Functions                                                   |
# +-----------------------------------------------------------------------------+

clean_security() {
    if [ "$CA" == "true" ]; then
        echo "Cleaning certificates along with the CA's"
        ${security_path}/clean_security.sh "--CA" >/dev/null 2>&1;
    else
        echo "Cleaning certificates"
        ${security_path}/clean_security.sh >/dev/null 2>&1;
    fi
        echo "Certificates all cleaned up"
}

# +-----------------------------------------------------------------------------+

security() {
    echo "Generating certificates"
    ${security_path}/security_setup.sh >/dev/null
    echo "Certificates generated"
}


# +-----------------------------------------------------------------------------+

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
                cp "${system_path}/entrypoint.sh" "${SERVICE_PATH}"
                cp "${system_path}/wait-for-it.sh" "${SERVICE_PATH}"
                cp "${system_path}/procom-erp-truststore.jks" "${SERVICE_PATH}"
                cp "${system_path}/procom-erp-ca.pem" "${SERVICE_PATH}"
                # Copy Maven Wrapper (mvnw) to backend services
                cp "${system_path}/mvnw" "${SERVICE_PATH}"
                cp -R "${system_path}/.mvn" "${SERVICE_PATH}/"
            fi
        done
    done

    # Copy system files to frontend services
    for frontend_path in "${frontend_paths[@]}"; do
        # Find and iterate over directories in the frontend
        for SERVICE_PATH in $frontend_path; do
            if [ -d "$SERVICE_PATH" ]; then
                cp "${system_path}/procom-erp-truststore.jks" "${SERVICE_PATH}"
                cp "${system_path}/procom-erp-ca.pem" "${SERVICE_PATH}"
            fi
        done
    done

    declare -a db_paths=("src/databases/*")
    for db_path in "${db_paths[@]}"; do
        # Find and iterate over directories in databases
        for SERVICE_PATH in $db_path; do
            if [ -d "$SERVICE_PATH" ]; then
                cp "${system_path}/db_entrypoint.sh" "${SERVICE_PATH}"
            fi
        done
    done
} 

# +-----------------------------------------------------------------------------+

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

# +-----------------------------------------------------------------------------+
# | Helper Functions                                                            |
# +-----------------------------------------------------------------------------+

is_stack_running() {
    local STACK_NAME="$1"
    local STACK_STATUS=$(docker stack ps --format "{{.Name}}" "$STACK_NAME" 2>/dev/null)
    if [ -z "$STACK_STATUS" ]; then
        return 1
    else
        return 0
    fi
}

# +-----------------------------------------------------------------------------+

latest_image_exists() {
    local IMAGE_NAME="$1"
    
    docker images "$IMAGE_NAME:latest" | awk 'NR>1{exit} END{if (NR == 0) exit 1}'
}

# +-----------------------------------------------------------------------------+

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
    done < "${docker_path}/.env"
}

# +-----------------------------------------------------------------------------+

remove_elk_setup_container() {
    echo "Now waiting for Elasticsearch setup to finish before starting complete ELK, and then ERP"
    # Wait for the container to exit
    docker wait "setup" >/dev/null

    # Remove the container
    docker rm "setup" >/dev/null
    docker rmi "elk-setup" > /dev/null
}

# +-----------------------------------------------------------------------------+

import_log_dashboard_in_kibana(){
    echo -n "Waiting for Kibana to start"
    while ! curl -s http://localhost:5601/api/status > /dev/null; do
        echo -n '.'
        sleep 2
    done
    echo ''

    # Import the NDJSON file into Kibana
    sleep 5
    curl -u "$ELASTIC_USER:$ELASTIC_PASSWORD" -POST 'http://localhost:5601/api/saved_objects/_import?overwrite=true' -H "kbn-xsrf: true" --form file=@docker/elk/export.ndjson > /dev/null 2>&1
}

# +-----------------------------------------------------------------------------+
# | Core Functions                                                              |
# +-----------------------------------------------------------------------------+

setup_elk(){
    echo "Setting up Elasticsearch"
    if [ "$SWARM" == "true" ]; then
        docker-compose -f $ELK_COMPOSE_SWARM_FILE -f $ELK_FILEBEAT_COMPOSE_SWARM_FILE -p elk up -d --build setup
    else
        docker-compose -f $ELK_COMPOSE_FILE -f $ELK_FILEBEAT_COMPOSE_FILE -p elk up -d --build setup
    fi
}

# +-----------------------------------------------------------------------------+

deploy_elk(){
    echo "Deploying Elastic stack (Filebeat, Logstash, Elasticsearch, Kibana)"
    if [ "$SWARM" == "true" ]; then
        docker-compose -f $ELK_COMPOSE_SWARM_FILE -f $ELK_FILEBEAT_COMPOSE_SWARM_FILE -p elk up -d --build
    else
        docker-compose -f $ELK_COMPOSE_FILE -f $ELK_FILEBEAT_COMPOSE_FILE -p elk up -d --build
    fi
}

# +-----------------------------------------------------------------------------+

build_images() {
    docker-compose -f $COMPOSE_FILE --env-file ${docker_path}/.env build
}

# +-----------------------------------------------------------------------------+

push_images() {
    DOCKER_REGISTRY_PREFIX="$1"
    
    for ((i = 0; i < ${#IMAGE_NAMES[@]}; i++)); do
        SERVICE="${IMAGE_NAMES[i]}"
        IMAGE_VERSION="${IMAGE_VERSIONS[i]}"

        # Tag and push the image
        if latest_image_exists "$SERVICE"; then
            IMAGE_NAME="$DOCKER_REGISTRY:$SERVICE-$IMAGE_VERSION"
            echo "Tagging and pushing $IMAGE_NAME"
            docker tag "$SERVICE" "$IMAGE_NAME"
            docker push "$IMAGE_NAME"
        else
            echo "Warning: You need to replace Image version by latest for $SERVICE"
            break
        fi
    done
}

# +-----------------------------------------------------------------------------+

pull_images() {
    DOCKER_REGISTRY_PREFIX="$1"
    
    SERVICES=$(docker-compose -f $COMPOSE_FILE config --services)
    
    for SERVICE in $SERVICES; do
        IMAGE_NAME="$DOCKER_REGISTRY:$SERVICE-$VERSION"
        echo "Pulling $IMAGE_NAME"
        docker pull "$IMAGE_NAME"
    done
}

# +-----------------------------------------------------------------------------+

deploy(){
    if [ "$SWARM" == "true" ]; then
        docker stack deploy -c $COMPOSE_FILE ERP
    else
        docker-compose -f $COMPOSE_FILE -p erp up -d 
    fi
}

# +-----------------------------------------------------------------------------+
# | Setup & Verification                                                        |
# +-----------------------------------------------------------------------------+

security_path="./security"
system_path="./system"
docker_path="./docker"
elk_path="${docker_path}/elk"
elk_filebeat_path="${elk_path}/extensions/filebeat"

if ! [ -f "${docker_path}/.env" ]; then
    echo "System error: .env file not found"
    exit 1
fi

if ! [ -f "${docker_path}/docker-compose.yml" ]; then
    echo "System error: docker-compose.yml file not found"
    exit 1
fi

if ! [ -f "${docker_path}/docker-compose-swarm.yml" ]; then
    echo "System error: docker-compose-swarm.yml file not found"
    exit 1
fi

SWARM=false
CLEAN_SEC=false
CA=false
SEC=false
PUSH=false
PULL=false
HOT=false
LOGS=true
VERSION=latest
VERSION_SPECIFIED=false

COMPOSE_FILE="${docker_path}/docker-compose.yml"
ELK_COMPOSE_FILE="${elk_path}/docker-compose.yml"
ELK_COMPOSE_SWARM_FILE="${elk_path}/docker-compose-swarm.yml"
ELK_FILEBEAT_COMPOSE_FILE="${elk_filebeat_path}/filebeat-compose.yml"
ELK_FILEBEAT_COMPOSE_SWARM_FILE="${elk_filebeat_path}/filebeat-compose-swarm.yml"

ELASTIC_USER="elastic"
ELASTIC_PASSWORD=$(grep "^ELASTIC_PASSWORD=" "${elk_path}/.env" | cut -d '=' -f2  | tr -d '\n' | sed "s/^'//;s/'$//")

# +-----------------------------------------------------------------------------+
# | Argument Parsing                                                            |
# +-----------------------------------------------------------------------------+

while [[ $# -gt 0 ]]; do
    case $1 in
        --swarm)
            SWARM=true
            COMPOSE_FILE="${docker_path}/docker-compose-swarm.yml"
            shift
            ;;
        --clean-sec)
            CLEAN_SEC=true
            if [ -n "$2" ] && [ "${2:0:1}" != "-" ] && [ "$2" == "CA" ]; then
                CA=true
                shift 2
            else
                shift
            fi
            ;;
        --sec)
            SEC=true
            shift
            ;;
        --push)
            PUSH=true
            if [ -n "$2" ] && [ "${2:0:1}" != "-" ]; then
                DOCKER_REGISTRY="$2"
                shift 2
            else
                echo "Usage:    --push option requires a value."
                echo " "
                echo "Example:  --push repository/image"
                exit 1
            fi
            ;;
        --pull)
            PULL=true
            if [ -n "$2" ] && [ "${2:0:1}" != "-" ]; then
                DOCKER_REGISTRY="$2"
                shift 2
            else
                echo "Usage:    --pull option requires a value."
                echo " "
                echo "Example:  --pull repository/image"
                exit 1
            fi
            ;;
        --version)
            if [ -n "$2" ] && [ "${2:0:1}" != "-" ]; then
                VERSION="$2"
                VERSION_SPECIFIED=true
                shift 2
            else
                echo "Usage:    --version option requires a value."
                echo " "
                echo "Example:  '--version latest' or '--version 1.0.0'"
                exit 1
            fi
            ;;
        --hot)
            HOT=true
            shift
            ;;
        --no-logs)
            LOGS=false
            shift
            ;;
        --help)
            echo "Usage:"
            echo "        './deploy.sh --option \"value\"'"
            echo " "
            echo "Possible options:"
            echo "        '--swarm': --------------------->  Deploy using Docker Swarm mode."
            echo "        '--clean-sec \"CA\"': ------------>  Execute the clean security setup before deploying, --sec option is required if you use this. CA: optional, to also clean CA cert files."
            echo "        '--sec'  ----------------------->  Execute the security setup script as well."
            echo "        '--push \"repository/image\"': --->  Tag and push Docker images to a repository, then deploy."
            echo "        '--pull \"repository/image\"': --->  Pull Docker images from a repository from which to deploy, --version is required if you use this option"
            echo "        '--version \"version\"': --------->  Specify the version of the images to use for the pull option."
            echo "        '--hot': ----------------------->  Force redeployment even if the stack is already running, for swarm mode."
            echo "        '--no-logs': ------------------->  Doesn't deploy monitoring and log aggregating containers (Elastic stack)."
            echo "        '--help': ---------------------->  Display this help message."
            echo "        '--doc': ----------------------->  Display the deployment documentation."
            echo " "
            echo "Examples:"
            echo "        './deploy.sh --swarm --clean-sec CA --sec --pull --version 1.0.0 --hot'"
            echo "        './deploy.sh --help'"
            echo "        './deploy.sh --doc'"
            echo " "
            echo "You can use this script without specifying options, it will build and deploy by default with compose from docker-compose.yml"
            exit 0
            ;;
        --doc)
            # Check if `glow` is installed
            if command -v glow >/dev/null 2>&1; then
                # If `glow` is installed, use it to view the Markdown file
                glow ./docs/DEPLOYING.md
            else
                # If `glow` is not installed, fallback to using `less`
                less ./docs/DEPLOYING.md
            fi
            exit 0
            ;;
        *)
            echo "Usage:"
            echo " "
            echo "Invalid option $1"
            exit 1
            ;;
    esac
done

# +-----------------------------------------------------------------------------+

if [ "$PULL" != "$VERSION_SPECIFIED" ]; then
    echo "Usage:    "
    echo " "
    echo "When using --pull 'registry/image', please also use --version to specify the version you'd like to pull."
    exit 1
fi

# +-----------------------------------------------------------------------------+

if [ "$SEC" != "$CLEAN_SEC" ]  && [ "$CLEAN_SEC" == "true" ]; then
    echo "Usage:    "
    echo " "
    echo "When using --clean-sec, please also use --sec option, otherwise you'll face certificate problems."
    exit 1
fi

# +-----------------------------------------------------------------------------+
# | MAIN LOGIC                                                                  |
# +-----------------------------------------------------------------------------+


# +---Swarm Specific------------------------------------------------------------+
if [ "$SWARM" == "true" ]; then

    export $(cat ${docker_path}/.env) > /dev/null 2>&1

    if ! docker info 2>/dev/null | grep -q "Swarm: active"; then
        docker swarm init > /dev/null 2>&1
        echo "Swarm initialized"
        # +---Docker Secrets for swarm mode-------------------------------------+
        echo "Generating docker secrets for Swarm"
        ${security_path}/docker_secrets.sh > /dev/null 2>&1
    else
        echo "Swarm detected"
    fi

    # Check if the stack is running
    while is_stack_running "ERP"; do
        if [ "$HOT" == "false" ]; then
            echo "Error: The stack is already running. Use --hot option to force redeployment."
            exit 1
        else
            echo "Hot deployment requested. Stopping the stack..."
            docker stack rm ERP
            while is_stack_running "ERP"; do
                sleep 3
            done
        fi
    done
else
    # +---Docker Secrets for simple compose-------------------------------------+
    echo "Generating docker secrets"
    ${security_path}/docker_secrets_files.sh >/dev/null 2>&1
    echo "Docker secrets generated"
    echo " "
fi

# +---System-relative setup-----------------------------------------------------+

if [ "$CLEAN_SEC" == "true" ]; then
    clean_security
fi

if [ "$SEC" == "true" ]; then
    security
fi

copy_system_files


get_image_versions


# +---Deployment-----------------------------------------------------------------+


# +---Log Setup & Deployment-----------------------------------------------------+

if [ "$LOGS" == "true" ]; then
    setup_elk

    remove_elk_setup_container

    deploy_elk

    import_log_dashboard_in_kibana
fi

# +---ERP Setup & Deployment-----------------------------------------------------+

if [ "$PUSH" == "true" ]; then
    docker login
    build_images
    push_images "$DOCKER_REGISTRY"
fi

if [ "$PULL" == "true" ] && [ "$VERSION_SPECIFIED" == "true" ]; then
    docker login
    # Pull the images instead of building
    pull_images "$DOCKER_REGISTRY"
fi


if [ "$PUSH" == "false" ] && [ "$PULL" == "false"  ]; then
    build_images
fi

echo "Deploying ERP"
deploy
clean_system_files

cat << "EOF"
 ______                                    _______  ______   ______  
(_____ \                                  (_______)(_____ \ (_____ \ 
 _____) )____  ___    ____  ___   ____     _____    _____) ) _____) )
|  ____// ___)/ _ \  / ___)/ _ \ |    \   |  ___)  |  __  / |  ____/ 
| |    | |   | |_| |( (___| |_| || | | |  | |_____ | |  \ \ | |      
|_|    |_|    \___/  \____)\___/ |_|_|_|  |_______)|_|   |_||_|      

EOF

echo "Welcome to Procom ERP, here are the access links:"
echo " "
echo "1. Link to the frontend: [http://localhost:3000]"
echo "2. Link to the gateway hello world to accept its certificate as well: [http://localhost:8041/api/v1/authentication/hello]"
echo "3. Link to the Elastic stack (Kibana): [http://localhost:5601]"
echo "4. Link to the Custom Dashboard: [http://http://localhost:5601/app/discover]. Click on \"Open\" to find and open the custom ERP-Dashboard"

echo -e "\a"

exit 0
# +----End of this handy script------------------------------------------------+
