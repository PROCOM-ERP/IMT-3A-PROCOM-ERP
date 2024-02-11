-- Title :             Database creation for IMT-3A-PROCOM-ERP project
-- Version :           1.0
-- Creation date :     2023-12-22
-- Update date :       2023-12-22
-- Author :            Thibaut RUZICKA
-- Description :       Database initialisation script for IMT-3A-PROCOM-ERP project
--                     Note : Script for PostgreSQL

-- +----------------------------------------------------------------------------------------------+
-- | Create table                                                                                 |
-- +----------------------------------------------------------------------------------------------+

CREATE TABLE roles
(
    name VARCHAR(32) UNIQUE NOT NULL,
    isEnable BOOLEAN NOT NULL DEFAULT true,

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

CREATE TABLE addresses
(
    id SERIAL UNIQUE NOT NULL,
    number INT,
    street VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100),
    country VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    info TEXT,

    CONSTRAINT pk_addresses PRIMARY KEY (id)
);


-- +----------------------------------------------------------------------------------------------+

CREATE TABLE organisations
(
    id SERIAL UNIQUE NOT NULL,
    name VARCHAR(255) UNIQUE NOT NULL,
    address INT UNIQUE,

    CONSTRAINT pk_organisations
        PRIMARY KEY (id),
    CONSTRAINT fk_organisations_table_addresses
        FOREIGN KEY (address) REFERENCES addresses(id)
            ON UPDATE CASCADE ON DELETE SET NULL
);

-- +----------------------------------------------------------------------------------------------+

CREATE TABLE services
(
    id SERIAL UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    address INT,
    organisation INT,

    CONSTRAINT pk_services
        PRIMARY KEY (id),
    CONSTRAINT fk_services_table_addresses
        FOREIGN KEY (address) REFERENCES addresses(id)
            ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT fk_services_table_organisations
        FOREIGN KEY (organisation) REFERENCES organisations(id)
            ON UPDATE CASCADE  ON DELETE SET NULL
);

-- +----------------------------------------------------------------------------------------------+

CREATE TABLE employees
(
    id CHAR(6) UNIQUE NOT NULL,
    creation DATE NOT NULL DEFAULT current_timestamp,
    enable BOOLEAN NOT NULL DEFAULT true,
    jwt_min_creation TIMESTAMP NOT NULL DEFAULT current_timestamp,
    last_name VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    email VARCHAR(320) UNIQUE NOT NULL,
    phone_number VARCHAR(24),
    service INT,

    CONSTRAINT pk_employees
        PRIMARY KEY (id),
    CONSTRAINT fk_employees_table_services
        FOREIGN KEY (service) REFERENCES services(id)
            ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT check_employees_id
        CHECK (employees.id ~* '[A-Z][0-9]{5}')-- ,
    -- CONSTRAINT check_employees_email
    -- CHECK (employees.email ~* '^[a-zA-Z0-9](?:[a-zA-Z0-9-._-]{0,62}[a-zA-Z0-9])?@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z]{2,})+$'),
    -- CONSTRAINT check_employees_phone_number
    -- CHECK (employees.phone_number ~* '^(\+((?:9[679]|8[0357-9]|6[7-9]|5[09]|42|3[578]|2[1-689])\d|9[0-58]|8[1246]|6[0-6]|5[1-8]|4[013-9]|3[0-469]|2[07]|[17])|0)\W?\d\W?\d\W?\d\W?\d\W?\d\W?\d\W?\d\W?\d\W?(\d{1,2})$')
);

-- +----------------------------------------------------------------------------------------------+
-- | Insert into                                                                                  |
-- +----------------------------------------------------------------------------------------------+

INSERT INTO roles (name)
VALUES ('admin'),
       ('employee'),
       ('HRManager');

-- +----------------------------------------------------------------------------------------------+

INSERT INTO role_permissions (role, permission)
VALUES ('admin', 'CanCreateRole'),
       ('admin', 'CanReadRole'),
       ('admin', 'CanModifyRolePermissions'),
       ('admin', 'CanDeactivateRole'),
       ('admin', 'CanReadPermission'),
       ('admin', 'CanCreateAddress'),
       ('admin', 'CanReadAddress'),
       ('admin', 'CanDeleteAddress'),
       ('admin', 'CanCreateEmployee'),
       ('admin', 'CanReadEmployee'),
       ('admin', 'CanModifyEmployeeInfo'),
       ('admin', 'CanModifyEmployeeService'),
       ('admin', 'CanDeactivateEmployee'),
       ('admin', 'CanCreateOrganisation'),
       ('admin', 'CanReadOrganisation'),
       ('admin', 'CanModifyOrganisationAddress'),
       ('admin', 'CanDeleteOrganisation'),
       ('admin', 'CanCreateService'),
       ('admin', 'CanReadService'),
       ('admin', 'CanModifyServiceAddress'),
       ('admin', 'CanModifyServiceOrganisation'),
       ('admin', 'CanDeleteService'),

       ('employee', 'CanReadAddress'),
       ('employee', 'CanReadEmployee'),
       ('employee', 'CanModifyEmployeeInfo'),
       ('employee', 'CanReadOrganisation'),
       ('employee', 'CanReadService'),

       ('HRManager', 'CanCreateAddress'),
       ('HRManager', 'CanCreateEmployee'),
       ('HRManager', 'CanModifyEmployeeService'),
       ('HRManager', 'CanDeactivateEmployee'),
       ('HRManager', 'CanModifyServiceAddress');

INSERT INTO addresses (number, street, city, country, postal_code)
VALUES (1, 'rue de la Paix', 'Paris', 'France', '75000'),
       (2, 'rue de la Paix', 'Paris', 'France', '75000');

-- +----------------------------------------------------------------------------------------------+

INSERT INTO organisations (name, address)
VALUES ('Google', 1);

-- +----------------------------------------------------------------------------------------------+

INSERT INTO services (name, address, organisation)
VALUES ('R&D', 2, 1),
       ('HR', 1, 1);

-- +----------------------------------------------------------------------------------------------+

INSERT INTO employees (id, last_name, first_name, email, service)
VALUES ('A00001', 'Bonnot', 'Jean', 'jean.bonnot@gmail.com', 1),
       ('A00002', 'De La Compta', 'Séverine', 'severine.de-la-compta@gmail.com', 2);

