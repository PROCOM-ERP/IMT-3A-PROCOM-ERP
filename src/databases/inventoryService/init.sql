-- Title :             Database creation for L04EE02-SpringBoot-MicroServices: inventoryService
-- Version :           1.0
-- Creation date :     2024-01-31
-- Update date :       2023-01-31
-- Author :            Arthur Maquin
-- Description :       Database initialisation script for L04EE02-SpringBoot-MicroServices: inventoryService
--                     Note : Script for PostgreSQL

-- +----------------------------------------------------------------------------------------------+
-- | Create table                                                                                 |
-- +----------------------------------------------------------------------------------------------+

CREATE TABLE roles
(
    name VARCHAR(32) UNIQUE NOT NULL,
    is_enable BOOLEAN NOT NULL DEFAULT true,

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

-- Creation of 'inventories'
CREATE TABLE addresses (
    id_address SERIAL NOT NULL,
    number VARCHAR(31) NOT NULL,
    street VARCHAR (255) NOT NULL,
    city VARCHAR (63) NOT NULL,
    state VARCHAR (63) NOT NULL,
    country VARCHAR (63) NOT NULL,
    postal_code VARCHAR(15) NOT NULL,
    info TEXT,

    CONSTRAINT pk_addresses PRIMARY KEY (id_address)
);

CREATE TABLE products (
    id_product SERIAL NOT NULL,
    title VARCHAR(127) NOT NULL,
    description TEXT,

    CONSTRAINT pk_products PRIMARY KEY (id_product)
);

CREATE TABLE categories (
    id_category SERIAL NOT NULL,
    title VARCHAR(127) NOT NULL,
    description TEXT,

    CONSTRAINT pk_categories PRIMARY KEY (id_category)
);

CREATE TABLE product_meta (
    id_product_meta SERIAL NOT NULL,
    key VARCHAR(255) NOT NULL,
    type VARCHAR(15) NOT NULL,
    value TEXT NOT NULL,
    description TEXT,
    id_product INT NOT NULL,

    CONSTRAINT pk_product_meta PRIMARY KEY (id_product_meta),
    CONSTRAINT fk_product_meta_table_products
        FOREIGN KEY (id_product) REFERENCES products(id_product)
);

CREATE TABLE items (
    id_item SERIAL NOT NULL,
    quantity INT NOT NULL,
    id_address INT NOT NULL,
    id_product INT NOT NULL,

    CONSTRAINT pk_items PRIMARY KEY (id_item),
    CONSTRAINT fk_items_table_products
        FOREIGN KEY (id_product) REFERENCES products(id_product),
    CONSTRAINT fk_items_table_addresses
        FOREIGN KEY (id_address) REFERENCES addresses(id_address),
    CONSTRAINT positive_item_quantity
        CHECK (quantity >= 0 OR quantity is NULL)
);

CREATE TABLE transactions (
    id_transaction SERIAL NOT NULL,
    quantity INT NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT current_timestamp,
    employee CHAR(6) NOT NULL,
    id_item INT NOT NULL,

    CONSTRAINT pk_transactions PRIMARY KEY (id_transaction),
    CONSTRAINT fk_transactions_table_items
        FOREIGN KEY (id_item) REFERENCES items(id_item),
    CONSTRAINT employee_constraint CHECK (employee ~* '[A-Z][0-9]{5}')--,
);

CREATE TABLE joint_category_product (
    id_category INT,
    id_product INT,

    CONSTRAINT pk_joint_category_product PRIMARY KEY (id_category, id_product),
    CONSTRAINT fk_joint_category_product_table_categories
        FOREIGN KEY (id_category) REFERENCES categories(id_category),
    CONSTRAINT fk_joint_category_product_table_products
        FOREIGN KEY (id_product) REFERENCES products(id_product)
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
       ('admin', 'CanModifyRole'),
       ('admin', 'CanReadRole'),
       ('admin', 'CanReadInventories'),
       ('admin', 'CanManageInventories'),
       ('admin', 'CanCreateCategories'),

       ('user', 'CanReadInventories');

-- +----------------------------------------------------------------------------------------------+

INSERT INTO login_profiles (id)
VALUES ('A00001'),
       ('A00002');

-- +----------------------------------------------------------------------------------------------+

INSERT INTO categories (title, description)
VALUES ('Tous', 'Catégorie par défaut sur l''ERP'),
       ('Tissu', null),
       ('Fibre', null);


INSERT INTO products (title, description)
VALUES ('Nylon (tissu)', 'Unité en [cm] ; Utilisé pour le développement du produit A180'),
       ('Nylon(fibre)', 'Unité en [cm] ; Utilisé pour le développement du produit A180'),
       ('Kevlar', 'Unité en [cm] ; Utilisé pour le développement du produit D150'),
       ('Mylar', null),
       ('Kinder Bueno', null);


INSERT INTO joint_category_product (id_product, id_category)
VALUES (1, 1),
       (1, 2),
       (2, 1),
       (2, 3),
       (3, 1),
       (3, 2),
       (4, 1),
       (4, 2),
       (5, 1);


INSERT INTO addresses (number, street, city, state, country, postal_code, info)
VALUES ('162', 'Cannon Rd', 'Santa Venera', 'Malte', 'Malte', '9034', 'MaltaShopper Limited : on stocke les stagiaires dans des cartons'),
       ('10', 'Rue Louis veuillot', 'Brest', 'Bretage', 'France', '27000', 'Chalet'),
       ('VF2F+RQF', 'Ghadajma', 'Mqabba', 'Malte', 'Malte', 'indisponible', 'MaltaShopper Warehouse : Stocke les matières premières');

INSERT INTO items (quantity, id_address, id_product)
VALUES (850, 1, 1),
       (10110, 1, 2),
       (920, 3, 2),
       (7440, 3, 3),
       (1001, 2, 5);

INSERT INTO transactions(quantity, timestamp, employee, id_item)
VALUES (850, '2024-02-11 09:00:00', 'A00002', 1),
       (10110, '2024-02-11 10:00:00', 'A00002', 2),
       (920, '2024-02-11 09:00:00', 'A00002', 3),
       (7440, '2024-02-11 09:00:00', 'A00002', 4),
       (1001, '2024-02-11 09:00:00', 'A00002', 5);

INSERT INTO product_meta(key, type, value, description, id_product)
VALUES
    ('Largeur (cm)', 'float', '110.5', 'dpe < 95cm', 1),
    ('Epaisseur (mm)', 'float', '4.8', null, 2),
    ('Couleur', 'string', 'cyan', null, 2),
    ('Largeur (cm)', 'float', '60', null, 3);
