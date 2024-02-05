ARG JAVA_IMAGE
ARG BACKEND_GATEWAY_SERVICE_PORT_INT
ARG ENTRYPOINT
ARG SECURITY_TRUST_STORE_PATH


FROM ${JAVA_IMAGE}

ENV SECURITY_TRUST_STORE_PATH=$SECURITY_TRUST_STORE_PATH

EXPOSE ${BACKEND_GATEWAY_SERVICE_PORT_INT}

# Create a group 'erp' if it doesn't already exist
RUN groupadd -g 10001 erp

# Create a user 'erp' if it doesn't already exist
RUN useradd -r -u 10001 -g erp erp

WORKDIR /

COPY --chown=erp ./target ./app
COPY --chown=erp ./gateway-service-keystore.p12 ./app/certs/gateway-service-keystore.p12
COPY --chown=erp ./gateway-service-keystore.p12 ./app/certs/gateway-service-keystore.p12
COPY --chown=erp ./procom-erp-truststore.jks ./config/procom-erp-truststore.jks
COPY --chown=erp ./wait-for-it.sh ./config/wait-for-it.sh
COPY --chown=erp ./entrypoint.sh ./config/entrypoint.sh

RUN chmod +x ./config/wait-for-it.sh
RUN chmod +x ./config/entrypoint.sh

USER erp

ENTRYPOINT ./config/entrypoint.sh
