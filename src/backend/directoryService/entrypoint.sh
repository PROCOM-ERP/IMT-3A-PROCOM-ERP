#!/bin/sh
JAR_FILE=$(ls /app/*.jar | tail -n 1)
java -jar "$JAR_FILE"
