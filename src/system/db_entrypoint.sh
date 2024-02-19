#!/bin/bash
set -e

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

set_postgres_variable() {
  local pattern="DB_.*_SERVICE_$1"
  local variable_name="POSTGRES_$2"

  # Iterate through environment variables
  for var in $(compgen -e); do
    # Check if the variable name matches the pattern
    if [[ $var =~ $pattern ]]; then
      # Get the value of the matched variable and set POSTGRES_XXX
      export "$variable_name"="${!var}"
      break  # Stop after the first match (you can remove this if needed)
    fi
  done
}

set_postgres_variable USER USER
set_postgres_variable PASSWORD PASSWORD

# Start the PostgreSQL service
/usr/local/bin/docker-entrypoint.sh "$@"
