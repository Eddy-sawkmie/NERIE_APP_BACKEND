package com.nic.nerie.mt_programdetails.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.nic.nerie.mt_programdetails.model.MT_ProgramDetails;

public interface MT_ProgramDetailsRepository extends JpaRepository<MT_ProgramDetails, String> {
        // Query to fetch distinct financial years based on finalized programs for an
        // office
        @Query(value = "SELECT DISTINCT " +
                        "CASE WHEN TO_CHAR(pd.startdate,'MM')>='01' AND TO_CHAR(pd.startdate,'MM')<='03' " +
                        "THEN (DATE_PART('year',pd.startdate)-1) || '-04##' || TO_CHAR(pd.startdate,'yyyy') || '-03' " +
                        "WHEN TO_CHAR(pd.startdate,'MM')>='04' AND TO_CHAR(pd.startdate,'MM')<='12' " +
                        "THEN TO_CHAR(pd.startdate,'yyyy') ||'-04##'|| DATE_PART('year',pd.startdate)+1 || '-03' END AS key, "
                        +
                        "CASE WHEN TO_CHAR(pd.startdate,'MM')>='01' AND TO_CHAR(pd.startdate,'MM')<='03' " +
                        "THEN (DATE_PART('year',pd.startdate)-1) ||'-'|| TO_CHAR(pd.startdate,'yy') " +
                        "WHEN TO_CHAR(pd.startdate,'MM')>='04' AND TO_CHAR(pd.startdate,'MM')<='12' " +
                        "THEN TO_CHAR(pd.startdate,'yyyy') ||'-'|| SUBSTRING(DATE_PART('year',pd.startdate)+1 || '',3) END AS value "
                        +
                        "From nerie.mt_programdetails pd, nerie.m_programs p, nerie.m_phases ph " +
                        "WHERE pd.finalized='Y' " +
                        "  AND p.officecode = :officecode " +
                        "  AND pd.programcode = p.programcode " +
                        "  AND ph.phaseid = pd.phaseid " +
                        "  AND pd.startdate IS NOT NULL " +
                        "ORDER BY value", nativeQuery = true)
        List<Object[]> findDistinctFinancialYearsByOfficeCode(@Param("officecode") String officecode);

        @Query(value = """
                SELECT C.phaseid,
                       C.enddate - C.startdate + 1 AS noofdays,
                       C.ttfinalized,
                       TT.programtimetablecode,
                       TO_CHAR(TT.programdate, 'dd/MM/yyyy') AS programdate,
                       TT.programday,
                       TO_CHAR(starttime, 'HH12') || ':' || TO_CHAR(starttime, 'MI') || ' ' || TO_CHAR(starttime, 'AM') AS starttime,
                       TO_CHAR(endtime, 'HH12') || ':' || TO_CHAR(endtime, 'MI') || ' ' || TO_CHAR(endtime, 'AM') AS endtime,
                       TT.subject,
                       STRING_AGG(R.rpslno, ',') AS rpslnos,
                       STRING_AGG(R.rpname, ',') AS rpnames,
                       TT.usercode,
                       TT.entrydate,
                       V.venuename,
                       V.venuecode,
                       VR.roomcode,
                       VR.roomname,
                       C.programcode 
                FROM nerie.mt_programdetails C
                LEFT OUTER JOIN nerie.t_programtimetable TT ON C.phaseid = TT.phaseid
                LEFT OUTER JOIN nerie.mt_venuerooms VR ON VR.roomcode = TT.roomcode
                LEFT OUTER JOIN nerie.m_venues V ON V.venuecode = VR.venuecode
                LEFT OUTER JOIN nerie.mt_programttresourceperson ttrp ON ttrp.programtimetablecode = TT.programtimetablecode
                LEFT OUTER JOIN nerie.mt_resourcepersons R ON R.rpslno = ttrp.rpslno
                WHERE C.phaseid = :phaseid
                  AND C.finalized != 'R'
                  AND TT.programday = :programday
                GROUP BY C.phaseid, C.enddate, C.startdate, C.ttfinalized, TT.programtimetablecode, V.venuename, V.venuecode, VR.roomcode, C.programcode
                ORDER BY TT.programday, TT.starttime
                """, nativeQuery = true)
        List<Object[]> getProgramTimetableDetailsByPhaseidAndProgramday(@Param("phaseid") String phaseid,
                        @Param("programday") Integer programday);

        @Query("SELECT CAST(pd.programdetailid as int) FROM MT_ProgramDetails pd " +
                        "WHERE pd.programcode.programcode =:programcode AND pd.phaseid.phaseid = :phaseid")
        Integer getProgramdetailidByProgramcodeAndPhaseid(@Param("programcode") String programcode,
                        @Param("phaseid") String phaseid);

        @Query("SELECT MAX(CAST(p.programdetailid as int)) FROM MT_ProgramDetails p")
        Integer getLastUsedProgramdetailid();

        @Query(value = """
                        SELECT TO_CHAR(Z.days, 'dd/MM/yyyy') AS days,
                                TO_CHAR(Z.days, 'dd/MM/yyyy') || ' - Day ' || ROW_NUMBER() OVER(ORDER BY Z.days) AS value,
                                ROW_NUMBER() OVER(ORDER BY Z.days) AS key
                        FROM (
                                SELECT generate_series(startdate, enddate, '1 day') AS days
                                FROM nerie.mt_programdetails C
                                WHERE C.phaseid = :phaseid
                        ) Z
                        WHERE EXTRACT(DOW FROM Z.days) != 0
                        AND Z.days NOT IN (SELECT holidaydate FROM nerie.m_holidays)
                        """, nativeQuery = true)
        List<Object[]> getProgramDaysByPhaseid(@Param("phaseid") String phaseid);

