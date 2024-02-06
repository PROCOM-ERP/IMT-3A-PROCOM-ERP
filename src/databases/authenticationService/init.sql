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

CREATE TABLE login_profiles
(
    id CHAR(6) UNIQUE NOT NULL,
    id_login_profile_gen SERIAL UNIQUE NOT NULL,
    password VARCHAR(128) NOT NULL,
    email VARCHAR(320) UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT current_timestamp,
    is_enable BOOLEAN NOT NULL DEFAULT true,
    jwt_gen_min_at TIMESTAMP NOT NULL DEFAULT current_timestamp,

    CONSTRAINT pk_login_profiles PRIMARY KEY (id),

    CONSTRAINT check_login_profiles_id
        CHECK (login_profiles.id ~* '[A-Z][0-9]{5}')--,
    --CONSTRAINT check_employees_email
        --CHECK (employees.email ~* '^[a-zA-Z0-9](?:[a-zA-Z0-9-._-]{0,62}[a-zA-Z0-9])?@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z]{2,})+$')

);

-- +----------------------------------------------------------------------------------------------+

CREATE TABLE roles
(
    name VARCHAR(32) UNIQUE NOT NULL,
    is_enable BOOLEAN NOT NULL DEFAULT true,

    CONSTRAINT pk_roles PRIMARY KEY (name)
);

-- +----------------------------------------------------------------------------------------------+

CREATE TABLE role_activations
(
    role VARCHAR(32) NOT NULL,
    microservice VARCHAR(32) NOT NULL,
    is_enable BOOLEAN NOT NULL DEFAULT true,

    CONSTRAINT pk_role_activations
        PRIMARY KEY (role, microservice),
    CONSTRAINT fk_role_activations_table_roles
        FOREIGN KEY (role) REFERENCES roles(name)
            ON UPDATE CASCADE ON DELETE CASCADE
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

CREATE TABLE join_login_profiles_roles
(
    login_profile CHAR(6) NOT NULL,
    role VARCHAR(32) NOT NULL,

    CONSTRAINT pk_join_login_profiles_roles
        PRIMARY KEY (login_profile, role),
    CONSTRAINT fk_join_login_profiles_roles_table_login_profiles
        FOREIGN KEY (login_profile) REFERENCES login_profiles(id)
            ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_join_login_profiles_roles_table_roles
        FOREIGN KEY (role) REFERENCES roles(name)
            ON UPDATE CASCADE ON DELETE CASCADE
);

-- +----------------------------------------------------------------------------------------------+
-- | Insert into                                                                                  |
-- +----------------------------------------------------------------------------------------------+

INSERT INTO login_profiles (id, password)
VALUES ('A00001', '$2a$10$MAxOfcOCypgcExmZjSp/Fu1rMBbepSZPGDX9y4u1XLkKipYsrVcnK'),
       ('A00002', '$2a$10$MAxOfcOCypgcExmZjSp/Fu1rMBbepSZPGDX9y4u1XLkKipYsrVcnK');

-- +----------------------------------------------------------------------------------------------+

INSERT INTO roles (name)
VALUES ('employee'),
       ('admin');

-- +----------------------------------------------------------------------------------------------+

INSERT INTO role_activations (role, microservice)
VALUES ('employee', 'authentication'),
       ('admin', 'authentication');

-- +----------------------------------------------------------------------------------------------+

INSERT INTO join_login_profiles_roles (login_profile, role)
VALUES ('A00001', 'admin'),
       ('A00001', 'employee'),
       ('A00002', 'employee');

-- +----------------------------------------------------------------------------------------------+

INSERT INTO role_permissions (role, permission)
VALUES ('admin', 'CanCreateLoginProfile'),
       ('admin', 'CanReadLoginProfile'),
       ('admin', 'CanModifyLoginProfileRoles'),
       ('admin', 'CanModifyLoginProfileEmail'),
       ('admin', 'CanDeactivateLoginProfile'),
       ('admin', 'CanCreateRole'),
       ('admin', 'CanReadRole'),
       ('admin', 'CanModifyRolePermissions'),
       ('admin', 'CanDeactivateRole'),
       ('admin', 'CanReadPermission'),
       ('employee', 'CanReadLoginProfile'),
       ('employee', 'CanModifyLoginProfilePassword');
