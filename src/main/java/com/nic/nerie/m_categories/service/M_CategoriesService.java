// M_CategoriesService.java
package com.nic.nerie.m_categories.service;

import com.nic.nerie.m_categories.model.M_Categories;
import com.nic.nerie.m_categories.repository.M_CategoriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class M_CategoriesService {
    private final M_CategoriesRepository categoriesRepository;

    @Autowired
    public M_CategoriesService(M_CategoriesRepository categoriesRepository) {
        this.categoriesRepository = categoriesRepository;
    }

    public List<M_Categories> getAllCategories() {
        return categoriesRepository.findAllByOrderByCategorycode();
    }

    @Transactional(readOnly = true)
    public Optional<M_Categories> findById(String categoryCode) {
        try {
            return categoriesRepository.findById(categoryCode.trim());
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving M_Categories by categoryCode " + categoryCode, ex);
        }
    }
}