-- WARNING: DO NOT MODIFY THIS FILE

-- This script iterates through all sequences in the 'nerie' schema
-- and resets their value to the maximum ID of the corresponding table column.
-- This is necessary after a pg_restore that doesn't update sequences.
-- SFG -> Straight From Gemini

SELECT 'SELECT SETVAL(' ||
       quote_literal(quote_ident(seq_ns.nspname) || '.' || quote_ident(seq.relname)) ||
       ', COALESCE(MAX(' || quote_ident(col.attname) || '), 1) ) FROM ' ||
       quote_ident(tbl_ns.nspname) || '.' || quote_ident(tbl.relname) || ';'
FROM pg_class AS seq
JOIN pg_namespace AS seq_ns ON (seq.relnamespace = seq_ns.oid)
JOIN pg_depend AS dep ON (seq.oid = dep.objid)
JOIN pg_class AS tbl ON (dep.refobjid = tbl.oid)
JOIN pg_namespace AS tbl_ns ON (tbl.relnamespace = tbl_ns.oid)
JOIN pg_attribute AS col ON (dep.refobjid = col.attrelid AND dep.refobjsubid = col.attnum)
WHERE seq.relkind = 'S' AND seq_ns.nspname = 'nerie'
GROUP BY seq_ns.nspname, seq.relname, tbl_ns.nspname, tbl.relname, col.attname;