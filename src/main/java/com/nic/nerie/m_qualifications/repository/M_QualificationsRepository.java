package com.nic.nerie.m_qualifications.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nic.nerie.m_qualifications.model.M_Qualifications;

@Repository
public interface M_QualificationsRepository extends JpaRepository<M_Qualifications, String> {
    @Query("SELECT q FROM M_Qualifications q ORDER BY q.mqualificationcategories.qualificationcategorycode, q.qualificationname")
    List<M_Qualifications> findAllOrdered();

    @Query("SELECT q FROM M_Qualifications q WHERE q.mqualificationcategories.qualificationcategorycode = :qualificationcategorycode")
    List<M_Qualifications> getQualificationsByQualificationcategorycode(@Param("qualificationcategorycode") String qualificationcategorycode);

    @Query("FROM M_Qualifications q where UPPER(q.qualificationname) = :qname and q.mqualificationcategories.qualificationcategorycode = :qcatcode")
    Optional<M_Qualifications> findQualificationByNameAndCategory(@Param("qname") String qualificationname, @Param("qcatcode") String qualificationcategorycode);

    @Query("SELECT MAX(CAST(q.qualificationcode AS int)) FROM M_Qualifications q")
    Integer findMaxQualificationCode();

    @Query("SELECT CASE WHEN COUNT(q) > 0 THEN true ELSE false END " +
           "FROM M_Qualifications q " +
           "WHERE UPPER(q.qualificationname) = UPPER(:qualificationName) " +
           "AND (:qualificationCode IS NULL OR q.qualificationcode != :qualificationCode)")
    boolean existsByQualificationNameAndCode(String qualificationName, String qualificationCode);

    @Query("SELECT CASE WHEN COUNT(q) > 0 THEN true ELSE false END " +
        "FROM M_Qualifications q " +
        "WHERE UPPER(q.qualificationname) = UPPER(:qualificationName) " +
        "AND q.mqualificationcategories.qualificationcategorycode = :qualificationCategoryCode")
    boolean existsByQualificationNameAndCategoryCode(@Param("qualificationName") String qualificationName, 
                                                    @Param("qualificationCategoryCode") String qualificationCategoryCode);
}

