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

CREATE TABLE employees
(
    id CHAR(6) UNIQUE NOT NULL,
    creation DATE NOT NULL DEFAULT current_timestamp,
    enable BOOLEAN NOT NULL DEFAULT true,

    CONSTRAINT pk_employees PRIMARY KEY (id),

    CONSTRAINT check_employees_id
        CHECK (employees.id ~* '[A-Z][0-9]{5}')
);

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
-- | Insert into                                                                                  |
-- +----------------------------------------------------------------------------------------------+

INSERT INTO employees (id)
VALUES ('A00001'),
       ('A00002');

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