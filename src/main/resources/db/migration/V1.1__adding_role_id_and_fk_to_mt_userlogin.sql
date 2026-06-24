-- WARNING: DO NOT MODIFY THIS FILE

DO $$
BEGIN
    -- Check if the column exists before adding
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'nerie'
        AND table_name = 'mt_userlogin'
        AND column_name = 'role_id'
    ) THEN
        ALTER TABLE nerie.mt_userlogin
        ADD COLUMN role_id INT;
    END IF;
END $$;

DO $$
BEGIN
    -- Check if the foreign key constraint exists before adding
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE table_schema = 'nerie'
        AND table_name = 'mt_userlogin'
        AND constraint_name = 'fk_mt_userlogin_role'
    ) THEN
        ALTER TABLE nerie.mt_userlogin
        ADD CONSTRAINT fk_mt_userlogin_role
        FOREIGN KEY (role_id)
        REFERENCES nerie.mt_userloginrole (role_id)
        ON DELETE SET NULL;
    END IF;
END $$;
