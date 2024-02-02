-- Title :             Database creation for L04EE02-SpringBoot-MicroServices: stockService
-- Version :           1.0
-- Creation date :     2023-12-06
-- Update date :       2023-12-06
-- Author :            Arthur Maquin
-- Description :       Database initialisation script for L04EE02-SpringBoot-MicroServices: stockService
--                     Note : Script for PostgreSQL

-- +----------------------------------------------------------------------------------------------+
-- | Create table                                                                                 |
-- +----------------------------------------------------------------------------------------------+

-- Creation of 'inventories'
CREATE TABLE inventories (
    id SERIAL PRIMARY KEY,
    group_id INT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    isActive BOOLEAN
);

CREATE TABLE items (
    id SERIAL PRIMARY KEY,
    inventory_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    alias VARCHAR(255),
    status VARCHAR(255),
    arrival_date DATE NOT NULL,
    removal_date DATE,
    FOREIGN KEY (inventory_id) REFERENCES inventories(id)
);

CREATE TABLE attributes (
    id SERIAL PRIMARY KEY,
    item_id INT NOT NULL,
    attribute_name_id INT NOT NULL,
    type VARCHAR(255),
    string_attribute VARCHAR(255),
    int_attribute VARCHAR(255),
    local_date_attribute VARCHAR(255),
    text_attribute VARCHAR(255),
    FOREIGN KEY (item_id) REFERENCES items(id)
);

CREATE TABLE inventory_groups (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    description VARCHAR(255),
    isActive BOOLEAN
);

CREATE TABLE attribute_names (
    id SERIAL PRIMARY KEY,
    inventory_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    string_value VARCHAR(255) NOT NULL,
    int_value INTEGER NOT NULL,
    local_date_value VARCHAR NOT NULL CHECK (local_date_value ~
                                             '^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}$'),
                                            -- This checks if the string is like 2023-09-25T09:55:30
    text_value VARCHAR(255) NOT NULL,
    FOREIGN KEY (inventory_id) REFERENCES inventories(id)
);


-- Test
/*
INSERT INTO items (inventory_id, name, alias, arrival_date)
VALUES (1, 'Arthur ambulant', 'Respo goûter', '2023-01-01');

INSERT INTO int_attributes (inventory_id, name, value)
VALUES (1, 'Humour en Kilo', 100, 1); --*/
