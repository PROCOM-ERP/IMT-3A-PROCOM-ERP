#!/bin/bash
# Script Name: security_auto_setup.sh
# Description: Generate CA and services security essentials, and moves them to the correct spot.
# Author: maestro-bene (GitHub)
# Date Created: 2024-01-15
# Last Modified: 2024-03-03
# Version: 1.4
# Usage: Just run the script within the src/security directory, it will analyze the frontend and backend directories.
# Notes: Another scripts "clean_security.sh" works with this one to undo the changes made by this script, by giving the names.

# +-----------------------------------------------------------------------------+
# | Setup & Verification                                                        |
# +-----------------------------------------------------------------------------+

# Check if OpenSSL is installed
if ! command -v openssl &> /dev/null; then
    echo "OpenSSL is not installed. Please install OpenSSL and try again."
    exit 1
fi

# +-----------------------------------------------------------------------------+

# Check if keytool is installed
if ! command -v keytool &> /dev/null; then
    echo "keytool is not installed. Please install an openjdk 17-jre-headless and try again."
    exit 1
fi

# +-----------------------------------------------------------------------------+

# Save the current directory
currentDir=$(pwd)

# Change directory to ./security
cd ./security || exit

# +-----------------------------------------------------------------------------+

# Initialize arrays for services and hostnames
backend_services=()
backend_service_directories=($(find ../src/backend/ -maxdepth 1 -type d -name '*Service'))

frontend_services=()
frontend_service_directories=($(find ../src/frontend/ -maxdepth 1 -type d -name '*Service'))

# +-----------------------------------------------------------------------------+

ca_dir="./CA"

# +-----------------------------------------------------------------------------+

if [ ! -f "./CA/procom-erp-ca.crt" ] || [ ! -f "./CA/procom-erp-ca.key" ]; then
    CA_exists="true"
    ca_crt="procom-erp-ca.crt"
    ca_key="procom-erp-ca.key"
else
    CA_exists="false"
    ca_crt="${ca_dir}/procom-erp-ca.crt"
    ca_key="${ca_dir}/procom-erp-ca.key"
fi

# +-----------------------------------------------------------------------------+

# Extract service names and count them
for dir in "${backend_service_directories[@]}"; do
    service_name=$(basename "$dir" | sed 's/Service$//')
    backend_services+=("$service_name")
done

for dir in "${frontend_service_directories[@]}"; do
    service_name=$(basename "$dir" | sed 's/Service$//')
    frontend_services+=("$service_name")
done

# +-----------------------------------------------------------------------------+

# Calculate the total number of services
num_backend_services=${#backend_services[@]}
num_frontend_services=${#frontend_services[@]}
num_services=$((num_backend_services + num_frontend_services))

# +-----------------------------------------------------------------------------+
# | Core Functions                                                              |
# +-----------------------------------------------------------------------------+

# Function to generate certificates for a service
generate_certificate() {
    local service_name="$1"
    local service_type="$2"

    # Step 1: Generate Certificates for the Service
    openssl genrsa -out "${service_name}-service.key" 4096 2>/dev/null

    openssl req -new -key "${service_name}-service.key" -out "${service_name}-service.csr" -subj "/C=FR/ST=France/L=Paris/O=Procom-ERP/OU=IT/CN=springboot-procom-erp-${service_name}-service" 2>/dev/null


    # Step 2: Sign the CSR with Your CA
    openssl x509 -req -days 365 -in "${service_name}-service.csr" -CA "${ca_crt}" -CAkey "${ca_key}" -out "${service_name}-service.crt" 2>/dev/null
    
    # +-Calling external generation---------------------------------------------+
    ./generate_certificate_passwords.sh ${service_name}
    
    # Move the generated files to the service directory
    service_dir="../src/${service_type}/${service_name}Service"
    mkdir -p "${service_dir}"
    mkdir -p "./${service}"
    
    # If the service is 'message-broker', move PEM files
    if [ "${service}" = "message-broker" ]; then

        mv "${service}-service-key.pem" "${service_dir}" 2>/dev/null

        mv "${service}-service-certificate.pem" "${service_dir}" 2>/dev/null

    fi

    if [ "${service}" = "webapp" ]; then
        cp "${service_name}-service.key" "${service_name}-service.crt" "${service_dir}/"
        openssl x509 -in "${ca_crt}" -out "${service_dir}/procom-erp-ca.pem" -outform PEM 2>/dev/null

    fi

    mv "${service_name}-service.key" "${service_name}-service.csr" "${service_name}-service.crt" "./${service}/" 2>/dev/null

    mv "${service_name}-service-keystore.p12" "${service_dir}/" 2>/dev/null

    echo "Certificates for ${service_name} moved to ./${service} and the keystore to ${service_dir}"
}

# +-----------------------------------------------------------------------------+

generate_CA(){
    # Generate the Root Key
    openssl genrsa -out procom-erp-ca.key 4096 2>/dev/null


    # Create and Self-Sign the Root Certificate
    openssl req -new -x509 -days 3650 -key procom-erp-ca.key -out procom-erp-ca.crt -subj "/C=FR/ST=France/L=Paris/O=Procom-ERP/OU=IT/CN=Procom-ERP" 2>/dev/null

    openssl x509 -in "${ca_crt}" -out "../system/procom-erp-ca.pem" -outform PEM 2>/dev/null

    
    # Create a trust store for the services to trust
    CA_password=$(./generate_certificate_passwords.sh "CA")

    keytool -importcert -noprompt -alias ca_cert -file procom-erp-ca.crt -keystore procom-erp-truststore.jks --store-pass "${CA_password}" 2>/dev/null
    echo "New CA files generated."
}

# +-----------------------------------------------------------------------------+
# | Main Logic                                                                  |
# +-----------------------------------------------------------------------------+

# Check if CA files exist or generate them if needed
if [ "${CA_exists}" == "true" ]; then
    generate_CA
fi

# +-----------------------------------------------------------------------------+

# Generate certificates for each service
for ((i=0; i<num_services; i++)); do
    if [ $i -lt $num_backend_services ]; then
        service="${backend_services[i]}"
        service_type="backend"
    else
        index=$((i - num_backend_services))
        service="${frontend_services[index]}"
        service_type="frontend"
    fi
    
    echo "Generating certificates for ${service} (${service_type})"
    
    generate_certificate "${service}" "${service_type}"
done

# +-----------------------------------------------------------------------------+

# Move the CA's keys and certificates to the CA directory
mkdir -p "${ca_dir}"
mv procom-erp-truststore.jks "../system/"

openssl x509 -in "${ca_crt}" -out "../system/procom-erp-ca.pem" -outform PEM 2>/dev/null

mv "${ca_key}" "${ca_crt}" "${ca_dir}/" 2>/dev/null

echo "CA's keys and certificates moved to ${ca_dir}"

# Change back to the original directory
cd "$currentDir" || exit

# +-----------------------------------------------------------------------------+

echo "Certificates generation completed."

# +----End of this handy script------------------------------------------------+
