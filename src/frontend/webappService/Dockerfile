ARG FRONT_BUILD_IMAGE
ARG FRONT_IMAGE

# Use an existing node alpine image as a base image.
FROM ${FRONT_BUILD_IMAGE} AS builder

# Create a new user.
RUN addgroup -S -g 10001 erp && adduser -S -G erp -u 10001 erp

# Set the working directory.
WORKDIR /opt/erp/frontend/
RUN chown -R erp:erp /opt/erp/frontend

# Set the user.
USER erp

# Copy package files
COPY --chown=erp:erp package*.json ./

# Install dependencies.
RUN npm install --omit=dev && npm cache clean --force

# Copy the source code.
COPY --chown=erp:erp ./src ./src
COPY --chown=erp:erp ./public ./public
COPY --chown=erp:erp ./webapp-service.crt ./certs/webapp-service.crt
COPY --chown=erp:erp ./webapp-service.key ./certs/webapp-service.key
COPY --chown=erp:erp ./procom-erp-ca.pem ./certs/procom-erp-ca.pem

# Build the application.
RUN npm run build

# Use an existing plane alpine image as the image to run.
FROM ${FRONT_IMAGE}

# Create a new user.
RUN addgroup -S -g 10001 erp && adduser -S -G erp -u 10001 erp

# Install Node.js runtime on Alpine
RUN apk add --no-cache nodejs
RUN apk add --no-cache npm
RUN npm install -g serve && npm cache clean --force
RUN apk add --no-cache curl
RUN rm -rf /root/.npm

# Set the working directory.
WORKDIR /app
RUN chown -R erp:erp /app

# Set the user.
USER erp


COPY --from=builder /opt/erp/frontend/build /app
COPY --from=builder /opt/erp/frontend/package*.json /app
COPY --from=builder /opt/erp/frontend/certs/webapp-service.crt /certs/webapp-service.crt
COPY --from=builder /opt/erp/frontend/certs/webapp-service.key /certs/webapp-service.key
COPY --from=builder /opt/erp/frontend/certs/procom-erp-ca.pem /app/certs/procom-erp-ca.pem

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:${FRONTEND_WEBAPP_SERVICE_PORT_INT}/ || exit 1

# Run the application.
ENTRYPOINT serve -s /app -l ${FRONTEND_WEBAPP_SERVICE_PORT_INT} --ssl-cert /certs/webapp-service.crt --ssl-key /certs/webapp-service.key
