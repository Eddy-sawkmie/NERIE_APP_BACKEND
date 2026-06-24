package com.nic.nerie.m_designations.repository;

import com.nic.nerie.m_designations.model.M_Designations;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface M_DesignationsRepository extends JpaRepository<M_Designations, String> {
       @Query("SELECT d FROM M_Designations d WHERE d.isparticipantdesignation = :dtype ORDER BY d.designationname")
       List<M_Designations> findByIsparticipantdesignationOrderByDesignationname(String dtype);

       @Query("SELECT MAX(CAST(d.designationcode AS int)) FROM M_Designations d")
       Integer findMaxDesignationCode();

       @Query("SELECT d FROM M_Designations d WHERE d.designationcode = :designationcode")
       M_Designations findByDesignationcode(String designationcode);

       @Query("FROM M_Designations d WHERE UPPER(d.designationname) = :designationname")
       Optional<M_Designations> findByDesignationname(@Param("") String designationname);

       @Query("SELECT d FROM M_Designations d ORDER BY d.isparticipantdesignation, d.designationname")
       List<M_Designations> findAllOrderByIsparticipantdesignationAndDesignationname();

       @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END " +
                     "FROM M_Designations d " +
                     "WHERE UPPER(d.designationname) = :designationname " +
                     "AND d.isparticipantdesignation = :isparticipantdesignation " +
                     "AND (:designationcode IS NULL OR d.designationcode != :designationcode)")
       boolean existsByDesignationnameAndIsparticipantdesignationAndNotDesignationcode(
                     String designationname, String isparticipantdesignation, String designationcode);

       @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END " +
                     "FROM M_Designations d WHERE UPPER(d.designationname) = :designationname")
       boolean existsByDesignationname(@Param("designationname") String designationname);

       @Query(value = "SELECT * FROM m_designations", nativeQuery = true)
       List<M_Designations> findAllDesignations();
}
