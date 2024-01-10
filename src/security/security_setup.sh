#!/bin/bash

# Check if OpenSSL is installed
if ! command -v openssl &> /dev/null; then
    echo "OpenSSL is not installed. Please install OpenSSL and try again."
    exit 1
fi

# Function to generate certificates for a service
generate_certificate() {
    local service_name="$1"

    # Step 1: Generate Certificates for the Service
    openssl genrsa -out "${service_name}-service.key" 4096
    openssl req -new -key "${service_name}-service.key" -out "${service_name}-service.csr" -subj "/C=FR/ST=France/L=Paris/O=Procom-ERP/OU=IT/CN=springboot-procom-erp-${service_name}-service"

    # Step 2: Sign the CSR with Your CA
    openssl x509 -req -days 365 -in "${service_name}-service.csr" -CA procom-erp-ca.crt -CAkey procom-erp-ca.key -out "${service_name}-service.crt"

    expect <<EOF
spawn openssl pkcs12 -export -in "${service}-service.crt" -inkey "${service}-service.key" -out "${service}-service-keystore.p12" -name "${service}"
expect "Enter Export Password:"
send "procom-erp-directory-${service}-secure-keystore\r"
expect "Verifying - Enter Export Password:"
send "procom-erp-directory-${service}-secure-keystore\r"
expect eof
EOF
    
    # Move the generated files to the service directory
    service_dir="../backend/${service_name}Service"
    mkdir -p "${service_dir}"
    mkdir -p "./${service}"
    
    # If the service is 'message-broker', also create PEM files
    if [ "${service}" = "message-broker" ]; then
        expect <<EOF
spawn openssl pkcs12 -in "${service}-service-keystore.p12" -clcerts -nokeys -nodes -out "${service}-service-certificate.pem"
expect "Enter Import Password:"
send "procom-erp-directory-${service}-secure-keystore\r"
spawn openssl pkcs12 -in "${service}-service-keystore.p12" -nocerts -nodes -out "${service}-service-key.pem"
expect "Enter Import Password:"
send "procom-erp-directory-${service}-secure-keystore\r"
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
    # Generate the Root Key
    openssl genrsa -out procom-erp-ca.key 4096

    # Create and Self-Sign the Root Certificate
    openssl req -new -x509 -days 3650 -key procom-erp-ca.key -out procom-erp-ca.crt -subj "/C=FR/ST=France/L=Paris/O=Procom-ERP/OU=IT/CN=Procom-ERP"
    echo "New CA files generated."
    
    # Create a trust store for the services to trust
    keytool -importcert -noprompt -alias ca_cert -file procom-erp-ca.crt -keystore procom-erp-truststore.jks --store-pass "super-secure-password-for-trust-store" >/dev/null 2>&1

fi

# Prompt for the number of services
read -p "Enter the number of services: " num_services

# Initialize arrays for services and hostnames
services=()

# Read service names and hostnames from user input
for ((i=1; i<=num_services; i++)); do
    read -p "Enter the name for Service ${i}: " service_name

    services+=("${service_name}")
done

# Generate certificates for each service
for ((i=0; i<num_services; i++)); do
    service="${services[i]}"
    
    echo "Generating certificates for ${service}"
    
    generate_certificate "${service}"
    
    echo "Certificates generated for ${service}"
done

# Step 6: Convert CA certificate to .pem
openssl x509 -inform der -in procom-erp-ca.crt -out procom-erp-ca.pem

# Move the CA's keys and certificates to the CA directory
ca_dir="./CA"
mkdir -p "${ca_dir}"
mv procom-erp-truststore.jks "../system/"
mv procom-erp-ca.key procom-erp-ca.crt procom-erp-ca.pem "${ca_dir}/"

echo "CA's keys and certificates moved to ${ca_dir}"

# Step 7: Update Your Docker and Application Configuration
# Update your Docker and application configurations here

echo "Certificates generation completed."
