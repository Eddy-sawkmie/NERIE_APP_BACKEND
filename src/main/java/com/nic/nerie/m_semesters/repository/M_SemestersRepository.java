package com.nic.nerie.m_semesters.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nic.nerie.m_semesters.model.M_Semesters;

public interface M_SemestersRepository extends JpaRepository<M_Semesters, String> {
    @Query("FROM M_Semesters ORDER BY semestercode, semestername")
    List<M_Semesters> findAllByOrderBySemestercodeAscSemesternameAsc();

    @Query(value = "SELECT * FROM m_semesters ORDER BY semestercode, semestername", nativeQuery = true)
    List<M_Semesters> getSemesterList();

    @Query(value = "SELECT * FROM m_semesters ORDER BY semestercode", nativeQuery = true)
    List<Object[]> getMasterSemesters();
}
