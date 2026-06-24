BEGIN;

    ALTER TABLE m_offices
        ADD COLUMN longtermcoursecode VARCHAR(3),
        ADD COLUMN shorttermcoursecode VARCHAR(2);

    --FOR NERIE NCERT, Shillong
    UPDATE nerie.m_offices 
        SET longtermcoursecode='NER',
        shorttermcoursecode='NE'
        WHERE officecode='2';

    --FOR Regional Institute Of Education(RIE), MYSORE
    UPDATE nerie.m_offices 
        SET longtermcoursecode='MYS',
        shorttermcoursecode='MY'
        WHERE officecode='3';

    --FOR Regional Institute of Education(RIE), BHOPAL.
    UPDATE nerie.m_offices 
        SET longtermcoursecode='BHO',
        shorttermcoursecode='BL'
        WHERE officecode='4';

    --FOR Regional Institute Of Education, Ajmer.
    UPDATE nerie.m_offices 
        SET longtermcoursecode='AJM',
        shorttermcoursecode='AJ'
        WHERE officecode='5';
    
    --FOR Regional Institute Of Education(RIE) BHUBANESWAR.
    UPDATE nerie.m_offices 
        SET longtermcoursecode='BHU',
        shorttermcoursecode='BR'
        WHERE officecode='6';
    
    --FOR National Council of Education Research and Training(NCERT), NEW DELHI.
    UPDATE nerie.m_offices 
        SET longtermcoursecode='NCE',
        shorttermcoursecode='NC'
        WHERE officecode='7';

    --FOR Central Institute of Education Technology(CIET)
    UPDATE nerie.m_offices 
        SET longtermcoursecode='CIE',
        shorttermcoursecode='CI'
        WHERE officecode='8';

    --FOR Pandit Sunderlal Sharma Central Institute of Vocational Education(PSSCIVE)
    UPDATE nerie.m_offices 
        SET longtermcoursecode='PSS',
        shorttermcoursecode='PS'
        WHERE officecode='9';

COMMIT;