package com.nic.nerie.mt_test.repository;

import com.nic.nerie.mt_test.model.MT_Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MT_TestRepository extends JpaRepository<MT_Test, String> {
    @Query(value = "SELECT t.testid, t.testno, t.testdate, t.testname, t.passmark, t.fullmark, " +
            "s.subjectcode, s.subjectname, COALESCE(se.semestername, ph.phasedescription) AS term_name " +
            "FROM nerie.t_faculty_subject f " +
            "JOIN nerie.m_subjects s ON f.subjectcode = s.subjectcode " +
            "LEFT JOIN nerie.m_semesters se ON s.semestercode = se.semestercode " +
            "LEFT JOIN nerie.m_phases ph ON s.sphaseid = ph.phaseid " +
            "JOIN nerie.mt_test t ON t.subjectcode = s.subjectcode AND t.usercode = f.usercode " +
            "WHERE f.usercode = :usercode " +
            "ORDER BY COALESCE(se.semestercode, ph.phaseno)",
            nativeQuery = true)
    List<Object[]> getTestList(@Param("usercode") String usercode);

    @Query(value = "SELECT t.testid, t.testno, t.testdate, t.testname, t.passmark, t.fullmark, " +
            "s.subjectcode, s.subjectname, COALESCE(se.semestername, mstp.sphasename) AS term_name " +
            "FROM nerie.t_faculty_subject f " +
            "JOIN nerie.m_subjects s ON f.subjectcode = s.subjectcode " +
            "LEFT JOIN nerie.m_semesters se ON s.semestercode = se.semestercode " +
            "LEFT JOIN nerie.m_shortterm_phases mstp ON s.sphaseid = mstp.sphaseid " +
            "JOIN nerie.mt_test t ON t.subjectcode = s.subjectcode AND t.usercode = f.usercode " +
            "WHERE f.usercode = :usercode AND s.subjectcode = :subjectcode " +
            "ORDER BY t.testname",
            nativeQuery = true)
    List<Object[]> getTestsBySubjectAndUser(@Param("usercode") String usercode, @Param("subjectcode") String subjectcode);

    @Query(value = "SELECT COALESCE(MAX(CAST(testid AS INTEGER)), 0) FROM nerie.mt_test", nativeQuery = true)
    Integer getMaxTestId();

    @Query("SELECT t.fullmark FROM MT_Test t WHERE t.testid = :testid")
    String getFullMarkByTestid(@Param("testid") String testid);
}
