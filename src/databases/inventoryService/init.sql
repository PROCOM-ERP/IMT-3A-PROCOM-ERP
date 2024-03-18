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

INSERT INTO roles (name, is_enable)
VALUES ('admin', true),
       ('user', true);

-- +----------------------------------------------------------------------------------------------+

INSERT INTO role_permissions (role, permission)
VALUES ('admin', 'CanBypassAccessDeny'),
       ('admin', 'CanModifyRole'),
       ('admin', 'CanReadRole');

-- +----------------------------------------------------------------------------------------------+

INSERT INTO login_profiles (id)
VALUES ('A00001'), ('A00002'), ('A00003'), ('A00004'), ('A00005'),
       ('A00006'), ('A00007'), ('A00008'), ('A00009'), ('A00010'),
       ('A00011'), ('A00012'), ('A00013'), ('A00014'), ('A00015'),
       ('A00016'), ('A00017'), ('A00018'), ('A00019'), ('A00020'),
       ('A00021'), ('A00022'), ('A00023'), ('A00024'), ('A00025'),
       ('A00026'), ('A00027'), ('A00028'), ('A00029'), ('A00030');

-- +----------------------------------------------------------------------------------------------+

INSERT INTO categories (title, description)
VALUES ('test_category', 'test_description');


INSERT INTO products (title, description)
VALUES ('Nordinateur', 'gaming'),
       ('Carte graphique', 'très performante'),
       ('Chips', 'très craquantes');


INSERT INTO joint_category_product (id_product, id_category)
VALUES (1, 1),
       (2, 1);


INSERT INTO addresses (number, street, city, state, country, postal_code, info)
VALUES ('15', 'entrepot de Malta Shopper', 'Malte', 'Valleta', 'Malte', '66666', 'je n''y reviendrai plus jamais !'),
       ('100', 'Rue Louis veuillot', 'Brest', 'Bretage', 'France', '27000', '@ du challet');

INSERT INTO items (quantity, id_address, id_product)
VALUES (12, 1, 1),
       (10, 1, 2);

INSERT INTO transactions(quantity, timestamp, employee, id_item)
VALUES (10, '2024-02-11 09:00:00', 'A00001', 1),
       (2, '2024-02-11 10:00:00', 'A00002', 1);

INSERT INTO product_meta(key, type, value, description, id_product)
VALUES
('Volume', 'float', '1,553', 'Bière', 1),
('Quantité de gras', 'integer', '10', 'Sauciflard', 1),
('Poids', 'float', '168,4', 'Trop de BF', 1)

/*
SELECT * FROM addresses;

SELECT * FROM products;

SELECT * FROM categories;

SELECT * FROM items;

SELECT * FROM transactions;

SELECT * FROM category_product;

*/
