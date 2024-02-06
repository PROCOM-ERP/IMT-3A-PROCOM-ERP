# PowerShell Script: docker_secrets.ps1
# Description: Create Docker secrets for users and passwords in PowerShell
# Author: maestro-bene(Github)
# Date Created: 2024-02-06
# Usage: Run the script to create secrets for Docker compose in a .secrets directory

# WARNING: THIS SCRIPT SHOULD NOT BE INCLUDED IN A REMOTE REPOSITORY. KEEP IT LOCALLY.

# Define the secrets as key-value pairs
$secrets = @{
    "SECURITY_SERVICES_SHARED_KEY" = "MpoUIF+TE2QFX88Z3lliMeqqIX++xABtsIVJwTqE0fs="
    "SECURITY_JWT_SECRET_KEY" = "+w5PL27Cnf+TvmwrY/adImJGqrA8qSDZwvwiyB6fTt8="
    "SECURITY_JWT_CLAIMS_KEY_ROLES" = "jwtClaimRoles"
    "SECURITY_TRUST_STORE_PASSWORD" = "super-secure-password-for-trust-store"
    "SECURITY_TRUST_STORE_ALIAS" = "ca_cert"
    "DB_LOCAL_TEST_SERVICE_USER" = "pguser"
    "DB_LOCAL_TEST_SERVICE_PASSWORD" = "pgpwd"
    "DB_AUTHENTICATION_SERVICE_USER" = "pguser"
    "DB_AUTHENTICATION_SERVICE_PASSWORD" = "pgpwd"
    "DB_DIRECTORY_SERVICE_USER" = "pguser"
    "DB_DIRECTORY_SERVICE_PASSWORD" = "pgpwd"
    "BACKEND_MESSAGE_BROKER_SERVICE_USERNAME" = "rabbit_guest"
    "BACKEND_MESSAGE_BROKER_SERVICE_PASSWORD" = "rabbit_guest_password"
    "BACKEND_AUTHENTICATION_SERVICE_HTTPS_KEY_STORE_ALIAS" = "authentication"
    "BACKEND_AUTHENTICATION_SERVICE_HTTPS_KEY_STORE_PASSWORD" = "procom-erp-authentication-service-secure-keystore"
    "BACKEND_GATEWAY_SERVICE_HTTPS_KEY_STORE_ALIAS" = "gateway"
    "BACKEND_GATEWAY_SERVICE_HTTPS_KEY_STORE_PASSWORD" = "procom-erp-gateway-service-secure-keystore"
    "BACKEND_MESSAGE_BROKER_SERVICE_HTTPS_KEY_STORE_ALIAS" = "message-broker"
    "BACKEND_MESSAGE_BROKER_SERVICE_HTTPS_KEY_STORE_PASSWORD" = "procom-erp-message-broker-service-secure-keystore"
    "BACKEND_DIRECTORY_SERVICE_HTTPS_KEY_STORE_ALIAS" = "directory"
    "BACKEND_DIRECTORY_SERVICE_HTTPS_KEY_STORE_PASSWORD" = "procom-erp-directory-service-secure-keystore"
}

# Function to create Docker secret or skip if it already exists
function Create-DockerSecret {
    param (
        [string]$secretName,
        [string]$secretValue
    )

    # Check if the secret already exists
    if ((docker secret ls) -match $secretName) {
        Write-Host "Secret '$secretName' already exists. Skipping..."
    }
    else {
        # Create the secret
        docker secret create $secretName - <<< $secretValue
        Write-Host "Secret '$secretName' created."
    }
}

# Loop through the secrets and create Docker secrets
foreach ($key in $secrets.Keys) {
    $value = $secrets[$key]
    Create-DockerSecret -secretName $key -secretValue $value
}

Write-Host "Docker secrets creation completed."
