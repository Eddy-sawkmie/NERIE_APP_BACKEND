package com.nic.nerie.m_preposttestqcategory.repository;

import com.nic.nerie.m_preposttestqcategory.model.M_PrePostTestQCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface M_PrePostTestQCategoryRepository extends JpaRepository<M_PrePostTestQCategory, String> {
    @Query(value="SELECT * FROM M_PrePostTestQCategory", nativeQuery = true)
    List<M_PrePostTestQCategory> getAllPrePostTestQuestionCategories();

    @Query(value = "SELECT * FROM M_PrePostTestQCategory WHERE pptqcategoryname = :newCategory", nativeQuery = true)
    Optional<M_PrePostTestQCategory> findByCategoryName(@Param("newCategory") String newCategory);

    @Query(value = "SELECT MAX(CAST(pptqcategorycode AS INTEGER)) FROM M_PrePostTestQCategory", nativeQuery = true)
    Integer findMaxCategoryCode();
}
