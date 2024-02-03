ARG JAVA_IMAGE
ARG BACKEND_AUTHENTICATION_SERVICE_PORT_INT
ARG ENTRYPOINT
ARG BACKEND_MESSAGE_BROKER_SERVICE_IP_INT
ARG BACKEND_MESSAGE_BROKER_SERVICE_PORT_INT
ARG SECURITY_TRUST_STORE_PATH
ARG SECURITY_TRUST_STORE_PASSWORD

ENV BACKEND_MESSAGE_BROKER_SERVICE_IP_INT=$BACKEND_MESSAGE_BROKER_SERVICE_IP_INT
ENV BACKEND_MESSAGE_BROKER_SERVICE_PORT_INT=$BACKEND_MESSAGE_BROKER_SERVICE_PORT_INT
ENV SECURITY_TRUST_STORE_PATH=$SECURITY_TRUST_STORE_PATH
ENV SECURITY_TRUST_STORE_PASSWORD=$SECURITY_TRUST_STORE_PASSWORD

FROM ${JAVA_IMAGE}

EXPOSE ${BACKEND_AUTHENTICATION_SERVICE_PORT_INT}

RUN addgroup -S -g 10001 erp && adduser -S -G erp -u 10001 erp

COPY --chown=erp ./target ./app
COPY --chown=erp ./authentication-service-keystore.p12 ./app/certs/authentication-service-keystore.p12
COPY --chown=erp ../../system/procom-erp-truststore.jks ./config/procom-erp-truststore.jks
COPY --chown=erp ../../system/wait-for-it.sh ./config/wait-for-it.sh
COPY --chown=erp ../../system/entrypoint.sh ./config/entrypoint.sh

RUN chmod +x /config/wait-for-it.sh
RUN chmod +x /config/entrypoint.sh

USER erp

ENTRYPOINT ${ENTRYPOINT}
