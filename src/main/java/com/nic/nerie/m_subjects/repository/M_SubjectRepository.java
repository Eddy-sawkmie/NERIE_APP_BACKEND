package com.nic.nerie.m_subjects.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nic.nerie.m_subjects.model.M_Subjects;

public interface M_SubjectRepository extends JpaRepository<M_Subjects, String> {

        // Long-Term Compulsory Subjects
        @Query(value = "SELECT s.subjectname, " +
                "       (f.fname || ' ' || COALESCE(f.mname, '') || ' ' || f.lname) AS teacher, " +
                "       f.facultyid, " +
                "       s.subjectcode, " +
                "       COUNT(CASE WHEN att.attendancestatus = 'P' THEN 1 END) AS present_count, " +
                "       COUNT(CASE WHEN att.attendancestatus = 'A' THEN 1 END) AS absent_count, " +
                "       COUNT(att.attendancestatus) AS total_count " +
                "FROM nerie.m_subjects s " +
                "JOIN nerie.t_faculty_subject fs ON fs.subjectcode = s.subjectcode " +
                "JOIN nerie.t_faculties f ON fs.usercode = f.usercode " +
                "JOIN nerie.mt_userlogin u ON f.usercode = u.usercode " + // <-- ADDED JOIN
                "LEFT JOIN nerie.t_studentsattendance att ON att.subjectcode = s.subjectcode AND att.studentid = :studentid AND att.usercode = f.usercode " +
                "WHERE s.semestercode = :semestercode AND s.coursecode = :coursecode AND s.isoptional = '0' " +
                "AND u.enabled = 1 " + // <-- ADDED FILTER
                "GROUP BY s.subjectname, s.subjectcode, teacher, f.facultyid " +
                "ORDER BY s.subjectname, teacher", nativeQuery = true)
        List<Object[]> getGeneralStudentFacultySubjectListLongterm(@Param("semestercode") String semestercode,
                                                                   @Param("coursecode") String coursecode,
                                                                   @Param("studentid") String studentid);

//        // Optional Subjects
//        @Query(value = "SELECT s.subjectname, " +
//                "       (f.fname || ' ' || COALESCE(f.mname, '') || ' ' || f.lname) AS teachers, " +
//                "       s.subjectcode, " +
//                "       COALESCE(att_summary.present_count, 0) AS present_count, " +
//                "       COALESCE(att_summary.absent_count, 0) AS absent_count, " +
//                "       COALESCE(att_summary.total_count, 0) AS total_count " +
//                "FROM nerie.t_student_subject ts " +
//                "JOIN nerie.m_subjects s ON ts.subjectcode = s.subjectcode " +
//                "JOIN nerie.t_faculty_subject fs ON fs.subjectcode = s.subjectcode " +
//                "JOIN nerie.t_faculties f ON fs.usercode = f.usercode " +
//                "JOIN nerie.mt_userlogin u ON f.usercode = u.usercode " + // <-- ADDED JOIN
//                "LEFT JOIN ( " +
//                "    SELECT subjectcode, usercode, " +
//                "           COUNT(CASE WHEN attendancestatus = 'P' THEN 1 END) AS present_count, " +
//                "           COUNT(CASE WHEN attendancestatus = 'A' THEN 1 END) AS absent_count, " +
//                "           COUNT(*) AS total_count " +
//                "    FROM nerie.t_studentsattendance " +
//                "    WHERE studentid = :studentid " +
//                "    GROUP BY subjectcode, usercode " +
//                ") att_summary ON att_summary.subjectcode = s.subjectcode AND att_summary.usercode = f.usercode " +
//                "WHERE ts.usercode = :usercode AND ts.isactive = '1' " +
//                "AND u.enabled = 1 " + // <-- ADDED FILTER
//                "ORDER BY s.subjectname, teachers", nativeQuery = true)
//        List<Object[]> getoptionalstudentfacultysubjectlist(@Param("usercode") String usercode,
//                                                            @Param("studentid") String studentid);

