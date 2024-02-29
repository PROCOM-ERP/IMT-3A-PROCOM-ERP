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

CREATE TABLE organisations
(
    id SERIAL UNIQUE NOT NULL,
    name VARCHAR(255) UNIQUE NOT NULL,
    address VARCHAR(64) DEFAULT NULL,

    CONSTRAINT pk_organisations
        PRIMARY KEY (id),
    CONSTRAINT fk_organisations_table_addresses
        FOREIGN KEY (address) REFERENCES addresses(id)
            ON UPDATE CASCADE ON DELETE SET DEFAULT
);

-- +----------------------------------------------------------------------------------------------+

CREATE TABLE org_units
(
    id SERIAL UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    org_unit INT DEFAULT NULL,
    organisation INT NOT NULL,
    address VARCHAR(64) DEFAULT NULL,

    CONSTRAINT pk_org_units
        PRIMARY KEY (id),
    CONSTRAINT fk_org_units_table_org_units
        FOREIGN KEY (org_unit) REFERENCES org_units(id)
            ON UPDATE CASCADE ON DELETE SET DEFAULT,
    CONSTRAINT fk_org_units_table_organisations
        FOREIGN KEY (organisation) REFERENCES organisations(id)
            ON UPDATE CASCADE,
    CONSTRAINT fk_org_units_table_addresses
        FOREIGN KEY (address) REFERENCES addresses(id)
            ON UPDATE CASCADE ON DELETE SET DEFAULT,
    CONSTRAINT uq_org_units_name_organisation
        UNIQUE (name, organisation)
);

-- +----------------------------------------------------------------------------------------------+

CREATE TABLE employees
(
    id CHAR(6) UNIQUE NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    email VARCHAR(320) UNIQUE NOT NULL,
    phone_number VARCHAR(24),
    job VARCHAR(64),
    org_unit INT NOT NULL,

    CONSTRAINT pk_employees
        PRIMARY KEY (id),
    CONSTRAINT fk_employees_table_login_profiles
        FOREIGN KEY (id) REFERENCES login_profiles(id)
            ON UPDATE CASCADE,
    CONSTRAINT fk_employees_table_org_units
        FOREIGN KEY (org_unit) REFERENCES org_units(id)
            ON UPDATE CASCADE,
    CONSTRAINT uq_employees_email
        UNIQUE (email)
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
       ('admin', 'CanCreateAddress'),
       ('admin', 'CanCreateEmployee'),
       ('admin', 'CanModifyEmployee'),
       ('admin', 'CanModifyRole'),
       ('admin', 'CanReadAddress'),
       ('admin', 'CanReadEmployee'),
       ('admin', 'CanReadOrganisation'),
       ('admin', 'CanReadRole'),

       ('user', 'CanModifyEmployee'),
       ('user', 'CanReadAddress'),
       ('user', 'CanReadEmployee'),
       ('user', 'CanReadOrganisation');

-- +----------------------------------------------------------------------------------------------+

INSERT INTO login_profiles (id)
VALUES ('A00001'),
       ('A00002');

-- +----------------------------------------------------------------------------------------------+

INSERT INTO addresses (id, number, street, city, country, zipcode)
VALUES ('681370aec431f01f00f0949eecdd5afb640f6f9a195d14d5d229e722bc1ceb92',
        1, 'Rue de la Paix', 'Paris', 'France', '75000'),
       ('72e08cc844ccc2cde34dc2372166fe808f667d4dadbc4dd114386e4d9f88c574',
        2, 'Rue de la Paix', 'Paris', 'France', '75000');

-- +----------------------------------------------------------------------------------------------+

INSERT INTO organisations (name, address)
VALUES ('Google', '681370aec431f01f00f0949eecdd5afb640f6f9a195d14d5d229e722bc1ceb92'); -- id = 1

-- +----------------------------------------------------------------------------------------------+

INSERT INTO org_units (name, org_unit, organisation, address)
VALUES ('R&D', null, 1,
        '72e08cc844ccc2cde34dc2372166fe808f667d4dadbc4dd114386e4d9f88c574'), -- id = 1
       ('HR', null, 1,
        '681370aec431f01f00f0949eecdd5afb640f6f9a195d14d5d229e722bc1ceb92'); -- id = 2

-- +----------------------------------------------------------------------------------------------+

INSERT INTO employees (id, last_name, first_name, email, org_unit)
VALUES ('A00001', 'Bonnot', 'Jean', 'jean.bonnot@gmail.com', 1),
       ('A00002', 'De La Compta', 'Séverine', 'severine.de-la-compta@gmail.com', 2);

