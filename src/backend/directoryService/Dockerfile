ARG JAVA_IMAGE
ARG BACKEND_DIRECTORY_SERVICE_PORT_INT
ARG ENTRYPOINT
ARG BACKEND_MESSAGE_BROKER_SERVICE_IP_INT
ARG BACKEND_MESSAGE_BROKER_SERVICE_PORT_INT
ARG SECURITY_TRUST_STORE_PATH


FROM ${JAVA_IMAGE}

ENV BACKEND_MESSAGE_BROKER_SERVICE_IP_INT=$BACKEND_MESSAGE_BROKER_SERVICE_IP_INT
ENV BACKEND_MESSAGE_BROKER_SERVICE_PORT_INT=$BACKEND_MESSAGE_BROKER_SERVICE_PORT_INT
ENV SECURITY_TRUST_STORE_PATH=$SECURITY_TRUST_STORE_PATH

EXPOSE ${BACKEND_DIRECTORY_SERVICE_PORT_INT}

# Create a group 'erp' if it doesn't already exist
RUN groupadd -g 10001 erp

# Create a user 'erp' if it doesn't already exist
RUN useradd -r -u 10001 -g erp erp

WORKDIR /

COPY --chown=erp ./target ./app
COPY --chown=erp ./directory-service-keystore.p12 ./app/certs/directory-service-keystore.p12
COPY --chown=erp ./procom-erp-truststore.jks ./config/procom-erp-truststore.jks
COPY --chown=erp ./wait-for-it.sh ./config/wait-for-it.sh
COPY --chown=erp ./entrypoint.sh ./config/entrypoint.sh

RUN chmod +x ./config/wait-for-it.sh
RUN chmod +x ./config/entrypoint.sh

USER erp

ENTRYPOINT ./config/entrypoint.sh
