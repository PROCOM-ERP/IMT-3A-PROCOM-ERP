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
    enable BOOLEAN NOT NULL DEFAULT true,

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

CREATE TABLE organisations
(
    id SERIAL UNIQUE NOT NULL,
    name VARCHAR(255) UNIQUE NOT NULL,
    address VARCHAR(255) NOT NULL,

    CONSTRAINT pk_organisations
        PRIMARY KEY (id)
);

-- +----------------------------------------------------------------------------------------------+

CREATE TABLE services
(
    id SERIAL UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    organisation INT NOT NULL,

    CONSTRAINT pk_services
        PRIMARY KEY (id),
    CONSTRAINT fk_services_table_organisations
        FOREIGN KEY (organisation) REFERENCES organisations(id)
            ON UPDATE CASCADE
);

-- +----------------------------------------------------------------------------------------------+

CREATE TABLE employees
(
    id CHAR(6) UNIQUE NOT NULL,
    creation DATE NOT NULL DEFAULT current_timestamp,
    enable BOOLEAN NOT NULL DEFAULT true,
    last_name VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    email VARCHAR(320) UNIQUE NOT NULL,
    phone_number VARCHAR(24),
    service INT NOT NULL,

    CONSTRAINT pk_employees
        PRIMARY KEY (id),
    CONSTRAINT fk_employees_table_services
        FOREIGN KEY (service) REFERENCES services(id)
            ON UPDATE CASCADE,
    CONSTRAINT check_employees_id
        CHECK (employees.id ~* '[A-Z][0-9]{5}'),
    CONSTRAINT check_employees_email
        CHECK (employees.email ~* '^[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,62}[a-zA-Z0-9])?@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z]{2,})+$'),
    CONSTRAINT check_employees_phone_number
        CHECK (employees.phone_number ~* '^\+((?:9[679]|8[0357-9]|6[7-9]|5[09]|42|3[578]|2[1-689])\d|9[0-58]|8[1246]|6[0-6]|5[1-8]|4[013-9]|3[0-469]|2[07]|[017])\W?\d\W?\d\W?\d\W?\d\W?\d\W?\d\W?\d\W?\d\W?(\d{1,2})$')
);

-- +----------------------------------------------------------------------------------------------+
-- | Insert into                                                                                  |
-- +----------------------------------------------------------------------------------------------+

INSERT INTO roles (name)
VALUES ('employee'),
       ('admin');

-- +----------------------------------------------------------------------------------------------+

INSERT INTO role_permissions (role, permission)
VALUES ('admin', 'CanCreateRole'),
       ('admin', 'CanReadRole'),
       ('admin', 'CanModifyRolePermissions'),
       ('admin', 'CanDeactivateRole'),
       ('admin', 'CanReadPermission');
