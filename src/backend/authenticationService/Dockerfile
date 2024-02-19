ARG JAVA_BUILD_IMAGE
ARG JAVA_IMAGE

# +---------------------------------------------------------------------------+
# | Build section                                                             |
# +---------------------------------------------------------------------------+

FROM ${JAVA_BUILD_IMAGE} AS builder

ARG POM_JAVA_VERSION
ARG BACKEND_AUTHENTICATION_SERVICE_IMAGE_VERSION
ARG SPRING_BOOT_VERSION
ARG SPRINGDOC_OPENAPI_VERSION

ENV POM_JAVA_VERSION=${POM_JAVA_VERSION}
ENV SERVICE_VERSION=${BACKEND_AUTHENTICATION_SERVICE_IMAGE_VERSION}
ENV SPRING_BOOT_VERSION=${SPRING_BOOT_VERSION}
ENV SPRINGDOC_OPENAPI_VERSION=${SPRINGDOC_OPENAPI_VERSION}

COPY ./src/ ./builder/src
COPY ./.mvn/ ./builder/.mvn
COPY ./mvnw ./builder/mvnw
COPY ./prod-pom.xml ./builder/pom.xml

WORKDIR /builder

RUN chmod +x ./mvnw

RUN /builder/mvnw clean install \
    -Djava.version=$POM_JAVA_VERSION \
    -Dservice.version=$SERVICE_VERSION \
    -Dspring-boot.version=$SPRING_BOOT_VERSION \
    -Dspringdoc-openapi.version=$SPRINGDOC_OPENAPI_VERSION \
    -Dmaven.repo.local=/builder/.m2/repository

# +---------------------------------------------------------------------------+
# | Runtime section                                                           |
# +---------------------------------------------------------------------------+

FROM ${JAVA_IMAGE}

ARG BACKEND_AUTHENTICATION_SERVICE_PORT_INT
ARG BACKEND_MESSAGE_BROKER_SERVICE_PORT_INT
ARG SECURITY_TRUST_STORE_PATH
ARG ENTRYPOINT

ENV BACKEND_MESSAGE_BROKER_SERVICE_PORT_INT=$BACKEND_MESSAGE_BROKER_SERVICE_PORT_INT
ENV SECURITY_TRUST_STORE_PATH=$SECURITY_TRUST_STORE_PATH

EXPOSE ${BACKEND_AUTHENTICATION_SERVICE_PORT_INT}

# Create a group 'erp' if it doesn't already exist
RUN groupadd -g 10001 erp

# Create a user 'erp' if it doesn't already exist
RUN useradd -r -u 10001 -g erp erp

COPY --chown=erp --from=builder ./builder/target ./app
COPY --chown=erp ./authentication-service-keystore.p12 ./app/certs/authentication-service-keystore.p12
COPY --chown=erp ./procom-erp-truststore.jks ./config/procom-erp-truststore.jks
COPY --chown=erp ./wait-for-it.sh ./config/wait-for-it.sh
COPY --chown=erp ./entrypoint.sh ./config/entrypoint.sh

WORKDIR /

RUN chmod +x ./config/wait-for-it.sh
RUN chmod +x ./config/entrypoint.sh

USER erp

ENTRYPOINT ${ENTRYPOINT}
