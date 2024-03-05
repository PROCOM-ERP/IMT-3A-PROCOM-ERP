# Script Name : undeploy.ps1
# Description: Undeploy the ERP application
# Author: maestro-bene (GitHub)
# Date Created: 2024-03-02
# Last Modified: 2024-03-02
# Version: 1.0


# Check if Swarm is active
$swarmActive = docker info 2>$null | Select-String "Swarm: active"

if ($swarmActive) {
    Write-Host "Swarm detected"
    docker stack rm ERP
    docker swarm leave --force
} else {
    docker-compose -p erp down
    docker-compose -p elk down
}
