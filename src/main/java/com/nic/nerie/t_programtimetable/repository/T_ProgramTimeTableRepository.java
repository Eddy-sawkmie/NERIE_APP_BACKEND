package com.nic.nerie.t_programtimetable.repository;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nic.nerie.t_programtimetable.model.T_ProgramTimeTable;

import jakarta.transaction.Transactional;

@Repository
public interface T_ProgramTimeTableRepository extends JpaRepository<T_ProgramTimeTable, String> {

    @Query(nativeQuery = true, value = """
        SELECT p.programname, p.programdescription, pd.startdate, pd.enddate, 
               at.programtimetablecode, 
               TO_CHAR(ptt.starttime,'HH:mm AM') as starttime,
               TO_CHAR(ptt.endtime,'HH:mm AM') as endtime, 
               ptt.programdate, ph.phaseno, ptt.subject, ptt.programday,
               string_agg(distinct R.rpname,',') as resourceperson, 
               ptt.roomcode
        FROM nerie.m_programs p
        INNER JOIN nerie.m_phases ph ON ph.programcode = p.programcode
        INNER JOIN nerie.t_applications a ON a.phaseid = ph.phaseid
        INNER JOIN nerie.mt_programdetails pd ON pd.phaseid = ph.phaseid
        LEFT JOIN nerie.t_participantattendance at ON at.applicationcode = a.applicationcode 
            AND at.phaseid = ph.phaseid
        LEFT JOIN nerie.t_programtimetable ptt ON ptt.programtimetablecode = at.programtimetablecode
        LEFT JOIN nerie.mt_programttresourceperson prp ON prp.programtimetablecode = ptt.programtimetablecode
        LEFT JOIN nerie.mt_resourcepersons R ON R.rpslno = prp.rpslno
        LEFT JOIN nerie.mt_venuerooms VR ON VR.roomcode = ptt.roomcode
        WHERE at.phaseid = :phaseid 
          AND at.pusercode = :usercode 
          AND pd.finalized != 'R' 
          AND pd.closed != 'Y'
        GROUP BY p.programname, p.programdescription, pd.startdate, pd.enddate, 
                 at.programtimetablecode, ptt.starttime, ptt.endtime, ptt.programdate,
                 ph.phaseno, ptt.subject, ptt.programday, ptt.roomcode
        ORDER BY ptt.programdate, ptt.starttime
        """)
    List<Object[]> findParticipantTimetable(@Param("phaseid") String phaseid,
                                            @Param("usercode") String usercode);

    @Query(value = """
            SELECT ptt.programtimetablecode,
                   CONCAT(TO_CHAR(ptt.starttime, 'HH:MI AM'), ' - ', TO_CHAR(ptt.endtime, 'HH:MI AM'))
            FROM nerie.t_programtimetable ptt 
            WHERE ptt.programday = :programday 
            AND ptt.phaseid = :phaseid
            """, nativeQuery = true)
    List<Object[]> findProgramSessions(@Param("phaseid") String phaseid, @Param("programday") Short programday);

    @Query(value = """
            SELECT
                T.programcode AS key,
                T.programname
                    || '('
                    || TO_CHAR(MIN(pd.startdate), 'DD/MM/YYYY')
                    || '-'
                    || TO_CHAR(MAX(pd.enddate), 'DD/MM/YYYY')
                    || ')' AS value,
                MAX(pd.entrydate) AS entrydate
            FROM nerie.m_programs T
            JOIN nerie.mt_programdetails pd
                ON T.programcode = pd.programcode
            WHERE T.officecode = :officecode
              AND pd.finalized <> 'R'
              AND T.coursecodecategory = '1'
            GROUP BY
                T.programcode,
                T.programname
            ORDER BY
                MAX(pd.entrydate) DESC
            """, nativeQuery = true)
    List<Object[]> getProgramsTimetableByOfficecode(@Param("officecode") String officecode);

