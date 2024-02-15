# PowerShell Script: clean_security.ps1
# Description: Reverses the setup done by "security_setup.sh", removing or archiving keys, certs, CSRs, etc.
# Author: Adapted from maestro-bene's bash version
# Date Created: 2024-02-06
# Usage: Run this script to clean up, optionally including all CA keys, certs, and trust stores

param (
    [switch]$IncludeCA
)

# Verify running from the correct directory
$expectedLastEntries = "src\security"
$currentPath = Get-Location
$parentPath = Split-Path -Path $currentPath -Parent
$parentPathLast = Split-Path -Path $parentPath -Leaf
$currentPathLast = Split-Path -Path $currentPath -Leaf
$lastTwoEntries = "$parentPathLast\$currentPathLast"

if ($lastTwoEntries -ne $expectedLastEntries) {
    Write-Host "Please run this script from the '${expectedLastEntries}' directory."
    exit
}

# Discover service directories
$backendServiceDirs = Get-ChildItem -Path "../backend" -Directory | Where-Object { $_.Name -match "Service$" }
$frontendServiceDirs = Get-ChildItem -Path "../frontend" -Directory | Where-Object { $_.Name -match "Service$" }

# Archive directory setup
$currentDateTime = Get-Date -Format "yyyy-MM-dd-HH-mm-ss"
$archiveDir = ".\archive\$currentDateTime"
New-Item -ItemType Directory -Path $archiveDir -Force

foreach ($dir in $backendServiceDirs + $frontendServiceDirs) {
    $serviceName = $dir.Name -replace "Service$"
    $serviceType = if ($dir.FullName -match "backend") { "backend" } else { "frontend" }
    
    # Archive service-specific keystores and PEM files if applicable
    $servicePath = "..\$serviceType\$($dir.Name)"
    $filesToMove = @("$servicePath\$serviceName-service-keystore.p12")
    
    if ($serviceName -eq "message-broker") {
        $filesToMove += @("$servicePath\$serviceName-service-key.pem", "$servicePath\$serviceName-service-certificate.pem")
    }
    
    foreach ($file in $filesToMove) {
        if (Test-Path $file) {
            Move-Item -Path $file -Destination $archiveDir
        }
    }

    # Archive service directory if present
    $serviceDirPath = ".\$serviceName"
    if (Test-Path $serviceDirPath) {
        Move-Item -Path $serviceDirPath -Destination $archiveDir
    }
}

# Include CA in the cleanup if specified
if ($IncludeCA) {
    $caFiles = @("../system/procom-erp-truststore.jks", "../system/procom-erp-ca.pem")
    foreach ($file in $caFiles) {
        if (Test-Path $file) {
            Move-Item -Path $file -Destination $archiveDir -ErrorAction SilentlyContinue
        }
    }
}

# Optional cleanup for remaining directories except "archive" and optionally "CA"
Get-ChildItem -Directory | Where-Object { $_.Name -notmatch "^(archive)$" } | ForEach-Object {
    if (-not $IncludeCA -and $_.Name -eq "CA") { return }
    Move-Item -Path $_.FullName -Destination $archiveDir
}

Write-Host "Cleanup completed. Specific files created by the script moved to $archiveDir"

