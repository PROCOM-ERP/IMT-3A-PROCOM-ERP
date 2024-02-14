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
    number VARCHAR(31),
    street VARCHAR (255),
    city VARCHAR (63) ,
    state VARCHAR (63),
    country VARCHAR (63),
    postal_code VARCHAR(15),
    info TEXT
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


INSERT INTO category_product (id_product, id_category)
VALUES (1, 1),
       (2, 1);


INSERT INTO addresses (number, street, city, state, country, postal_code, info)
VALUES ('15', 'entrepot de Malta Shopper', 'Malte', 'Valleta', 'Malte', '66666', 'je n''y reviendrai plus jamais !');

INSERT INTO items (quantity, id_address, id_product)
VALUES (10, 1, 1),
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