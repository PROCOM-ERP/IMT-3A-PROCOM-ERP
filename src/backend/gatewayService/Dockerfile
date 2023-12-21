FROM eclipse-temurin:17-jdk
EXPOSE ${BACKEND_GATEWAY_SERVICE_PORT_INT}
COPY ./entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]
