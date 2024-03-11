#!/bin/bash
# Script Name : docker_secrets_files.sh
# Description: Create Docker secrets for users and passwords
# Author: maestro-bene (GitHub)
# Date Created: 2024-02-11
# Last Modified: 2024-03-03
# Version: 2.0
# Usage: Run the script to create secrets for Docker compose in a .secrets directory
# Notes : The secrets are kept in your project directory under .secrets, and will be passed through to the containers in the directory /run/secrets/
# Add entries when needed ...

# WARNING: THIS SCRIPT SHOULD NOT BE INCLUDED IN A REMOTE REPOSITORY. KEEP IT LOCALLY.

# Save the current directory
currentDir=$(pwd)

# Change directory to ./security
cd ./security || exit

# Ensure the .secrets directory exists in the project directory
secretsDir="./secrets"
if [ ! -d "$secretsDir" ]; then
    mkdir "$secretsDir"
fi

# Function to create a secret file
function create_secret_file {
    local fileName=$1
    local content=$2
    local filePath="$secretsDir/$fileName"
    echo "$content" > "$filePath"
}

# Security and global entries
create_secret_file "SECURITY_SERVICES_SHARED_KEY.txt" \
"ENTER_A_VALUE"
create_secret_file "SECURITY_JWT_SECRET_KEY.txt" \
"ENTER_A_VALUE"
create_secret_file "SECURITY_JWT_CLAIMS_KEY_ROLES.txt" \
"ENTER_A_VALUE"
create_secret_file "SECURITY_TRUST_STORE_PASSWORD.txt" \
"ENTER_A_VALUE"
create_secret_file "SECURITY_TRUST_STORE_ALIAS.txt" \
"ENTER_A_VALUE"
create_secret_file "BACKEND_MAIL_USERNAME.txt" \
"ENTER_A_VALUE"
create_secret_file "BACKEND_MAIL_PASSWORD.txt" \
"ENTER_A_VALUE"

# Databases information
create_secret_file "DB_AUTHENTICATION_SERVICE_USER.txt" \
"ENTER_A_VALUE"
create_secret_file "DB_AUTHENTICATION_SERVICE_PASSWORD.txt" \
"ENTER_A_VALUE"
create_secret_file "DB_DIRECTORY_SERVICE_USER.txt" \
"ENTER_A_VALUE"
create_secret_file "DB_DIRECTORY_SERVICE_PASSWORD.txt" \
"ENTER_A_VALUE"
create_secret_file "DB_INVENTORY_SERVICE_USER.txt" \
"ENTER_A_VALUE"
create_secret_file "DB_INVENTORY_SERVICE_PASSWORD.txt" \
"ENTER_A_VALUE"
create_secret_file "DB_ORDER_SERVICE_USER.txt" \
"ENTER_A_VALUE"
create_secret_file "DB_ORDER_SERVICE_PASSWORD.txt" \
"ENTER_A_VALUE"

# Services information
create_secret_file "BACKEND_MESSAGE_BROKER_SERVICE_USERNAME.txt" \
"ENTER_A_VALUE"
create_secret_file "BACKEND_MESSAGE_BROKER_SERVICE_PASSWORD.txt" \
"ENTER_A_VALUE"

# HTTPS-relative information
create_secret_file "BACKEND_AUTHENTICATION_SERVICE_HTTPS_KEY_STORE_ALIAS.txt" \
"ENTER_A_VALUE"
create_secret_file "BACKEND_AUTHENTICATION_SERVICE_HTTPS_KEY_STORE_PASSWORD.txt" \
"ENTER_A_VALUE"
create_secret_file "BACKEND_GATEWAY_SERVICE_HTTPS_KEY_STORE_ALIAS.txt" \
"ENTER_A_VALUE"
create_secret_file "BACKEND_GATEWAY_SERVICE_HTTPS_KEY_STORE_PASSWORD.txt" \
"ENTER_A_VALUE"
create_secret_file "BACKEND_MESSAGE_BROKER_SERVICE_HTTPS_KEY_STORE_ALIAS.txt" \
"ENTER_A_VALUE"
create_secret_file "BACKEND_MESSAGE_BROKER_SERVICE_HTTPS_KEY_STORE_PASSWORD.txt" \
"ENTER_A_VALUE"
create_secret_file "BACKEND_DIRECTORY_SERVICE_HTTPS_KEY_STORE_ALIAS.txt" \
"ENTER_A_VALUE"
create_secret_file "BACKEND_DIRECTORY_SERVICE_HTTPS_KEY_STORE_PASSWORD.txt" \
"ENTER_A_VALUE"
create_secret_file "BACKEND_INVENTORY_SERVICE_HTTPS_KEY_STORE_ALIAS.txt" \
"ENTER_A_VALUE"
create_secret_file "BACKEND_INVENTORY_SERVICE_HTTPS_KEY_STORE_PASSWORD.txt" \
"ENTER_A_VALUE"
create_secret_file "BACKEND_ORDER_SERVICE_HTTPS_KEY_STORE_ALIAS.txt" \
"ENTER_A_VALUE"
create_secret_file "BACKEND_ORDER_SERVICE_HTTPS_KEY_STORE_PASSWORD.txt" \
"ENTER_A_VALUE"
create_secret_file "FRONTEND_WEBAPP_SERVICE_HTTPS_KEY_STORE_ALIAS.txt" \
"ENTER_A_VALUE"
create_secret_file "FRONTEND_WEBAPP_SERVICE_HTTPS_KEY_STORE_PASSWORD.txt" \
"ENTER_A_VALUE"

# Change back to the original directory
cd "$currentDir" || exit

echo "Secrets creation completed. Check the .secrets directory."

