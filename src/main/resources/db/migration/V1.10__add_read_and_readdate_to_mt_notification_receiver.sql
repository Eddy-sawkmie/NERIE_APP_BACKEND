BEGIN;

    ALTER TABLE mt_notification_receiver
    ADD COLUMN is_read BOOLEAN NOT NULL DEFAULT FALSE;

    ALTER TABLE mt_notification_receiver
    ADD COLUMN read_date TIMESTAMP;

COMMIT;

