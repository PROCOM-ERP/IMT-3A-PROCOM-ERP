-- Title :             Database creation for IMT-3A-PROCOM-ERP project
-- Version :           1.0
-- Creation date :     2024-02-21
-- Update date :       2024-02-21
-- Author :            BOPS
-- Description :       Database initialisation script for IMT-3A-PROCOM-ERP project
--                     Note : Script for PostgreSQL

-- +----------------------------------------------------------------------------------------------+
-- | Create table                                                                                 |
-- +----------------------------------------------------------------------------------------------+

CREATE TABLE roles
(
    name VARCHAR(32) UNIQUE NOT NULL,
    is_enable BOOLEAN NOT NULL DEFAULT true,

    CONSTRAINT pk_roles PRIMARY KEY (name)
);

-- +----------------------------------------------------------------------------------------------+

CREATE TABLE role_permissions
(
    role VARCHAR(32) NOT NULL,
    permission VARCHAR(64) NOT NULL,

    CONSTRAINT pk_role_permissions
        PRIMARY KEY (role, permission),
    CONSTRAINT fk_role_permissions_table_roles
        FOREIGN KEY (role) REFERENCES roles(name)
            ON UPDATE CASCADE ON DELETE CASCADE
);

-- +----------------------------------------------------------------------------------------------+

CREATE TABLE login_profiles
(
    id CHAR(6) UNIQUE NOT NULL,
    is_enable BOOLEAN NOT NULL DEFAULT true,
    jwt_gen_min_at TIMESTAMP NOT NULL DEFAULT current_timestamp,

    CONSTRAINT pk_login_profiles PRIMARY KEY (id),
    CONSTRAINT check_login_profiles_id
        CHECK (login_profiles.id ~* '[A-Z][0-9]{5}')
);

-- +----------------------------------------------------------------------------------------------+

CREATE TABLE addresses
(
    id VARCHAR(64) UNIQUE NOT NULL,
    number INT NOT NULL,
    street VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100),
    country VARCHAR(100) NOT NULL,
    zipcode VARCHAR(20) NOT NULL,
    info TEXT,

    CONSTRAINT pk_addresses PRIMARY KEY (id)
);

-- +----------------------------------------------------------------------------------------------+

CREATE TABLE employees
(
    id SERIAL UNIQUE NOT NULL,
    login_profile CHAR(6) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    email VARCHAR(320) UNIQUE NOT NULL,
    phone_number VARCHAR(24),

    CONSTRAINT pk_employees
        PRIMARY KEY (id),
    CONSTRAINT fk_employees_table_login_profiles
        FOREIGN KEY (login_profile) REFERENCES login_profiles(id)
            ON UPDATE CASCADE
);

-- +----------------------------------------------------------------------------------------------+

CREATE TABLE providers
(
    id SERIAL UNIQUE NOT NULL,
    name VARCHAR(64) UNIQUE NOT NULL,

    CONSTRAINT pk_providers PRIMARY KEY (id),
    CONSTRAINT uq_providers_name UNIQUE (name)
);

-- +----------------------------------------------------------------------------------------------+

CREATE TABLE progress_status
(
    id SERIAL UNIQUE NOT NULL,
    name VARCHAR(64) UNIQUE NOT NULL,

    CONSTRAINT pk_progress_status PRIMARY KEY (id),
    CONSTRAINT uq_progress_status UNIQUE (name)
);

-- +----------------------------------------------------------------------------------------------+

CREATE TABLE orders
(
    id SERIAL UNIQUE NOT NULL,
    total_amount NUMERIC(10, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT current_timestamp,
    quote VARCHAR(64) NOT NULL DEFAULT '',
    address VARCHAR(64) NOT NULL DEFAULT '',
    provider INT NOT NULL,
    orderer INT NOT NULL,
    approver INT NOT NULL,

    CONSTRAINT pk_orders PRIMARY KEY (id),
    CONSTRAINT fk_orders_table_address
        FOREIGN KEY (address) REFERENCES addresses(id)
            ON UPDATE CASCADE ON DELETE SET DEFAULT,
    CONSTRAINT fk_orders_table_providers
        FOREIGN KEY (provider) REFERENCES providers(id)
            ON UPDATE CASCADE,
    CONSTRAINT fk_orders_table_employees_orderer
        FOREIGN KEY (orderer) REFERENCES employees(id),
    CONSTRAINT fk_orders_table_employes_approver
        FOREIGN KEY (approver) REFERENCES employees(id)
);

-- +----------------------------------------------------------------------------------------------+

CREATE TABLE order_products
(
    id SERIAL UNIQUE NOT NULL,
    reference VARCHAR(128) NOT NULL,
    unit_price NUMERIC(10, 2) NOT NULL,
    quantity INT NOT NULL,
    "order" INT NOT NULL,

    CONSTRAINT pk_order_products PRIMARY KEY (id),
    CONSTRAINT fk_order_products_table_orders
        FOREIGN KEY ("order") REFERENCES orders(id)
            ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT uq_order_products_reference_order UNIQUE (reference, "order")
);

-- +----------------------------------------------------------------------------------------------+

CREATE TABLE join_orders_progress_status
(
    id SERIAL UNIQUE NOT NULL,
    "order" INT NOT NULL,
    progress_status INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT current_timestamp,

    CONSTRAINT pk_join_orders_progress_status PRIMARY KEY (id),
    CONSTRAINT fk_join_orders_progress_status_table_orders
        FOREIGN KEY ("order") REFERENCES orders(id)
            ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_join_orders_progress_status_table_progress_status
        FOREIGN KEY (progress_status) REFERENCES progress_status(id)
            ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT uq_join_orders_progress_status_order_progress_status UNIQUE ("order", progress_status)
);

-- +----------------------------------------------------------------------------------------------+
-- | Insert into                                                                                  |
-- +----------------------------------------------------------------------------------------------+

INSERT INTO roles (name)
VALUES ('admin'),
       ('user');

-- +----------------------------------------------------------------------------------------------+

INSERT INTO role_permissions (role, permission)
VALUES ('admin', 'CanBypassAccessDeny'),
       ('admin', 'CanModifyRole'),
       ('admin', 'CanReadAddress'),
       ('admin', 'CanReadRole'),

       ('user', 'CanReadAddress');

-- +----------------------------------------------------------------------------------------------+

INSERT INTO login_profiles (id)
VALUES ('A00001'),
       ('A00002');
