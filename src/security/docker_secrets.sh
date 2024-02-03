#!/bin/bash
# Script Name : docker_secrets.sh
# Description: Create Docker secrets for users and passwords
# Author: maestro-bene (GitHub)
# Date Created: 2024-02-02
# Last Modified: 2024-02-02
# Version: 1.0
# Usage: Run the script to create secrets, needed to run the project via docker compose
# WARNING : THIS SCRIPT IS NOT TO BE INCLUDED IN A REMOTE REPOSITORY, KEEP IT LOCALLY
# Notes : The secrets are kept in your project directory under .secrets, and will be passed through to the containers in the directory /run/secrets/
# Add entries when needed ...

# Function to create Docker secret or skip if it already exists
create_secret() {
    secret_name="$1"
    secret_value="$2"

    # Check if the secret already exists
    if docker secret ls | grep -q "$secret_name"; then
        echo "Secret '$secret_name' already exists. Skipping..."
    else
        # Create the secret
        docker secret create "$secret_name" - <<< "$secret_value"
        echo "Secret '$secret_name' created."
    fi
}

# Security and global entries
create_secret "SECURITY_SERVICES_SHARED_KEY" \
"MpoUIF+TE2QFX88Z3lliMeqqIX++xABtsIVJwTqE0fs="

create_secret "SECURITY_JWT_SECRET_KEY" \
"+w5PL27Cnf+TvmwrY/adImJGqrA8qSDZwvwiyB6fTt8="

create_secret "SECURITY_JWT_CLAIMS_KEY_ROLES" \
"jwtClaimRoles"

create_secret "SECURITY_TRUST_STORE_PASSWORD" \
"super-secure-password-for-trust-store"

create_secret "SECURITY_TRUST_STORE_ALIAS" \
"ca_cert"

# Databases information
create_secret "DB_LOCAL_TEST_SERVICE_USER" \
"pguser"

create_secret "DB_LOCAL_TEST_SERVICE_PASSWORD" \
"pgpwd"

create_secret "DB_AUTHENTICATION_SERVICE_USER" \
"pguser"

create_secret "DB_AUTHENTICATION_SERVICE_PASSWORD" \
"pgpwd"

create_secret "DB_DIRECTORY_SERVICE_USER" \
"pguser"

create_secret "DB_DIRECTORY_SERVICE_PASSWORD" \
"pguser"

# Services information
create_secret "BACKEND_MESSAGE_BROKER_SERVICE_USERNAME" \
"guest"

create_secret "BACKEND_MESSAGE_BROKER_SERVICE_PASSWORD" \
"guest"

# HTTPS-relative information
create_secret "BACKEND_AUTHENTICATION_SERVICE_HTTPS_KEY_STORE_ALIAS" \
"authentication"

create_secret "BACKEND_AUTHENTICATION_SERVICE_HTTPS_KEY_STORE_PASSWORD" \
"procom-erp-authentication-service-secure-keystore"

create_secret "BACKEND_GATEWAY_SERVICE_HTTPS_KEY_STORE_ALIAS" \
"gateway"

create_secret "BACKEND_GATEWAY_SERVICE_HTTPS_KEY_STORE_PASSWORD" \
"procom-erp-gateway-service-secure-keystore"

create_secret "BACKEND_MESSAGE_BROKER_SERVICE_HTTPS_KEY_STORE_ALIAS" \
"message-broker"

create_secret "BACKEND_MESSAGE_BROKER_SERVICE_HTTPS_KEY_STORE_PASSWORD" \
"procom-erp-message-broker-service-secure-keystore"

create_secret "BACKEND_DIRECTORY_SERVICE_HTTPS_KEY_STORE_ALIAS" \
"directory"

create_secret "BACKEND_DIRECTORY_SERVICE_HTTPS_KEY_STORE_PASSWORD" \
"procom-erp-directory-service-secure-keystore"

