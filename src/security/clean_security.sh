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

# Prompt for the number of services
read -p "Enter the number of services: " num_services

# Initialize arrays for services and hostnames
services=()

for ((i=1; i<=num_services; i++)); do
    read -p "Enter the name for Service ${i}: " service_name

    services+=("${service_name}")
done


# Get the current date and time
current_datetime=$(date +"%Y-%m-%d-%H-%M")

# Create the archive directory
archive_dir="./archive/${current_datetime}"
mkdir -p "${archive_dir}"

# Move only the specific files created by the script to the archive directory
for ((i=0; i<num_services; i++)); do
    service="${services[i]}"

    echo ${service}
    # Move the .p12 file to the archive directory
    mv "../backend/${service}Service/${service}-service-keystore.p12" "${archive_dir}/"
    
    # If the service is 'message-broker', move the PEM files as well
    if [ "${service}" = "message-broker" ]; then
        mv "../backend/${service}Service/${service}-service-key.pem" "${archive_dir}/"
        mv "../backend/${service}Service/${service}-service-certificate.pem" "${archive_dir}/"
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

mv "../system/procom-erp-truststore.jks" "${archive_dir}"
rm "../system/procom-erp-ca.pem"
    
# Notify the user
echo "Specific files created by the script moved to ${archive_dir}"

