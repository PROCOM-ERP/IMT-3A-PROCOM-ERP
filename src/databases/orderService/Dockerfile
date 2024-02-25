ARG DB_IMAGE
ARG DB_ORDER_SERVICE_PORT_INT

# Use the official PostgreSQL image as the base image
FROM ${DB_IMAGE}

# Expose the PostgreSQL port
EXPOSE ${DB_ORDER_SERVICE_PORT_INT}

# Copy the custom entrypoint script to the image
COPY ./db_entrypoint.sh ./config/db_entrypoint.sh
COPY ./init.sql ./docker-entrypoint-initdb.d/init.sql
 
RUN chmod +x ./config/db_entrypoint.sh

# Set the custom entrypoint script as the entrypoint
ENTRYPOINT ["./config/db_entrypoint.sh"]