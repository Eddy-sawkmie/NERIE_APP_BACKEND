package com.nic.nerie.m_coursecategories.repository;

import com.nic.nerie.m_coursecategories.model.M_CourseCategories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface M_CourseCategoriesRepository extends JpaRepository<M_CourseCategories, String> {

    @Query("FROM M_CourseCategories ORDER BY coursecategoryname")
    List<M_CourseCategories> findAllOrderByCourseCategoryName();

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
           "FROM M_CourseCategories c " +
           "WHERE UPPER(c.coursecategoryname) = UPPER(:courseCategoryName) " +
           "AND (:courseCategoryCode IS NULL OR c.coursecategorycode != :courseCategoryCode)")
    boolean existsByCourseCategoryNameAndNotCourseCategoryCode(String courseCategoryName, String courseCategoryCode);

    @Query("SELECT MAX(CAST(c.coursecategorycode AS int)) FROM M_CourseCategories c")
    Integer findMaxCourseCategoryCode();

    @Query(value = "SELECT * FROM m_coursecategories WHERE coursecategorycode = :code", nativeQuery = true)
    Optional<M_CourseCategories> findByCodeNative(@Param("code") String coursecategorycode);
}
