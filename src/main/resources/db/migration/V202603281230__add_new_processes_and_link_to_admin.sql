BEGIN;

    INSERT INTO m_processes(
        processcode, processname, modulecode, pageurl, menuname, mainmenucode, newpageurl)
        VALUES (47, 'Participant List', 1, '', 'Participant List', 4, '/participants');
    
    INSERT INTO m_processes(
        processcode, processname, modulecode, pageurl, menuname, mainmenucode, newpageurl)
        VALUES (48, 'Resource Person List', 1, '', 'Resource Person List', 4, '/resource-persons');

    INSERT INTO mt_role_process(
        role_id, processcode)
        VALUES (1, 47);

    INSERT INTO mt_role_process(
        role_id, processcode)
        VALUES (1, 48);

COMMIT;