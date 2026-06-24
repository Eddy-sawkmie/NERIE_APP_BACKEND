package com.nic.nerie.t_faculties.repository;

import com.nic.nerie.t_faculties.model.T_Faculties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface T_FacultiesRepository extends JpaRepository<T_Faculties, String> {
    @Query(value = "SELECT CONCAT(f.fname, ' ', f.mname, ' ', f.lname) AS facultyname, " +
            "d.departmentname, " +
            "STRING_AGG(c.coursename, ',') AS coursenames " +
            "FROM nerie.t_faculties f " +
            "INNER JOIN nerie.t_faculty_courses fac ON fac.usercode = f.usercode " +
            "INNER JOIN nerie.m_departments d ON d.departmentcode = f.departmentcode " +
            "INNER JOIN nerie.m_course_academics c ON fac.coursecode = c.coursecode " +
            "WHERE f.usercode = :usercode " +
            "GROUP BY f.fname, f.mname, f.lname, d.departmentname",
            nativeQuery = true)
    List<Object[]> getDeptAndFacultyDetails(@Param("usercode") String usercode);

    @Query(value = "SELECT * FROM nerie.t_faculties WHERE usercode = :usercode", nativeQuery = true)
    T_Faculties getFacultyByUsercode(@Param("usercode") String usercode);

    @Query(value = """
        SELECT f.usercode, f.facultyid, f.fname, f.mname, f.lname, 
               d.designationname, e.departmentname,
               array_to_string(
                   ARRAY(
                       SELECT t.subjectname 
                       FROM nerie.m_subjects t, nerie.t_faculty_subject q
                       WHERE q.usercode = f.usercode AND q.subjectcode = t.subjectcode
                   ), ','
               ) AS subjects,
               array_to_string(
                   ARRAY(
                       SELECT fac.coursename 
                       FROM nerie.t_faculty_courses fc, nerie.m_course_academics fac
                       WHERE fc.usercode = f.usercode AND fac.coursecode = fc.coursecode
                   ), ','
               ) AS courses
        FROM nerie.t_faculties f
        JOIN nerie.m_designations d ON f.designationcode = d.designationcode
        JOIN nerie.m_departments e ON f.departmentcode = e.departmentcode
        WHERE f.usercode = :usercode AND f.officecode = :officecode
        GROUP BY f.usercode, f.facultyid, f.fname, f.mname, f.lname, 
                 d.designationcode, d.designationname,
                 e.departmentcode, e.departmentname
        """, nativeQuery = true)
    List<Object[]> findFacultySubjectsAndCoursesByUsercode(@Param("usercode") String usercode,
                                                           @Param("officecode") String officecode);

    @Query(value = """
        SELECT
            f.usercode, f.facultyid, f.fname, f.mname, f.lname, 
            d.designationname, e.departmentname,
            array_to_string(
                ARRAY(
                    SELECT t.subjectname 
                    FROM nerie.m_subjects t, nerie.t_faculty_subject q
                    WHERE q.usercode = f.usercode AND q.subjectcode = t.subjectcode
                ), ','
            ) AS subjects,
            array_to_string(
                ARRAY(
                    SELECT fac.coursename 
                    FROM nerie.t_faculty_courses fc, nerie.m_course_academics fac
                    WHERE fc.usercode = f.usercode AND fac.coursecode = fc.coursecode
                ), ','
            ) AS courses
        FROM 
            nerie.t_faculties f
        JOIN nerie.mt_userlogin u ON f.usercode = u.usercode
        JOIN nerie.m_designations d ON f.designationcode = d.designationcode
        JOIN nerie.m_departments e ON f.departmentcode = e.departmentcode
        WHERE 
            u.enabled = 1 AND u.userrole = 'U' AND f.officecode = :officecode
        GROUP BY 
            f.usercode, f.facultyid, f.fname, f.mname, f.lname, 
            d.designationcode, d.designationname,
            e.departmentcode, e.departmentname
        """, nativeQuery = true)
    List<Object[]> findAllFacultySubjectsAndCourses(@Param("officecode") String officecode);

    @Query(value = """
    SELECT f.usercode, f.facultyid, f.fname, f.mname, f.lname, d.designationcode, d.designationname, e.departmentcode, e.departmentname,
        array_to_string(
            ARRAY(
                SELECT fac.coursecode 
                FROM nerie.t_faculty_courses fc, nerie.m_course_academics fac
                WHERE fc.usercode = f.usercode AND fac.coursecode = fc.coursecode
            ), ','
        ) AS courses
    FROM 
        nerie.t_faculties f
        JOIN nerie.m_designations d ON f.designationcode = d.designationcode
        JOIN nerie.m_departments e ON f.departmentcode = e.departmentcode
        JOIN nerie.t_faculty_subject fs ON f.usercode = fs.usercode
        JOIN nerie.m_subjects s ON fs.subjectcode = s.subjectcode
    WHERE 
        f.usercode = :usercode
    GROUP BY 
        f.facultyid, f.fname, f.mname, f.lname, 
        d.designationcode, d.designationname,
        e.departmentcode, e.departmentname
    """, nativeQuery = true)
    List<Object[]> findFacultyDetailsByUsercode(@Param("usercode") String usercode);

    @Query(value = "SELECT MAX(CAST(facultyid AS INTEGER)) FROM nerie.t_faculties", nativeQuery = true)
    Integer findMaxFacultyId();

    @Modifying
    @Query(value = "DELETE FROM nerie.t_faculty_subject WHERE usercode = :usercode", nativeQuery = true)
    void deleteFacultySubjectByUsercode(@Param("usercode") String usercode);

    @Modifying
    @Query(value = "INSERT INTO nerie.t_faculty_subject (usercode, subjectcode) VALUES (:usercode, :subjectcode)", nativeQuery = true)
    void insertFacultySubject(@Param("usercode") String usercode, @Param("subjectcode") String subjectcode);

    @Modifying
    @Query(value = "DELETE FROM nerie.t_faculty_courses WHERE usercode = :usercode", nativeQuery = true)
    void deleteFacultyCoursesByUsercode(@Param("usercode") String usercode);

    @Modifying
    @Query(value = "INSERT INTO nerie.t_faculty_courses (usercode, coursecode) VALUES (:usercode, :coursecode)", nativeQuery = true)
    void insertFacultyCourse(@Param("usercode") String usercode, @Param("coursecode") String coursecode);

    @Query(value = "SELECT * FROM nerie.t_faculties WHERE facultyid = :facultyid", nativeQuery = true)
    T_Faculties findFacultyByFacultyId(@Param("facultyid") String facultyid);

    @Query(value = """
        SELECT ul.username, d.designationname, dept.departmentname, ul.usercode 
        FROM nerie.mt_userlogin ul 
        INNER JOIN nerie.m_designations d ON ul.designationcode = d.designationcode 
        INNER JOIN nerie.t_faculties f ON ul.usercode = f.usercode 
        INNER JOIN nerie.m_departments dept ON f.departmentcode = dept.departmentcode
        WHERE ul.userrole = 'U' 
          AND ul.officecode = :officecode 
          AND ul.iscurrentfaculty = '1'
        ORDER BY ul.userid
        """, nativeQuery = true)
    List<Object[]> findFacultyResearchList(@Param("officecode") String officecode);
}
