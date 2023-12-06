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

-- Création de la table 'roles'
CREATE TABLE roles
(
    role VARCHAR(50) UNIQUE NOT NULL,

    CONSTRAINT pk_roles PRIMARY KEY (role)
);

-- Création de la table 'users'
CREATE TABLE users
(
    index_user SERIAL UNIQUE NOT NULL,
    id_user CHAR(6) UNIQUE NOT NULL,
    password VARCHAR(128) NOT NULL,
    role VARCHAR(50) NOT NULL,
    timestamp DATE NOT NULL,

    CONSTRAINT pk_users PRIMARY KEY (id_user),
    CONSTRAINT fk_users_role FOREIGN KEY(role) REFERENCES roles(role),

    CONSTRAINT check_users_id
        CHECK (users.id_user ~* '[A-Z][0-9]{5}')
);

-- Insertion de rôles par défaut
INSERT INTO roles (role) VALUES ('user'), ('admin');

INSERT INTO users (id_user, password, role, timestamp)
VALUES ('A00001', '$2a$10$MAxOfcOCypgcExmZjSp/Fu1rMBbepSZPGDX9y4u1XLkKipYsrVcnK', 'admin', '2023-11-23');
