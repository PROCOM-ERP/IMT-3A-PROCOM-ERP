# PowerShell Script: deploy.ps1

function Copy-SystemFiles {
    $paths = @("src/backend/*", "src/frontend/*")
    foreach ($path in $paths) {
        Get-ChildItem $path -Directory | ForEach-Object {
            $servicePath = $_.FullName
            Write-Host "Copying system files to $servicePath"
            Copy-Item -Path "./src/system/entrypoint.sh" -Destination $servicePath
            Copy-Item -Path "./src/system/procom-erp-truststore.jks" -Destination $servicePath
            Copy-Item -Path "./src/system/procom-erp-ca.pem" -Destination $servicePath
        }
    }

    $db_paths = @("src/databases/*")
    foreach ($db_path in $db_paths) {
        Get-ChildItem $db_path -Directory | ForEach-Object {
            $servicePath = $_.FullName
            Write-Host "Copying system files to $servicePath"
            Copy-Item -Path "./src/system/db_entrypoint.sh" -Destination $servicePath
        }
    }
}

function Is-StackRunning {
    param (
        [string]$stackName
    )
    $stackStatus = docker stack ps --format "{{.Name}}" $stackName 2>$null
    return [string]::IsNullOrWhiteSpace($stackStatus) -eq $false
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
        $fullImageName = "$dockerRegistry/{$imageName}:$imageVersion"
        
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
        $imageName = "$dockerRegistry/$service-latest"
        Write-Host "Pulling $imageName"
        docker pull $imageName
    }
}

function Deploy {
    # Example deploy function, replace with actual deployment logic from your script
    docker stack deploy -c docker-compose.yml "ERP"
}

if (-not (Test-Path ".\.env")) {
    Write-Host ".env file not found"
    exit
}

if (-not (Test-Path docker-compose.yml)) {
    Write-Host "docker-compose.yml file not found"
    exit
}

# Handling command line arguments (equivalent to bash positional parameters)
$push = $false
$pull = $false
$hot = $false
$dockerRegistry = ""

for ($i = 0; $i -lt $args.Count; $i++) {
    switch ($args[$i]) {
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


# Main deployment logic based on the provided flags
if (Is-StackRunning -stackName "ERP") {
    if (-not $hot) {
        Write-Host "The stack is already running. Use --hot option to force redeployment."
        exit 1
    } else {
        Write-Host "Hot deployment requested. Stopping the stack..."
        docker stack rm ERP
        Start-Sleep -Seconds 1 # Assuming a wait is needed before redeployment
    }
}

Copy-SystemFiles
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
