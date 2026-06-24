-- WARNING: DO NOT MODIFY THIS FILE

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'nerie'
        AND table_name = 'mt_userlogin'
        AND column_name = 'iscoordinator'
    ) THEN
        ALTER TABLE nerie.mt_userlogin
        ADD COLUMN iscoordinator VARCHAR(1) DEFAULT '0';
    END IF;
END $$;

-- 2. First, reset all 'U' role users to 0 for both flags
UPDATE nerie.mt_userlogin
SET iscoordinator = '0',
    isfaculty = '0'
WHERE userrole = 'U';

-- 3. Set iscoordinator = '1' if their description contains "Coordinator"
UPDATE nerie.mt_userlogin
SET iscoordinator = '1'
WHERE userrole = 'U'
  AND userdescription ILIKE '%Coordinator%';

-- 4. Set isfaculty = '1' if their description contains "Faculty" or "Professor"
UPDATE nerie.mt_userlogin
SET isfaculty = '1'
WHERE userrole = 'U'
  AND (userdescription ILIKE '%Faculty%' OR userdescription ILIKE '%Professor%');

-- 5. For roles A, S, Z, T, P explicitly set iscoordinator to 0
UPDATE nerie.mt_userlogin
SET iscoordinator = '0'
WHERE userrole IN ('A', 'S', 'Z', 'T', 'P');

-- 6. Fix missing officecode for specific user (Baiawan)
UPDATE nerie.mt_userlogin
SET officecode = '2'
WHERE usercode = '47';

-- 7. Update description and roles for users where iscurrentfaculty is '1'
UPDATE nerie.mt_userlogin
SET userdescription = 'Coordinator & Faculty',
    isfaculty = '1',
    iscoordinator = '1'
WHERE iscurrentfaculty = '1';