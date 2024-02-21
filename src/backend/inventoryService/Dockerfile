ARG JAVA_IMAGE
ARG JAVA_BUILD_IMAGE

# +---------------------------------------------------------------------------+
# | Build section                                                             |
# +---------------------------------------------------------------------------+

FROM ${JAVA_BUILD_IMAGE} AS builder

ARG JAVA_IMAGE
ARG MAVEN_JAVA_VERSION
ARG BACKEND_INVENTORY_SERVICE_IMAGE_VERSION
ARG MAVEN_SPRING_BOOT_VERSION
ARG MAVEN_SPRINGDOC_OPENAPI_VERSION
ARG MAVEN_POSTGRESQL_VERSION
ARG MAVEN_LOMBOK_VERSION

ENV JAVA_IMAGE=${JAVA_IMAGE}
ENV MAVEN_JAVA_VERSION=${MAVEN_JAVA_VERSION}
ENV SERVICE_VERSION=${BACKEND_INVENTORY_SERVICE_IMAGE_VERSION}
ENV MAVEN_SPRING_BOOT_VERSION=${MAVEN_SPRING_BOOT_VERSION}
ENV MAVEN_SPRINGDOC_OPENAPI_VERSION=${MAVEN_SPRINGDOC_OPENAPI_VERSION}
ENV MAVEN_POSTGRESQL_VERSION=${MAVEN_POSTGRESQL_VERSION}
ENV MAVEN_LOMBOK_VERSION=${MAVEN_LOMBOK_VERSION}

COPY ./src/ ./builder/src
COPY ./.mvn/ ./builder/.mvn
COPY ./mvnw ./builder/mvnw
COPY ./prod-pom.xml ./builder/pom.xml

WORKDIR /builder

RUN chmod +x ./mvnw

RUN /builder/mvnw clean install \
    -Djava.version=$MAVEN_JAVA_VERSION \
    -Dservice.version=$SERVICE_VERSION \
    -Dspring-boot.version=$MAVEN_SPRING_BOOT_VERSION \
    -Dspringdoc-openapi.version=$MAVEN_SPRINGDOC_OPENAPI_VERSION \
    -Dpostgresql.version=$MAVEN_POSTGRESQL_VERSION \
    -Dlombok.version=$MAVEN_LOMBOK_VERSION \
    -Dmaven.repo.local=/builder/.m2/repository

# +---------------------------------------------------------------------------+
# | Runtime section                                                           |
# +---------------------------------------------------------------------------+

FROM ${JAVA_IMAGE}

ARG BACKEND_INVENTORY_SERVICE_PORT_INT
ARG BACKEND_MESSAGE_BROKER_SERVICE_PORT_INT
ARG SECURITY_TRUST_STORE_PATH
ARG ENTRYPOINT

ENV BACKEND_MESSAGE_BROKER_SERVICE_PORT_INT=$BACKEND_MESSAGE_BROKER_SERVICE_PORT_INT
ENV SECURITY_TRUST_STORE_PATH=$SECURITY_TRUST_STORE_PATH

EXPOSE ${BACKEND_INVENTORY_SERVICE_PORT_INT}

# Create a group 'erp' if it doesn't already exist
RUN groupadd -g 10001 erp

# Create a user 'erp' if it doesn't already exist
RUN useradd -r -u 10001 -g erp erp

COPY --chown=erp --from=builder ./builder/target ./app
COPY --chown=erp ./inventory-service-keystore.p12 ./app/certs/inventory-service-keystore.p12
COPY --chown=erp ./procom-erp-truststore.jks ./config/procom-erp-truststore.jks
COPY --chown=erp ./wait-for-it.sh ./config/wait-for-it.sh
COPY --chown=erp ./entrypoint.sh ./config/entrypoint.sh

WORKDIR /

RUN chmod +x ./config/wait-for-it.sh
RUN chmod +x ./config/entrypoint.sh

USER erp

ENTRYPOINT ${ENTRYPOINT}
