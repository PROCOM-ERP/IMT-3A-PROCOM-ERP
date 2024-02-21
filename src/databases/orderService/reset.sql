-- Title :             Database reset for T02EE01-GitHub-Actions project
-- Version :           1.0
-- Creation date :     2023-11-16
-- Update date :       2023-11-16
-- Author :            Thibaut RUZICKA
-- Description :       Database reset script for T02EE01-GitHub-Actions
--                     Note : Script for PostgreSQL

-- +----------------------------------------------------------------------------------------------+
-- | Drop table                                                                                   |
-- +----------------------------------------------------------------------------------------------+

DO
$$
    DECLARE
        table_name text;
        seq_name   text;
    BEGIN
        -- Drop all tables
        FOR table_name IN (SELECT tablename FROM pg_tables WHERE schemaname = 'public')
            LOOP
                EXECUTE 'DROP TABLE IF EXISTS ' || table_name || ' CASCADE';
            END LOOP;

        -- Drop all sequences
        FOR seq_name IN (SELECT sequence_name FROM information_schema.sequences WHERE sequence_schema = 'public')
            LOOP
                EXECUTE 'DROP SEQUENCE IF EXISTS ' || seq_name || ' CASCADE';
            END LOOP;
    END
$$;