        @Query(value = """
                        SELECT DISTINCT
                            CASE
                                WHEN TO_CHAR(C.startdate,'MM') >= '01' AND TO_CHAR(C.startdate,'MM') <= '03'
                                    THEN (DATE_PART('year',C.startdate)-1) || '-04##' || TO_CHAR(C.startdate,'yyyy') || '-03'
                                WHEN TO_CHAR(C.startdate,'MM') >= '04' AND TO_CHAR(C.startdate,'MM') <= '12'
                                    THEN TO_CHAR(C.startdate,'yyyy') || '-04##' || DATE_PART('year',C.startdate)+1 || '-03'
                            END AS key,
                            CASE
                                WHEN TO_CHAR(C.startdate,'MM') >= '01' AND TO_CHAR(C.startdate,'MM') <= '03'
                                    THEN (DATE_PART('year',C.startdate)-1) || '-' || TO_CHAR(C.startdate,'yy')
                                WHEN TO_CHAR(C.startdate,'MM') >= '04' AND TO_CHAR(C.startdate,'MM') <= '12'
                                    THEN TO_CHAR(C.startdate,'yyyy') || '-' || SUBSTRING(DATE_PART('year',C.startdate)+1 || '',3)
                            END AS value
                        FROM nerie.mt_programdetails C, nerie.m_programs M
                        WHERE M.officecode = :officecode
                            AND M.closed = 'N'
                            --AND TO_CHAR(C.enddate,'yyyy-MM-dd') < TO_CHAR(now(),'yyyy-MM-dd')
                            AND C.finalized != 'R'
                            AND M.programcode = C.programcode
                        ORDER BY value
                        """, nativeQuery = true)
        List<Object[]> findDistinctFYOfUnclosedCourseByOfficeCode(@Param("officecode") String officecode);

        @Transactional
        @Modifying
        @Query(value = "UPDATE nerie.mt_programdetails SET closed = 'Y', closingreport = :closingreport WHERE phaseid = :phaseid", nativeQuery = true)
        int closePhase(@Param("phaseid") String phaseid, @Param("closingreport") String closingreport);

        // Unused method
        @Transactional
        @Modifying
        @Query(value = "UPDATE nerie.mt_programdetails SET closed = 'N' WHERE programdetailid = :programdetailid", nativeQuery = true)
        int reopenPhase(@Param("programdetailid") String programdetailid);

        @Query(value = "SELECT * FROM nerie.mt_programdetails WHERE programcode = :programcode ORDER BY programdetailid", nativeQuery = true)
        List<MT_ProgramDetails> getProgramDetailsByProgramCode(@Param("programcode") String programcode);

        @Query(value = "SELECT DISTINCT P.programcode, " +
                        "P.programname || '(' || TO_CHAR(pd.startdate, 'dd/MM/yyyy') || '-' || " +
                        "TO_CHAR(pd.enddate, 'dd/MM/yyyy') || ')' AS programname, " +
                        "ph.phaseno, ph.phaseid, ph.phasedescription, pd.entrydate " +
                        "FROM nerie.m_programs P " +
                        "JOIN nerie.mt_programdetails pd ON P.programcode = pd.programcode " +
                        "JOIN nerie.m_phases ph ON ph.phaseid = pd.phaseid AND P.programcode = ph.programcode " +
                        "WHERE P.officecode = :officecode " +
                        "AND pd.finalized != 'R' " +
                        "AND P.closed != 'Y' " +
                        "AND TO_CHAR(pd.startdate, 'yyyy-MM') BETWEEN :fystart AND :fyend " +
                        "ORDER BY pd.entrydate DESC", nativeQuery = true)
        List<Object[]> findForRoleAorZ(@Param("officecode") String officecode,
                        @Param("fystart") String fystart,
                        @Param("fyend") String fyend);

        @Query(value = "SELECT DISTINCT P.programcode, " +
                        "P.programname || '(' || TO_CHAR(pd.startdate, 'dd/MM/yyyy') || '-' || " +
                        "TO_CHAR(pd.enddate, 'dd/MM/yyyy') || ')' AS programname, " +
                        "ph.phaseno, ph.phaseid, ph.phasedescription, pd.entrydate " +
                        "FROM nerie.m_programs P " +
                        "JOIN nerie.mt_programdetails pd ON P.programcode = pd.programcode " +
                        "JOIN nerie.m_phases ph ON ph.phaseid = pd.phaseid AND P.programcode = ph.programcode " +
                        "JOIN nerie.mt_program_members PC ON pd.phaseid = PC.phaseid AND PC.usercode = :usercode " +
                        "WHERE P.officecode = :officecode " +
                        "AND pd.finalized != 'R' " +
                        "AND P.closed != 'Y' " +
                        "AND TO_CHAR(pd.startdate, 'yyyy-MM') BETWEEN :fystart AND :fyend " +
                        "ORDER BY pd.entrydate DESC", nativeQuery = true)
        List<Object[]> findForMember(@Param("officecode") String officecode,
                        @Param("usercode") String usercode,
                        @Param("fystart") String fystart,
                        @Param("fyend") String fyend);

