#!/bin/bash
# Script Name: security_auto_setup.sh
# Description: Generate CA and services security essentials, and moves them to the correct spot.
# Author: maestro-bene (GitHub)
# Date Created: 2024-01-15
# Last Modified: 2024-01-15
# Version: 1.1
# Usage: Just run the script within the src/security directory, it will analyze the frontend and backend directories.
# Notes: Another scripts "clean_security.sh" works with this one to undo the changes made by this script, by giving the names.

# Check if OpenSSL is installed
if ! command -v openssl &> /dev/null; then
    echo "OpenSSL is not installed. Please install OpenSSL and try again."
    exit 1
fi

# Define the expected last three directory entries
expected_last_entries="src/security"

# Get the last three entries of the current working directory path
current_directory="$(pwd)"
last_three_entries=$(basename "$current_directory" | tr '/' ' ' | awk '{print $(NF-1), $NF}')

# Check if the last three entries match the expected ones
if [ "$last_three_entries" != "$expected_last_entries" ]; then
    echo "Please run this script from the '${expected_last_entries}' directory."
    exit 1
fi

# Function to generate certificates for a service
generate_certificate() {
    local service_name="$1"
    local service_type="$2"

    # Step 1: Generate Certificates for the Service
    openssl genrsa -out "${service_name}-service.key" 4096
    openssl req -new -key "${service_name}-service.key" -out "${service_name}-service.csr" -subj "/C=FR/ST=France/L=Paris/O=Procom-ERP/OU=IT/CN=springboot-procom-erp-${service_name}-service"

    # Step 2: Sign the CSR with Your CA
    openssl x509 -req -days 365 -in "${service_name}-service.csr" -CA "${ca_crt}" -CAkey "${ca_key}" -out "${service_name}-service.crt"

    expect <<EOF
spawn openssl pkcs12 -export -in "${service}-service.crt" -inkey "${service}-service.key" -out "${service}-service-keystore.p12" -name "${service}"
expect "Enter Export Password:"
send "procom-erp-${service}-service-secure-keystore\r"
expect "Verifying - Enter Export Password:"
send "procom-erp-${service}-service-secure-keystore\r"
expect eof
EOF
    
    # Move the generated files to the service directory
    service_dir="../${service_type}/${service_name}Service"
    mkdir -p "${service_dir}"
    mkdir -p "./${service}"
    
    # If the service is 'message-broker', also create PEM files
    if [ "${service}" = "message-broker" ]; then
        expect <<EOF
spawn openssl pkcs12 -in "${service}-service-keystore.p12" -clcerts -nokeys -nodes -out "${service}-service-certificate.pem"
expect "Enter Import Password:"
send "procom-erp-${service}-service-secure-keystore\r"
spawn openssl pkcs12 -in "${service}-service-keystore.p12" -nocerts -nodes -out "${service}-service-key.pem"
expect "Enter Import Password:"
send "procom-erp-${service}-service-secure-keystore\r"
expect eof
EOF
        mv "${service}-service-key.pem" "${service_dir}/"
        mv "${service}-service-certificate.pem" "${service_dir}/"
    fi

    mv "${service_name}-service.key" "${service_name}-service.csr" "${service_name}-service.crt" "./${service}/"
    mv "${service_name}-service-keystore.p12" "${service_dir}/"
    echo "Certificates for ${service_name} moved to ./${service} and the keystore to ${service_dir}"
}

# Step 4: Check if CA files exist or generate them if needed
if [ ! -f "./CA/procom-erp-ca.crt" ] || [ ! -f "./CA/procom-erp-ca.key" ]; then
    ca_crt="procom-erp-ca.crt"
    ca_key="procom-erp-ca.key"
    # Generate the Root Key
    openssl genrsa -out procom-erp-ca.key 4096

    # Create and Self-Sign the Root Certificate
    openssl req -new -x509 -days 3650 -key procom-erp-ca.key -out procom-erp-ca.crt -subj "/C=FR/ST=France/L=Paris/O=Procom-ERP/OU=IT/CN=Procom-ERP"
    
    # Create a trust store for the services to trust
    keytool -importcert -noprompt -alias ca_cert -file procom-erp-ca.crt -keystore procom-erp-truststore.jks --store-pass "super-secure-password-for-trust-store" >/dev/null 2>&1
    echo "New CA files generated."
else
    ca_crt="./CA/procom-erp-ca.crt"
    ca_key="./CA/procom-erp-ca.key"
fi

# Initialize arrays for services and hostnames
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
    
    echo "Certificates generated for ${service} (${service_type})"
done

# Step 6: Convert CA certificate to .pem
# openssl x509 -inform der -in "${ca_crt}" -out procom-erp-ca.pem

# Move the CA's keys and certificates to the CA directory
ca_dir="./CA"
mkdir -p "${ca_dir}"
mv procom-erp-truststore.jks "../system/"
cp "${ca_crt}" "../system/procom-erp-ca.pem"
mv "${ca_key}" "${ca_crt}" "${ca_dir}/"

echo "CA's keys and certificates moved to ${ca_dir}"

# Step 7: Update Your Docker and Application Configuration
# Update your Docker and application configurations here

echo "Certificates generation completed."
