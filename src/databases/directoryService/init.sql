-- Title :             Database creation for IMT-3A-PROCOM-ERP project
-- Version :           1.0.0
-- Creation date :     2023-12-22
-- Update date :       2024-03-05
-- Author :            BOPS
-- Description :       Directory service database initialisation script
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

    CONSTRAINT pk_addresses PRIMARY KEY (id),
    CONSTRAINT check_addresses_id
        CHECK (addresses.id ~* '^[0-9a-f]+$')
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
            ON UPDATE CASCADE ON DELETE SET DEFAULT,
    CONSTRAINT check_organisations_name
        CHECK (organisations.name ~* '^[a-zA-Z]([&_\-\.\s]?[a-zA-Z0-9])*$')
);

-- +----------------------------------------------------------------------------------------------+

CREATE TABLE org_units
(
    id SERIAL UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    manager CHAR(6) DEFAULT NULL,
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
        UNIQUE (name, organisation),
    CONSTRAINT check_organisations_name
        CHECK (org_units.name ~* '^[a-zA-Z]([&_\-\.\s]?[a-zA-Z0-9])*$')
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
        UNIQUE (email),
    CONSTRAINT check_employees_last_name
        CHECK (employees.last_name ~* '^''?[[:alpha:]]([''\.\-]? ?[[:alpha:]])*[''\.]?$'),
    CONSTRAINT check_employees_first_name
        CHECK (employees.first_name ~* '^''?[[:alpha:]]([''\.\-]? ?[[:alpha:]])*[''\.]?$'),
    CONSTRAINT check_employees_email
        CHECK (employees.email ~* '^[a-z0-9]([\-\.]?[a-z0-9])*@[a-z0-9]([\-\.]?[a-z0-9])*$'),
    CONSTRAINT check_employees_phone_number
        CHECK (employees.phone_number ~* '^\+?[0-9]{1,3}[\- ]?([0-9]{1,4}[\- ]?)*[0-9]{1,4}$'),
    CONSTRAINT check_employees_job
        CHECK (employees.job ~* '^([[:alpha:]]''?[[:alpha:]]+ ?)*[[:alpha:]]$')
);

-- +----------------------------------------------------------------------------------------------+

ALTER TABLE org_units ADD CONSTRAINT fk_org_units_table_employees
    FOREIGN KEY (manager) REFERENCES employees(id) ON UPDATE CASCADE ON DELETE SET DEFAULT;

-- +----------------------------------------------------------------------------------------------+
-- | Insert into                                                                                  |
-- +----------------------------------------------------------------------------------------------+

INSERT INTO roles (name, is_enable)
VALUES ('admin', true),
       ('HR', true),
       ('user', true);

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

       ('HR', 'CanCreateEmployee'),
       ('HR', 'CanModifyEmployee'),

       ('user', 'CanModifyEmployee'),
       ('user', 'CanReadAddress'),
       ('user', 'CanReadEmployee'),
       ('user', 'CanReadOrganisation');

-- +----------------------------------------------------------------------------------------------+

INSERT INTO login_profiles (id)
VALUES ('A00001'), ('A00002'), ('A00003'), ('A00004'), ('A00005'),
       ('A00006'), ('A00007'), ('A00008'), ('A00009'), ('A00010'),
       ('A00011'), ('A00012'), ('A00013'), ('A00014'), ('A00015'),
       ('A00016'), ('A00017'), ('A00018'), ('A00019'), ('A00020'),
       ('A00021'), ('A00022'), ('A00023'), ('A00024'), ('A00025'),
       ('A00026'), ('A00027'), ('A00028'), ('A00029'), ('A00030');

-- +----------------------------------------------------------------------------------------------+

INSERT INTO addresses (id, number, street, city, country, zipcode, state)
VALUES ('681370aec431f01f00f0949eecdd5afb640f6f9a195d14d5d229e722bc1ceb92',
        1, 'Rue de la Paix', 'Paris', 'France', '75000', 'Île-de-France'),
       ('0a7535a629ce45ded54b7f2411934fa0e7d49e25716495e862342006101ca192',
        180, 'Kerlaurent', 'Guipavas', 'France', '29490', 'Bretagne');

-- +----------------------------------------------------------------------------------------------+

INSERT INTO organisations (name, address)
VALUES ('Texte Ile', '681370aec431f01f00f0949eecdd5afb640f6f9a195d14d5d229e722bc1ceb92'); -- id = 1

-- +----------------------------------------------------------------------------------------------+

INSERT INTO org_units (name, org_unit, organisation, address)
VALUES ('Management', null, 1,
        '681370aec431f01f00f0949eecdd5afb640f6f9a195d14d5d229e722bc1ceb92'), -- id = 1
       ('Human Resources', 1, 1,
        '681370aec431f01f00f0949eecdd5afb640f6f9a195d14d5d229e722bc1ceb92'), -- id = 2
       ('Sales', 1, 1,
        '681370aec431f01f00f0949eecdd5afb640f6f9a195d14d5d229e722bc1ceb92'), -- id = 3
       ('Production', 1, 1,
        '0a7535a629ce45ded54b7f2411934fa0e7d49e25716495e862342006101ca192'), -- id = 4
       ('Design', 2, 1,
        '0a7535a629ce45ded54b7f2411934fa0e7d49e25716495e862342006101ca192'), -- id = 5
       ('Manufacturing', 2, 1,
        '0a7535a629ce45ded54b7f2411934fa0e7d49e25716495e862342006101ca192'), -- id = 6
       ('Logistics', 2, 1,
        '0a7535a629ce45ded54b7f2411934fa0e7d49e25716495e862342006101ca192'); -- id = 7

-- +----------------------------------------------------------------------------------------------+

