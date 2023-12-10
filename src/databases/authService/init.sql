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

CREATE TABLE users
(
    id_user CHAR(6) UNIQUE NOT NULL,
    index_user SERIAL UNIQUE NOT NULL,
    password VARCHAR(128) NOT NULL,
    timestamp DATE NOT NULL DEFAULT current_timestamp,

    CONSTRAINT pk_users PRIMARY KEY (id_user),

    CONSTRAINT check_users_id
        CHECK (users.id_user ~* '[A-Z][0-9]{5}')
);

-- +----------------------------------------------------------------------------------------------+

CREATE TABLE roles
(
    role VARCHAR(32) UNIQUE NOT NULL,

    CONSTRAINT pk_roles PRIMARY KEY (role)
);

-- +----------------------------------------------------------------------------------------------+

CREATE TABLE resources
(
     resource VARCHAR(64) UNIQUE NOT NULL,

     CONSTRAINT pk_resources PRIMARY KEY (resource)
);

-- +----------------------------------------------------------------------------------------------+

CREATE TABLE permissions
(
    permission VARCHAR(32) UNIQUE NOT NULL,

    CONSTRAINT pk_permissions PRIMARY KEY (permission)
);

-- +----------------------------------------------------------------------------------------------+

CREATE TABLE http_methods
(
    http_method VARCHAR(8) UNIQUE NOT NULL,

    CONSTRAINT pk_http_methods PRIMARY KEY (http_method)
);

-- +----------------------------------------------------------------------------------------------+

CREATE TABLE api_patterns
(
    api_pattern VARCHAR(128) UNIQUE NOT NULL,

    CONSTRAINT pk_api_patterns PRIMARY KEY (api_pattern)
);

-- +----------------------------------------------------------------------------------------------+

CREATE TABLE user_roles
(
    id_user_roles SERIAL UNIQUE NOT NULL,
    id_user CHAR(6) NOT NULL,
    role VARCHAR(32) NOT NULL,

    CONSTRAINT pk_user_roles
        PRIMARY KEY (id_user_roles),
    CONSTRAINT fk_user_roles_table_users
        FOREIGN KEY(id_user) REFERENCES users(id_user),
    CONSTRAINT fk_user_roles_table_roles
        FOREIGN KEY(role) REFERENCES roles(role),
    CONSTRAINT uni_user_roles_cols_id_user_and_role
        UNIQUE (id_user, role)

);

-- +----------------------------------------------------------------------------------------------+

CREATE TABLE authorities
(
    id_authority SERIAL UNIQUE NOT NULL,
    resource VARCHAR(64) NOT NULL,
    permission VARCHAR(32) NOT NULL,

    CONSTRAINT pk_authorities
        PRIMARY KEY (id_authority),
    CONSTRAINT fk_authorities_table_resources
        FOREIGN KEY(resource) REFERENCES resources(resource),
    CONSTRAINT fk_authorities_table_permissions
        FOREIGN KEY(permission) REFERENCES permissions(permission),
    CONSTRAINT uni_authorities_cols_resource_and_permission
        UNIQUE (resource, permission)
);

-- +----------------------------------------------------------------------------------------------+

CREATE TABLE role_authorities
(
    id_role_authorities SERIAL UNIQUE NOT NULL,
    role VARCHAR(32) NOT NULL,
    id_authority INTEGER NOT NULL,

    CONSTRAINT pk_role_authorities
        PRIMARY KEY (id_role_authorities),
    CONSTRAINT fk_role_authorities_table_roles
        FOREIGN KEY(role) REFERENCES roles(role),
    CONSTRAINT fk_role_authorities_table_authorities
        FOREIGN KEY(id_authority) REFERENCES authorities(id_authority),
    CONSTRAINT uni_role_authorities_cols_role_and_id_authority
        UNIQUE (role, id_authority)
);

-- +----------------------------------------------------------------------------------------------+

