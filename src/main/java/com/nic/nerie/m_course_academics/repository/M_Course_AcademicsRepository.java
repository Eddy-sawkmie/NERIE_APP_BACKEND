package com.nic.nerie.m_course_academics.repository;

import com.nic.nerie.m_course_academics.model.M_Course_Academics;
import com.nic.nerie.m_semesters.model.M_Semesters;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface M_Course_AcademicsRepository extends JpaRepository<M_Course_Academics, String> {
    @Query(value = "SELECT m FROM M_Course_Academics m WHERE m.departmentcode.departmentcode = :departmentcode " +
        "AND (:isshortterm IS NULL OR m.isshortterm = :isshortterm) " +
        "ORDER BY m.coursecode, m.coursename")
    List<M_Course_Academics> findByDepartmentCodeAndShortTerm(@Param("departmentcode") String departmentcode, @Param("isshortterm") String isshortterm);

    @Query(value = "select * from nerie.m_course_academics where departmentcode=:departmentcode order by coursecode, coursename", nativeQuery = true)
    List<Object[]> getByDepartmentcodeOrderByCoursecodeCoursenameAsc(String departmentcode);
    
    @Query(value = "SELECT * FROM m_course_academics WHERE departmentcode = :departmentcode",nativeQuery = true)
    List<M_Course_Academics> findByDepartmentCode(@Param("departmentcode") String departmentcode);
    
    @Query(value = "SELECT * FROM nerie.m_course_academics " +
            "WHERE departmentcode = :departmentcode " +
            "AND (:isshortterm IS NULL OR isshortterm = :isshortterm) " +
            "ORDER BY coursecode, coursename",
            nativeQuery = true)
    List<Object[]> findCoursesByDepartmentAndShortTerm(@Param("departmentcode") String departmentcode,
                                                       @Param("isshortterm") String isshortterm);

    @Query(value = "SELECT c.coursecode, c.coursename, d.departmentname, d.departmentcode, c.courseid, c.isshortterm, c.duration " +
            "FROM nerie.m_course_academics c " +
            "INNER JOIN nerie.m_departments d ON c.departmentcode = d.departmentcode " +
            "WHERE (:dcode IS NULL OR d.departmentcode = :dcode) " +
            "ORDER BY d.departmentcode",
            nativeQuery = true)
    List<Object[]> findCoursesWithDepartmentDetails(@Param("dcode") String dcode);

    @Query(value = "SELECT COUNT(*) > 0 FROM nerie.m_course_academics " +
            "WHERE UPPER(coursename) = UPPER(:cname) " +
            "AND departmentcode = :dcode",
            nativeQuery = true)
    boolean existsByCourseNameAndDepartmentCode(@Param("cname") String courseName,
                                                @Param("dcode") String departmentCode);

    @Query(value = "SELECT MAX(CAST(coursecode AS INTEGER)) FROM nerie.m_course_academics", nativeQuery = true)
    Integer getMaxCourseCode();

    @Query(value = "SELECT * FROM nerie.m_course_academics WHERE coursecode = :ccode", nativeQuery = true)
    Optional<M_Course_Academics> getCourseByCode(@Param("ccode") String coursecode);

    @Query(value = "SELECT EXISTS ( " +
            "    SELECT 1 FROM nerie.m_course_academics " +
            "    WHERE UPPER(coursename) = UPPER(:cname) " +
            "      AND departmentcode = :dcode " +
            "      AND coursecode != :ccode " +
            ")",
            nativeQuery = true)
    Boolean isCourseNameTakenByOtherCourse(@Param("cname") String courseName,
                                           @Param("dcode") String departmentCode,
                                           @Param("ccode") String courseCode);

    @Query(value = "SELECT * FROM m_course_academics ORDER BY coursecode, coursename", nativeQuery = true)
    List<M_Course_Academics> findAllOrderedByCourseCodeAndName();

    @Query(value = "SELECT * FROM m_course_academics WHERE departmentcode = :departmentcode ORDER BY coursecode, coursename", nativeQuery = true)
    List<Object[]> getCoursesBasedOnDepartmentFaculty(@Param("departmentcode") String departmentcode);

    @Query(value = "SELECT * FROM m_course_academics", nativeQuery = true)
    List<M_Course_Academics> findAllCoursesAcademics();

    @Query(value = "SELECT mca.* FROM m_course_academics mca INNER JOIN m_departments dept ON mca.departmentcode = dept.departmentcode WHERE dept.officecode = :officecode", nativeQuery = true)
    List<M_Course_Academics> findCoursesByOfficeCode(@Param("officecode") String officecode);
}
