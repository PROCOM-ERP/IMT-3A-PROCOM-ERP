ARG DB_IMAGE
ARG DB_ENTRYPOINT
ARG DB_AUTHENTICATION_SERVICE_PORT_INT

# Use the official PostgreSQL image as the base image
FROM ${DB_IMAGE}

# Copy the custom entrypoint script to the image
COPY ./db_entrypoint.sh ./config/db_entrypoint.sh
COPY ./init.sql ./docker-entrypoint-initdb.d/init.sql
COPY ./postgresql.conf ./etc/postgresql/postgresql.conf

RUN chmod +x ./config/db_entrypoint.sh

# Expose the PostgreSQL port
EXPOSE ${DB_AUTHENTICATION_SERVICE_PORT_INT}

# Set the custom entrypoint script as the entrypoint
ENTRYPOINT ["./config/db_entrypoint.sh"]
