# PowerShell Script: docker_secrets.ps1
# Description: Create Docker secrets for users and passwords in PowerShell
# Author: maestro-bene(Github)
# Date Created: 2024-02-06
# Usage: Run the script to create secrets for Docker compose in a .secrets directory

# WARNING: THIS SCRIPT SHOULD NOT BE INCLUDED IN A REMOTE REPOSITORY. KEEP IT LOCALLY.

# Define the secrets as key-value pairs
$secrets = @{
    "SECURITY_SERVICES_SHARED_KEY" = `
    "ENTER_A_VALUE"
    "SECURITY_JWT_SECRET_KEY" = `
    "ENTER_A_VALUE"
    "SECURITY_JWT_CLAIMS_KEY_ROLES" = `
    "ENTER_A_VALUE"
    "SECURITY_TRUST_STORE_PASSWORD" = `
    "ENTER_A_VALUE"
    "SECURITY_TRUST_STORE_ALIAS" = `
    "ENTER_A_VALUE"
    "BACKEND_MAIL_USERNAME" = `
    "ENTER_A_VALUE"
    "BACKEND_MAIL_PASSWORD" = `
    "ENTER_A_VALUE"
    "DB_LOCAL_TEST_SERVICE_USER" = `
    "ENTER_A_VALUE"
    "DB_LOCAL_TEST_SERVICE_PASSWORD" = `
    "ENTER_A_VALUE"
    "DB_AUTHENTICATION_SERVICE_USER" = `
    "ENTER_A_VALUE"
    "DB_AUTHENTICATION_SERVICE_PASSWORD" = `
    "ENTER_A_VALUE"
    "DB_DIRECTORY_SERVICE_USER" = `
    "ENTER_A_VALUE"
    "DB_DIRECTORY_SERVICE_PASSWORD" = `
    "ENTER_A_VALUE"
    "DB_INVENTORY_SERVICE_USER" = `
    "ENTER_A_VALUE"
    "DB_INVENTORY_SERVICE_PASSWORD" = `
    "ENTER_A_VALUE"
    "DB_ORDER_SERVICE_USER" = `
    "ENTER_A_VALUE"
    "DB_ORDER_SERVICE_PASSWORD" = `
    "ENTER_A_VALUE"
    "BACKEND_MESSAGE_BROKER_SERVICE_USERNAME" = `
    "ENTER_A_VALUE"
    "BACKEND_MESSAGE_BROKER_SERVICE_PASSWORD" = `
    "ENTER_A_VALUE"
    "BACKEND_AUTHENTICATION_SERVICE_HTTPS_KEY_STORE_ALIAS" = `
    "ENTER_A_VALUE"
    "BACKEND_AUTHENTICATION_SERVICE_HTTPS_KEY_STORE_PASSWORD" = `
    "ENTER_A_VALUE"
    "BACKEND_GATEWAY_SERVICE_HTTPS_KEY_STORE_ALIAS" = `
    "ENTER_A_VALUE"
    "BACKEND_GATEWAY_SERVICE_HTTPS_KEY_STORE_PASSWORD" = `
    "ENTER_A_VALUE"
    "BACKEND_MESSAGE_BROKER_SERVICE_HTTPS_KEY_STORE_ALIAS" = `
    "ENTER_A_VALUE"
    "BACKEND_MESSAGE_BROKER_SERVICE_HTTPS_KEY_STORE_PASSWORD" = `
    "ENTER_A_VALUE"
    "BACKEND_DIRECTORY_SERVICE_HTTPS_KEY_STORE_ALIAS" = `
    "ENTER_A_VALUE"
    "BACKEND_DIRECTORY_SERVICE_HTTPS_KEY_STORE_PASSWORD" = `
    "ENTER_A_VALUE"
    "BACKEND_INVENTORY_SERVICE_HTTPS_KEY_STORE_ALIAS" = `
    "ENTER_A_VALUE"
    "BACKEND_INVENTORY_SERVICE_HTTPS_KEY_STORE_PASSWORD" = `
    "ENTER_A_VALUE"
    "BACKEND_ORDER_SERVICE_HTTPS_KEY_STORE_ALIAS" = `
    "ENTER_A_VALUE"
    "BACKEND_ORDER_SERVICE_HTTPS_KEY_STORE_PASSWORD" = `
    "ENTER_A_VALUE"
    "FRONTEND_WEBAPP_SERVICE_HTTPS_KEY_STORE_ALIAS" = `
    "ENTER_A_VALUE"
    "FRONTEND_WEBAPP_SERVICE_HTTPS_KEY_STORE_PASSWORD" = `
    "ENTER_A_VALUE"
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
        $secretFile = New-TemporaryFile
        $secretValue | Out-File -FilePath $secretFile
        docker secret create $secretName $secretFile
        Write-Host "Secret '$secretName' created."
    }
}

# Loop through the secrets and create Docker secrets
foreach ($key in $secrets.Keys) {
    $value = $secrets[$key]
    Create-DockerSecret -secretName $key -secretValue $value
}

Write-Host "Docker secrets creation completed."

