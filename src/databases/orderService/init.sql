-- Title :             Database creation for IMT-3A-PROCOM-ERP project
-- Version :           1.0.0
-- Creation date :     2024-02-21
-- Update date :       2024-03-05
-- Author :            BOPS
-- Description :       Order management service database initialisation script
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

CREATE TABLE employees
(
    id SERIAL UNIQUE NOT NULL,
    login_profile CHAR(6) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    email VARCHAR(320) NOT NULL,
    phone_number VARCHAR(24) DEFAULT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT current_timestamp,

    CONSTRAINT pk_employees
        PRIMARY KEY (id),
    CONSTRAINT fk_employees_table_login_profiles
        FOREIGN KEY (login_profile) REFERENCES login_profiles(id)
            ON UPDATE CASCADE,
    CONSTRAINT check_employees_last_name
        CHECK (employees.last_name ~* '^''?[[:alpha:]]([''\.\-]? ?[[:alpha:]])*[''\.]?$'),
    CONSTRAINT check_employees_first_name
        CHECK (employees.first_name ~* '^''?[[:alpha:]]([''\.\-]? ?[[:alpha:]])*[''\.]?$'),
    CONSTRAINT check_employees_email
        CHECK (employees.email ~* '^[a-z0-9]([\-\.]?[a-z0-9])*@[a-z0-9]([\-\.]?[a-z0-9])*$'),
    CONSTRAINT check_employees_phone_number
        CHECK (employees.phone_number ~* '^\+?[0-9]{1,3} ?[\- ]?([0-9]{1,4}[\- ]?)*[0-9]{1,4}$')
);

-- +----------------------------------------------------------------------------------------------+

CREATE TABLE providers
(
    id SERIAL UNIQUE NOT NULL,
    name VARCHAR(64) UNIQUE NOT NULL,

    CONSTRAINT pk_providers PRIMARY KEY (id),
    CONSTRAINT uq_providers_name UNIQUE (name),
    CONSTRAINT check_providers_name
        CHECK (providers.name ~* '^([[:alpha:]]+[&_\-\. ]?)+$')

);

-- +----------------------------------------------------------------------------------------------+

