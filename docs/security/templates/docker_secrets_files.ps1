# PowerShell Script: docker_secrets.ps1
# Description: Create Docker secrets for users and passwords in PowerShell
# Author: maestro-bene(Github)
# Date Created: 2024-02-06
# Usage: Run the script to create secrets for Docker compose in a .secrets directory

# WARNING: THIS SCRIPT SHOULD NOT BE INCLUDED IN A REMOTE REPOSITORY. KEEP IT LOCALLY.

# Save the current directory
$currentDir = Get-Location

# Change directory to ./security
Set-Location -Path ".\security"

# Ensure the .secrets directory exists in the project directory
$secretsDir = ".\secrets"
if (-not (Test-Path $secretsDir)) {
    New-Item -ItemType Directory -Path $secretsDir
}

# Function to create a secret file
function Create-SecretFile {
    param (
        [string]$fileName,
        [string]$content
    )
    $filePath = Join-Path $secretsDir $fileName
    Set-Content -Path $filePath -Value $content
}

# Security and global entries
Create-SecretFile -fileName "SECURITY_SERVICES_SHARED_KEY.txt" -content "ENTER_A_VALUE"
Create-SecretFile -fileName "SECURITY_JWT_SECRET_KEY.txt" -content "ENTER_A_VALUE"
Create-SecretFile -fileName "SECURITY_JWT_CLAIMS_KEY_ROLES.txt" -content "ENTER_A_VALUE"
Create-SecretFile -fileName "SECURITY_TRUST_STORE_PASSWORD.txt" -content "ENTER_A_VALUE"
Create-SecretFile -fileName "SECURITY_TRUST_STORE_ALIAS.txt" -content "ENTER_A_VALUE"
Create-SecretFile -fileName "BACKEND_MAIL_USERNAME.txt" -content "ENTER_A_VALUE"
Create-SecretFile -fileName "BACKEND_MAIL_PASSWORD.txt" -content "ENTER_A_VALUE"

# Databases information
Create-SecretFile -fileName "DB_AUTHENTICATION_SERVICE_USER.txt" -content "ENTER_A_VALUE"
Create-SecretFile -fileName "DB_AUTHENTICATION_SERVICE_PASSWORD.txt" -content "ENTER_A_VALUE"
Create-SecretFile -fileName "DB_DIRECTORY_SERVICE_USER.txt" -content "ENTER_A_VALUE"
Create-SecretFile -fileName "DB_DIRECTORY_SERVICE_PASSWORD.txt" -content "ENTER_A_VALUE"
Create-SecretFile -fileName "DB_INVENTORY_SERVICE_USER.txt" -content "ENTER_A_VALUE"
Create-SecretFile -fileName "DB_INVENTORY_SERVICE_PASSWORD.txt" -content "ENTER_A_VALUE"
Create-SecretFile -fileName "DB_ORDER_SERVICE_USER.txt" -content "ENTER_A_VALUE"
Create-SecretFile -fileName "DB_ORDER_SERVICE_PASSWORD.txt" -content "ENTER_A_VALUE"

# Services information
Create-SecretFile -fileName "BACKEND_MESSAGE_BROKER_SERVICE_USERNAME.txt" -content "ENTER_A_VALUE"
Create-SecretFile -fileName "BACKEND_MESSAGE_BROKER_SERVICE_PASSWORD.txt" -content "ENTER_A_VALUE"

# HTTPS-relative information
Create-SecretFile -fileName "BACKEND_AUTHENTICATION_SERVICE_HTTPS_KEY_STORE_ALIAS.txt" -content "ENTER_A_VALUE"
Create-SecretFile -fileName "BACKEND_AUTHENTICATION_SERVICE_HTTPS_KEY_STORE_PASSWORD.txt" -content "ENTER_A_VALUE"
Create-SecretFile -fileName "BACKEND_GATEWAY_SERVICE_HTTPS_KEY_STORE_ALIAS.txt" -content "ENTER_A_VALUE"
Create-SecretFile -fileName "BACKEND_GATEWAY_SERVICE_HTTPS_KEY_STORE_PASSWORD.txt" -content "ENTER_A_VALUE"
Create-SecretFile -fileName "BACKEND_MESSAGE_BROKER_SERVICE_HTTPS_KEY_STORE_ALIAS.txt" -content "ENTER_A_VALUE"
Create-SecretFile -fileName "BACKEND_MESSAGE_BROKER_SERVICE_HTTPS_KEY_STORE_PASSWORD.txt" -content "ENTER_A_VALUE"
Create-SecretFile -fileName "BACKEND_DIRECTORY_SERVICE_HTTPS_KEY_STORE_ALIAS.txt" -content "ENTER_A_VALUE"
Create-SecretFile -fileName "BACKEND_DIRECTORY_SERVICE_HTTPS_KEY_STORE_PASSWORD.txt" -content "ENTER_A_VALUE"
Create-SecretFile -fileName "BACKEND_INVENTORY_SERVICE_HTTPS_KEY_STORE_ALIAS.txt" -content "ENTER_A_VALUE"
Create-SecretFile -fileName "BACKEND_INVENTORY_SERVICE_HTTPS_KEY_STORE_PASSWORD.txt" -content "ENTER_A_VALUE"
Create-SecretFile -fileName "BACKEND_ORDER_SERVICE_HTTPS_KEY_STORE_ALIAS.txt" -content "ENTER_A_VALUE"
Create-SecretFile -fileName "BACKEND_ORDER_SERVICE_HTTPS_KEY_STORE_PASSWORD.txt" -content "ENTER_A_VALUE"
Create-SecretFile -fileName "FRONTEND_WEBAPP_SERVICE_HTTPS_KEY_STORE_ALIAS.txt" -content "ENTER_A_VALUE"
Create-SecretFile -fileName "FRONTEND_WEBAPP_SERVICE_HTTPS_KEY_STORE_PASSWORD.txt" -content "ENTER_A_VALUE"

# Change back to the original directory
Set-Location -Path $currentDir

Write-Host "Secrets creation completed. Check the .secrets directory."

