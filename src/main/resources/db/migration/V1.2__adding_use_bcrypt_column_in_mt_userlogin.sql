-- WARNING: DO NOT MODIFY THIS FILE

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'nerie'
        AND table_name = 'mt_userlogin'
        AND column_name = 'use_bcrypt'
    ) THEN
        ALTER TABLE nerie.mt_userlogin
        ADD COLUMN use_bcrypt BOOLEAN DEFAULT FALSE;
    END IF;
END $$;
