package com.nic.nerie.m_categories.repository;

import com.nic.nerie.m_categories.model.M_Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface M_CategoriesRepository extends JpaRepository<M_Categories, String> {
    List<M_Categories> findAllByOrderByCategorycode();
}