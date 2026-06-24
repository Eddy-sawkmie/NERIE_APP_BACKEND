-- WARNING: DO NOT MODIFY THIS FILE

DO $$
BEGIN
    -- Check if table exists before creating
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.tables
        WHERE table_schema = 'nerie'
        AND table_name = 'mt_userloginrole'
    ) THEN
        CREATE TABLE nerie.mt_userloginrole (
            role_id INTEGER PRIMARY KEY,
            role_name VARCHAR(25) NOT NULL,
            role_code VARCHAR(2) UNIQUE NOT NULL
        );
    END IF;
END $$;

-- Insert values only if they don't exist
INSERT INTO nerie.mt_userloginrole(role_id, role_name, role_code)
SELECT 1, 'LOCAL_ADMIN', 'A'
WHERE NOT EXISTS (SELECT 1 FROM nerie.mt_userloginrole WHERE role_id = 1);

INSERT INTO nerie.mt_userloginrole(role_id, role_name, role_code)
SELECT 2, 'ADMIN', 'S'
WHERE NOT EXISTS (SELECT 1 FROM nerie.mt_userloginrole WHERE role_id = 2);

INSERT INTO nerie.mt_userloginrole(role_id, role_name, role_code)
SELECT 3, 'PARTICIPANT', 'P'
WHERE NOT EXISTS (SELECT 1 FROM nerie.mt_userloginrole WHERE role_id = 3);

INSERT INTO nerie.mt_userloginrole(role_id, role_name, role_code)
SELECT 4, 'PRINCIPAL_DIRECTOR', 'Z'
WHERE NOT EXISTS (SELECT 1 FROM nerie.mt_userloginrole WHERE role_id = 4);

INSERT INTO nerie.mt_userloginrole(role_id, role_name, role_code)
SELECT 5, 'COORDINATOR_FACULTY', 'U'
WHERE NOT EXISTS (SELECT 1 FROM nerie.mt_userloginrole WHERE role_id = 5);

INSERT INTO nerie.mt_userloginrole(role_id, role_name, role_code)
SELECT 6, 'STUDENT', 'T'
WHERE NOT EXISTS (SELECT 1 FROM nerie.mt_userloginrole WHERE role_id = 6);