        @Query(value = """
                        SELECT DISTINCT
                            CASE
                                WHEN TO_CHAR(pd.startdate, 'MM') BETWEEN '01' AND '03'
                                    THEN (DATE_PART('year', pd.startdate) - 1) || '-04##' || TO_CHAR(pd.startdate, 'yyyy') || '-03'
                                WHEN TO_CHAR(pd.startdate, 'MM') BETWEEN '04' AND '12'
                                    THEN TO_CHAR(pd.startdate, 'yyyy') || '-04##' || (DATE_PART('year', pd.startdate) + 1) || '-03'
                            END AS key,
                            CASE
                                WHEN TO_CHAR(pd.startdate, 'MM') BETWEEN '01' AND '03'
                                    THEN (DATE_PART('year', pd.startdate) - 1) || '-' || TO_CHAR(pd.startdate, 'yy')
                                WHEN TO_CHAR(pd.startdate, 'MM') BETWEEN '04' AND '12'
                                    THEN TO_CHAR(pd.startdate, 'yyyy') || '-' || SUBSTRING((DATE_PART('year', pd.startdate) + 1)::text, 3)
                            END AS value
                        FROM nerie.mt_programdetails pd
                        JOIN nerie.m_programs p ON pd.programcode = p.programcode
                        JOIN nerie.m_phases ph ON pd.phaseid = ph.phaseid
                        WHERE pd.finalized != 'R'
                        AND p.officecode = :officecode
                        ORDER BY value
                        """, nativeQuery = true)
        List<Object[]> getReportFinancialYearLA(@Param("officecode") String officecode);

        @Query(value = """
                            SELECT DISTINCT T.programcode AS key,
                                T.programname || '(' || TO_CHAR(min(pd.startdate), 'dd/MM/yyyy') || '-' || TO_CHAR(max(pd.enddate), 'dd/MM/yyyy') || ')' AS value,
                                pd.entrydate
                            FROM nerie.m_programs T
                            JOIN nerie.mt_programdetails pd ON T.programcode = pd.programcode
                            WHERE T.officecode = :officecode
                                AND pd.finalized != 'R'
                                AND T.coursecodecategory = '1'
                            GROUP BY T.programcode, pd.enddate, pd.entrydate, pd.startdate
                            ORDER BY pd.entrydate DESC
                        """, nativeQuery = true)
        List<Object[]> findProgramsForTimeTableByOffice(@Param("officecode") String officecode);

        @Query(value = """
                            SELECT T.programcode AS key,
                                MAX(T.programname) AS value,
                                MAX(pd.entrydate) AS entrydate
                            FROM nerie.m_programs T
                            JOIN nerie.mt_programdetails pd
                                ON T.programcode = pd.programcode
                            WHERE T.officecode = :officecode
                                AND pd.finalized != 'R'
                                AND T.enteredby = :usercode
                                AND T.coursecodecategory = '1'
                            GROUP BY T.programcode
                        """, nativeQuery = true)
        List<Object[]> findProgramsForTimeTableEnteredByUser(@Param("officecode") String officecode,
                        @Param("usercode") String usercode);

        @Query(value = """
                            SELECT DISTINCT T.programcode AS key,
                                T.programname || '(' || TO_CHAR(min(pd.startdate), 'dd/MM/yyyy') || '-' || TO_CHAR(max(pd.enddate), 'dd/MM/yyyy') || ')' AS value,
                                pd.entrydate
                            FROM nerie.m_programs T, nerie.mt_programdetails pd, nerie.mt_program_members m
                            WHERE m.usercode = :usercode
                                AND pd.finalized != 'R'
                                AND T.programcode = pd.programcode
                                AND T.programcode = m.programcode
                                AND TO_CHAR(pd.enddate, 'yyyy-MM-dd') >= TO_CHAR(NOW(), 'yyyy-MM-dd')
                            GROUP BY T.programcode, pd.enddate, pd.entrydate, pd.startdate
                            ORDER BY pd.entrydate DESC
                        """, nativeQuery = true)
        List<Object[]> findProgramsForTimeTableByMember(@Param("usercode") String usercode);

        @Query(value = "SELECT pd.phaseid, pd.programcode, p.programname, p.programdescription, ph.phaseno, ph.phasedescription, "
                        +
                        "2 AS coursecategoryname, " +
                        "TRIM(TO_CHAR(pd.enddate,'DDth Month')) || TO_CHAR(pd.enddate,' YYYY') AS enddate, " +
                        "TRIM(TO_CHAR(pd.lastdate,'DDth Month')) || TO_CHAR(pd.lastdate,' YYYY') AS lastdate, " +
                        "TRIM(TO_CHAR(pd.startdate,'DDth Month')) || TO_CHAR(pd.startdate,' YYYY') AS startdate, " +
                        "O.officename || ',' || S.statename AS officename, p.programid " +
                        "FROM nerie.mt_programdetails pd " +
                        "INNER JOIN nerie.m_programs p ON pd.programcode = p.programcode " +
                        "INNER JOIN nerie.m_phases ph ON pd.phaseid = ph.phaseid " +
                        "INNER JOIN nerie.m_offices O ON p.officecode = O.officecode " +
                        "INNER JOIN nerie.m_districts D ON O.officedistrictcode = D.districtcode " +
                        "INNER JOIN nerie.m_states S ON S.statecode = D.statecode " +
                        "WHERE pd.finalized = 'Y' AND TO_CHAR(pd.startdate,'yyyy-MM-dd') <= TO_CHAR(now(),'yyyy-MM-dd') AND "
                        +
                        "TO_CHAR(pd.enddate,'yyyy-MM-dd') >= TO_CHAR(now(),'yyyy-MM-dd') AND " +
                        "(:coursetype = 0 OR :coursetype = 2) " +
                        "ORDER BY pd.startdate " +
                        "LIMIT :limit OFFSET :offset", nativeQuery = true)
        List<Object[]> findOngoingPrograms(@Param("coursetype") Integer coursetype,
                        @Param("limit") Integer limit,
                        @Param("offset") Integer offset);

