# PowerShell Script: deploy.ps1
# Usage: see .sh script, this is only an adaptation of it, not maintained as well.

# +-----------------------------------------------------------------------------+
# | System-relative Functions                                                   |
# +-----------------------------------------------------------------------------+

function Clean-Security {
    param (
        [bool]$CA = $false
    )

    if ($CA) {
        Write-Host "Cleaning certificates along with the CA's"
        & "${security_path}\clean_security.ps1" "--CA" | Out-Null
    }
    else {
        Write-Host "Cleaning certificates"
        & "${security_path}\clean_security.ps1" | Out-Null
    }
    Write-Host "Certificates all cleaned up"
}

# +-----------------------------------------------------------------------------+

function Security {
        Write-Host "Generating certificates"
    & .\src\security\security_setup.ps1
        Write-Host "Certificates generated"
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
    $backendPaths = Get-ChildItem -Path "src\backend" -Directory
    $frontendPaths = Get-ChildItem -Path "src\frontend" -Directory
    $dbPaths = Get-ChildItem -Path "src\databases" -Directory

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

function Latest-ImageExists {
    param (
        [string]$imageName
    )
    $latestImage = docker images "${imageName}:latest" | Select-String "$imageName"
    return $null -ne $latestImage
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

function Remove-ElkSetupContainer {
    Write-Host "Now waiting for Elasticsearch setup to finish before starting complete ELK, and then ERP"
    
    # Wait for the container to exit
    docker wait "setup" | Out-Null
    
    # Remove the container
    docker rm "setup" | Out-Null
    docker rmi "elk-setup" | Out-Null
}

# +-----------------------------------------------------------------------------+

function Import-LogDashboardInKibana {
    Write-Host -NoNewline "Waiting for Kibana to start"
    while (-not (Test-Connection localhost -Port 5601 -Quiet)) {
        Write-Host -NoNewline '.'
        Start-Sleep -Seconds 2
    }
    Write-Host ''

    # Import the NDJSON file into Kibana
    Start-Sleep -Seconds 5

    $import = "Successful"
    $max_attempts = 10
    $attempt = 0

    while ($attempt -lt $max_attempts) {
        if ((Invoke-WebRequest -Uri "http://localhost:5601/api/saved_objects/_import?overwrite=true" -Method POST -Headers @{ "kbn-xsrf" = "true" } -Credential $ELASTIC_USER -Body @{ file = [System.IO.File]::ReadAllBytes("docker/elk/export.ndjson") }) -eq "Success") {
            Write-Host "Import successful."
            break
        } else {
            $attempt++
            Start-Sleep -Seconds 1
        }
    }

    if ($attempt -eq $max_attempts) {
        Write-Host "Import failed after $max_attempts attempts. Please check and retry manually."
        $import = "Failed"
    }
}

# +-----------------------------------------------------------------------------+

function CheckSystemFiles {
    # List of files to verify and copy
    $filesToCopy = @(
        "entrypoint.sh",
        "wait-for-it.sh",
        "procom-erp-truststore.jks",
        "procom-erp-ca.pem",
        "mvnw",
        ".mvn", # Assuming you want to include the .mvn directory as well
        "db_entrypoint.sh"
    )

    # Loop through the files and verify existence
    foreach ($file in $filesToCopy) {
        $sourcePath = Join-Path -Path $systemPath -ChildPath $file

        if (-Not (Test-Path -Path $sourcePath)) {
            Write-Host "Required file $file not found in $systemPath."
            exit 1
        }
    }
}

# +-----------------------------------------------------------------------------+
# | Core Functions                                                              |
# +-----------------------------------------------------------------------------+

function Setup-Elk {
    Write-Host "Setting up Elasticsearch"
    if ($swarm) {
        docker-compose -f $elkComposeSwarmFile -f $elkFilebeatComposeSwarmFile -p elk up -d --build setup
    }
    else {
        docker-compose -f $elkComposeFile -f $elkFilebeatComposeFile -p elk up -d --build setup
    }
}

# +-----------------------------------------------------------------------------+

function Deploy-Elk {
    Write-Host "Deploying Elastic stack (Filebeat, Logstash, Elasticsearch, Kibana)"
    if ($swarm) {
        docker-compose -f $elkComposeFile -f $elkFilebeatComposeFile -p elk up -d --build
    } else {
        docker-compose -f $elkComposeSwarmFile -f $elkFilebeatComposeSwarmFile -p elk up -d --build
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
$envPath = "${dockerPath}\.env"

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
$CA= $false
$cleanSec = $false
$push = $false
$pull = $false
$hot = $false
$logs = $true

$dockerRegistry = ""
$composeFile = "$dockerPath\docker-compose.yml"
$elkComposeFile = "$elkPath\docker-compose.yml"
$elkComposeSwarmFile = "$elkPath\docker-compose-swarm.yml"
$elkFilebeatComposeFile = "$elkPath\extensions\filebeat\filebeat-compose.yml"
$elkFilebeatComposeSwarmFile = "$elkPath\extensions\filebeat\filebeat-compose-swarm.yml"


$elasticUser = "elastic"
$elasticPassword = (Get-Content "${elkPath}/.env" | Where-Object { $_ -match "^ELASTIC_PASSWORD=" } | ForEach-Object { $_.Split('=')[1] }).Trim().Trim("'")

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
        "--clean-sec" {
            $cleanSec = $true
            if ($args.Count -gt 1 -and $args[1] -eq "CA") {
                $CA = $true
                $args = $args[2..$args.Count]
            }
            else {
                $args = $args[1..$args.Count]
            }
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
        "--help" {
            Write-Host "Usage:"
            Write-Host "        '.\deploy.ps1 --option `"value`"'"
            Write-Host " "
            Write-Host "Possible options:"
            Write-Host "        '--swarm': --------------------->  Deploy using Docker Swarm mode."
            Write-Host "        '--clean-sec `"CA`"': ------------>  Execute the clean security setup before deploying, --sec option is required if you use this. CA: optional, to also clean CA cert files."
            Write-Host "        '--sec'  ----------------------->  Execute the security setup script as well."
            Write-Host "        '--push `"repository/image`"': --->  Tag and push Docker images to a repository, then deploy."
            Write-Host "        '--pull `"repository/image`"': --->  Pull Docker images from a repository from which to deploy, --version is required if you use this option"
            Write-Host "        '--version `"version`"': --------->  Specify the version of the images to use for the pull option."
            Write-Host "        '--hot': ----------------------->  Force redeployment even if the stack is already running, for swarm mode."
            Write-Host "        '--no-logs': ------------------->  Doesn't deploy monitoring and log aggregating containers (Elastic stack)."
            Write-Host "        '--help': ---------------------->  Display this help message."
            Write-Host "        '--doc': ----------------------->  Display the deployment documentation."
            Write-Host " "
            Write-Host "Examples:"
            Write-Host "        '.\deploy.ps1 --swarm --clean-sec CA --sec --pull --version 1.0.0 --hot'"
            Write-Host "        '.\deploy.ps1 --help'"
            Write-Host "        '.\deploy.ps1 --doc'"
            Write-Host " "
            Write-Host "You can use this script without specifying options, it will build and deploy by default with compose from docker-compose.yml"
            exit 0
        }
        default {
            Write-Host "Invalid option: $($args[$i])"
            exit 1
        }
    }
}

# +-----------------------------------------------------------------------------+

if ($pull -and -not $version_specified) {
    Write-Host "Error: --pull option requires --version to be specified."
    exit 1
}

# +-----------------------------------------------------------------------------+

if (-not $sec -and $cleanSec) {
    Write-Host "Usage:"
    Write-Host " "
    Write-Host "When using --clean-sec, please also use --sec option, otherwise you'll face certificate problems."
    exit
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
        # +---Docker secrets for Swarm mode-------------------------------------+
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
   # +---Docker Secrets for simple Compose-------------------------------------+
    Write-Host "Generating docker secrets"
    $securityPath\docker_secrets_files.ps1 > $null 2>&1
    Write-Host "Docker secrets generated"
    Write-Host ""
}


# +---System-relative setup-----------------------------------------------------+

if ($cleanSec) {
    Clean-Security
}

if ($sec) {
    Security
}

CheckSystemFiles

Copy-SystemFiles

Get-ImageVersions

# +---Deployment----------------------------------------------------------------+

# +---Logs Setup & Deployment---------------------------------------------------+
if ($logs) {
    Setup-Elk

    Remove-ElkSetupContainer

    Deploy-Elk

    Import-LogDashboardInKibana
}

# +---ERP Setup & Deployment----------------------------------------------------+
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
@"
 ______                                    _______  ______   ______  
(_____ \                                  (_______)(_____ \ (_____ \ 
 _____) )____  ___    ____  ___   ____     _____    _____) ) _____) )
|  ____// ___)/ _ \  / ___)/ _ \ |    \   |  ___)  |  __  / |  ____/ 
| |    | |   | |_| |( (___| |_| || | | |  | |_____ | |  \ \ | |      
|_|    |_|    \___/  \____)\___/ |_|_|_|  |_______)|_|   |_||_|      

"@

Write-Host "Welcome to Procom ERP, here are the access links:"
Write-Host " "
Write-Host "1. Link to the frontend: [https://localhost:3000]"
Write-Host "2. Link to the gateway hello world to accept its certificate as well: [https://localhost:8041/api/v1/authentication/hello]"
Write-Host "3. Link to the Elastic stack (Kibana): [http://localhost:5601]"

if ($import -eq "Successful") {
Write-Host "4. Link to the Custom Dashboard: [http://localhost:5601/app/discover]. Click on 'Open' to find and open the custom ERP-Dashboard"
}

if ($import -eq "Failed") {
    Write-Host ""
    Write-Host "The Import of the custom dashboard failed, please try to do it manually within Elastic:"
    Write-Host " - In Elastic, in Discover [http://localhost:5601/app/discover], click on \"Open\"."
    Write-Host " - Click on \"Manage Searches\"."
    Write-Host " - Click on \"Import\"."
    Write-Host " - Import the ERP-Dashboard by selecting the export.ndjson file located here : \"./docker/elk/export.ndjson\"."
}
# +----End of this handy script------------------------------------------------+
