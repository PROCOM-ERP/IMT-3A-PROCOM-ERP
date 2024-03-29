#!/bin/bash
# Script Name: clean_security.sh
# Description: Opposite of "security_setup.sh"
# Author: maestro-bene (GitHub)
# Date Created: 2024-01-15
# Last Modified: 2024-02-03
# Version: 1.3
# Usage: Just run the script, it will analyze the backend and frontend directory and remove every keys, certs, csr, etc.
# Notes: Another scripts "security_setup.sh" works with this one to undo the changes made by this script.
# Option: --CA includes all CA keys, certs, and trust stores as well, to fully clean your environment

# +-----------------------------------------------------------------------------+
# | Initial Setup & Argument Parsing                                            |
# +-----------------------------------------------------------------------------+

include_ca=false

while [[ $# -gt 0 ]]; do
    case "$1" in
        --CA)
            include_ca=true
            shift
            ;;
        *)
            echo "Unknown option: $1"
            exit 1
            ;;
    esac
done

# +-----------------------------------------------------------------------------+

# Save the current directory
currentDir=$(pwd)

# Change directory to ./security
cd ./security || exit

src_path="../src"
system_path="../system"

# +-----------------------------------------------------------------------------+

backend_services=()
backend_service_directories=($(find ${src_path}/backend/ -maxdepth 1 -type d -name '*Service'))

frontend_services=()
frontend_service_directories=($(find ${src_path}/frontend/ -maxdepth 1 -type d -name '*Service'))

# Extract service names and count them
for dir in "${backend_service_directories[@]}"; do
    service_name=$(basename "$dir" | sed 's/Service$//')
    backend_services+=("$service_name")
done

for dir in "${frontend_service_directories[@]}"; do
    service_name=$(basename "$dir" | sed 's/Service$//')
    frontend_services+=("$service_name")
done

# Calculate the total number of services
num_backend_services=${#backend_services[@]}
num_frontend_services=${#frontend_services[@]}
num_services=$((num_backend_services + num_frontend_services))

# +-----------------------------------------------------------------------------+

# Get the current date and time
current_datetime=$(date +"%Y-%m-%d-%H-%M-%S")

# Create the archive directory
archive_dir="./archive/${current_datetime}"
mkdir -p "${archive_dir}"

# Initialize arrays for services and hostnames
services=()

# +-----------------------------------------------------------------------------+
# | Core Function                                                               |
# +-----------------------------------------------------------------------------+

# Function to move service related files to the archive directory
move_service_files() {
    local service=$1
    local service_type=$2

    mv "${src_path}/${service_type}/${service}Service/${service}-service-keystore.p12" "${archive_dir}/" 2>/dev/null

    if [[ "${include_ca}" == "true" ]]; then
        mv "${src_path}/${service_type}/${service}Service/procom-erp-truststore.jks" "${archive_dir}/" 2>/dev/null
    fi
    
    # If the service is 'message-broker', move the PEM files as well
    if [ "${service}" = "message-broker" ]; then
        mv "${src_path}/${service_type}/${service}Service/${service}-service-key.pem" "${archive_dir}/" 2>/dev/null

        mv "${src_path}/${service_type}/${service}Service/${service}-service-certificate.pem" "${archive_dir}/" 2>/dev/null

    fi
    
    # If the service is 'webapp', move the PEM files as well
    if [ "${service}" = "webapp" ]; then
        rm "${src_path}/${service_type}/${service}Service/${service}-service.crt" 2>/dev/null
        rm "${src_path}/${service_type}/${service}Service/${service}-service.key" 2>/dev/null

        if [[ "${include_ca}" == "true" ]]; then
            mv "${src_path}/${service_type}/${service}Service/procom-erp-ca.pem" "${archive_dir}/" 2>/dev/null

        fi
    fi
}

# +-----------------------------------------------------------------------------+
# | Main Logic                                                                  |
# +-----------------------------------------------------------------------------+


# Move only the specific files created by the script to the archive directory
for ((i=0; i<num_services; i++)); do
    if [ $i -lt $num_backend_services ]; then
        service="${backend_services[i]}"
        service_type="backend"
    else
        index=$((i - num_backend_services))
        service="${frontend_services[index]}"
        service_type="frontend"
    fi
    move_service_files $service $service_type
done

# Move all remaining unused certificates and security files created by the script to the archive directory
for dir in ./*; do
    if [[ -d "${dir}" && "${dir}" != "./archive" && "${dir}" != "./secrets" ]]; then
        if [[ "${include_ca}" == "true" ]]; then
            # If include_ca is true, move everything except the archive directory
            mv "${dir}" "${archive_dir}/"
        else
            # If include_ca is false, move everything except archive, secrets and CA directory
            if [[ "${dir}" != "./CA" ]]; then
                mv "${dir}" "${archive_dir}/"
            fi
        fi
    fi
done

if [[ "${include_ca}" == "true" ]]; then
    mv "${system_path}/procom-erp-truststore.jks" "${archive_dir}" 2>/dev/null
    rm "${system_path}/procom-erp-ca.pem" 2>/dev/null
fi

# Change back to the original directory
cd "$currentDir" || exit

# Notify the user
echo "Specific files created by the script moved to ${archive_dir}"

# +----End of this handy script------------------------------------------------+