        // Optional Subjects
        @Query(value = "SELECT s.subjectname, " +
                "       (f.fname || ' ' || COALESCE(f.mname, '') || ' ' || f.lname) AS teachers, " +
                "       f.facultyid, " +
                "       s.subjectcode, " +
                "       COALESCE(att_summary.present_count, 0) AS present_count, " +
                "       COALESCE(att_summary.absent_count, 0) AS absent_count, " +
                "       COALESCE(att_summary.total_count, 0) AS total_count " +
                "FROM nerie.t_student_subject ts " +
                "JOIN nerie.m_subjects s ON ts.subjectcode = s.subjectcode " +
                "JOIN nerie.t_faculty_subject fs ON fs.subjectcode = s.subjectcode " +
                "JOIN nerie.t_faculties f ON fs.usercode = f.usercode " +
                "JOIN nerie.mt_userlogin u ON f.usercode = u.usercode " +
                "LEFT JOIN ( " +
                "    SELECT subjectcode, usercode, " +
                "           COUNT(CASE WHEN attendancestatus = 'P' THEN 1 END) AS present_count, " +
                "           COUNT(CASE WHEN attendancestatus = 'A' THEN 1 END) AS absent_count, " +
                "           COUNT(*) AS total_count " +
                "    FROM nerie.t_studentsattendance " +
                "    WHERE studentid = :studentid " +
                "    GROUP BY subjectcode, usercode " +
                ") att_summary ON att_summary.subjectcode = s.subjectcode AND att_summary.usercode = f.usercode " +
                "WHERE ts.usercode = :usercode AND ts.isactive = '1' " +
                "AND u.enabled = 1 " +
                "ORDER BY s.subjectname, teachers", nativeQuery = true)
        List<Object[]> getoptionalstudentfacultysubjectlist(@Param("usercode") String usercode,
                                                            @Param("studentid") String studentid);


        // New method for optional course subject list
        @Query(value = "SELECT s.subjectname, s.subjectcode, " +
                "string_agg(DISTINCT(f.fname || ' ' || COALESCE(f.mname, '') || ' ' || f.lname), ', ') as teachers, " +
                "MAX(f.facultyid) as facultyid " +
                "FROM nerie.t_student_subject t " +
                "JOIN nerie.m_subjects s ON t.subjectcode = s.subjectcode " +
                "JOIN nerie.t_faculty_subject fs ON fs.subjectcode = s.subjectcode " +
                "JOIN nerie.t_faculties f ON fs.usercode = f.usercode " +
                "WHERE t.usercode = :usercode AND t.isactive = '1' " +
                "GROUP BY s.subjectname, s.subjectcode", nativeQuery = true)
        List<Object[]> getStudentSubjectsList(@Param("usercode") String usercode);

        // New method for Long-Term course compulsory subjects
        @Query(value = "SELECT s.subjectname, s.subjectcode, " +
                "string_agg(DISTINCT(f.fname || ' ' || COALESCE(f.mname, '') || ' ' || f.lname), ', ') as teachers, " +
                "MAX(f.facultyid) as facultyid " +
                "FROM nerie.m_subjects s " +
                "JOIN nerie.t_faculty_subject fs ON fs.subjectcode = s.subjectcode " +
                "JOIN nerie.t_faculties f ON fs.usercode = f.usercode " +
                "WHERE s.semestercode = :semestercode AND s.coursecode = :coursecode AND s.isoptional = '0' " +
                "GROUP BY s.subjectname, s.subjectcode", nativeQuery = true)
        List<Object[]> getCompulsorySubjectsLongTerm(@Param("semestercode") String semestercode, @Param("coursecode") String coursecode);

        // New method for Short-Term course compulsory subjects
        @Query(value = "SELECT s.subjectname, s.subjectcode, " +
                "string_agg(DISTINCT(f.fname || ' ' || COALESCE(f.mname, '') || ' ' || f.lname), ', ') as teachers, " +
                "MAX(f.facultyid) as facultyid " +
                "FROM nerie.m_subjects s " +
                "JOIN nerie.t_faculty_subject fs ON fs.subjectcode = s.subjectcode " +
                "JOIN nerie.t_faculties f ON fs.usercode = f.usercode " +
                "WHERE s.sphaseid = :sphaseid AND s.coursecode = :coursecode AND s.isoptional = '0' " +
                "GROUP BY s.subjectname, s.subjectcode", nativeQuery = true)
        List<Object[]> getCompulsorySubjectsShortTerm(@Param("sphaseid") String sphaseid, @Param("coursecode") String coursecode);

        @Query("FROM M_Subjects ORDER BY subjectcode")
        List<M_Subjects> findAllByOrderBySubjectcodeAsc();

        @Query("SELECT s FROM M_Subjects s " +
                "WHERE s.departmentcode.moffices.officecode = :officecode " +
                "ORDER BY s.subjectcode ASC")
        List<M_Subjects> findAllByOfficeCode(@Param("officecode") String officecode);

        // New Query
        @Query("SELECT s FROM M_Subjects s WHERE s.coursecode.coursecode IN :courseCodes ORDER BY s.subjectname ASC")
        List<M_Subjects> findSubjectsByCourseCodes(@Param("courseCodes") List<String> courseCodes);

