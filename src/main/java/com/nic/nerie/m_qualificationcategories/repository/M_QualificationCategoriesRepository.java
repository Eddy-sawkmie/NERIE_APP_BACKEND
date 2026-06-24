package com.nic.nerie.m_qualificationcategories.repository;

import com.nic.nerie.m_qualificationcategories.model.M_QualificationCategories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface M_QualificationCategoriesRepository extends JpaRepository<M_QualificationCategories, String> {
    @Query("SELECT q FROM M_QualificationCategories q ORDER BY q.qualificationcategorycode")
    List<M_QualificationCategories> findAllOrdered();

    @Query("FROM M_QualificationCategories qc ORDER BY qc.qualificationcategoryname")
    List<M_QualificationCategories> findAllOrderedByQualificationcategoryname();

    @Query("SELECT q FROM M_QualificationCategories q WHERE q.qualificationcategorycode = :qualificationcategorycode")
    Optional<M_QualificationCategories> findByQualificationcategorycode(@Param("qualificationcategorycode") String qualificationcategorycode);
}