        @Query(value = "SELECT pd.phaseid, pd.programcode, p.programname, p.programdescription, ph.phaseno, ph.phasedescription, "
                        +
                        "1 AS coursecategoryname, " +
                        "COALESCE(TRIM(TO_CHAR(pd.enddate, 'DDth Month')) || TO_CHAR(pd.enddate, ' YYYY'), 'No dates finalized yet!') AS enddate, "
                        +
                        "COALESCE(TRIM(TO_CHAR(pd.lastdate, 'DDth Month')) || TO_CHAR(pd.lastdate, ' YYYY'), 'No dates finalized yet!') AS lastdate, "
                        +
                        "COALESCE(TRIM(TO_CHAR(pd.startdate, 'DDth Month')) || TO_CHAR(pd.startdate, ' YYYY'), 'No dates finalized yet!') AS startdate, "
                        +
                        "O.officename || ',' || S.statename AS officename, p.programid " +
                        "FROM nerie.mt_programdetails pd " +
                        "INNER JOIN nerie.m_programs p ON pd.programcode = p.programcode " +
                        "INNER JOIN nerie.m_phases ph ON pd.phaseid = ph.phaseid " +
                        "INNER JOIN nerie.m_offices O ON p.officecode = O.officecode " +
                        "INNER JOIN nerie.m_districts D ON O.officedistrictcode = D.districtcode " +
                        "INNER JOIN nerie.m_states S ON S.statecode = D.statecode " +
                        "WHERE pd.finalized = 'Y' " +
                        "AND ( " +
                        "   (pd.startdate IS NOT NULL AND TO_CHAR(pd.startdate, 'yyyy-MM-dd') > TO_CHAR(NOW(), 'yyyy-MM-dd')) "
                        +
                        "   OR " +
                        "   (pd.startdate IS NULL AND EXTRACT(YEAR FROM pd.entrydate) = EXTRACT(YEAR FROM NOW())) " +
                        ") " +
                        "AND (:coursetype = 0 OR :coursetype = 1) " +
                        "ORDER BY pd.startdate " +
                        "LIMIT :limit OFFSET :offset", nativeQuery = true)
        List<Object[]> findUpcomingPrograms(@Param("coursetype") Integer coursetype,
                        @Param("limit") Integer limit,
                        @Param("offset") Integer offset);

        @Query(value = "SELECT pd.phaseid, pd.programcode, p.programname, p.programdescription, ph.phaseno, ph.phasedescription, "
                        +
                        "3 AS coursecategoryname, " +
                        "TRIM(TO_CHAR(pd.enddate, 'DDth Month')) || TO_CHAR(pd.enddate, ' YYYY') AS enddate, " +
                        "TRIM(TO_CHAR(pd.lastdate, 'DDth Month')) || TO_CHAR(pd.lastdate, ' YYYY') AS lastdate, " +
                        "TRIM(TO_CHAR(pd.startdate, 'DDth Month')) || TO_CHAR(pd.startdate, ' YYYY') AS startdate, " +
                        "O.officename || ',' || S.statename AS officename, p.programid " +
                        "FROM nerie.mt_programdetails pd " +
                        "INNER JOIN nerie.m_programs p ON pd.programcode = p.programcode " +
                        "INNER JOIN nerie.m_phases ph ON pd.phaseid = ph.phaseid " +
                        "INNER JOIN nerie.m_offices O ON p.officecode = O.officecode " +
                        "INNER JOIN nerie.m_districts D ON O.officedistrictcode = D.districtcode " +
                        "INNER JOIN nerie.m_states S ON S.statecode = D.statecode " +
                        "WHERE pd.finalized = 'Y' " +
                        "AND TO_CHAR(pd.enddate, 'yyyy-MM-dd') < TO_CHAR(NOW(), 'yyyy-MM-dd') " +
                        "AND (:coursetype = 0 OR :coursetype = 3) " +
                        "ORDER BY pd.enddate DESC " +
                        "LIMIT :limit OFFSET :offset", nativeQuery = true)
        List<Object[]> findCompletedPrograms(@Param("coursetype") Integer coursetype,
                        @Param("limit") Integer limit,
                        @Param("offset") Integer offset);

        @Query(value = "SELECT COUNT(*) FROM nerie.mt_programdetails pd "
                        + "INNER JOIN nerie.m_programs p ON pd.programcode = p.programcode "
                        + "INNER JOIN nerie.m_phases ph ON pd.phaseid = ph.phaseid "
                        + "WHERE pd.finalized = 'Y' "
                        + "AND TO_CHAR(pd.startdate, 'yyyy-MM-dd') <= TO_CHAR(NOW(), 'yyyy-MM-dd') "
                        + "AND TO_CHAR(pd.enddate, 'yyyy-MM-dd') >= TO_CHAR(NOW(), 'yyyy-MM-dd')", nativeQuery = true)
        Integer countOngoingPrograms();

