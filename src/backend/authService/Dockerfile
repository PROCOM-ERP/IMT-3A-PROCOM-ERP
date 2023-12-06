FROM eclipse-temurin:17-jdk
EXPOSE ${BACKEND_AUTH_SERVICE_PORT_INT}
COPY ./${BACKEND_AUTH_SERVICE_HTTPS_KEY_STORE} /${BACKEND_AUTH_SERVICE_HTTPS_KEY_STORE}
COPY ./entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]