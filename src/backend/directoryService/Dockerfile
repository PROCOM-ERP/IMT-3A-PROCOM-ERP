FROM eclipse-temurin:17-jdk
EXPOSE ${BACKEND_DIR_SERVICE_PORT_INT}
#COPY ./${BACKEND_DIR_SERVICE_HTTPS_KEY_STORE} /${BACKEND_DIR_SERVICE_HTTPS_KEY_STORE}
COPY ./entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]