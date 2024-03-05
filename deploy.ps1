# PowerShell Script: deploy.ps1
# Usage: see .sh script, this is only an adaptation of it, not maintained as well.

# +-----------------------------------------------------------------------------+
# | System-relative Functions                                                   |
# +-----------------------------------------------------------------------------+

function Security {
    # Execute the security_setup.sh script using bash
    & .\src\security\security_setup.ps1
}

# +-----------------------------------------------------------------------------+

function Copy-SystemFiles {
    # Define paths to backend and frontend directories
    $backendPaths = Get-ChildItem -Path "src/backend" -Directory
    $frontendPaths = Get-ChildItem -Path "src/frontend" -Directory

    Write-Output "Copying system files to services"

    # Copy system files to backend services
    foreach ($backendPath in $backendPaths) {
        if (Test-Path -Path $backendPath.FullName) {
            Copy-Item -Path "$systemPath\entrypoint.sh" -Destination $backendPath.FullName -Force
            Copy-Item -Path "$systemPath\wait-for-it.sh" -Destination $backendPath.FullName -Force
            Copy-Item -Path "$systemPath\procom-erp-truststore.jks" -Destination $backendPath.FullName -Force
            Copy-Item -Path "$systemPath\procom-erp-ca.pem" -Destination $backendPath.FullName -Force
            # Copy Maven Wrapper (mvnw) to backend services
            Copy-Item -Path "$systemPath\mvnw" -Destination $backendPath.FullName -Force
            Copy-Item -Path "$systemPath\.mvn" -Destination "$($backendPath.FullName)" -Recurse -Force
        }
    }

    # Copy system files to frontend services
    foreach ($frontendPath in $frontendPaths) {
        if (Test-Path -Path $frontendPath.FullName) {
            Copy-Item -Path "$systemPath\procom-erp-truststore.jks" -Destination $frontendPath.FullName -Force
            Copy-Item -Path "$systemPath\procom-erp-ca.pem" -Destination $frontendPath.FullName -Force
        }
    }

    $db_paths = @("src\databases\*")
    foreach ($db_path in $db_paths) {
        Get-ChildItem $db_path -Directory | ForEach-Object {
            $servicePath = $_.FullName
            Copy-Item -Path "$systemPath\db_entrypoint.sh" -Destination $servicePath
        }
    }
}

# +-----------------------------------------------------------------------------+

function Clean-SystemFiles {
    # Define paths to backend, frontend, and database directories
    $backendPaths = Get-ChildItem -Path "src/backend" -Directory
    $frontendPaths = Get-ChildItem -Path "src/frontend" -Directory
    $dbPaths = Get-ChildItem -Path "src/databases" -Directory

    Write-Output "Cleaning up system files from services"

    # Clean up system files from backend services
    foreach ($backendPath in $backendPaths) {
        if (Test-Path -Path $backendPath.FullName) {
            Remove-Item -Path "$($backendPath.FullName)\entrypoint.sh" -ErrorAction SilentlyContinue
            Remove-Item -Path "$($backendPath.FullName)\wait-for-it.sh" -ErrorAction SilentlyContinue
            Remove-Item -Path "$($backendPath.FullName)\procom-erp-truststore.jks" -ErrorAction SilentlyContinue
            Remove-Item -Path "$($backendPath.FullName)\procom-erp-ca.pem" -ErrorAction SilentlyContinue
            Remove-Item -Path "$($backendPath.FullName)\mvnw" -ErrorAction SilentlyContinue
            Remove-Item -Path "$($backendPath.FullName)\.mvn" -Recurse -ErrorAction SilentlyContinue
        }
    }

    # Clean up system files from frontend services
    foreach ($frontendPath in $frontendPaths) {
        if (Test-Path -Path $frontendPath.FullName) {
            Remove-Item -Path "$($frontendPath.FullName)\procom-erp-truststore.jks" -ErrorAction SilentlyContinue
            Remove-Item -Path "$($frontendPath.FullName)\procom-erp-ca.pem" -ErrorAction SilentlyContinue
        }
    }

    # Clean up system files from database services
    foreach ($dbPath in $dbPaths) {
        if (Test-Path -Path $dbPath.FullName) {
            Remove-Item -Path "$($dbPath.FullName)\db_entrypoint.sh" -ErrorAction SilentlyContinue
        }
    }
}