CREATE TABLE endpoints
(
    id_endpoint SERIAL UNIQUE NOT NULL,
    http_method VARCHAR(8) NOT NULL,
    api_pattern VARCHAR(128) NOT NULL,
    id_authority INTEGER,

    CONSTRAINT pk_endpoints
        PRIMARY KEY (id_endpoint),
    CONSTRAINT fk_endpoints_table_http_methods
        FOREIGN KEY(http_method) REFERENCES http_methods(http_method),
    CONSTRAINT fk_endpoints_table_api_patterns
        FOREIGN KEY(api_pattern) REFERENCES api_patterns(api_pattern),
    CONSTRAINT fk_endpoints_table_authorities
        FOREIGN KEY(id_authority) REFERENCES authorities(id_authority),
    CONSTRAINT uni_endpoints_cols_http_method_and_api_pattern_and_id_authority
        UNIQUE (http_method, api_pattern, id_authority)
);

-- +----------------------------------------------------------------------------------------------+
-- | Insert into                                                                                  |
-- +----------------------------------------------------------------------------------------------+

INSERT INTO users (id_user, password)
VALUES ('A00001', '$2a$10$MAxOfcOCypgcExmZjSp/Fu1rMBbepSZPGDX9y4u1XLkKipYsrVcnK');

-- +----------------------------------------------------------------------------------------------+

INSERT INTO roles (role)
VALUES ('user'),
       ('admin');

-- +----------------------------------------------------------------------------------------------+

INSERT INTO resources (resource)
VALUES ('user'),
       ('user/roles'),
       ('user/password');

-- +----------------------------------------------------------------------------------------------+

INSERT INTO permissions(permission)
VALUES ('read'),
       ('create'),
       ('modify'),
       ('delete');

-- +----------------------------------------------------------------------------------------------+

INSERT INTO http_methods (http_method)
VALUES ('DELETE'),
       ('GET'),
       ('HEAD'),
       ('OPTIONS'),
       ('PATCH'),
       ('POST'),
       ('PUT'),
       ('TRACE');

-- +----------------------------------------------------------------------------------------------+

INSERT INTO api_patterns (api_pattern)
VALUES ('/api/v1/hello'),
       ('/api/docs/**'),
       ('/api/v1/login/token'),
       ('/api/v1/users/**'),
       ('/api/v1/users/**/roles'),
       ('/api/v1/users/**/password');

-- +----------------------------------------------------------------------------------------------+

INSERT INTO user_roles(id_user, role)
VALUES ('A00001', 'admin');

-- +----------------------------------------------------------------------------------------------+

INSERT INTO authorities (resource, permission)
VALUES ('user', 'create'),
       ('user/roles', 'modify'),
       ('user/password', 'modify');

-- +----------------------------------------------------------------------------------------------+

INSERT INTO role_authorities (role, id_authority)
SELECT 'admin', id_authority
FROM authorities
WHERE resource LIKE 'user/roles' AND permission LIKE 'modify' OR
      resource LIKE 'user' AND permission LIKE 'create';

INSERT INTO role_authorities (role, id_authority)
SELECT 'user', id_authority
FROM authorities
WHERE resource LIKE 'user/password' AND permission LIKE 'modify';

-- +----------------------------------------------------------------------------------------------+

INSERT INTO endpoints (http_method, api_pattern, id_authority)
VALUES ('GET', '/api/v1/hello', NULL),
       ('GET', '/api/docs/**', NULL),
       ('POST', '/api/v1/login/token', NULL);

INSERT INTO endpoints (http_method, api_pattern, id_authority)
SELECT 'POST', '/api/v1/users/**', id_authority
FROM authorities
WHERE resource LIKE 'user' AND permission LIKE 'create';

INSERT INTO endpoints (http_method, api_pattern, id_authority)
SELECT 'PUT', '/api/v1/users/**/roles', id_authority
FROM authorities
WHERE resource LIKE 'user/roles' AND permission LIKE 'modify';

INSERT INTO endpoints (http_method, api_pattern, id_authority)
SELECT 'PUT', '/api/v1/users/**/password', id_authority
FROM authorities
WHERE resource LIKE 'user/password' AND permission LIKE 'modify';
