#!/bin/bash
# Script Name : docker_secrets.sh
# Description: Create Docker secrets for users and passwords
# Author: maestro-bene (GitHub)
# Date Created: 2024-02-02
# Last Modified: 2024-03-03
# Version: 2.0
# Usage: Run the script to create secrets, needed to run the project via docker swarm
# Notes : The secrets are kept in docker deamon, and will be passed through to the containers in the directory /run/secrets/
# Add entries when needed ...

# WARNING : THIS SCRIPT IS NOT TO BE INCLUDED IN A REMOTE REPOSITORY, KEEP IT LOCALLY

# Function to create Docker secret or skip if it already exists
create_secret() {
    secret_name="$1"
    secret_value="$2"

    # Check if the secret already exists
    if docker secret ls | grep -q "$secret_name"; then
        echo "Secret '$secret_name' already exists. Skipping..."
    else
        # Create the secret
        echo -n "$secret_value" | docker secret create "$secret_name" -
        echo "Secret '$secret_name' created."
    fi
}

# Security and global entries
create_secret "SECURITY_SERVICES_SHARED_KEY" \
"ENTER_A_VALUE"
create_secret "SECURITY_JWT_SECRET_KEY" \
"ENTER_A_VALUE"
create_secret "SECURITY_JWT_CLAIMS_KEY_ROLES" \
"ENTER_A_VALUE"
create_secret "SECURITY_TRUST_STORE_PASSWORD" \
"ENTER_A_VALUE"
create_secret "SECURITY_TRUST_STORE_ALIAS" \
"ENTER_A_VALUE"
create_secret "BACKEND_MAIL_USERNAME" \
"ENTER_A_VALUE"
create_secret "BACKEND_MAIL_PASSWORD" \
"ENTER_A_VALUE"

# Databases information
create_secret "DB_AUTHENTICATION_SERVICE_USER" \
"ENTER_A_VALUE"
create_secret "DB_AUTHENTICATION_SERVICE_PASSWORD" \
"ENTER_A_VALUE"
create_secret "DB_DIRECTORY_SERVICE_USER" \
"ENTER_A_VALUE"
create_secret "DB_DIRECTORY_SERVICE_PASSWORD" \
"ENTER_A_VALUE"
create_secret "DB_INVENTORY_SERVICE_USER" \
"ENTER_A_VALUE"
create_secret "DB_INVENTORY_SERVICE_PASSWORD" \
"ENTER_A_VALUE"
create_secret "DB_ORDER_SERVICE_USER" \
"ENTER_A_VALUE"
create_secret "DB_ORDER_SERVICE_PASSWORD" \
"ENTER_A_VALUE"

# Services information
create_secret "BACKEND_MESSAGE_BROKER_SERVICE_USERNAME" \
"ENTER_A_VALUE"
create_secret "BACKEND_MESSAGE_BROKER_SERVICE_PASSWORD" \
"ENTER_A_VALUE"

# HTTPS-relative information
create_secret "BACKEND_AUTHENTICATION_SERVICE_HTTPS_KEY_STORE_ALIAS" \
"ENTER_A_VALUE"
create_secret "BACKEND_AUTHENTICATION_SERVICE_HTTPS_KEY_STORE_PASSWORD" \
"ENTER_A_VALUE"
create_secret "BACKEND_GATEWAY_SERVICE_HTTPS_KEY_STORE_ALIAS" \
"ENTER_A_VALUE"
create_secret "BACKEND_GATEWAY_SERVICE_HTTPS_KEY_STORE_PASSWORD" \
"ENTER_A_VALUE"
create_secret "BACKEND_MESSAGE_BROKER_SERVICE_HTTPS_KEY_STORE_ALIAS" \
"ENTER_A_VALUE"
create_secret "BACKEND_MESSAGE_BROKER_SERVICE_HTTPS_KEY_STORE_PASSWORD" \
"ENTER_A_VALUE"
create_secret "BACKEND_DIRECTORY_SERVICE_HTTPS_KEY_STORE_ALIAS" \
"ENTER_A_VALUE"
create_secret "BACKEND_DIRECTORY_SERVICE_HTTPS_KEY_STORE_PASSWORD" \
"ENTER_A_VALUE"
create_secret "BACKEND_INVENTORY_SERVICE_HTTPS_KEY_STORE_ALIAS" \
"ENTER_A_VALUE"
create_secret "BACKEND_INVENTORY_SERVICE_HTTPS_KEY_STORE_PASSWORD" \
"ENTER_A_VALUE"
create_secret "BACKEND_ORDER_SERVICE_HTTPS_KEY_STORE_ALIAS" \
"ENTER_A_VALUE"
create_secret "BACKEND_ORDER_SERVICE_HTTPS_KEY_STORE_PASSWORD" \
"ENTER_A_VALUE"
create_secret "FRONTEND_WEBAPP_SERVICE_HTTPS_KEY_STORE_ALIAS" \
"ENTER_A_VALUE"
create_secret "FRONTEND_WEBAPP_SERVICE_HTTPS_KEY_STORE_PASSWORD" \
"ENTER_A_VALUE"

