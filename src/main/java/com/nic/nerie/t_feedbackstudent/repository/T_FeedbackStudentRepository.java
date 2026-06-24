package com.nic.nerie.t_feedbackstudent.repository;

import com.nic.nerie.t_feedbackstudent.model.T_FeedbackStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;

public interface T_FeedbackStudentRepository extends JpaRepository<T_FeedbackStudent, String> {
    @Query(value = "SELECT s.subjectcode, s.subjectname, COALESCE(e.semestername, p.sphasename) AS term_name, f.facultyid " +
            "FROM nerie.t_faculty_subject t " +
            "JOIN nerie.m_subjects s ON t.subjectcode = s.subjectcode " +
            "LEFT JOIN nerie.m_semesters e ON s.semestercode = e.semestercode " +
            "LEFT JOIN nerie.m_shortterm_phases p ON s.sphaseid = p.sphaseid " +
            "JOIN nerie.t_faculties f ON t.usercode = f.usercode " +
            "WHERE t.usercode = :usercode",
            nativeQuery = true)
    List<Object[]> getSubjectsListFeed(@Param("usercode") String usercode);

    @Query(value = "SELECT f.feedback, TO_CHAR(f.entrydate, 'MM/DD/YYYY HH12:MI AM'), f.studentid, " +
            "CONCAT(s.fname, ' ', s.mname, ' ', s.lname) AS student_name " +
            "FROM nerie.t_feedback_student f " +
            "JOIN nerie.t_students s ON f.studentid = s.studentid " +
            "JOIN nerie.t_faculties fac ON f.facultyid = fac.facultyid " +
            "WHERE f.subjectcode = :subjectcode AND fac.usercode = :usercode", nativeQuery = true)
    List<Object[]> getStudentsFeedbackList(@Param("subjectcode") String subjectcode,
                                           @Param("usercode") String usercode);

//     @Query(value = """
//         SELECT COALESCE(MAX(CAST(feedbackid AS INTEGER)),0)FROM nerie.t_feedback_student
//                 """,
//                 nativeQuery = true)
//         Integer findMaxFeedbackId();

    @Query(value = "SELECT s.subjectcode, s.subjectname, " +
            "CONCAT(st.fname, ' ', st.mname, ' ', st.lname) AS faculty_name, " +
            "COALESCE(e.semestername, p.sphasename) AS term_name " +
            "FROM nerie.t_student_subject ts " +
            "JOIN nerie.m_subjects s ON ts.subjectcode = s.subjectcode " +
            "LEFT JOIN nerie.m_semesters e ON s.semestercode = e.semestercode " +
            "LEFT JOIN nerie.m_shortterm_phases p ON s.sphaseid = p.sphaseid " +
            "LEFT JOIN nerie.t_faculty_subject fs ON fs.subjectcode = s.subjectcode " +
            "LEFT JOIN nerie.t_faculties f ON fs.usercode = f.usercode " +
            "LEFT JOIN nerie.mt_userlogin u ON f.usercode = u.usercode " +
            "LEFT JOIN nerie.t_students st ON st.usercode = u.usercode " +
            "WHERE ts.usercode = :usercode AND ts.isactive = '1'",
            nativeQuery = true)
    List<Object[]> getSubjectsListForStudent(@Param("usercode") String usercode);
}
