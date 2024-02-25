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
    email VARCHAR(320) NOT NULL,
    phone_number VARCHAR(24) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT current_timestamp,

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
    updated_at TIMESTAMP NOT NULL DEFAULT current_timestamp,
    quote VARCHAR(64) NOT NULL DEFAULT '',
    progress_status INT NOT NULL DEFAULT 1,
    address VARCHAR(64) NOT NULL,
    provider INT NOT NULL,
    orderer INT NOT NULL,
    approver INT,

    CONSTRAINT pk_orders PRIMARY KEY (id),
    CONSTRAINT fk_orders_table_progress_status
        FOREIGN KEY (progress_status) REFERENCES progress_status(id)
            ON UPDATE CASCADE,
    CONSTRAINT fk_orders_table_address
        FOREIGN KEY (address) REFERENCES addresses(id)
            ON UPDATE CASCADE,
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

-- +----------------------------------------------------------------------------------------------+

INSERT INTO addresses (id, number, street, city, country, zipcode)
VALUES ('7d82842eb167c3ed224a329fba7fbb2820a8c99f3771f9e216b968c1cccd0d6e',
        1, 'rue de la Paix', 'Paris', 'France', '75000'),
       ('e8ffdf9a6ffc553cd234a04e6a5f63547838f367af45cb029f0c2a3412278412',
        2, 'rue de la Paix', 'Paris', 'France', '75000');

-- +----------------------------------------------------------------------------------------------+

INSERT INTO employees (login_profile, last_name, first_name, email, phone_number)
VALUES ('A00001', 'Bonnot', 'Jean', 'jean.bonnot@gmail.com', '+123456789'),
       ('A00002', 'De La Compta', 'Séverine', 'severine.de-la-compta@gmail.com', '+987654321');

-- +----------------------------------------------------------------------------------------------+

INSERT INTO providers (name)
VALUES ('Wool Factory'),
       ('Cotton retailer');

-- +----------------------------------------------------------------------------------------------+

INSERT INTO progress_status (name)
VALUES ('Created'),
       ('Waiting for approval'),
       ('Approved'),
       ('Waiting for delivery'),
       ('Received');

-- +----------------------------------------------------------------------------------------------+

INSERT INTO orders (total_amount, quote, provider, orderer, approver, address)
VALUES (900.00, 'CRE0000000001', 2, 2, 1,
        '7d82842eb167c3ed224a329fba7fbb2820a8c99f3771f9e216b968c1cccd0d6e'),
       (1650.00, 'WFAEAD547FB00892387', 1, 1, 1,
        'e8ffdf9a6ffc553cd234a04e6a5f63547838f367af45cb029f0c2a3412278412');

-- +----------------------------------------------------------------------------------------------+

INSERT INTO order_products (reference, unit_price, quantity, "order")
VALUES ('Cotton1000', 5.00, 100, 1),
       ('Polyester500', 8.00, 50, 1),

       ('Silk300', 15.00, 50, 2),
       ('Wool200', 12.00, 75, 2);