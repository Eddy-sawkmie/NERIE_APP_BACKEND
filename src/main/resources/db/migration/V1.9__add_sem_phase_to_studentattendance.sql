BEGIN;

------------------------------------------------
-- add columns only if not exists
------------------------------------------------
ALTER TABLE nerie.t_studentsattendance
ADD COLUMN IF NOT EXISTS attsemestercode VARCHAR(10);

ALTER TABLE nerie.t_studentsattendance
ADD COLUMN IF NOT EXISTS attsphaseid VARCHAR(10);
------------------------------------------------
-- add FK for semester only if not exists
------------------------------------------------
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_studentsattendance_semester'
        AND table_schema = 'nerie'
        AND table_name = 't_studentsattendance'
    ) THEN
        ALTER TABLE nerie.t_studentsattendance
        ADD CONSTRAINT fk_studentsattendance_semester
        
        FOREIGN KEY (attsemestercode)
        REFERENCES nerie.m_semesters (semestercode)

        ON UPDATE CASCADE
        ON DELETE RESTRICT;
    END IF;

END $$;


------------------------------------------------
-- add FK for short term phase only if not exists
------------------------------------------------

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_studentsattendance_sphase'
        AND table_schema = 'nerie'
        AND table_name = 't_studentsattendance'
    ) THEN
        ALTER TABLE nerie.t_studentsattendance
        ADD CONSTRAINT fk_studentsattendance_sphase

        FOREIGN KEY (attsphaseid)
        REFERENCES nerie.m_shortterm_phases (sphaseid)

        ON UPDATE CASCADE
        ON DELETE RESTRICT;
    END IF;

END $$;


COMMIT;