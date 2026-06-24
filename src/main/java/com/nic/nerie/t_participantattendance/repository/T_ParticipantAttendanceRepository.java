package com.nic.nerie.t_participantattendance.repository;

import com.nic.nerie.t_participantattendance.model.T_P_Attendance_Id;
import com.nic.nerie.t_participantattendance.model.T_ParticipantAttendance;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface T_ParticipantAttendanceRepository extends JpaRepository<T_ParticipantAttendance, T_P_Attendance_Id> {
        @Query(nativeQuery = true, value = """
                        SELECT p.programname, pd.startdate, pd.enddate,
                               pt.starttime, pt.endtime, pa.entrydate
                        FROM nerie.t_participantattendance pa
                        JOIN nerie.t_programtimetable pt ON pa.programtimetablecode = pt.programtimetablecode
                        JOIN nerie.m_phases ph ON pa.phaseid = ph.phaseid
                        JOIN nerie.mt_programdetails pd ON ph.phaseid = pd.phaseid
                        JOIN nerie.m_programs p ON ph.programcode = p.programcode
                        WHERE pa.phaseid = :phaseid AND pa.pusercode = :usercode
                        ORDER BY pa.entrydate ASC, pt.starttime ASC
                        """)
        List<Object[]> findParticipantAttendanceDetails(@Param("phaseid") String phaseId,
                        @Param("usercode") String usercode);

        @Query(value = """
                        SELECT DISTINCT T.programcode AS key,
                               T.programname || '(' || TO_CHAR(min(pd.startdate),'dd/MM/yyyy') || '-' ||
                               TO_CHAR(max(pd.enddate),'dd/MM/yyyy') || ')' AS value,
                               pd.entrydate
                        FROM nerie.m_programs T
                        JOIN nerie.mt_programdetails pd ON T.programcode = pd.programcode
                        JOIN nerie.t_programtimetable tt ON tt.phaseid = pd.phaseid
                        WHERE T.officecode = :officecode AND pd.finalized != 'R'
                        GROUP BY T.programcode, pd.enddate, pd.entrydate, pd.startdate
                        ORDER BY pd.entrydate DESC
                        """, nativeQuery = true)
        List<Object[]> findProgramsForParticipantAttendanceRoleA(@Param("officecode") String officecode);

        @Query(value = """
                    SELECT DISTINCT
                           T.programcode AS key,
                           T.programname || '(' ||
                           TO_CHAR(MIN(pd.startdate), 'dd/MM/yyyy') || '-' ||
                           TO_CHAR(MAX(pd.enddate), 'dd/MM/yyyy') || ')' AS value,
                           pd.entrydate
                    FROM nerie.m_programs T
                    JOIN nerie.mt_programdetails pd
                        ON T.programcode = pd.programcode
                    JOIN nerie.t_programtimetable tt
                        ON tt.phaseid = pd.phaseid
                    WHERE T.officecode = :officecode
                      AND pd.finalized <> 'R'
                      AND (
                            T.enteredby = :usercode
                            OR EXISTS (
                                SELECT 1
                                FROM nerie.mt_program_members pm2
                                WHERE pm2.programcode = T.programcode
                                  AND pm2.usercode = :usercode
                                  AND (pm2.isheadcoordinator = '1'
                                       OR pm2.isdelegated = '1')
                            )
                          )
                    GROUP BY
                        T.programcode,
                        pd.entrydate
                    ORDER BY
                        pd.entrydate DESC
                    """, nativeQuery = true)
        List<Object[]> findProgramsForParticipantAttendanceRoleU(
                @Param("officecode") String officecode,
                @Param("usercode") String usercode);


        @Query(value = """
                        SELECT DISTINCT T.programcode AS key,
                               T.programname || '(' || TO_CHAR(min(pd.startdate),'dd/MM/yyyy') || '-' ||
                               TO_CHAR(max(pd.enddate),'dd/MM/yyyy') || ')' AS value,
                               pd.entrydate
                        FROM nerie.m_programs T
                        JOIN nerie.mt_programdetails pd ON T.programcode = pd.programcode
                        JOIN nerie.t_programtimetable tt ON tt.phaseid = pd.phaseid
                        JOIN nerie.mt_program_members m ON m.phaseid = pd.phaseid
                        WHERE m.usercode = :usercode
                        AND pd.finalized != 'R'
                        GROUP BY T.programcode, pd.enddate, pd.entrydate, pd.startdate
                        ORDER BY pd.entrydate DESC
                        """, nativeQuery = true)
        List<Object[]> findProgramsForParticipantAttendanceDefault(@Param("usercode") String usercode);

        @Transactional
        @Modifying
        @Query(value = "DELETE FROM nerie.t_participantattendance WHERE programtimetablecode = :programtimetablecode AND phaseid = :phaseid", 
                nativeQuery = true)
        int deleteByProgramTimetableCodeAndPhaseId(@Param("programtimetablecode") String programtimetablecode, @Param("phaseid") String phaseid);

        @Transactional
        @Modifying
        @Query(value = """
                        INSERT INTO nerie.t_participantattendance
                        (attendance, entryusercode, applicationcode, phaseid, programtimetablecode, pusercode)
                        VALUES ('P', :entryusercode, :applicationcode, :phaseid, :programtimetablecode,
                        (SELECT usercode FROM nerie.t_applications WHERE applicationcode = :applicationcode))
                        """, nativeQuery = true)
        int insertParticipantAttendance(@Param("entryusercode") String entryusercode, @Param("applicationcode") String applicationcode, @Param("phaseid") String phaseid, 
                @Param("programtimetablecode") String programtimetablecode);
}