        @Query(value = "SELECT s.* FROM m_subjects s INNER JOIN t_faculty_subject fs on s.subjectcode = fs.subjectcode WHERE s.coursecode = :courseCode AND fs.usercode = :usercode",nativeQuery = true)
        List<M_Subjects> findSubjectsByCourseCodeFacultyWise(@Param("courseCode") String courseCode, @Param("usercode") String usercode);

        @Query("FROM M_Subjects WHERE departmentcode.departmentcode = :departmentcode " +
                "AND semestercode.semestercode = :semestercode " +
                "AND coursecode.coursecode = :coursecode " +
                "ORDER BY subjectcode")
        List<M_Subjects> findByDepartmentAndSemesterAndCourse(
                @Param("departmentcode") String departmentcode,
                @Param("semestercode") String semestercode,
                @Param("coursecode") String coursecode);

        @Query("FROM M_Subjects WHERE departmentcode.departmentcode = :departmentcode " +
                "AND sphaseid.sphaseid = :sphaseid " +
                "AND coursecode.coursecode = :coursecode " +
                "ORDER BY subjectcode")
        List<M_Subjects> findByDepartmentAndPhaseAndCourse(
                @Param("departmentcode") String departmentcode,
                @Param("sphaseid") String sphaseid,
                @Param("coursecode") String coursecode);

        @Query("FROM M_Subjects m WHERE m.departmentcode.departmentcode = :departmentcode " +
                "AND m.semestercode.semestercode = :spcode AND m.isoptional = '1'")
        List<M_Subjects> findNextSemesterOptionalSubjects(@Param("departmentcode") String departmentcode,
                                                          @Param("spcode") String spcode);

        @Query("FROM M_Subjects m WHERE m.departmentcode.departmentcode = :departmentcode " +
                "AND m.sphaseid.sphaseid = :spcode AND m.isoptional = '1'")
        List<M_Subjects> findNextPhaseOptionalSubjects(@Param("departmentcode") String departmentcode,
                                                       @Param("spcode") String spcode);

        @Query(value = "SELECT DISTINCT s.subjectcode, s.subjectname " +
                "FROM nerie.m_subjects s " +
                "JOIN nerie.t_faculty_subject t ON s.subjectcode = t.subjectcode " +
                "WHERE t.usercode = :usercode", nativeQuery = true)
        List<Object[]> getSubjectsList(@Param("usercode") String usercode);

        @Query(value =
                "SELECT DISTINCT s.subjectcode, s.subjectname " +
                        "FROM nerie.m_subjects s " +
                        "JOIN nerie.t_faculty_subject t ON s.subjectcode = t.subjectcode " +
                        "WHERE t.usercode = :usercode " +
                        "AND ( " +
                        "      (:semphase LIKE 'S%' AND s.semestercode = SUBSTRING(:semphase,2)) " +
                        "   OR (:semphase LIKE 'P%' AND s.sphaseid = SUBSTRING(:semphase,2)) " +
                        ")",
                nativeQuery = true)
        List<Object[]> getSubjectsListBySemPhaseAndUsercode(
                @Param("usercode") String usercode,
                @Param("semphase") String semphase);

        @Query(value = "SELECT * FROM nerie.m_subjects WHERE subjectcode = :subjectcode", nativeQuery = true)
        M_Subjects getSubjectBySubjectCode(@Param("subjectcode") String subjectcode);

        @Query(value = "SELECT " +
                //    0              1             2                      3
                "s.subjectcode, s.subjectname, d.departmentname, st.semestername, " +
                //    4                 5                6              7
                "s.departmentcode, s.semestercode, stp.sphasename, s.sphaseid, " +
                //    8               9            10            11
                "s.isshortterm, s.coursecode, c.coursename, s.isoptional " +
                "FROM nerie.m_subjects s " +
                "INNER JOIN nerie.m_departments d ON d.departmentcode = s.departmentcode " +
                "LEFT JOIN nerie.m_semesters st ON st.semestercode = s.semestercode " +
                "LEFT JOIN nerie.m_shortterm_phases stp ON stp.sphaseid = s.sphaseid " +
                "LEFT JOIN nerie.m_course_academics c ON c.coursecode = s.coursecode " +
                "WHERE s.subjectname IS NOT NULL " +
                "AND s.departmentcode = :dcode " +
                "AND s.sphaseid = :sphase " +
                "AND c.coursecode = :coursecode " +
                "ORDER BY s.subjectcode", nativeQuery = true)
        List<Object[]> getSubjectListByPhaseid(@Param("dcode") String departmentCode,
                                               @Param("sphase") String shortTermPhaseId,
                                               @Param("coursecode") String courseCode);

