INSERT INTO nerie.mt_role_process (role_id, processcode)
SELECT 
    1,
    p.processcode
FROM unnest(ARRAY[
        1,2,3,4,5,6,8,9,10,11,12,13,14,15,16,17,18,19,
        22,24,28,29,30,31,32,33,34,44,46
     ]) AS p(processcode)
WHERE NOT EXISTS (
    SELECT 1
    FROM nerie.mt_role_process m
    WHERE m.role_id = 1
      AND m.processcode = p.processcode
);