package com.nic.nerie.t_studentassignment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nic.nerie.t_studentassignment.model.T_StudentAssignment;

public interface T_StudentAssignmentRepository extends JpaRepository<T_StudentAssignment, String> {
        @Query(value = "SELECT s.subjectname, a.title, a.uploaddate, a.submissiondate, " +
                "NULL AS assignmentmarks, " +
                "NULL AS studentassignmentid " +
                "FROM nerie.t_assignmenttest a " +
                "JOIN nerie.m_subjects s ON a.subjectcode = s.subjectcode " +
                "WHERE a.submissiontype <> 'LINK' " +
                "AND a.uploaddate <= CURRENT_DATE " +   // <-- added
                "AND a.submissiondate >= CURRENT_DATE " +
                "AND a.subjectcode IN ( " +
                "    SELECT s.subjectcode " +
                "    FROM nerie.m_subjects s " +
                "    JOIN nerie.t_students stud ON s.coursecode = stud.coursecode " +
                "    WHERE stud.usercode = :usercode AND s.isoptional = '0' " +
                "      AND ( " +
                "            (stud.isshortterm = '1' AND s.sphaseid = stud.sphaseid) " +
                "         OR (stud.isshortterm = '0' AND s.semestercode = stud.semestercode) " +
                "          ) " +
                "    UNION " +
                "    SELECT ts.subjectcode " +
                "    FROM nerie.t_student_subject ts " +
                "    WHERE ts.usercode = :usercode " +
                ") " +
                "AND NOT EXISTS ( " +
                "    SELECT 1 FROM nerie.t_studentassignment sa " +
                "    WHERE sa.usercode = :usercode AND sa.assignmenttestid = a.assignmenttestid " +
                ") " +
                "ORDER BY a.submissiondate", nativeQuery = true)
        List<Object[]> findAssignmentDetailsByUsercode(@Param("usercode") String usercode);

        @Query("SELECT a FROM T_StudentAssignment a WHERE a.studentassignmentid = :studentassignmentid")
        Optional<T_StudentAssignment> findByStudentassignmentid(
                        @Param("studentassignmentid") String studentassignmentid);

//        @Query(value = "SELECT t1.assignmenttestid, t1.subjectcode, subs.subjectname, t1.reldoc, t1.title, " +
//                "t1.uploaddate, t1.submissiondate, " +
//                "(SELECT assignmentmark FROM nerie.t_studentassignment x " +
//                " WHERE x.usercode = :usercode AND x.assignmenttestid = t1.assignmenttestid LIMIT 1) AS assignmentmarks, " +
//                "(SELECT studentassignmentid FROM nerie.t_studentassignment x " +
//                " WHERE x.usercode = :usercode AND x.assignmenttestid = t1.assignmenttestid LIMIT 1) AS studentassignmentid, " +
//                "t1.description, t1.fullmark, t1.submissiontype " +
//                "FROM nerie.t_assignmenttest t1 " +
//                "JOIN nerie.m_subjects subs ON t1.subjectcode = subs.subjectcode " +
//                "JOIN nerie.t_faculty_subject fs ON fs.subjectcode = t1.subjectcode AND fs.usercode = t1.usercode " +
//                "WHERE t1.uploaddate <= CURRENT_DATE AND t1.subjectcode IN ( " +
//                "    SELECT s.subjectcode " +
//                "    FROM nerie.m_subjects s " +
//                "    JOIN nerie.t_students stud ON s.coursecode = stud.coursecode " +
//                "    WHERE stud.usercode = :usercode AND s.isoptional = '0' " +
//                "      AND ( " +
//                "           (stud.isshortterm = '1' AND s.sphaseid = stud.sphaseid) " +
//                "           OR " +
//                "           (stud.isshortterm = '0' AND s.semestercode = stud.semestercode) " +
//                "          ) " +
//                "    UNION " +
//                "    SELECT ts.subjectcode FROM nerie.t_student_subject ts WHERE ts.usercode = :usercode " +
//                ")",
//                nativeQuery = true)
//        List<Object[]> getSubmitAssignmentList(@Param("usercode") String usercode);
@Query(value = "SELECT * FROM ( " +
        "  SELECT t1.assignmenttestid, t1.subjectcode, subs.subjectname, t1.reldoc, t1.title, " +
        "  t1.uploaddate, t1.submissiondate, " +

        "  (SELECT sa.assignmentmark FROM nerie.t_studentassignment sa " +
        "   WHERE sa.assignmenttestid = t1.assignmenttestid " +
        "   AND sa.usercode = :usercode LIMIT 1) AS assignmentmarks, " +

        "  (SELECT sa.studentassignmentid FROM nerie.t_studentassignment sa " +
        "   WHERE sa.assignmenttestid = t1.assignmenttestid " +
        "   AND sa.usercode = :usercode LIMIT 1) AS studentassignmentid, " +

        "  t1.description, t1.fullmark, t1.submissiontype " +

        "  FROM nerie.t_assignmenttest t1 " +
        "  JOIN nerie.m_subjects subs ON t1.subjectcode = subs.subjectcode " +
        "  JOIN nerie.t_faculty_subject fs ON fs.subjectcode = t1.subjectcode AND fs.usercode = t1.usercode " +

        "  WHERE t1.uploaddate <= CURRENT_DATE " +

        // --- Filter by Semester/Phase logic ---
        "    AND (:semphase = '-1' OR CONCAT('S', CAST(subs.semestercode AS text)) = :semphase OR CONCAT('P', CAST(subs.sphaseid AS text)) = :semphase) " +

        // --- FIXED: Always filter by student's academicyear from t_students ---
        "    AND EXTRACT(YEAR FROM t1.uploaddate) >= CAST(SPLIT_PART( " +
        "        (SELECT stud.academicyear FROM nerie.t_students stud WHERE stud.usercode = :usercode LIMIT 1), '-', 1 " +
        "    ) AS INTEGER) " +
        "    AND EXTRACT(YEAR FROM t1.uploaddate) <= CAST(SPLIT_PART( " +
        "        (SELECT stud.academicyear FROM nerie.t_students stud WHERE stud.usercode = :usercode LIMIT 1), '-', 2 " +
        "    ) AS INTEGER) " +

        "    AND t1.subjectcode IN ( " +
        "        SELECT s.subjectcode " +
        "        FROM nerie.m_subjects s " +
        "        JOIN nerie.t_students stud ON s.coursecode = stud.coursecode " +
        "        WHERE stud.usercode = :usercode AND s.isoptional = '0' " +
        "          AND ( " +
        "               (stud.isshortterm = '1' AND s.sphaseid = stud.sphaseid) " +
        "               OR " +
        "               (stud.isshortterm = '0' AND s.semestercode = stud.semestercode) " +
        "              ) " +
        "        UNION " +
        "        SELECT ts.subjectcode FROM nerie.t_student_subject ts WHERE ts.usercode = :usercode " +
        "    ) " +

        ") AS subquery " +

        "ORDER BY " +
        "  CASE " +
        "    WHEN subquery.studentassignmentid IS NULL THEN 1 " +
        "    WHEN subquery.assignmentmarks IS NULL THEN 2 " +
        "    ELSE 3 " +
        "  END ASC, " +
        "  subquery.submissiondate ASC",

        nativeQuery = true)
List<Object[]> getSubmitAssignmentList(@Param("usercode") String usercode, @Param("semphase") String semphase);

