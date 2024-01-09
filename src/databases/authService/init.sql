-- Title :             Database creation for L04EE02-SpringBoot-MicroServices project
-- Version :           1.0
-- Creation date :     2023-11-23
-- Update date :       2023-11-23
-- Author :            Thibaut RUZICKA
-- Description :       Database initialisation script for L04EE02-SpringBoot-MicroServices
--                     Note : Script for PostgreSQL

-- +----------------------------------------------------------------------------------------------+
-- | Create table                                                                                 |
-- +----------------------------------------------------------------------------------------------+

CREATE TABLE employees
(
    id CHAR(6) UNIQUE NOT NULL,
    id_employee_gen SERIAL UNIQUE NOT NULL,
    password VARCHAR(128) NOT NULL,
    email VARCHAR(320) UNIQUE,
    creation TIMESTAMP NOT NULL DEFAULT current_timestamp,
    enable BOOLEAN NOT NULL DEFAULT true,
    jwt_min_creation TIMESTAMP NOT NULL DEFAULT current_timestamp,

    CONSTRAINT pk_employees PRIMARY KEY (id),

    CONSTRAINT check_employees_id
        CHECK (employees.id ~* '[A-Z][0-9]{5}'),
    CONSTRAINT check_employees_email
        CHECK (employees.email ~* '^[a-zA-Z0-9](?:[a-zA-Z0-9-._-]{0,62}[a-zA-Z0-9])?@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z]{2,})+$')

);

-- +----------------------------------------------------------------------------------------------+

CREATE TABLE roles
(
    name VARCHAR(32) UNIQUE NOT NULL,
    enable BOOLEAN NOT NULL DEFAULT true,
    counter INTEGER NOT NULL DEFAULT 1,

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

CREATE TABLE join_employees_roles
(
    employee CHAR(6) NOT NULL,
    role VARCHAR(32) NOT NULL,

    CONSTRAINT pk_join_employees_roles
        PRIMARY KEY (employee, role),
    CONSTRAINT fk_join_employees_roles_table_employees
        FOREIGN KEY (employee) REFERENCES employees(id)
            ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_join_employees_roles_table_roles
        FOREIGN KEY (role) REFERENCES roles(name)
            ON UPDATE CASCADE ON DELETE CASCADE
);

-- +----------------------------------------------------------------------------------------------+
-- | Insert into                                                                                  |
-- +----------------------------------------------------------------------------------------------+

INSERT INTO employees (id, password)
VALUES ('A00001', '$2a$10$MAxOfcOCypgcExmZjSp/Fu1rMBbepSZPGDX9y4u1XLkKipYsrVcnK'),
       ('A00002', '$2a$10$MAxOfcOCypgcExmZjSp/Fu1rMBbepSZPGDX9y4u1XLkKipYsrVcnK');

-- +----------------------------------------------------------------------------------------------+

INSERT INTO roles (name)
VALUES ('employee'),
       ('admin');

-- +----------------------------------------------------------------------------------------------+

INSERT INTO join_employees_roles (employee, role)
VALUES ('A00001', 'admin'),
       ('A00001', 'employee'),
       ('A00002', 'employee');

-- +----------------------------------------------------------------------------------------------+

INSERT INTO role_permissions (role, permission)
VALUES ('admin', 'CanCreateEmployee'),
       ('admin', 'CanReadEmployee'),
       ('admin', 'CanModifyEmployeeRoles'),
       ('admin', 'CanModifyEmployeeEmail'),
       ('admin', 'CanDeactivateEmployee'),
       ('admin', 'CanCreateRole'),
       ('admin', 'CanReadRole'),
       ('admin', 'CanModifyRolePermissions'),
       ('admin', 'CanDeactivateRole'),
       ('admin', 'CanReadPermission'),
       ('employee', 'CanReadEmployee'),
       ('employee', 'CanModifyEmployeePassword');
