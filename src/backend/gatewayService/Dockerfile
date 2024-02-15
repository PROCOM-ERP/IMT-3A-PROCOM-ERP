ARG JAVA_IMAGE
ARG JAVA_BUILD_IMAGE
ARG BACKEND_GATEWAY_SERVICE_PORT_INT
ARG ENTRYPOINT
ARG SECURITY_TRUST_STORE_PATH

FROM ${JAVA_BUILD_IMAGE} AS builder

ARG POM_JAVA_VERSION
ARG MAVEN_COMPILER_SOURCE
ARG MAVEN_COMPILER_TARGET
ARG SPRING_BOOT_VERSION
ARG SPRING_CLOUD_VERSION


COPY ./src/ ./builder/src
COPY ./.mvn/ ./builder/.mvn
COPY ./mvnw ./builder/mvnw
COPY ./prod-pom.xml ./builder/pom.xml

RUN chmod +x ./builder/mvnw

WORKDIR /builder

ENV POM_JAVA_VERSION=${POM_JAVA_VERSION}
ENV MAVEN_COMPILER_SOURCE=${MAVEN_COMPILER_SOURCE}
ENV MAVEN_COMPILER_TARGET=${MAVEN_COMPILER_TARGET}
ENV SPRING_BOOT_VERSION=${SPRING_BOOT_VERSION}
ENV SPRING_CLOUD_VERSION=${SPRING_CLOUD_VERSION}

RUN /builder/mvnw clean install \
    -Djava.version=$POM_JAVA_VERSION \
    -Dmaven.compiler.source=$MAVEN_COMPILER_SOURCE \
    -Dmaven.compiler.target=$MAVEN_COMPILER_TARGET \
    -Dspring-boot.version=$SPRING_BOOT_VERSION \
    -Dspring-cloud.version=$SPRING_CLOUD_VERSION \
    -Dmaven.repo.local=/builder/.m2/repository

FROM ${JAVA_IMAGE}

ENV SECURITY_TRUST_STORE_PATH=$SECURITY_TRUST_STORE_PATH

EXPOSE ${BACKEND_GATEWAY_SERVICE_PORT_INT}

# Create a group 'erp' if it doesn't already exist
RUN groupadd -g 10001 erp

# Create a user 'erp' if it doesn't already exist
RUN useradd -r -u 10001 -g erp erp

WORKDIR /

COPY --chown=erp --from=builder ./builder/target ./app
COPY --chown=erp ./gateway-service-keystore.p12 ./app/certs/gateway-service-keystore.p12
COPY --chown=erp ./gateway-service-keystore.p12 ./app/certs/gateway-service-keystore.p12
COPY --chown=erp ./procom-erp-truststore.jks ./config/procom-erp-truststore.jks
COPY --chown=erp ./wait-for-it.sh ./config/wait-for-it.sh
COPY --chown=erp ./entrypoint.sh ./config/entrypoint.sh

RUN chmod +x ./config/wait-for-it.sh
RUN chmod +x ./config/entrypoint.sh

USER erp

ENTRYPOINT ./config/entrypoint.sh