        @Query(value = "SELECT " +
                //    0              1             2                      3
                "s.subjectcode, s.subjectname, d.departmentname, st.semestername, " +
                //    4                 5                6              7
                "s.departmentcode, s.semestercode, stp.sphasename, s.sphaseid, " +
                //    8               9            10            11
                "s.isshortterm, s.coursecode, c.coursename, s.isoptional " +
                "FROM nerie.m_subjects s " +
                "INNER JOIN nerie.m_departments d ON d.departmentcode = s.departmentcode " +
                "LEFT JOIN nerie.m_semesters st ON st.semestercode = s.semestercode " +
                "LEFT JOIN nerie.m_shortterm_phases stp ON stp.sphaseid = s.sphaseid " +
                "LEFT JOIN nerie.m_course_academics c ON c.coursecode = s.coursecode " +
                "WHERE s.subjectname IS NOT NULL " +
                "AND s.departmentcode = :dcode " +
                "AND s.semestercode = :scode " +
                "AND c.coursecode = :coursecode " +
                "ORDER BY s.subjectcode", nativeQuery = true)
        List<Object[]> getSubjectListBySemestercode(@Param("dcode") String departmentCode,
                                                    @Param("scode") String semesterCode,
                                                    @Param("coursecode") String courseCode);

        @Query(value = "SELECT MAX(CAST(subjectcode AS INTEGER)) FROM nerie.m_subjects", nativeQuery = true)
        Integer getMaxSubjectCode();

        @Query(value = "SELECT * FROM m_subjects ORDER BY subjectcode", nativeQuery = true)
        List<M_Subjects> findAllSubjectsList();

        @Query(value = "SELECT s.subjectcode, s.subjectname " + // Only subjectcode and subjectname are strictly needed
                // by JS for display, but subjectcode is key
                "FROM nerie.t_faculty_subject tfs " +
                "JOIN nerie.m_subjects s ON tfs.subjectcode = s.subjectcode " +
                // "JOIN nerie.m_semesters e ON s.semestercode = e.semestercode " +
                "WHERE tfs.usercode = :usercode " +
                "ORDER BY s.subjectcode", nativeQuery = true)
        List<Object[]> findSubjectsByFacultyUsercode(@Param("usercode") String usercode);

        // Short-Term Compulsory Subjects
        @Query(value = "SELECT s.subjectname, " +
                "       (f.fname || ' ' || COALESCE(f.mname, '') || ' ' || f.lname) as teacher, " +
                "       f.facultyid, " +
                "       s.subjectcode, " +
                "       COUNT(CASE WHEN att.attendancestatus = 'P' THEN 1 END) as present_count, " +
                "       COUNT(CASE WHEN att.attendancestatus = 'A' THEN 1 END) as absent_count, " +
                "       COUNT(att.attendancestatus) as total_count " +
                "FROM nerie.m_subjects s " +
                "JOIN nerie.t_faculty_subject fs ON fs.subjectcode = s.subjectcode " +
                "JOIN nerie.t_faculties f ON fs.usercode = f.usercode " +
                "JOIN nerie.mt_userlogin u ON f.usercode = u.usercode " + // <-- ADDED JOIN
                "LEFT JOIN nerie.t_studentsattendance att ON att.subjectcode = s.subjectcode AND att.studentid = :studentid AND att.usercode = f.usercode " +
                "WHERE s.sphaseid = :sphaseid AND s.coursecode = :coursecode AND s.isoptional = '0' " +
                "AND u.enabled = 1 " + // <-- ADDED FILTER
                "GROUP BY s.subjectname, s.subjectcode, teacher, f.facultyid " +
                "ORDER BY s.subjectname, teacher", nativeQuery = true)
        List<Object[]> getGeneralStudentFacultySubjectListShortterm(@Param("sphaseid") String sphaseid,
                                                                    @Param("coursecode") String coursecode,
                                                                    @Param("studentid") String studentid);

        @Query("SELECT COUNT(s) > 0 FROM M_Subjects s WHERE s.departmentcode.departmentcode = :departmentCode " +
                "AND s.coursecode.coursecode = :courseCode " +
                "AND s.subjectname = :subjectName")
        Boolean existsByDepartmentAndCourseAndSubjectName(@Param("departmentCode") String departmentCode, @Param("courseCode") String courseCode, @Param("subjectName") String subjectName);

        @Query("SELECT COUNT(s) > 0 FROM M_Subjects s WHERE s.departmentcode.departmentcode = :departmentCode " +
                "AND s.coursecode.coursecode = :courseCode " +
                "AND s.subjectname = :subjectName " +
                "AND s.subjectcode <> :subjectCode")
        Boolean existsWithDifferentCode(@Param("departmentCode") String departmentCode,
                                        @Param("courseCode") String courseCode,
                                        @Param("subjectName") String subjectName,
                                        @Param("subjectCode") String subjectCode);
}