        @Query(value = "SELECT COUNT(*) FROM nerie.mt_programdetails pd "
                        + "JOIN nerie.m_programs p ON p.programcode = pd.programcode "
                        + "JOIN nerie.m_phases ph ON ph.phaseid = pd.phaseid "
                        + "WHERE pd.finalized = 'Y' "
                        + "  AND ( "
                        + "        (pd.startdate IS NOT NULL AND TO_CHAR(pd.startdate, 'yyyy-MM-dd') > TO_CHAR(NOW(), 'yyyy-MM-dd')) "
                        + "        OR "
                        + "        (pd.startdate IS NULL AND EXTRACT(YEAR FROM pd.entrydate) = EXTRACT(YEAR FROM NOW())) "
                        + "      )", nativeQuery = true)
        Integer countUpcomingPrograms();

        @Query(value = "SELECT COUNT(DISTINCT p.programcode) " +
                        "FROM nerie.mt_programdetails pd " +
                        "INNER JOIN nerie.m_programs p ON pd.programcode = p.programcode " +
                        "INNER JOIN nerie.m_phases ph ON pd.phaseid = ph.phaseid " +
                        "WHERE pd.finalized = 'Y' " +
                        "  AND TO_CHAR(pd.enddate, 'yyyy-MM-dd') < TO_CHAR(NOW(), 'yyyy-MM-dd')", nativeQuery = true)
        Integer countCompletedPrograms();

        @Query(value = """
            SELECT p.programcode, p.programid, p.programname, p.programdescription
            FROM nerie.m_programs p
            WHERE p.officecode = :oc
              AND (p.enteredby = :uc OR :uc = 'ALL')
              AND p.closed = 'N'
              AND p.programcode IN (
                    SELECT DISTINCT programcode
                    FROM nerie.mt_programdetails
                    WHERE finalized = 'Y'
                      AND TO_CHAR(startdate,'yyyy-MM-dd') <= TO_CHAR(NOW(),'yyyy-MM-dd')
                      AND TO_CHAR(enddate,'yyyy-MM-dd') >= TO_CHAR(NOW(),'yyyy-MM-dd')
              )
            ORDER BY p.programcode
        """, nativeQuery = true)
                List<Object[]> getDashboardOngoing(
                        @Param("uc") String usercode,
                        @Param("oc") String officecode);

        @Query(value = """
            SELECT p.programcode, p.programid, p.programname, p.programdescription
            FROM nerie.m_programs p
            WHERE p.officecode = :oc
              AND (p.enteredby = :uc OR :uc = 'ALL')
              AND p.closed = 'N'
              AND p.programcode IN (
                    SELECT DISTINCT programcode
                    FROM nerie.mt_programdetails
                    WHERE finalized = 'Y'
                      AND (
                            (startdate IS NOT NULL AND 
                             TO_CHAR(startdate,'yyyy-MM-dd') > TO_CHAR(NOW(),'yyyy-MM-dd'))
                            OR
                            (startdate IS NULL AND 
                             EXTRACT(YEAR FROM entrydate) = EXTRACT(YEAR FROM NOW()))
                          )
              )
            ORDER BY p.programcode
        """, nativeQuery = true)
                List<Object[]> getDashboardUpcoming(
                        @Param("uc") String usercode,
                        @Param("oc") String officecode);

        @Query(value = """
            WITH RankedPrograms AS (
                SELECT p.programcode, p.programid, p.programname, p.programdescription,
                       pd.startdate, pd.enddate,
                       ROW_NUMBER() OVER(PARTITION BY p.programcode ORDER BY pd.startdate DESC) AS row_num
                FROM nerie.m_programs p
                JOIN nerie.mt_programdetails pd ON pd.programcode = p.programcode
                WHERE p.officecode = :oc
                  AND pd.programcode IN (
                        SELECT DISTINCT programcode
                        FROM nerie.mt_programdetails
                        WHERE finalized = 'Y'
                          AND TO_CHAR(enddate,'yyyy-MM-dd') < TO_CHAR(NOW(),'yyyy-MM-dd')
                  )
            )
            SELECT programcode, programid, programname, programdescription, startdate, enddate
            FROM RankedPrograms
            WHERE row_num = 1
            ORDER BY programcode
        """, nativeQuery = true)
                List<Object[]> getCompletedProgDetailsForPublic(
                        @Param("oc") String officecode);

        // 1. Global Ongoing Programs
        @Query(value = """
            SELECT p.programcode, p.programid, p.programname, p.programdescription
            FROM nerie.m_programs p
            WHERE p.closed = 'N'
              AND p.programcode IN (
                    SELECT DISTINCT programcode
                    FROM nerie.mt_programdetails
                    WHERE finalized = 'Y'
                      AND TO_CHAR(startdate,'yyyy-MM-dd') <= TO_CHAR(NOW(),'yyyy-MM-dd')
                      AND TO_CHAR(enddate,'yyyy-MM-dd') >= TO_CHAR(NOW(),'yyyy-MM-dd')
              )
            ORDER BY p.programcode
        """, nativeQuery = true)
                List<Object[]> getLandingDashboardOngoing();

