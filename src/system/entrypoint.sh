#!/bin/sh
JAR_FILE=$(ls /app/*.jar | tail -n 1)
/config/wait-for-it.sh $BACKEND_MESSAGE_BROKER_SERVICE_IP_INT:$BACKEND_MESSAGE_BROKER_SERVICE_PORT_INT -- java -Djavax.net.ssl.trustStore="$SECURITY_TRUST_STORE_PATH" -Djavax.net.ssl.trustStorePassword="$SECURITY_TRUST_STORE_PASSWORD" -jar "$JAR_FILE"
