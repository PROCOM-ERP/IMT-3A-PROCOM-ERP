#!/bin/sh
JAR_FILE=$(ls /app/*.jar | tail -n 1)
/config/wait-for-it.sh $BACKEND_MESSAGE_BROKER_SERVICE_IP_INT:$BACKEND_MESSAGE_BROKER_SERVICE_PORT_INT -- java -jar "$JAR_FILE"
