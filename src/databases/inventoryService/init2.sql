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
    title VARCHAR(128),
    description TEXT
);

CREATE TABLE products (
    id_product SERIAL PRIMARY KEY,
    title VARCHAR(128),
    description TEXT
);

CREATE TABLE categories (
    id_category SERIAL PRIMARY KEY,
    title VARCHAR(128),
    description TEXT
);

CREATE TABLE product_meta (
    id_address SERIAL PRIMARY KEY,
    title VARCHAR(128),
    description TEXT
);

CREATE TABLE items (
);

CREATE TABLE transactions (
);



CREATE TABLE category_product (
);

CREATE TABLE product_meta_product (
);

CREATE TABLE addresses (
);