    @Query(value = """
            SELECT
                T.programcode AS key,
                MAX(T.programname) AS value,
                MAX(pd.entrydate) AS entrydate
            FROM nerie.m_programs T
            JOIN nerie.mt_programdetails pd
                ON T.programcode = pd.programcode
            WHERE T.officecode = :officecode
              AND pd.finalized <> 'R'
              AND T.coursecodecategory = '1'
              AND (
                    T.enteredby = :usercode
                    OR EXISTS (
                        SELECT 1
                        FROM nerie.mt_program_members pm2
                        WHERE pm2.programcode = T.programcode
                          AND pm2.usercode = :usercode
                          AND (pm2.isheadcoordinator = '1' OR pm2.isdelegated = '1')
                    )
                  )
            GROUP BY
                T.programcode
            ORDER BY
                MAX(pd.entrydate) DESC
            """, nativeQuery = true)
    List<Object[]> getProgramsTimetableByOfficecodeAndUsercode(@Param("officecode") String officecode, @Param("usercode") String usercode);


    @Query(value = """
            SELECT DISTINCT T.programcode AS key, 
            T.programname || '(' || TO_CHAR(MIN(pd.startdate), 'dd/MM/yyyy') || '-' || TO_CHAR(MAX(pd.enddate), 'dd/MM/yyyy') || ')' AS value, 
            pd.entrydate 
            FROM nerie.m_programs T 
            JOIN nerie.mt_programdetails pd ON T.programcode = pd.programcode 
            JOIN nerie.mt_program_members m ON T.programcode = m.programcode 
            WHERE m.usercode = :usercode 
            AND pd.finalized != 'R' 
            AND TO_CHAR(pd.enddate, 'yyyy-MM-dd') >= TO_CHAR(now(), 'yyyy-MM-dd') 
            GROUP BY T.programcode, pd.enddate, pd.entrydate, pd.startdate 
            ORDER BY pd.entrydate DESC
            """, nativeQuery = true)
    List<Object[]> getProgramsTimetableByUsercodeAndEndDate(@Param("usercode") String usercode);       
    
    @Query(value = "SELECT COUNT(*) > 0 FROM t_programtimetable T WHERE T.phaseid = :phaseid "
            + "AND T.programdate = :programdate AND T.roomcode = :roomcode "
            + "AND ((:starttime BETWEEN T.starttime AND T.endtime) OR (:endtime BETWEEN T.starttime AND T.endtime)) "
            + "AND (:programtimetablecode IS NULL OR programtimetablecode != :programtimetablecode)", nativeQuery = true)
    boolean existsProgramTimetableClash(@Param("phaseid") String phaseid, @Param("programdate") Date programdate, @Param("roomcode") String roomcode,
                                           @Param("starttime") LocalTime starttime, @Param("endtime") LocalTime endtime, @Param("programtimetablecode") String programtimetablecode);

    @Query(value = """
        SELECT COUNT(ptt.programtimetablecode) > 0
        FROM t_programtimetable ptt
        WHERE ptt.phaseid = :phaseid
          AND ptt.programday = :programday
          AND ((:starttime BETWEEN ptt.starttime AND ptt.endtime) OR (:endtime BETWEEN ptt.starttime AND ptt.endtime))
          AND (:programtimetablecode IS NULL OR ptt.programtimetablecode != :programtimetablecode)
        """, nativeQuery = true)
    boolean existsProgramTimetable(@Param("phaseid") String phaseid, @Param("programday") short programday, @Param("starttime") LocalTime starttime,
                                   @Param("endtime") LocalTime endtime, @Param("programtimetablecode") String programtimetablecode);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO nerie.mt_programttresourceperson(programtimetablecode, rpslno) VALUES(:programtimetablecode, :rpslno)", nativeQuery = true)
    void createMTProgramttResourcePersonEntryByProgramtimetablecode(@Param("programtimetablecode") String programtimetablecode, @Param("rpslno") String rpslno);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM nerie.mt_programttresourceperson prs where prs.programtimetablecode = :programtimetablecode", nativeQuery = true)
    void deleteMTProgramttResourcePersonEntryByProgramtimetablecode(@Param("programtimetablecode") String programtimetablecode);

    @Query("SELECT MAX(CAST(pt.programtimetablecode AS int)) FROM T_ProgramTimeTable pt")
    Integer getLastUsedProgramtimetablecode();

    @Query(value = "SELECT * FROM t_programtimetable WHERE phaseid = :phaseid AND subject != 'BREAK' ORDER BY subject", nativeQuery = true)
    List<T_ProgramTimeTable> getSubjectDaysByPhaseId(String phaseid);
}