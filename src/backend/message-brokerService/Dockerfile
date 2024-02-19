ARG RABBIT_MQ_IMAGE
ARG BACKEND_MESSAGE_BROKER_SERVICE_PORT_INT

FROM ${RABBIT_MQ_IMAGE}

ENV BACKEND_MESSAGE_BROKER_SERVICE_PORT_INT=${BACKEND_MESSAGE_BROKER_SERVICE_PORT_INT}

RUN rm -rf /etc/rabbitmq/conf.d

# Create a group 'erp' if it doesn't already exist
RUN groupadd -g 10001 erp

# Create a user 'erp' if it doesn't already exist
RUN useradd -r -u 10001 -g erp erp

WORKDIR /

COPY --chown=erp ./config/rabbitmq.conf /etc/rabbitmq/rabbitmq.conf
COPY --chown=erp ./message-broker-service-keystore.p12 /etc/rabbitmq/certs/message-broker-service-keystore.p12
COPY --chown=erp ./message-broker-service-key.pem /etc/rabbitmq/certs/message-broker-service-key.pem
COPY --chown=erp ./message-broker-service-certificate.pem /etc/rabbitmq/certs/message-broker-service-certificate.pem
COPY --chown=erp ./procom-erp-ca.pem /etc/rabbitmq/CA/procom-erp-ca.pem

USER erp

CMD rabbitmq-server
