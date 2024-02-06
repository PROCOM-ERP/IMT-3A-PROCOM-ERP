# PowerShell Script: docker_secrets.ps1
# Description: Create Docker secrets for users and passwords in PowerShell
# Author: maestro-bene(Github)
# Date Created: 2024-02-06
# Usage: Run the script to create secrets for Docker compose in a .secrets directory

# WARNING: THIS SCRIPT SHOULD NOT BE INCLUDED IN A REMOTE REPOSITORY. KEEP IT LOCALLY.

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
Create-SecretFile -fileName "SECURITY_SERVICES_SHARED_KEY.txt" -content "MpoUIF+TE2QFX88Z3lliMeqqIX++xABtsIVJwTqE0fs="
Create-SecretFile -fileName "SECURITY_JWT_SECRET_KEY.txt" -content "+w5PL27Cnf+TvmwrY/adImJGqrA8qSDZwvwiyB6fTt8="
Create-SecretFile -fileName "SECURITY_JWT_CLAIMS_KEY_ROLES.txt" -content "jwtClaimRoles"
Create-SecretFile -fileName "SECURITY_TRUST_STORE_PASSWORD.txt" -content "super-secure-password-for-trust-store"
Create-SecretFile -fileName "SECURITY_TRUST_STORE_ALIAS.txt" -content "ca_cert"

# Databases information
Create-SecretFile -fileName "DB_LOCAL_TEST_SERVICE_USER.txt" -content "pguser"
Create-SecretFile -fileName "DB_LOCAL_TEST_SERVICE_PASSWORD.txt" -content "pgpwd"
Create-SecretFile -fileName "DB_AUTHENTICATION_SERVICE_USER.txt" -content "pguser"
Create-SecretFile -fileName "DB_AUTHENTICATION_SERVICE_PASSWORD.txt" -content "pgpwd"
Create-SecretFile -fileName "DB_DIRECTORY_SERVICE_USER.txt" -content "pguser"
Create-SecretFile -fileName "DB_DIRECTORY_SERVICE_PASSWORD.txt" -content "pgpwd"

# Services information
Create-SecretFile -fileName "BACKEND_MESSAGE_BROKER_SERVICE_USERNAME.txt" -content "rabbit_guest"
Create-SecretFile -fileName "BACKEND_MESSAGE_BROKER_SERVICE_PASSWORD.txt" -content "rabbit_guest_password"

# HTTPS-relative information
Create-SecretFile -fileName "BACKEND_AUTHENTICATION_SERVICE_HTTPS_KEY_STORE_ALIAS.txt" -content "authentication"
Create-SecretFile -fileName "BACKEND_AUTHENTICATION_SERVICE_HTTPS_KEY_STORE_PASSWORD.txt" -content "procom-erp-authentication-service-secure-keystore"
Create-SecretFile -fileName "BACKEND_GATEWAY_SERVICE_HTTPS_KEY_STORE_ALIAS.txt" -content "gateway"
Create-SecretFile -fileName "BACKEND_GATEWAY_SERVICE_HTTPS_KEY_STORE_PASSWORD.txt" -content "procom-erp-gateway-service-secure-keystore"
Create-SecretFile -fileName "BACKEND_MESSAGE_BROKER_SERVICE_HTTPS_KEY_STORE_ALIAS.txt" -content "message-broker"
Create-SecretFile -fileName "BACKEND_MESSAGE_BROKER_SERVICE_HTTPS_KEY_STORE_PASSWORD.txt" -content "procom-erp-message-broker-service-secure-keystore"
Create-SecretFile -fileName "BACKEND_DIRECTORY_SERVICE_HTTPS_KEY_STORE_ALIAS.txt" -content "directory"
Create-SecretFile -fileName "BACKEND_DIRECTORY_SERVICE_HTTPS_KEY_STORE_PASSWORD.txt" -content "procom-erp-directory-service-secure-keystore"

Write-Host "Secrets creation completed. Check the .secrets directory."
