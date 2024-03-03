# PowerShell Script: security_auto_setup.ps1
# Description: Generate CA and services security essentials, and moves them to the correct spot.
# Author: Adapted from maestro-bene's bash version
# Date Created: 2024-02-06
# Usage: Run this script within the src/security directory.

# Ensure OpenSSL is available in the system
if (-not (Get-Command "openssl" -ErrorAction SilentlyContinue)) {
    Write-Host "OpenSSL is not installed. Please install OpenSSL and try again."
    exit
}

# Verify running from the correct directory
$expectedLastEntries = "security"
$currentPath = Get-Location
$currentPathLast = Split-Path -Path $currentPath -Leaf
$lastEntry = "$currentPathLast"

if ($lastEntry -ne $expectedLastEntries) {
    Write-Host "Please run this script from the '${expectedLastEntries}' directory."
    exit
}

# Generate certificates for a service
function Generate-Certificate {
    param (
        [string]$serviceName,
        [string]$serviceType
    )
    $serviceDir = "..\src\$serviceType\$($serviceName)Service"
    $certDir = ".\$serviceName"

    # Ensure directories exist
    New-Item -ItemType Directory -Path $serviceDir, $certDir -Force

    # Step 1: Generate Certificates for the Service
    openssl genrsa -out "$certDir\$serviceName-service.key" 4096
    openssl req -new -key "$certDir\$serviceName-service.key" -out "$certDir\$serviceName-service.csr" -subj "/C=FR/ST=France/L=Paris/O=Procom-ERP/OU=IT/CN=springboot-procom-erp-$serviceName-service"
    
    # Step 2: Sign the CSR with Your CA
    openssl x509 -req -days 365 -in "$certDir\$serviceName-service.csr" -CA "$caCrt" -CAkey "$caKey" -out "$certDir\$serviceName-service.crt"
    
    # Export to PKCS12 format
    $securePassword = ConvertTo-SecureString -String "procom-erp-$serviceName-service-secure-keystore" -Force -AsPlainText
    openssl pkcs12 -export -in "$certDir\$serviceName-service.crt" -inkey "$certDir\$serviceName-service.key" -out "$certDir\$serviceName-service-keystore.p12" -name "$serviceName" -passout pass:$securePassword

    if ($serviceName -match "message-broker"){
        openssl pkcs12 -in "$certDir\${serviceName}-service-keystore.p12" -clcerts -nokeys -nodes -out "${serviceName}-service-certificate.pem" -passout pass:$securePassword -passin pass:$securePassword

        openssl pkcs12 -in "$certDir\${serviceName}-service-keystore.p12" -nocerts -nodes -out "${serviceName}-service-key.pem" -passout pass:$securePassword -passin pass:$securePassword

        Move-Item -Force "${serviceName}-service-key.pem" -Destination "$serviceDir"
        Move-Item -Force "${serviceName}-service-certificate.pem" -Destination "$serviceDir"
    }
    
    # Move generated files to the service directory
    Move-Item -Force -Path "$certDir\*.p12" -Destination $serviceDir
    Move-Item -Force -Path "$certDir\*" -Destination $certDir
    Write-Host "Certificates for $serviceName moved to $certDir and the keystore to $serviceDir"
}

# Initialize CA files and check if they exist
$caCrt = "procom-erp-ca.crt"
$caKey = "procom-erp-ca.key"
$caDir = ".\CA"
if (-not (Test-Path "$caDir\$caCrt") -or -not (Test-Path "$caDir\$caKey")) {
    New-Item -ItemType Directory -Path $caDir -Force
    
    # Generate the Root Key and Certificate
    openssl genrsa -out "$caDir\$caKey" 4096
    openssl req -new -x509 -days 3650 -key "$caDir\$caKey" -out "$caDir\$caCrt" -subj "/C=FR/ST=France/L=Paris/O=Procom-ERP/OU=IT/CN=Procom-ERP"
    
    # Import into trust store (simulated with keytool command in bash)
    keytool -importcert -noprompt -alias $caCrt -file $caDir\$caCrt -keystore "$caDir\procom-erp-truststore.jks" --store-pass "super-secure-password-for-trust-store"
    Write-Host "New CA files generated."
    Move-Item -Force -Path "$caDir\procom-erp-truststore.jks" -Destination "..\system"
}
$caCrt = "$caDir\$caCrt"
$caKey = "$caDir\$caKey"

# Discover service directories and generate certificates
$backendServices = Get-ChildItem -Path "..\src\backend" -Directory | Where-Object { $_.Name -match "Service$" }
$frontendServices = Get-ChildItem -Path "..\src\frontend" -Directory | Where-Object { $_.Name -match "Service$" }

foreach ($service in $backendServices + $frontendServices) {
    $serviceName = $service.Name -replace "Service$"
    $serviceType = if ($service.FullName -match "backend") { "backend" } else { "frontend" }
    
    Write-Host "Generating certificates for $serviceName ($serviceType)"
    Generate-Certificate -serviceName $serviceName -serviceType $serviceType
}

# Final CA management and truststore setup not directly translatable to PowerShell without external tools like Java's keytool or manual steps.
Copy-Item -Force -Path $caCrt -Destination "..\system\procom-erp-ca.pem"
# Adapt this section based on your security requirements.

Write-Host "Certificates generation completed."