                // 2. Global Upcoming Programs
                @Query(value = """
            SELECT p.programcode, p.programid, p.programname, p.programdescription
            FROM nerie.m_programs p
            WHERE p.closed = 'N'
              AND p.programcode IN (
                    SELECT DISTINCT programcode
                    FROM nerie.mt_programdetails
                    WHERE finalized = 'Y'
                      AND (
                            (startdate IS NOT NULL AND 
                             TO_CHAR(startdate,'yyyy-MM-dd') > TO_CHAR(NOW(),'yyyy-MM-dd'))
                            OR
                            (startdate IS NULL AND 
                             EXTRACT(YEAR FROM entrydate) = EXTRACT(YEAR FROM NOW()))
                          )
              )
            ORDER BY p.programcode
        """, nativeQuery = true)
                List<Object[]> getLandingDashboardUpcoming();

                // 3. Global Completed Programs
                @Query(value = """
            WITH RankedPrograms AS (
                SELECT p.programcode, p.programid, p.programname, p.programdescription,
                       pd.startdate, pd.enddate,
                       ROW_NUMBER() OVER(PARTITION BY p.programcode ORDER BY pd.startdate DESC) AS row_num
                FROM nerie.m_programs p
                JOIN nerie.mt_programdetails pd ON pd.programcode = p.programcode
                WHERE pd.programcode IN (
                        SELECT DISTINCT programcode
                        FROM nerie.mt_programdetails
                        WHERE finalized = 'Y'
                          AND TO_CHAR(enddate,'yyyy-MM-dd') < TO_CHAR(NOW(),'yyyy-MM-dd')
                  )
            )
            SELECT programcode, programid, programname, programdescription, startdate, enddate
            FROM RankedPrograms
            WHERE row_num = 1
            ORDER BY programcode
        """, nativeQuery = true)
                List<Object[]> getLandingCompletedProgDetails();
        
            @Query(value = """
            WITH RankedPrograms AS (
                SELECT p.programcode, p.programid, p.programname, p.programdescription,
                    pd.startdate, pd.enddate,
                    ROW_NUMBER() OVER(PARTITION BY p.programcode ORDER BY pd.startdate DESC) AS row_num
                FROM nerie.m_programs p
                JOIN nerie.mt_programdetails pd ON pd.programcode = p.programcode
                WHERE pd.programcode IN (
                        SELECT DISTINCT programcode
                        FROM nerie.mt_programdetails
                        WHERE finalized = 'Y'
                        AND TO_CHAR(enddate,'yyyy-MM-dd') < TO_CHAR(NOW(),'yyyy-MM-dd')
                )
                AND TO_CHAR(pd.startdate, 'yyyy-MM') BETWEEN :fystart AND :fyend
            )
            SELECT programcode, programid, programname, programdescription, startdate, enddate
            FROM RankedPrograms
            WHERE row_num = 1
            ORDER BY programcode
        """, nativeQuery = true)
        List<Object[]> getLandingCompletedProgDetailsByFy(@Param("fystart") String fystart, @Param("fyend") String fyend);


        @Query(value = "SELECT pd.phaseid, pd.programcode, p.programname, p.programdescription, ph.phaseno, ph.phasedescription, "
                        + "TRIM(TO_CHAR(pd.enddate, 'DDth Month')) || TO_CHAR(pd.enddate, ' YYYY') AS enddate, "
                        + "TRIM(TO_CHAR(pd.lastdate, 'DDth Month')) || TO_CHAR(pd.lastdate, ' YYYY') AS lastdate, "
                        + "TRIM(TO_CHAR(pd.startdate, 'DDth Month')) || TO_CHAR(pd.startdate, ' YYYY') AS startdate, "
                        + "O.officename || ',' || S.statename AS officename, 2 AS coursecategoryname, p.programid "
                        + "FROM nerie.mt_programdetails pd "
                        + "INNER JOIN nerie.m_programs p ON pd.programcode = p.programcode "
                        + "INNER JOIN nerie.m_phases ph ON pd.phaseid = ph.phaseid "
                        + "INNER JOIN nerie.m_offices O ON p.officecode = O.officecode "
                        + "INNER JOIN nerie.m_districts D ON O.officedistrictcode = D.districtcode "
                        + "INNER JOIN nerie.m_states S ON S.statecode = D.statecode "
                        + "WHERE pd.finalized = 'Y' "
                        + "AND TO_CHAR(pd.startdate, 'yyyy-MM-dd') <= TO_CHAR(NOW(), 'yyyy-MM-dd') "
                        + "AND TO_CHAR(pd.enddate, 'yyyy-MM-dd') >= TO_CHAR(NOW(), 'yyyy-MM-dd') "
                        + "AND (:coursetype = 0 OR :coursetype = 2) "
                        + "ORDER BY pd.startdate", nativeQuery = true)
        List<Object[]> findMoreOngoingPrograms(@Param("coursetype") Integer coursetype);

