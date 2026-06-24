-- WARNING: DO NOT MODIFY THIS FILE

--Inserting role_id FK values in mt_userlogin table where role_id references mt_userloginrole(role_id)
UPDATE nerie.mt_userlogin
SET role_id = (SELECT role_id FROM nerie.mt_userloginrole WHERE mt_userlogin.userrole = mt_userloginrole.role_code);
