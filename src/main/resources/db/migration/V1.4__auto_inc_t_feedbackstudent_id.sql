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
        AND c.relname = 'feedbackid_seq'
        AND n.nspname = 'nerie'
    ) THEN
        CREATE SEQUENCE nerie.feedbackid_seq
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
ALTER TABLE nerie.t_feedback_student
ALTER COLUMN feedbackid
SET DEFAULT nextval('nerie.feedbackid_seq'::regclass);

---------------------------------------------------------
-- CLEANUP: drop legacy trigger/function to prevent 
-- the sequence from double-incrementing
---------------------------------------------------------
DROP TRIGGER IF EXISTS set_feedbackid_trigger ON nerie.t_feedback_student;
DROP FUNCTION IF EXISTS nerie.set_feedbackid();

---------------------------------------------------------
-- sync sequence with existing data to prevent collisions
---------------------------------------------------------
DO $$
BEGIN
    PERFORM setval(
        'nerie.feedbackid_seq', 
        COALESCE((SELECT MAX(CAST(feedbackid AS INTEGER)) FROM nerie.t_feedback_student), 1)
    );
END $$;