INSERT INTO employees (id, last_name, first_name, email, phone_number, org_unit, job)
VALUES -- Management (3 employees)
       ('A00001', 'Rousseau', 'Emilie', 'emilie.rousseau@example.com', '+33 1 01 02 03 04', 1, 'CEO'),
       ('A00002', 'Bernard', 'Lucas', 'lucas.bernard@example.com', '+33 1 05 06 07 08', 1, 'CFO'),
       ('A00003', 'Petit', 'Chloé', 'chloe.petit@example.com', '+33 1 09 10 11 12', 1, 'CTO'),

       -- Human Resources (3 employees)
       ('A00004', 'Durand', 'Maxime', 'maxime.durand@example.com', '+33 1 13 14 15 16', 2, 'HR Manager'),
       ('A00005', 'Leroy', 'Sophie', 'sophie.leroy@example.com', '+33 1 17 18 19 20', 2, 'Recruiter'),
       ('A00006', 'Moreau', 'Alexandre', 'alexandre.moreau@example.com', '+33 1 21 22 23 24', 2, 'HR Analyst'),

       -- Sales (2 employees)
       ('A00007', 'Fournier', 'Camille', 'camille.fournier@example.com', '+33 1 25 26 27 28', 3, 'Sales Manager'),
       ('A00008', 'Girard', 'Nicolas', 'nicolas.girard@example.com', '+33 1 29 30 31 32', 3, 'Sales Representative'),

       -- Production (3 employees)
       ('A00009', 'Dupont', 'Juliette', 'juliette.dupont@example.com', '+33 2 33 34 35 36', 4, 'Production Manager'),
       ('A00010', 'Lefebvre', 'Raphaël', 'raphael.lefebvre@example.com', '+33 2 37 38 39 40', 4, 'Quality Control Specialist'),
       ('A00011', 'Perrin', 'Marie', 'marie.perrin@example.com', '+33 2 41 42 43 44', 4, 'Operations Coordinator'),

       -- Design (4 employees)
       ('A00012', 'Martin', 'Jules', 'jules.martin@example.com', '+33 2 45 46 47 48', 5, 'Design Manager'),
       ('A00013', 'Mercier', 'Charlotte', 'charlotte.mercier@example.com', '+33 2 49 50 51 52', 5, 'Graphic Designer'),
       ('A00014', 'Marchand', 'Théo', 'theo.marchand@example.com', '+33 2 53 54 55 56', 5, 'Graphic Designer'),
       ('A00015', 'Dubois', 'Laura', 'laura.dubois@example.com', '+33 2 57 58 59 60', 5, 'Industrial Designer'),

       -- Manufacturing (10 employees)
       ('A00016', 'Roux', 'Gabriel', 'gabriel.roux@example.com', '+33 2 61 62 63 64', 6, 'Production Supervisor'),
       ('A00017', 'Vincent', 'Louise', 'louise.vincent@example.com', '+33 2 65 66 67 68', 6, 'Process Operator'),
       ('A00018', 'Muller', 'Antoine', 'antoine.muller@example.com', '+33 2 69 70 71 72', 6, 'Quality Assurance Technician'),
       ('A00019', 'Lambert', 'Alice', 'alice.lambert@example.com', '+33 2 73 74 75 76', 6, 'Assembly Line Worker'),
       ('A00020', 'Faure', 'Mathieu', 'mathieu.faure@example.com', '+33 2 77 78 79 80', 6, 'Machine Operator'),
       ('A00021', 'Blanc', 'Léa', 'lea.blanc@example.com', '+33 2 81 82 83 84', 6, 'Manufacturing Engineer'),
       ('A00022', 'Fontaine', 'Hugo', 'hugo.fontaine@example.com', '+33 2 85 86 87 88', 6, 'Maintenance Technician'),
       ('A00023', 'Robin', 'Anaïs', 'anais.robin@example.com', '+33 2 89 90 91 92', 6, 'Material Handler'),
       ('A00024', 'Henry', 'Sébastien', 'sebastien.henry@example.com', '+33 2 93 94 95 96', 6, 'Packaging Operator'),
       ('A00025', 'Morin', 'Elise', 'elise.morin@example.com', '+33 2 97 98 99 00', 6, 'Inventory Specialist'),

       -- Logistics (5 employees)
       ('A00026', 'Nicolas', 'Rémi', 'remi.nicolas@example.com', '+33 2 01 02 03 04', 7, 'Logistics Manager'),
       ('A00027', 'Pierre', 'Clara', 'clara.pierre@example.com', '+33 2 05 06 07 08', 7, 'Warehouse Supervisor'),
       ('A00028', 'Sanchez', 'Florian', 'florian.sanchez@example.com', '+33 2 09 10 11 12', 7, 'Transport Coordinator'),
       ('A00029', 'Robert', 'Eva', 'eva.robert@example.com', '+33 2 13 14 15 16', 7, 'Supply Chain Analyst'),
       ('A00030', 'Morel', 'David', 'david.morel@example.com', '+33 2 17 18 19 20', 7, 'Logistics Specialist');

-- +----------------------------------------------------------------------------------------------+

-- Assigning managers to each organizational unit
UPDATE org_units SET manager = 'A00001' WHERE id = 1; -- Management manager
UPDATE org_units SET manager = 'A00004' WHERE id = 2; -- Human Resources manager
UPDATE org_units SET manager = 'A00007' WHERE id = 3; -- Sales manager
UPDATE org_units SET manager = 'A00009' WHERE id = 4; -- Production manager
UPDATE org_units SET manager = 'A00012' WHERE id = 5; -- Design manager
UPDATE org_units SET manager = 'A00016' WHERE id = 6; -- Manufacturing manager
UPDATE org_units SET manager = 'A00026' WHERE id = 7; -- Logistics manager
