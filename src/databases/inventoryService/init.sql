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

-- Creation of 'inventories'
CREATE TABLE addresses (
    id_address SERIAL PRIMARY KEY,
    title VARCHAR(128) NOT NULL,
    description TEXT
);

CREATE TABLE products (
    id_product SERIAL PRIMARY KEY ,
    title VARCHAR(128) NOT NULL,
    description TEXT
);

CREATE TABLE categories (
    id_category SERIAL PRIMARY KEY,
    title VARCHAR(128) NOT NULL,
    description TEXT
);

CREATE TABLE product_meta (
    id_product_meta SERIAL PRIMARY KEY,
    key VARCHAR(255) NOT NULL,
    type VARCHAR(16) NOT NULL,
    value TEXT NOT NULL,
    description TEXT,
    id_product INT NOT NULL,
    FOREIGN KEY (id_product) REFERENCES products(id_product)
);

CREATE TABLE items (
    id_item SERIAL PRIMARY KEY,
    quantity INT NOT NULL,
    id_address INT NOT NULL,
    id_product INT NOT NULL,
    FOREIGN KEY (id_product) REFERENCES products(id_product),
    FOREIGN KEY (id_address) REFERENCES addresses(id_address)
);

CREATE TABLE transactions (
    id_transaction SERIAL PRIMARY KEY,
    quantity INT NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT current_timestamp,
    employee CHAR(6) NOT NULL,
    id_item INT NOT NULL,
    FOREIGN KEY (id_item) REFERENCES items(id_item),

    CONSTRAINT employee_constraint CHECK (employee ~* '[A-Z][0-9]{5}')--,
);

CREATE TABLE category_product (
    id_category INT,
    id_product INT,
    PRIMARY KEY (id_category, id_product),
    FOREIGN KEY (id_category) REFERENCES categories(id_category),
    FOREIGN KEY (id_product) REFERENCES products(id_product)
);

INSERT INTO categories (title, description)
VALUES ('test_category', 'test_description');


INSERT INTO products (title, description)
VALUES ('Nordinateur', 'gaming'),
       ('Carte graphique', 'très performante'),
       ('Chips', 'très craquantes');

/*
INSERT INTO category_product (id_product, id_category)
VALUES (1, 1),
       (2, 1);

INSERT INTO items (quantity, id_address, id_product)
VALUES (10, 1, 1);

INSERT INTO addresses (title, description)
VALUES ('entrepot de Malta Shopper', 'tkt');

SELECT * FROM addresses;

SELECT * FROM products;

SELECT * FROM categories;

SELECT * FROM items;

SELECT * FROM transactions;

SELECT * FROM category_product;

*/
