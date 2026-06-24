-- WARNING: DO NOT MODIFY THIS FILE

--Name will be V1.8__mvc_db_updates
BEGIN;
--1. Update nerie.m_processes

UPDATE nerie.m_processes
SET newpageurl = REPLACE(newpageurl, '/nerie', '')
WHERE newpageurl IS NOT NULL
  AND newpageurl <> ''
  AND newpageurl LIKE '%/nerie%';

--we dont begin with /nerie anymore

---------------------------------------------------------

--2. Update T_Application
ALTER TABLE nerie.t_applications
ADD  COLUMN IF NOT EXISTS  name varchar(200),
ADD  COLUMN IF NOT EXISTS designation varchar(200),
ADD  COLUMN IF NOT EXISTS educationalqualification varchar(200),
ADD  COLUMN IF NOT EXISTS experience varchar(30),
ADD  COLUMN IF NOT EXISTS gender varchar(10),
ADD  COLUMN IF NOT EXISTS addressoffice varchar(200),
ADD  COLUMN IF NOT EXISTS addressresidence varchar(200),
ADD  COLUMN IF NOT EXISTS contactno varchar(15),
ADD  COLUMN IF NOT EXISTS emailid varchar(60),
ADD  COLUMN IF NOT EXISTS localityregion varchar(20),
ADD  COLUMN IF NOT EXISTS category varchar(30),
ADD  COLUMN IF NOT EXISTS religiousminority varchar(30),
ADD  COLUMN IF NOT EXISTS religiousminorityname varchar(30);
---------------------------------------------------------

--3. T_Participant differentlyableddetails

ALTER TABLE nerie.t_participants
ADD COLUMN IF NOT EXISTS differentlyableddetails varchar(300);
---------------------------------------------------------
COMMIT;