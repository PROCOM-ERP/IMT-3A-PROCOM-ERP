#!/bin/bash

# Directory containing Docker secrets
SECRETS_DIR=/run/secrets

# Check if the secrets directory exists and is not empty
if [ -d "$SECRETS_DIR" ] && [ "$(ls -A $SECRETS_DIR)" ]; then
  echo "Exporting secrets from $SECRETS_DIR as environment variables..."

  # Loop through all files in the secrets directory
  for secret_file in $SECRETS_DIR/*; do
    # Extract filename for use as the environment variable name
    secret_name=$(basename "$secret_file")
    # Read file content for use as the environment variable value
    secret_value=$(cat "$secret_file")
    # Export the environment variable
    export "$secret_name"="$secret_value"
    echo "Exported secret $secret_name"
  done
fi

JAR_FILE=$(ls /app/*.jar | tail -n 1)
/config/wait-for-it.sh $BACKEND_MESSAGE_BROKER_SERVICE_HOSTNAME:$BACKEND_MESSAGE_BROKER_SERVICE_PORT_INT -- java -Djavax.net.ssl.trustStore="$SECURITY_TRUST_STORE_PATH" -Djavax.net.ssl.trustStorePassword="$SECURITY_TRUST_STORE_PASSWORD" -jar "$JAR_FILE"
