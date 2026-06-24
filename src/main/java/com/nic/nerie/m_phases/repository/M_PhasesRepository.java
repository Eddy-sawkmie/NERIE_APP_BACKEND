package com.nic.nerie.m_phases.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nic.nerie.m_phases.model.M_Phases;

public interface M_PhasesRepository extends JpaRepository<M_Phases, String> {
    @Query(value = "SELECT p.programname, p.programdescription, ph.phaseno, ph.phasedescription, " +
            "TRIM(TO_CHAR(pd.enddate, 'DDth Month')) || TO_CHAR(pd.enddate, ' YYYY') AS enddate, " +
            "TRIM(TO_CHAR(pd.startdate, 'DDth Month')) || TO_CHAR(pd.startdate, ' YYYY') AS startdate, " +
            "p.programid " +
            "FROM nerie.mt_programdetails pd " +
            "INNER JOIN nerie.m_programs p ON pd.programcode = p.programcode " +
            "INNER JOIN nerie.m_phases ph ON pd.phaseid = ph.phaseid " +
            "WHERE pd.finalized = 'Y' AND pd.closed = 'Y' AND TO_CHAR(pd.enddate, 'yyyy-MM-dd') < TO_CHAR(now(), 'yyyy-MM-dd') "
            +
            "AND (:coursetype = 0 OR :coursetype = 3) AND p.officecode = :officecode " +
            "ORDER BY pd.enddate DESC LIMIT :limit", nativeQuery = true)
    List<Object[]> getDashboardRecentlyCompletedPhasesList(@Param("coursetype") Integer coursetype,
            @Param("limit") Integer limit,
            @Param("officecode") String officecode);

    @Query(value = """
                select p.phaseid,p.phaseno
                from nerie.m_phases p, nerie.mt_programdetails pd
                where pd.phaseid = p.phaseid and pd.closed !='Y' and pd.finalized != 'R' and p.programcode = :pcode
            """, nativeQuery = true)
    List<Object[]> getPhasesByProgramcode(@Param("pcode") String programcode);

    @Query("SELECT CAST(p.phaseno as int) FROM M_Phases p where p.phaseid = :phaseid")
    Integer getPhasenoByPhaseid(@Param("phaseid") String phaseid);

    @Query("SELECT MAX(CAST(p.phaseid as int)) FROM M_Phases p")
    Integer getLastUsedPhaseid();

    @Query("SELECT MAX(CAST(p.phaseno as int)) FROM M_Phases p WHERE p.programcode.programcode = :programcode")
    Integer getLastUsedPhaseno(@Param("programcode") String programcode);

    @Query(value = """
            select p.phaseid, p.phaseno, p.phasedescription, pd.startdate, pd.enddate, pd.courseclosedate, pd.closed
            from nerie.m_phases p, nerie.mt_programdetails pd
            where pd.phaseid = p.phaseid and p.programcode = :pcode
            order by p.phaseno
            """, nativeQuery = true)
    List<Object[]> getUnClosePhasesList(@Param("pcode") String pcode);

    @Query(value = "SELECT p.phaseid, p.phaseno " +
            "FROM nerie.m_phases p " +
            "JOIN nerie.mt_programdetails pd ON p.phaseid = pd.phaseid " +
            "WHERE pd.closed != 'Y' AND pd.finalized != 'R' AND p.programcode = :pcode", nativeQuery = true)
    List<Object[]> getPhasesBasedOnProgramCode(@Param("pcode") String pcode);

    @Query(value = "SELECT * FROM nerie.m_phases WHERE phaseid = :phaseid", nativeQuery = true)
    M_Phases getPhaseByPhaseId(@Param("phaseid") String phaseid);

    @Query(nativeQuery = true, value = """
            SELECT p.programcode, p.programname, p.programid, p.programdescription,
                   pd.startdate, pd.enddate, pd.entrydate, O.officename, a.applicationcode,
                   STRING_AGG(DISTINCT v.venuename, ',') AS venuename,
                   STRING_AGG(DISTINCT u.username, ',') AS coordinator,
                   ph.phaseid, ph.phaseno
            FROM nerie.m_programs p
            INNER JOIN nerie.m_phases ph ON ph.programcode = p.programcode
            INNER JOIN nerie.mt_programdetails pd ON pd.phaseid = ph.phaseid
            INNER JOIN nerie.mt_programvenues pv ON ph.phaseid = pv.phaseid
            INNER JOIN nerie.m_venues v ON pv.venuecode = v.venuecode
            INNER JOIN nerie.mt_program_members pm ON pm.phaseid = ph.phaseid
            INNER JOIN nerie.mt_userlogin u ON u.usercode = pm.usercode
            INNER JOIN nerie.m_coursecategories CC ON p.coursecodecategory = CC.coursecategorycode
            INNER JOIN nerie.m_offices O ON O.officecode = p.officecode
            INNER JOIN nerie.t_applications a ON a.phaseid = ph.phaseid
            WHERE ph.phaseid = :phaseid AND a.status = 'A'
            GROUP BY p.programcode, p.programname, p.programid, p.programdescription,
                     pd.startdate, pd.enddate, pd.entrydate, O.officename, a.applicationcode,
                     ph.phaseid, ph.phaseno
            ORDER BY p.programcode, pd.entrydate DESC
            """)
    List<Object[]> findPhaseDetailsForFeedbackByPhaseId(@Param("phaseid") String phaseId);

    @Query("SELECT COUNT(*) FROM M_Phases WHERE programcode.programcode = :programcode")
    Long getPhasesCountByProgramcode(@Param("programcode") String programcode);

    @Modifying
    @Query("DELETE FROM M_Phases WHERE programcode.programcode = :programcode")
    int deleteByProgramcode(@Param("programcode") String programcode);

    @Query(value = """
        SELECT p.phaseid, p.phaseno
        FROM nerie.m_phases p
        JOIN nerie.mt_programdetails pd ON pd.phaseid = p.phaseid
        WHERE pd.closed != 'Y' 
        AND pd.finalized != 'R' 
        AND p.programcode = :programCode
        AND p.phaseid NOT IN (
            SELECT phaseid FROM nerie.t_phase_preposttest
        )
        """, nativeQuery = true)
    List<Object[]> findPhasesBasedOnProgramCodeWithoutPrePostTest(@Param("programCode") String programCode);
}