CREATE TABLE orders
(
    id SERIAL UNIQUE NOT NULL,
    total_amount NUMERIC(10, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT current_timestamp,
    updated_at TIMESTAMP NOT NULL DEFAULT current_timestamp,
    quote VARCHAR(64) NOT NULL DEFAULT '',
    progress_status INT NOT NULL DEFAULT 1,
    address VARCHAR(64) NOT NULL,
    provider INT NOT NULL,
    orderer INT NOT NULL,
    approver INT,

    CONSTRAINT pk_orders PRIMARY KEY (id),
    CONSTRAINT fk_orders_table_address
        FOREIGN KEY (address) REFERENCES addresses(id)
            ON UPDATE CASCADE,
    CONSTRAINT fk_orders_table_providers
        FOREIGN KEY (provider) REFERENCES providers(id)
            ON UPDATE CASCADE,
    CONSTRAINT fk_orders_table_employees_orderer
        FOREIGN KEY (orderer) REFERENCES employees(id),
    CONSTRAINT fk_orders_table_employes_approver
        FOREIGN KEY (approver) REFERENCES employees(id),
    CONSTRAINT check_orders_quote
        CHECK (orders.quote ~* '^[a-zA-Z0-9]([_\- ]?[a-zA-Z0-9])*$')
);

-- +----------------------------------------------------------------------------------------------+

CREATE TABLE order_products
(
    id SERIAL UNIQUE NOT NULL,
    reference VARCHAR(128) NOT NULL,
    unit_price NUMERIC(10, 2) NOT NULL,
    quantity INT NOT NULL,
    "order" INT NOT NULL, -- quotes because order is a PostgreSQL reserved keyword

    CONSTRAINT pk_order_products PRIMARY KEY (id),
    CONSTRAINT fk_order_products_table_orders
        FOREIGN KEY ("order") REFERENCES orders(id)
            ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT uq_order_products_reference_order UNIQUE (reference, "order"),
    CONSTRAINT check_orders_quote
        CHECK (order_products.reference ~* '^[a-zA-Z0-9]([_\- ]?[a-zA-Z0-9])*$')
);

-- +----------------------------------------------------------------------------------------------+
-- | Insert into                                                                                  |
-- +----------------------------------------------------------------------------------------------+

INSERT INTO roles (name, is_enable)
VALUES ('admin', true),
       ('user', true),
       ('orderer', true);

-- +----------------------------------------------------------------------------------------------+

INSERT INTO role_permissions (role, permission)
VALUES ('admin', 'CanBypassAccessDeny'),
       ('admin', 'CanCreateOrder'),
       ('admin', 'CanModifyOrder'),
       ('admin', 'CanModifyRole'),
       ('admin', 'CanReadEmployee'),
       ('admin', 'CanReadOrder'),
       ('admin', 'CanReadProvider'),
       ('admin', 'CanReadRole'),

       ('orderer', 'CanCreateOrder'),
       ('orderer', 'CanModifyOrder'),
       ('orderer', 'CanReadEmployee'),
       ('orderer', 'CanReadOrder'),
       ('orderer', 'CanReadProvider'),

       ('user', 'CanReadEmployee'),
       ('user', 'CanReadOrder'),
       ('user', 'CanReadProvider');

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

INSERT INTO employees (login_profile, last_name, first_name, email, phone_number)
VALUES ('A00001', 'Rousseau', 'Emilie', 'emilie.rousseau@example.com', '+33 1 01 02 03 04'),
       ('A00002', 'Bernard', 'Lucas', 'lucas.bernard@example.com', '+33 1 05 06 07 08'),
       ('A00003', 'Petit', 'Chloé', 'chloe.petit@example.com', '+33 1 09 10 11 12'),

       ('A00004', 'Durand', 'Maxime', 'maxime.durand@example.com', '+33 1 13 14 15 16'),
       ('A00005', 'Leroy', 'Sophie', 'sophie.leroy@example.com', '+33 1 17 18 19 20'),
       ('A00006', 'Moreau', 'Alexandre', 'alexandre.moreau@example.com', '+33 1 21 22 23 24'),

       ('A00007', 'Fournier', 'Camille', 'camille.fournier@example.com', '+33 1 25 26 27 28'),
       ('A00008', 'Girard', 'Nicolas', 'nicolas.girard@example.com', '+33 1 29 30 31 32'),

       ('A00009', 'Dupont', 'Juliette', 'juliette.dupont@example.com', '+33 2 33 34 35 36'),
       ('A00010', 'Lefebvre', 'Raphaël', 'raphael.lefebvre@example.com', '+33 2 37 38 39 40'),
       ('A00011', 'Perrin', 'Marie', 'marie.perrin@example.com', '+33 2 41 42 43 44'),

       ('A00012', 'Martin', 'Jules', 'jules.martin@example.com', '+33 2 45 46 47 48'),
       ('A00013', 'Mercier', 'Charlotte', 'charlotte.mercier@example.com', '+33 2 49 50 51 52'),
       ('A00014', 'Marchand', 'Théo', 'theo.marchand@example.com', '+33 2 53 54 55 56'),
       ('A00015', 'Dubois', 'Laura', 'laura.dubois@example.com', '+33 2 57 58 59 60'),

       ('A00016', 'Roux', 'Gabriel', 'gabriel.roux@example.com', '+33 2 61 62 63 64'),
       ('A00017', 'Vincent', 'Louise', 'louise.vincent@example.com', '+33 2 65 66 67 68'),
       ('A00018', 'Muller', 'Antoine', 'antoine.muller@example.com', '+33 2 69 70 71 72'),
       ('A00019', 'Lambert', 'Alice', 'alice.lambert@example.com', '+33 2 73 74 75 76'),
       ('A00020', 'Faure', 'Mathieu', 'mathieu.faure@example.com', '+33 2 77 78 79 80'),
       ('A00021', 'Blanc', 'Léa', 'lea.blanc@example.com', '+33 2 81 82 83 84'),
       ('A00022', 'Fontaine', 'Hugo', 'hugo.fontaine@example.com', '+33 2 85 86 87 88'),
       ('A00023', 'Robin', 'Anaïs', 'anais.robin@example.com', '+33 2 89 90 91 92'),
       ('A00024', 'Henry', 'Sébastien', 'sebastien.henry@example.com', '+33 2 93 94 95 96'),
       ('A00025', 'Morin', 'Elise', 'elise.morin@example.com', '+33 2 97 98 99 00'),

       ('A00026', 'Nicolas', 'Rémi', 'remi.nicolas@example.com', '+33 2 01 02 03 04'),
       ('A00027', 'Pierre', 'Clara', 'clara.pierre@example.com', '+33 2 05 06 07 08'),
       ('A00028', 'Sanchez', 'Florian', 'florian.sanchez@example.com', '+33 2 09 10 11 12'),
       ('A00029', 'Robert', 'Eva', 'eva.robert@example.com', '+33 2 13 14 15 16'),
       ('A00030', 'Morel', 'David', 'david.morel@example.com', '+33 2 17 18 19 20');

-- +----------------------------------------------------------------------------------------------+

INSERT INTO providers (name)
VALUES ('Tech Supplies Inc.'), -- id = 1
       ('Office Needs Ltd.'), -- id = 2
       ('Wool Supplier Co.'), -- id = 3
       ('Logistics Solutions LLC'), -- id = 4
       ('HR Essentials S.A.'), -- id = 5
       ('Designing Lab Co.'); -- id = 6
