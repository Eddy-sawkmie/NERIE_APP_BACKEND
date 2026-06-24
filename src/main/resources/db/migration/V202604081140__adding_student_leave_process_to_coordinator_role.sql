-- WARNING: DO NOT MODIFY THIS FILE

DO $$
BEGIN
    -- ==========================================
    -- 1. Coordinator (role_id = 7)
    -- Add access to Student Leave (processcode = 44)
    -- ==========================================
    IF NOT EXISTS (
        SELECT 1 FROM nerie.mt_role_process
        WHERE role_id = 7 AND processcode = 44
    ) THEN
        INSERT INTO nerie.mt_role_process (role_id, processcode)
        VALUES (7, 44);
    END IF;

END $$;