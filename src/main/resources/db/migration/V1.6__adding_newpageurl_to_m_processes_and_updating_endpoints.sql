-- WARNING: DO NOT MODIFY THIS FILE

-- Adds the 'newpageurl' column to the 'nerie.m_processes' table
-- and populates it with new URL values based on the 'processcode'.

-- Step 1: Add the new column 'newpageurl' to the table if it does not exist.
-- The column is of type character varying with a length of 100.
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'nerie'
        AND table_name = 'm_processes'
        AND column_name = 'newpageurl'
    ) THEN
        ALTER TABLE nerie.m_processes
        ADD COLUMN newpageurl VARCHAR(100);
    END IF;
END $$;

-- Step 2: Update the newly added 'newpageurl' column.
-- A CASE statement is used to set the value of 'newpageurl' based on the
-- existing 'processcode' for each row. This is more efficient than
-- running individual UPDATE statements for each process code.
UPDATE nerie.m_processes
SET newpageurl = CASE processcode
    WHEN 1 THEN '/nerie/users/manage'
    WHEN 2 THEN '/nerie/program/manage'
    WHEN 3 THEN '/nerie/venues/manage'
    WHEN 4 THEN '/nerie/course-categories/manage'
    WHEN 5 THEN '/nerie/designations/manage'
    WHEN 6 THEN '/nerie/participant/manage'
    WHEN 7 THEN '/nerie/program/principal-director/manage'
    WHEN 8 THEN '/nerie/resource-persons/create'
    WHEN 9 THEN '/nerie/resource-persons/map'
    WHEN 10 THEN '/nerie/reports/report-participant-resource-list'
    WHEN 11 THEN '/nerie/program-materials/manage'
    WHEN 12 THEN '/nerie/participant/attendance/manage'
    WHEN 13 THEN '/nerie/qualifications/manage'
    WHEN 14 THEN '/nerie/qualification-subjects/manage'
    WHEN 15 THEN '/nerie/qualification-subjects/map'
    WHEN 16 THEN '/nerie/timetable/program-timetable/create'
    WHEN 17 THEN '/nerie/program/close'
    WHEN 18 THEN '/nerie/venue-rooms/manage'
    WHEN 19 THEN '/nerie/program/reopen'
    WHEN 22 THEN '/nerie/holidays/init'
    WHEN 24 THEN '/nerie/reports/report-program-schedule'
    WHEN 25 THEN '' -- No new URL provided for this processcode
    WHEN 28 THEN '/nerie/subjects/create-academic-subjects'
    WHEN 29 THEN '/nerie/course-categories/create-academic-courses'
    WHEN 30 THEN '/nerie/faculties/register-faculties'
    WHEN 31 THEN '/nerie/students/manage'
    WHEN 32 THEN '/nerie/alumni/manage'
    WHEN 33 THEN '/nerie/reports/report-attendance-list'
    WHEN 34 THEN '/nerie/students/promotion-student-list'
    WHEN 35 THEN '/nerie/attendance/upload-attendance'
    WHEN 36 THEN '/nerie/attendance/view-student-attendance'
    WHEN 37 THEN '/nerie/study-materials/upload-study-materials'
    WHEN 38 THEN '/nerie/tests/create-tests'
    WHEN 39 THEN '/nerie/internal-evaluation-marks/upload-internal-evaluation-marks'
    WHEN 40 THEN '/nerie/assignments/upload-assignment'
    WHEN 41 THEN '/nerie/feedbacks/view-student-feedback'
    WHEN 42 THEN '/nerie/notifications/manage'
    WHEN 43 THEN '' -- No new URL provided for this processcode
    WHEN 44 THEN '/nerie/student-leaves/approve-student-leave'
    WHEN 45 THEN '/nerie/student-leaves/student-leave-reports'
    WHEN 46 THEN '/nerie/pre-post-test/create'
    ELSE newpageurl -- Keep the existing value (NULL) for any other processcodes
END;