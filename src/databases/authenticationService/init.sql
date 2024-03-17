-- Title :             Database creation for IMT-3A-PROCOM-ERP project
-- Version :           1.0.0
-- Creation date :     2023-11-23
-- Update date :       2024-03-05
-- Author :            BOPS
-- Description :       Authentication service database initialisation script
--                     Note : Script for PostgreSQL

-- +----------------------------------------------------------------------------------------------+
-- | Create table                                                                                 |
-- +----------------------------------------------------------------------------------------------+

CREATE TABLE roles
(
    name VARCHAR(32) UNIQUE NOT NULL,
    is_enable BOOLEAN NOT NULL DEFAULT false,

    CONSTRAINT pk_roles PRIMARY KEY (name),
    CONSTRAINT check_roles_name
        CHECK (roles.name ~* '^[a-zA-Z]([\-\.]?[a-zA-Z0-9])*$')
);

-- +----------------------------------------------------------------------------------------------+

CREATE TABLE role_activations
(
    id SERIAL UNIQUE NOT NULL,
    role VARCHAR(32) NOT NULL,
    microservice VARCHAR(32) NOT NULL,
    is_enable BOOLEAN NOT NULL DEFAULT false,

    CONSTRAINT pk_role_activations
        PRIMARY KEY (id),
    CONSTRAINT fk_role_activations_table_roles
        FOREIGN KEY (role) REFERENCES roles(name)
            ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT uq_role_activations_role_microservice
        UNIQUE (role, microservice),
    CONSTRAINT check_role_activations_microservice
        CHECK (role_activations.microservice ~* '^[a-zA-Z]([\-\.]?[a-zA-Z0-9])*$')
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
            ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT check_role_permissions_permission
        CHECK (role_permissions.permission ~* '^Can[A-Z][a-z]([A-Z]?[a-z])*$')
);

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
        CHECK (login_profiles.id ~* '^[A-Z][0-9]{5}$'),
    CONSTRAINT check_login_profiles_email
        CHECK (login_profiles.email ~* '^[a-z0-9]([\-\.]?[a-z0-9])*@[a-z0-9]([\-\.]?[a-z0-9])*$')
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
VALUES ('A00001', '$2a$10$4ckdBc/ZzfmfHkQL8fyvcO.8QnV8Vf62olUN9k0BWr.h1x3BWEUxC'), -- Toto0001toto!
       ('A00002', '$2a$10$PZhHv2PxEy/ZhFhaHHMLPutd3aNlunQsThjoX2Hjp0dnoHbK3cLda'), -- Toto0002toto!
       ('A00003', '$2a$10$.4XNJhZ82AB8fMAcYZkIW.ZTf/ehdB1fuga9r6zpcmAmJ9pUiDO2S'), -- Toto0003toto!
       ('A00004', '$2a$10$Go5P.iUjzF4vAz8D02b66eg0NnnOhZyS73v4P47uQaAxA7myImFCK'), -- Toto0004toto!
       ('A00005', '$2a$10$LVYq6fj591DjvkdyXy0E7OLLC9nNEfumNuuUmvNb7vy16ZQOXXmp.'), -- Toto0005toto!
       ('A00006', '$2a$10$mbZsl72nOQ2HNCptrFR86.KEaRnTQqrU0xHgpjJaKQdUM1AwvtxhC'), -- Toto0006toto!
       ('A00007', '$2a$10$.BfD5ftDfmP58FfvN1CVhee3bJS13AIs4UJxrtHri2f9EuSfFtlHG'), -- Toto0007toto!
       ('A00008', '$2a$10$B7/.hq8GFpRNKbr5G1hTseYJ47biAY0FYdH/0RxYDaLTzLPD.daDS'), -- Toto0008toto!
       ('A00009', '$2a$10$dKAeXuBWcXmAzbATr7GTu.CyiVtq9xsgTa6rXVKIWD9xxhb5Q3kd2'), -- Toto0009toto!
       ('A00010', '$2a$10$lvuikYu/jssCb62wVHwIr.AecAXsr7IySGczfZxU7WsQtXxei9gki'), -- Toto0010toto!
       ('A00011', '$2a$10$Y2LjBlvW27NoXjX2GT6zLuFcka0B71VtXoaGbVhvSv0Z/Fe3ntGV.'), -- Toto0011toto!
       ('A00012', '$2a$10$W5AwB2T4DvrC8tLj2xjWO.u4lLErhlvcENhbZLz3maCHa9mBdJPO2'), -- Toto0012toto!
       ('A00013', '$2a$10$1mIFl3EtIFqIBh2foDdVEugu36hjWK73zoXKtsmvn4bt9YaE0jHIC'), -- Toto0013toto!
       ('A00014', '$2a$10$yBoQ2iEJEZ77/y7khmhereLQu4RQ6Af385mB1gdW/qL3tRb18XgQm'), -- Toto0014toto!
       ('A00015', '$2a$10$m/Z.jcmg1A0xhWEigGq5jeY6S5jzbgu6zK2WCc985ezbe7xDu9FFW'), -- Toto0015toto!
       ('A00016', '$2a$10$wnLK.lql85KRCyFbauEdKeH8g5atRXv0SjK.tKK2/EkqPp/feUM6G'), -- Toto0016toto!
       ('A00017', '$2a$10$YQL4qtppCZLs/7vnbt.rie.BkteZufYYGzyB/G4G48JOGzszHyqAC'), -- Toto0017toto!
       ('A00018', '$2a$10$f2KFlh.XaxMr2siiPcqjDe6PGH8AJX/NGKawVtxSrcJPCGYXAEDza'), -- Toto0018toto!
       ('A00019', '$2a$10$c2JLFzOC1buxAmg1QAJrvOA96NvHLzkSqVltPz9TzyKv8/zR0NS1y'), -- Toto0019toto!
       ('A00020', '$2a$10$6rZ1QlTucJFw2/X/ieerMeOm6yut4cpGcuMCnzW74OQ7FZEtt6h32'), -- Toto0020toto!
       ('A00021', '$2a$10$hn7plhb3k9Swy0x3RXnsve6CWhCDE9L4SfUdOZ1Wt7l/iyKnM7SCO'), -- Toto0021toto!
       ('A00022', '$2a$10$L2JM9Tnl/tU9jebToHlzee8lum340QzPfHVJj93Lxr8E6xuAG7m.K'), -- Toto0022toto!
       ('A00023', '$2a$10$xMldOv49nzXyVJ23P2bBEOZk5C2fjuphEwYLBGR41WmjMGi8dDd9y'), -- Toto0023toto!
       ('A00024', '$2a$10$E0GgBHCdiXnHYUOGXBEQQupADMK1hZ2.UHbUNs5XhlynVPc/wwlba'), -- Toto0024toto!
       ('A00025', '$2a$10$TYeEv0QSxZn1X1qH.pUXMeYZxOoiY89Z284j1xRKHJJpl9trFngnG'), -- Toto0025toto!
       ('A00026', '$2a$10$cmSv61NqxSxb3LlO2eiM0ecR7YMB7.ayiCVPnp5j3i1K03PZLEu7O'), -- Toto0026toto!
       ('A00027', '$2a$10$F1uTswDwcfhntYW7p9n9feU7J6eGARy1V8t0N/3CVopO1Z0kPjYly'), -- Toto0027toto!
       ('A00028', '$2a$10$cx5ntRLic1jOmD2uP4DhsOmmS6.RDxSbntGgQ.sg2IA8hrmZIjjee'), -- Toto0028toto!
       ('A00029', '$2a$10$YCRA9WEoa88Ghv9oOlo3KeNDZ4BflTFdIh3n6zOogVgK/H4axwmqu'), -- Toto0029toto!
       ('A00030', '$2a$10$HcoC.ywfBKrqkWrypR7iSuH6bzUekjC.5Z2xSqbqO2rVtgnueN7G.'); -- Toto0030toto!