        @Query(value = "SELECT pd.phaseid, pd.programcode, p.programname, p.programdescription, ph.phaseno, ph.phasedescription, 1 AS coursecategoryname, "
                        + "COALESCE(TRIM(TO_CHAR(pd.enddate, 'DDth Month')) || TO_CHAR(pd.enddate, ' YYYY'), 'No dates finalized yet!') AS enddate, "
                        + "COALESCE(TRIM(TO_CHAR(pd.lastdate, 'DDth Month')) || TO_CHAR(pd.lastdate, ' YYYY'), 'No dates finalized yet!') AS lastdate, "
                        + "COALESCE(TRIM(TO_CHAR(pd.startdate, 'DDth Month')) || TO_CHAR(pd.startdate, ' YYYY'), 'No dates finalized yet!') AS startdate, "
                        + "O.officename || ',' || S.statename AS officename, p.programid "
                        + "FROM nerie.mt_programdetails pd "
                        + "INNER JOIN nerie.m_programs p ON pd.programcode = p.programcode "
                        + "INNER JOIN nerie.m_phases ph ON pd.phaseid = ph.phaseid "
                        + "INNER JOIN nerie.m_offices O ON p.officecode = O.officecode "
                        + "INNER JOIN nerie.m_districts D ON O.officedistrictcode = D.districtcode "
                        + "INNER JOIN nerie.m_states S ON S.statecode = D.statecode "
                        + "WHERE pd.finalized = 'Y' "
                        + "AND ( "
                        + "    pd.startdate IS NULL AND EXTRACT(YEAR FROM pd.entrydate) = EXTRACT(YEAR FROM NOW()) "
                        + "    OR TO_CHAR(pd.startdate, 'yyyy-MM-dd') > TO_CHAR(NOW(), 'yyyy-MM-dd') "
                        + ") "
                        + "AND (:coursetype = 0 OR :coursetype = 1) "
                        + "ORDER BY pd.startdate", nativeQuery = true)
        List<Object[]> findMoreUpcomingPrograms(@Param("coursetype") Integer coursetype);

        @Query(value = "SELECT pd.phaseid, pd.programcode, p.programname, p.programdescription, ph.phaseno, ph.phasedescription, 3 AS coursecategoryname, "
                        + "TRIM(TO_CHAR(pd.enddate, 'DDth Month')) || TO_CHAR(pd.enddate, ' YYYY') AS enddate, "
                        + "TRIM(TO_CHAR(pd.lastdate, 'DDth Month')) || TO_CHAR(pd.lastdate, ' YYYY') AS lastdate, "
                        + "TRIM(TO_CHAR(pd.startdate, 'DDth Month')) || TO_CHAR(pd.startdate, ' YYYY') AS startdate, "
                        + "O.officename || ',' || S.statename AS officename, p.programid "
                        + "FROM nerie.mt_programdetails pd "
                        + "INNER JOIN nerie.m_programs p ON pd.programcode = p.programcode "
                        + "INNER JOIN nerie.m_phases ph ON pd.phaseid = ph.phaseid "
                        + "INNER JOIN nerie.m_offices O ON p.officecode = O.officecode "
                        + "INNER JOIN nerie.m_districts D ON O.officedistrictcode = D.districtcode "
                        + "INNER JOIN nerie.m_states S ON S.statecode = D.statecode "
                        + "WHERE pd.finalized = 'Y' "
                        + "AND TO_CHAR(pd.enddate, 'yyyy-MM-dd') < TO_CHAR(NOW(), 'yyyy-MM-dd') "
                        + "AND (:coursetype = 0 OR :coursetype = 3) "
                        + "ORDER BY pd.enddate DESC", nativeQuery = true)
        List<Object[]> findMoreCompletedPrograms(@Param("coursetype") Integer coursetype);

        @Query("SELECT CASE WHEN COUNT(pd) > 0 THEN true ELSE false END " +
                        "FROM MT_ProgramDetails pd WHERE pd.phaseid.phaseid = :phaseid")
        boolean existsByPhaseid(@Param("phaseid") String phaseid);

        @Modifying
        @Query(value = "UPDATE mt_programdetails SET finalized = :finalized, approvalletter = :approvalletter, " +
                "approvaldate = :approvaldate, approvedusercode = :approvedusercode  WHERE programdetailid = :pdid", nativeQuery = true)
        void approveProgram(@Param("finalized") String finalized, @Param("approvalletter") byte[] approvalletter, 
                @Param("approvaldate") Date approvaldate, @Param("approvedusercode") String approvedusercode, @Param("pdid") String pdid);

        @Modifying
        @Query(value = "UPDATE mt_programdetails SET finalized = :finalized, rejectionremarks = :rejectionremarks, " +
                "rejectionletter = :rejectionletter, rejectiondate = :rejectiondate, rejectedusercode = :rejectedusercode WHERE programdetailid = :pdid", nativeQuery = true)
        void rejectProgram(@Param("finalized") String finalized, @Param("rejectionletter") byte[] rejectionletter, @Param("rejectiondate") Date rejectiondate, 
        @Param("rejectionremarks") String rejectionremarks, @Param("rejectedusercode") String rejectedusercode, @Param("pdid") String pdid);

        @Modifying
        @Query(value = "DELETE FROM nerie.mt_programvenues pv WHERE pv.programcode = :programcode", nativeQuery = true)
        void deleteProgramVenuesByProgramcode(@Param("programcode") String programcode);

        @Modifying
        @Query("DELETE FROM MT_ProgramDetails WHERE programcode.programcode = :programcode")
        int deleteByProgramcode(@Param("programcode") String programcode);

        @Modifying
        @Transactional
        @Query(value = "DELETE FROM mt_programvenues WHERE phaseid = :phaseid", nativeQuery = true)
        void deleteProgramVenuesByPhaseId(@Param("phaseid") String phaseid);

