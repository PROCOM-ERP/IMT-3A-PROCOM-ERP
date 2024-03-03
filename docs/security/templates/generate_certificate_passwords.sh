#!/bin/bash
# generate_certificate_passwords.sh
# Description: Exports certificates and keys using expect to automate password entry.
# Usage: ./generate_certificate_passwords.sh <service_name>

SERVICE_NAME=$1

if [[ "$SERVICE_NAME" == "CA" ]]; then
    echo "ENTER_YOUR_CA_PASSWORD" 
    exit 0
else
    expect <<EOF > /dev/null 2>&1

spawn openssl pkcs12 -export -in "${SERVICE_NAME}-service.crt" -inkey "${SERVICE_NAME}-service.key" -out "${SERVICE_NAME}-service-keystore.p12" -name "${SERVICE_NAME}"
expect "Enter Export Password:"
send "ENTER_YOUR_PASSWORD\r"  # Password removed
expect "Verifying - Enter Export Password:"
send "ENTER_YOUR_PASSWORD\r"  # Password removed
expect eof
EOF

# For message broker, convert to PEM format
    if [ "${SERVICE_NAME}" = "message-broker" ]; then
        expect <<EOF > /dev/null 2>&1
spawn openssl pkcs12 -in "${SERVICE_NAME}-service-keystore.p12" -clcerts -nokeys -nodes -out "${SERVICE_NAME}-service-certificate.pem"
expect "Enter Import Password:"
send "ENTER_YOUR_MESSAGE_BROKER_PASSWORD\r" 
spawn openssl pkcs12 -in "${SERVICE_NAME}-service-keystore.p12" -nocerts -nodes -out "${SERVICE_NAME}-service-key.pem"
expect "Enter Import Password:"
send "ENTER_YOUR_MESSAGE_BROKERPASSWORD\r" 
expect eof
EOF
    fi
fi