-- +----------------------------------------------------------------------------------------------+

INSERT INTO roles (name, is_enable)
VALUES ('admin', true),

       ('user', true);

-- +----------------------------------------------------------------------------------------------+

INSERT INTO role_activations (role, microservice, is_enable)
VALUES ('admin', 'authentication', true),

       ('user', 'authentication', true);

-- +----------------------------------------------------------------------------------------------+

INSERT INTO join_login_profiles_roles (login_profile, role)
VALUES ('A00001', 'admin'),
       ('A00002', 'admin'),
       ('A00003', 'admin'),

       ('A00001', 'user'),
       ('A00002', 'user'),
       ('A00003', 'user'),
       ('A00004', 'user'),
       ('A00005', 'user'),
       ('A00006', 'user'),
       ('A00007', 'user'),
       ('A00008', 'user'),
       ('A00009', 'user'),
       ('A00010', 'user'),
       ('A00011', 'user'),
       ('A00012', 'user'),
       ('A00013', 'user'),
       ('A00014', 'user'),
       ('A00015', 'user'),
       ('A00016', 'user'),
       ('A00017', 'user'),
       ('A00018', 'user'),
       ('A00019', 'user'),
       ('A00020', 'user'),
       ('A00021', 'user'),
       ('A00022', 'user'),
       ('A00023', 'user'),
       ('A00024', 'user'),
       ('A00025', 'user'),
       ('A00026', 'user'),
       ('A00027', 'user'),
       ('A00028', 'user'),
       ('A00029', 'user'),
       ('A00030', 'user');

-- +----------------------------------------------------------------------------------------------+

INSERT INTO role_permissions (role, permission)
VALUES ('admin', 'CanBypassAccessDeny'),
       ('admin', 'CanCreateLoginProfile'),
       ('admin', 'CanReadLoginProfile'),
       ('admin', 'CanModifyLoginProfile'),
       ('admin', 'CanModifyLoginProfilePassword'),
       ('admin', 'CanCreateRole'),
       ('admin', 'CanReadRole'),
       ('admin', 'CanModifyRole'),

       ('user', 'CanModifyLoginProfilePassword');
