-- WARNING: DO NOT MODIFY THIS FILE

---------------------------------------------------------
-- create sequence if not exists (schema aware)
---------------------------------------------------------
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_class c
        JOIN pg_namespace n
             ON n.oid = c.relnamespace

        WHERE c.relkind = 'S'
        AND c.relname = 'studentleaveid_seq'
        AND n.nspname = 'nerie'

    ) THEN
        CREATE SEQUENCE nerie.studentleaveid_seq
        START WITH 1
        INCREMENT BY 1
        NO MINVALUE
        NO MAXVALUE
        CACHE 1;
    END IF;
END $$;

---------------------------------------------------------
-- attach sequence as default value
---------------------------------------------------------
ALTER TABLE nerie.t_studentleave
ALTER COLUMN studentleaveid
SET DEFAULT nextval('nerie.studentleaveid_seq'::regclass);

---------------------------------------------------------
-- CLEANUP: drop legacy trigger/function to prevent 
-- the sequence from double-incrementing
---------------------------------------------------------
DROP TRIGGER IF EXISTS set_studentleaveid_trigger ON nerie.t_studentleave;
DROP FUNCTION IF EXISTS nerie.set_studentleaveid();

---------------------------------------------------------
-- sync sequence with existing data to prevent collisions
---------------------------------------------------------
DO $$
BEGIN
    PERFORM setval(
        'nerie.studentleaveid_seq', 
        COALESCE((SELECT MAX(CAST(studentleaveid AS INTEGER)) FROM nerie.t_studentleave), 1)
    );
END $$;