# +-----------------------------------------------------------------------------+
# | Helper Functions                                                            |
# +-----------------------------------------------------------------------------+

function Is-StackRunning {
    param (
        [string]$stackName
    )
    $stackStatus = docker stack ps --format "{{.Name}}" $stackName 2>$null
    return [string]::IsNullOrWhiteSpace($stackStatus) -eq $false
}

# +-----------------------------------------------------------------------------+

function Import-DotEnv {
    if (Test-Path $envPath) {
        Get-Content $envPath | ForEach-Object {
            if ($_ -match '^\s*([a-zA-Z_][a-zA-Z0-9_]*)\s*=\s*(.*)$') {
                $name = $matches[1]
                $value = $matches[2].Trim().Trim('"','''') # Trim spaces, double and single quotes
                
                # Set environment variable for the current process
                [System.Environment]::SetEnvironmentVariable($name, $value, [System.EnvironmentVariableTarget]::Process)
            }
        }
    }
    else {
        Write-Host "System error: .env file not found"
    }
}

# +-----------------------------------------------------------------------------+

function Get-ImageVersions {
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
        Write-Host "System error: .env file not found"
        exit
    }
}

# +-----------------------------------------------------------------------------+

function Latest-ImageExists {
    param (
        [string]$imageName
    )
    $latestImage = docker images "${imageName}:latest" | Select-String "$imageName"
    return $null -ne $latestImage
}

# +-----------------------------------------------------------------------------+
# | Core Functions                                                              |
# +-----------------------------------------------------------------------------+

function Setup-Elk {
    docker-compose -f $elkComposeFile up -d --build setup
}

# +-----------------------------------------------------------------------------+

function Deploy-Elk {
    if ($swarm) {
        docker-compose -f $elkComposeFile -f $elkFilebeatComposeFile-p elk up -d --build
    } else {
        docker-compose -f $elkComposeSwarmFile -f $elkFilebeatComposeSwarmFile-p elk up -d --build
    }
}

# +-----------------------------------------------------------------------------+

function Build-Images {
    docker-compose -f $composeFile build
}

# +-----------------------------------------------------------------------------+

function PushImages {
    param (
        [string]$dockerRegistry
    )
    Get-ImageVersions # Ensure image versions are loaded
    foreach ($imageName in $global:imageNames.Keys) {
        $imageVersion = $global:imageVersions[$imageName]
        $fullImageName = "$dockerRegistry\{$imageName}:$imageVersion"
        
        Write-Host "Tagging and pushing $fullImageName"
        docker tag "${imageName}:$imageVersion" $fullImageName
        docker push $fullImageName
    }
}

# +-----------------------------------------------------------------------------+

function Pull-Images {
    param (
        [string]$dockerRegistry
    )
    $services = docker-compose -f $composeFile config --services
    foreach ($service in $services) {
        $imageName = "$dockerRegistry\$service-$version"
        Write-Host "Pulling $imageName"
        docker pull $imageName
    }
}

# +-----------------------------------------------------------------------------+

function Deploy {
    if ($swarm) {
        docker stack deploy -c $composeFile ERP
    } else {
        docker-compose -f $composeFile -p erp up -d
    }
}

# +-----------------------------------------------------------------------------+
# | Setup & Verification                                                        |
# +-----------------------------------------------------------------------------+

$securityPath = ".\security"
$systemPath = ".\system"
$dockerPath = ".\docker"
$elkPath = "${dockerPath}\elk"
$envPath = "$dockerPath\.env"

if (-not (Test-Path "$envPath")) {
    Write-Host "System error: .env file not found"
    exit
}

if (-not (Test-Path "$dockerPath\docker-compose.yml")) {
    Write-Host "System error: docker-compose.yml file not found"
    exit
}

if (-not (Test-Path "$dockerPath\docker-compose-swarm.yml")) {
    Write-Host "System error: docker-compose-swarm.yml file not found"
    exit
}

# Handling command line arguments (equivalent to bash positional parameters)
$swarm = $false
$sec = $false
$push = $false
$pull = $false
$hot = $false
$logs = $true
$dockerRegistry = ""
$composeFile = "$dockerPath\docker-compose.yml"
$elkComposeFile = "$elkPath\docker-compose.yml"
$elkComposeSwarmFile = "$elkPath\docker-compose.yml"
$elkFilebeatComposeFile = "$elkPath\extensions\filebeat\filebeat-compose-swarm.yml"
$elkFilebeatComposeSwarmFile = "$elkPath\extensions\filebeat\filebeat-compose-swarm.yml"
$version = "latest"
$version_specified = $false

# +-----------------------------------------------------------------------------+
# | Argument Parsing                                                            |
# +-----------------------------------------------------------------------------+

for ($i = 0; $i -lt $args.Count; $i++) {
    switch ($args[$i]) {
        "--swarm" {
            $swarm = $true
            $composeFile = "$dockerPath\docker-compose-swarm.yml"
        }
        "--sec" {
            $sec = $true
        }
        "--push" {
            $push = $true
            if ($i + 1 -lt $args.Count -and $args[$i + 1] -notmatch "^--") {
                $dockerRegistry = $args[++$i]
            } else {
                Write-Host "Error: --push option requires a value."
                exit 1
            }
        }
        "--pull" {
            $pull = $true
            if ($i + 1 -lt $args.Count -and $args[$i + 1] -notmatch "^--") {
                $dockerRegistry = $args[++$i]
            } else {
                Write-Host "Error: --pull option requires a value."
                exit 1
            }
        }
        "--version" {
            if ($i + 1 -lt $args.Count -and $args[$i + 1] -notmatch "^--") {
                $version = $args[++$i]
                $version_specified = $true
            } else {
                Write-Host "Error: --version option requires a value."
                exit 1
            }
        }
        "--hot" {
            $hot = $true
        }
        "--no-logs" {
            $logs = $false
        }
        "--docs" {
            type .\docs\DEPLOYING.md
            exit 0
        }
        default {
            Write-Host "Invalid option: $($args[$i])"
            exit 1
        }
    }
}

if ($pull -and -not $version_specified) {
    Write-Host "Error: --pull option requires --version to be specified."
    exit 1
}

# +-----------------------------------------------------------------------------+
# | MAIN LOGIC                                                                  |
# +-----------------------------------------------------------------------------+

# +---Swarm Specific------------------------------------------------------------+
if ($swarm) {

    Import-DotEnv

    $swarmActive = docker info 2>$null | Select-String "Swarm: active"
    if (-not $swarmActive) {
        docker swarm init > $null 2>&1
        Write-Host "Swarm initialized"
        $securityPath\docker_secrets.ps1 > $null 2>&1
    } else {
        Write-Host "Swarm detected"
    }

    if (Is-StackRunning -stackName "ERP") {
        if (-not $hot) {
            Write-Host "Error: The stack is already running. Use --hot option to force redeployment."
            exit 1
        } else {
            Write-Host "Hot deployment requested. Stopping the stack..."
            docker stack rm ERP
            while (Is-StackRunning -stackName "ERP") {
                Start-Sleep -Seconds 3
            }
        }
    }
} else {
    # +---Docker Secrets for simple compose-----------------------------------------+
    $securityPath\docker_secrets_files.ps1 > $null 2>&1
}

if ($logs) {
    # +---Setting up the monitoring and logs stack----------------------------------+
    Setup-Elk

    # Wait for the container to exit
    docker wait "setup" *>$null

    # Remove the container
    docker rm "setup" *>$null
    docker rmi "elk-setup" *>$null

    # +---Deploying the monitoring and logs stack-----------------------------------+
    Deploy-Elk
}

# +---System-relative setup-----------------------------------------------------+

if ($sec) {
    Security
}

Copy-SystemFiles

Get-ImageVersions

# +---Deployment----------------------------------------------------------------+

if ($push) {
    docker login
    Build-Images
    PushImages -dockerRegistry $dockerRegistry
} elseif ($pull -and $version_specified) {
    docker login
    Pull-Images -dockerRegistry $dockerRegistry
} else {
    # When neither push nor pull is specified, build images locally
    # This assumes that building images is necessary before deployment
    # Adjust this section if your workflow differs
    Build-Images
}

# Deployment is the final step regardless of the path taken above
Deploy

Clean-SystemFiles
# +----End of this handy script------------------------------------------------+
