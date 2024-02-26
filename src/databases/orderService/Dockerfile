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

# Create a group 'erp' if it doesn't already exist
RUN groupadd -g 10001 erp

# Create a user 'erp' if it doesn't already exist
RUN useradd -r -u 10001 -g erp erp

RUN chown erp:erp /var/run/postgresql
RUN chown erp:erp /var/lib/postgresql/data

USER erp

# Set the custom entrypoint script as the entrypoint
ENTRYPOINT ["./config/db_entrypoint.sh"]
