// M_QualificationSubjectsRepository.java
package com.nic.nerie.m_qualificationsubjects.repository;

import com.nic.nerie.m_qualificationsubjects.model.M_QualificationSubjects;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface M_QualificationSubjectsRepository extends JpaRepository<M_QualificationSubjects, String> {

    @Query(value = """
        SELECT s.qualificationsubjectcode, s.qualificationsubjectname, m.qualificationcode 
        FROM m_qualificationsubjects s 
        JOIN mt_qualificationssubjectsmap m 
          ON m.qualificationsubjectcode = s.qualificationsubjectcode 
        WHERE m.qualificationcode = :qualificationcode 
        ORDER BY s.qualificationsubjectname
        """, nativeQuery = true)
    List<Object[]> findSubjectsByQualificationCode(@Param("qualificationcode") String qualificationcode);

    @Query(value = """
        SELECT 
            s.qualificationsubjectcode, 
            s.qualificationsubjectname, 
            m.qualificationcode 
        FROM 
            m_qualificationsubjects s 
        LEFT OUTER JOIN 
            mt_qualificationssubjectsmap m 
        ON 
            m.qualificationsubjectcode = s.qualificationsubjectcode 
            AND m.qualificationcode = :qualificationcode 
        ORDER BY 
            s.qualificationsubjectname
        """, nativeQuery = true)
    List<Object[]> findSubjectsWithQualificationMapping(@Param("qualificationcode") String qualificationcode);

    @Query("FROM M_QualificationSubjects ORDER BY qualificationsubjectname")
    List<M_QualificationSubjects> findAllOrderedByQualificationSubjectName();

    @Query("SELECT MAX(CAST(q.qualificationsubjectcode AS int)) FROM M_QualificationSubjects q")
    Integer findMaxSubjectCode();

    @Transactional
    @Query(value = "SELECT m.qualificationsubjectcode FROM nerie.mt_qualificationssubjectsmap m WHERE m.qualificationcode = :qualificationcode", nativeQuery = true)
    List<String> getAllQualificationsubjectcodeByQualificationcode(@Param("qualificationcode") String qualificationcode);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO nerie.mt_qualificationssubjectsmap(qualificationcode, qualificationsubjectcode) " +
            "VALUES(:qualificationcode, :qualificationsubjectcode)", nativeQuery = true)
    int saveQualificationSubject(@Param("qualificationcode") String qualificationcode, @Param("qualificationsubjectcode") String qualificationsubjectcode);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM nerie.mt_qualificationssubjectsmap qs WHERE qs.qualificationcode = :qualificationcode", nativeQuery = true)
    void removeQualificationSubjects(@Param("qualificationcode") String qualificationcode);

    @Query("SELECT COUNT(q) > 0 FROM M_QualificationSubjects q WHERE UPPER(q.qualificationsubjectname) = :sname AND (:scode IS NULL OR q.qualificationsubjectcode != :scode)")
    boolean existsByNameAndNotCode(@Param("sname") String subjectName, @Param("scode") String subjectCode);
}
