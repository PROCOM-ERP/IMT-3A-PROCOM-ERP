# Script Name : undeploy.ps1
# Description: Undeploy the ERP application
# Author: maestro-bene (GitHub)
# Date Created: 2024-03-02
# Last Modified: 2024-03-02
# Version: 1.0

$docker_path = ".\docker"

# Check if Swarm is active
$swarmActive = docker info 2>$null | Select-String "Swarm: active"

if ($swarmActive) {
    Write-Host "Swarm detected"
    docker stack rm ERP
    docker swarm leave --force
} else {
    # Change directory to docker_path
    Set-Location $docker_path
    docker-compose down
    # Navigate back to the original directory
    Set-Location -Path ".."
}