        @Query("SELECT s FROM T_StudentAssignment s WHERE s.usercode.usercode = :usercode AND s.assignmenttestid.assignmenttestid = :assignmenttestid")
        T_StudentAssignment findByUsercodeAndAssignmentTestId(@Param("usercode") String usercode, @Param("assignmenttestid") String assignmenttestid);

        @Query(value = "SELECT * FROM nerie.t_studentassignment WHERE assignmenttestid = :asid ORDER BY usercode", nativeQuery = true)
        List<T_StudentAssignment> getSubmittedAssignmentsByAssignmentTestId(@Param("asid") String asid);

        @Query(value = "SELECT s.rollno, s.fname, s.lname " +
                "FROM nerie.T_StudentAssignment t " +
                "JOIN nerie.T_Students s ON t.usercode = s.usercode " +
                "WHERE t.assignmenttestid = :asid " +
                "ORDER BY t.usercode", nativeQuery = true)
        List<Object[]> getSubmittedAssignmentStudents(@Param("asid") String asid);

        @Query(value = "SELECT * FROM t_studentassignment " +
                        "WHERE assignmenttestid = :fid AND usercode = :sid", nativeQuery = true)
        Optional<T_StudentAssignment> findStudentAssignmentDocument(@Param("fid") String assignmentTestId, @Param("sid") String userCode);

        @Query("from T_StudentAssignment where assignmenttestid.assignmenttestid=:assignmenttestid and usercode.usercode=:usercode")
        Optional<T_StudentAssignment> findByAssignmentidAndUsercode(@Param("assignmentid") String assignmenttestid, @Param("usercode") String usercode);

        @Query("select max(cast(studentassignmentid as int)) from T_StudentAssignment")
        Integer getLastUsedAssignmentid();

        @Query(value = "SELECT MAX(CAST(studentassignmentid AS INTEGER)) FROM nerie.t_studentassignment", nativeQuery = true)
        Integer findMaxStudentAssignmentId();

        @Query(value =
                "SELECT DISTINCT " +
                "stud.usercode, " +
                "CONCAT_WS(' ', stud.fname, stud.mname, stud.lname) AS studentname, " +
                "CASE " +
                "   WHEN stud.isshortterm = '1' THEN stud.sphaseid " +
                "   ELSE stud.semestercode " +
                "END AS currentperiod " +
                "FROM nerie.m_subjects s " +
                "JOIN nerie.t_students stud ON s.coursecode = stud.coursecode " +
                "WHERE s.subjectcode = :subjectcode " +
                "AND s.isoptional = '0' " +
                "AND ( " +
                "       (stud.isshortterm = '1' AND s.sphaseid = stud.sphaseid) " +
                "       OR " +
                "       (stud.isshortterm = '0' AND s.semestercode = stud.semestercode) " +
                ") " +

                "UNION " +

                "SELECT DISTINCT " +
                "stud.usercode, " +
                "CONCAT_WS(' ', stud.fname, stud.mname, stud.lname) AS studentname, " +
                "CASE " +
                "   WHEN stud.isshortterm = '1' THEN stud.sphaseid " +
                "   ELSE stud.semestercode " +
                "END AS currentperiod " +
                "FROM nerie.t_student_subject ts " +
                "JOIN nerie.t_students stud ON ts.usercode = stud.usercode " +
                "WHERE ts.subjectcode = :subjectcode",
                nativeQuery = true)
        List<Object[]> getStudentsBySubject(@Param("subjectcode") String subjectcode);

}
