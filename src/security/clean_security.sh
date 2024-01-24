#!/bin/bash
# Script Name: clean_security.sh
# Description: Opposite of "security_setup.sh"
# Author: maestro-bene (GitHub)
# Date Created: 2024-01-15
# Last Modified: 2024-01-15
# Version: 1.0
# Usage: Enter the number of services, then each of their names.
# Notes: Another scripts "security_setup.sh" works with this one to undo the changes made by this script.

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


backend_services=()
backend_service_directories=($(find ../backend/ -maxdepth 1 -type d -name '*Service'))
frontend_services=()
frontend_service_directories=($(find ../frontend/ -maxdepth 1 -type d -name '*Service'))

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
# echo ${num_services}

# Get the current date and time
current_datetime=$(date +"%Y-%m-%d-%H-%M-%S")

# Create the archive directory
archive_dir="./archive/${current_datetime}"
mkdir -p "${archive_dir}"

# Initialize arrays for services and hostnames
services=()

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

    # echo ${service}
    # Move the .p12 file to the archive directory
    mv "../${service_type}/${service}Service/${service}-service-keystore.p12" "${archive_dir}/"
    
    # If the service is 'message-broker', move the PEM files as well
    if [ "${service}" = "message-broker" ]; then
        mv "../${service_type}/${service}Service/${service}-service-key.pem" "${archive_dir}/"
        mv "../${service_type}/${service}Service/${service}-service-certificate.pem" "${archive_dir}/"
    fi
    
    # Move the entire service directory to the archive directory
    mv "./${service}" "${archive_dir}/"
done

for dir in ./*; do
    if [[ -d "${dir}" && "${dir}" != "./archive" ]]; then
        if [[ "${include_ca}" == "true" ]]; then
            # If include_ca is true, move everything except the archive directory
            mv "${dir}" "${archive_dir}/"
        else
            # If include_ca is false, move everything except archive and CA directory
            if [[ "${dir}" != "./CA" ]]; then
                mv "${dir}" "${archive_dir}/"
            fi
        fi
    fi
done

if [[ "${include_ca}" == "true" ]]; then
    mv "../system/procom-erp-truststore.jks" "${archive_dir}"
    rm "../system/procom-erp-ca.pem"
fi
    
# Notify the user
echo "Specific files created by the script moved to ${archive_dir}"