        @Modifying
        @Transactional
        @Query(value = "INSERT INTO mt_programvenues(programcode, venuecode, phaseid) VALUES (:pcode, :vcode, :phaseid)", nativeQuery = true)
        void insertProgramVenue(@Param("pcode") String programCode,
                                @Param("vcode") String venueCode,
                                @Param("phaseid") String phaseId);

        @Modifying
        @Transactional
        @Query(value = "DELETE FROM mt_program_members WHERE phaseid = :phaseid", nativeQuery = true)
        void deleteProgramMembersByPhaseId(@Param("phaseid") String phaseid);

        // OLD
        @Modifying
        @Transactional
        @Query(value = "INSERT INTO mt_program_members(programcode, usercode, phaseid) VALUES (:pcode, :ucode, :phaseid)", nativeQuery = true)
        void insertProgramMember(@Param("pcode") String programCode,
                                 @Param("ucode") String userCode,
                                 @Param("phaseid") String phaseId);

        // NEW
        @Modifying
        @Query(value = "INSERT INTO mt_program_members(programcode, usercode, phaseid, isheadcoordinator, isdelegated) " +
                "VALUES (:pcode, :ucode, :phaseid, :ishead, '0')", nativeQuery = true)
        void insertProgramMemberWithRole(@Param("pcode") String programCode,
                                         @Param("ucode") String userCode,
                                         @Param("phaseid") String phaseId,
                                         @Param("ishead") String isHead);

        @Modifying
        @Query("UPDATE MT_ProgramDetails pd SET pd.startdate = :start, pd.enddate = :end, pd.lastdate = :last, pd.courseclosedate = :closed " +
                "WHERE pd.programdetailid = :id")
        void updateProgramDates(@Param("id") String id,
                                @Param("start") Date start,
                                @Param("end") Date end,
                                @Param("last") Date last,
                                @Param("closed") Date closed);

        @Query(value = "SELECT CAST(phaseno AS int) FROM m_phases WHERE phaseid = :phaseid", nativeQuery = true)
        Integer findPhaseNoByPhaseId(@Param("phaseid") String phaseId);

        @Query(value = "SELECT CAST(programdetailid AS int) FROM MT_ProgramDetails WHERE programcode = :pcode AND phaseid = :phaseid", nativeQuery = true)
        Integer findProgramDetailId(@Param("pcode") String programCode,
                                    @Param("phaseid") String phaseId);

        @Query(value = "SELECT * FROM nerie.mt_programdetails WHERE programcode = :pcode AND phaseid = :phaseid", nativeQuery = true)
        Optional<MT_ProgramDetails> findByProgramcodeAndPhaseidNative(@Param("pcode") String pcode, @Param("phaseid") String phaseid);

        @Query(value = """
                        SELECT 
                            p.programdescription, 
                            p.programid,
                            (CAST((SELECT MAX(CAST(ph2.phaseno AS INT)) 
                             FROM m_phases ph2 
                             WHERE ph2.programcode = p.programcode) AS INT) + 1) AS nextphase
                        FROM 
                            m_programs p
                        JOIN 
                            m_phases ph ON ph.programcode = p.programcode
                        JOIN 
                            mt_programdetails pd ON ph.phaseid = pd.phaseid
                        WHERE 
                            p.programcode = :programCode
                        ORDER BY 
                            ph.phaseno DESC 
                        LIMIT 1
                        """, nativeQuery = true)
        List<Object[]> findProgramDetailsBasedOnCodeToPopulateForm(@Param("programCode") String programCode);

        @Query(value = """
            SELECT *
            FROM mt_programdetails
            WHERE programdetailid = :pdid
        """, nativeQuery = true)
        MT_ProgramDetails findProgramDetailsById(@Param("pdid") String pdid);

        @Query(value = "SELECT p.programname, ph.phaseno, pd.programcode, pd.phaseid " +
                "FROM nerie.mt_programdetails pd " +
                "JOIN nerie.m_programs p ON pd.programcode = p.programcode " +
                "JOIN nerie.m_phases ph ON pd.phaseid = ph.phaseid " +
                "WHERE pd.programdetailid = :pdid", nativeQuery = true)
        List<Object[]> findProgramInfoForNotification(@Param("pdid") String pdid);

        @Query(value = "SELECT DISTINCT P.programcode, " +
                "P.programname || ' (' || TO_CHAR(pd.startdate, 'dd/MM/yyyy') || ' - ' || " +
                "TO_CHAR(pd.enddate, 'dd/MM/yyyy') || ')' AS programname, " +
                "ph.phaseno, ph.phaseid, ph.phasedescription, pd.entrydate " +
                "FROM nerie.m_programs P " +
                "JOIN nerie.mt_programdetails pd ON P.programcode = pd.programcode " +
                "JOIN nerie.m_phases ph ON ph.phaseid = pd.phaseid AND P.programcode = ph.programcode " +
                "WHERE pd.finalized != 'R' " +
                "AND P.closed = 'Y' " +  // Changed to target Closed programs
                "AND TO_CHAR(pd.startdate, 'yyyy-MM') BETWEEN :fystart AND :fyend " +
                "ORDER BY pd.entrydate DESC", nativeQuery = true)
        List<Object[]> findClosedProgramsByFy(@Param("fystart") String fystart, 
                                            @Param("fyend") String fyend);
}
