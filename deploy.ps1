# PowerShell Script: deploy.ps1

function Security {
    Set-Location -Path ".\src\security"
    # Execute the security_setup.sh script using bash
    & .\security_setup.ps1
}

function Copy-SystemFiles {
    $paths = @("src\backend\*", "src\frontend\*")
    foreach ($path in $paths) {
        Get-ChildItem $path -Directory | ForEach-Object {
            $servicePath = $_.FullName
            Write-Host "Copying system files to $servicePath"
            Copy-Item -Path ".\src\system\entrypoint.sh" -Destination $servicePath
            Copy-Item -Path ".\src\system\procom-erp-truststore.jks" -Destination $servicePath
            Copy-Item -Path ".\src\system\procom-erp-ca.pem" -Destination $servicePath
        }
    }

    $db_paths = @("src\databases\*")
    foreach ($db_path in $db_paths) {
        Get-ChildItem $db_path -Directory | ForEach-Object {
            $servicePath = $_.FullName
            Write-Host "Copying system files to $servicePath"
            Copy-Item -Path ".\src\system\db_entrypoint.sh" -Destination $servicePath
        }
    }
}

function Initialize-Swarm {
    $swarmActive = docker info 2>$null | Select-String "Swarm: active"
    if (-not $swarmActive) {
        docker swarm init > $null 2>&1
        Write-Host "Swarm initialized"
        .\src\security\docker_secrets.ps1 > $null 2>&1
    } else {
        Write-Host "Swarm detected"
    }
}

function Is-StackRunning {
    param (
        [string]$stackName
    )
    $stackStatus = docker stack ps --format "{{.Name}}" $stackName 2>$null
    return [string]::IsNullOrWhiteSpace($stackStatus) -eq $false
}

function Import-DotEnv {
    $envPath = ".\.env"
    if (Test-Path $envPath) {
        Get-Content $envPath | ForEach-Object {
            if ($_ -match '^\s*([a-zA-Z_][a-zA-Z0-9_]*)\s*=\s*(.*)$') {
                $name = $matches[1]
                $value = $matches[2].Trim().Trim('"','''') # Trim spaces, double and single quotes
                
                # Set environment variable for the current process
                [System.Environment]::SetEnvironmentVariable($name, $value, [System.EnvironmentVariableTarget]::Process)
            }
        }
        Write-Host "Environment variables imported from .env file."
    }
    else {
        Write-Host ".env file not found"
    }
}

function Get-ImageVersions {
    $envPath = ".\.env"
    $global:imageNames = @{}
    $global:imageVersions = @{}

    if (Test-Path $envPath) {
        $envContent = Get-Content $envPath
        foreach ($line in $envContent) {
            if ($line -match "HOSTNAME=(.+)") {
                $imageName = $matches[1]
            }
            if ($line -match "IMAGE_VERSION=([^ ]+)") {
                $imageVersion = $matches[1]
                $global:imageNames[$imageName] = $imageName
                $global:imageVersions[$imageName] = $imageVersion
            }
        }
    } else {
        Write-Host ".env file not found"
        exit
    }
}

function Latest-ImageExists {
    param (
        [string]$imageName
    )
    $latestImage = docker images "${imageName}:latest" | Select-String "$imageName"
    return $null -ne $latestImage
}

function Build-Images {
    docker-compose -f docker-compose.yml build
}

function Build-And-PushImages {
    param (
        [string]$dockerRegistry
    )
    Get-ImageVersions # Ensure image versions are loaded
    foreach ($imageName in $global:imageNames.Keys) {
        $imageVersion = $global:imageVersions[$imageName]
        $fullImageName = "$dockerRegistry\{$imageName}:$imageVersion"
        
        if (-not (Latest-ImageExists -imageName $imageName)) {
            Write-Host "Latest image for $imageName does not exist. Building..."
            Build-Images
        }
        
        Write-Host "Tagging and pushing $fullImageName"
        docker tag "${imageName}:$imageVersion" $fullImageName
        docker push $fullImageName
    }
}

function Pull-Images {
    param (
        [string]$dockerRegistry
    )
    $services = docker-compose -f docker-compose.yml config --services
    foreach ($service in $services) {
        $imageName = "$dockerRegistry\$service-latest"
        Write-Host "Pulling $imageName"
        docker pull $imageName
    }
}

function Deploy {
    if ($swarm) {
        docker stack deploy -c $composeFile ERP
    } else {
        docker-compose -f $composeFile up -d --build
    }
}

if (-not (Test-Path ".\.env")) {
    Write-Host ".env file not found"
    exit
}

if (-not (Test-Path ".\docker-compose.yml")) {
    Write-Host "docker-compose.yml file not found"
    exit
}

if (-not (Test-Path ".\docker-compose-swarm.yml")) {
    Write-Host "docker-compose-swarm.yml file not found"
    exit
}

# Handling command line arguments (equivalent to bash positional parameters)
$swarm = $false
$sec = $false
$push = $false
$pull = $false
$hot = $false
$dockerRegistry = ""
$composeFile = "docker-compose.yml"

for ($i = 0; $i -lt $args.Count; $i++) {
    switch ($args[$i]) {
        "--swarm" {
            $swarm = $true
            $composeFile = "docker-compose-swarm.yml"
        }
        "--sec" {
            $sec = $true
        }
        "--push" {
            $push = $true
            $dockerRegistry = $args[++$i]
        }
        "--pull" {
            $pull = $true
            $dockerRegistry = $args[++$i]
        }
        "--hot" {
            $hot = $true
        }
        default {
            Write-Host "Invalid option: $($args[$i])"
            exit 1
        }
    }
}

if ($swarm) {

    Import-DotEnv

    $swarmActive = docker info 2>$null | Select-String "Swarm: active"
    if (-not $swarmActive) {
        docker swarm init > $null 2>&1
        Write-Host "Swarm initialized"
    } else {
        Write-Host "Swarm detected"
    }

    if (Is-StackRunning -stackName "ERP") {
        if (-not $hot) {
            Write-Host "The stack is already running. Use --hot option to force redeployment."
            exit 1
        } else {
            Write-Host "Hot deployment requested. Stopping the stack..."
            docker stack rm ERP
            Start-Sleep -Seconds 30 # TODO dynamic redeployment
        }
    }
    Initialize-Swarm
}

Copy-SystemFiles

if ($sec) {
    Security
}

Get-ImageVersions

if ($push) {
    docker login
    Build-And-PushImages -dockerRegistry $dockerRegistry
} elseif ($pull) {
    docker login
    Pull-Images -dockerRegistry $dockerRegistry
} else {
    # When neither push nor pull is specified, build images locally
    # This assumes that building images is necessary before deployment
    # Adjust this section if your workflow differs
    Write-Host "Building images locally..."
    Build-Images
}

# Deployment is the final step regardless of the path taken above
Deploy
