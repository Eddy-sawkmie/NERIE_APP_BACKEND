package com.nic.nerie.t_alumni.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nic.nerie.t_alumni.model.T_Alumni;

public interface T_AlumniRepository extends JpaRepository<T_Alumni, String> {
    @Query(value = "SELECT MAX(CAST(alumniid AS INTEGER)) FROM t_alumni", nativeQuery = true)
    Integer findMaxAlumniId();

    @Query(value = "SELECT a.alumniid, a.rollno, " +
            "CONCAT(a.fname,' ', COALESCE(a.mname, ''), ' ', a.lname) AS name, " +
            "a.batch, a.mobileno, a.email, a.currentoccupation, " +
            "d.departmentname, c.coursename " +
            "FROM nerie.t_alumni a " +
            "INNER JOIN nerie.m_departments d ON d.departmentcode = a.departmentcode " +
            "INNER JOIN nerie.m_course_academics c ON c.coursecode = a.coursecode " +
            "WHERE a.officecode = :officecode", nativeQuery = true)
    List<Object[]> findAlumniList(@Param("officecode") String officecode);

    @Query(value = "SELECT a.alumniid, a.rollno, a.fname, a.mname, a.lname, " +
                   "a.gender, a.batch, a.mobileno, a.email, a.currentoccupation, " +
                   "a.departmentcode, a.coursecode " +
                   "FROM nerie.t_alumni a " +
                   "INNER JOIN nerie.m_departments d ON d.departmentcode = a.departmentcode " +
                   "INNER JOIN nerie.m_course_academics c ON c.coursecode = a.coursecode " +
                   "WHERE a.alumniid = :alumniid",
           nativeQuery = true)
    List<Object[]> findAlumniDetailsByAlumniid(@Param("alumniid") String alumniid);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM T_Alumni a WHERE UPPER(a.rollno) = :rollno")
    boolean existsByRollno(@Param("rollno") String rollno);

    @Query("SELECT MAX(CAST(a.alumniid as int)) FROM T_Alumni a")
    int findLastUsedAlumniid();
}
