package com.nic.nerie.m_preposttestqcategory.service;

import com.nic.nerie.m_phases.repository.M_PhasesRepository;
import com.nic.nerie.m_preposttestqcategory.model.M_PrePostTestQCategory;
import com.nic.nerie.m_preposttestqcategory.repository.M_PrePostTestQCategoryRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Service
@Validated
public class M_PrePostTestQCategoryService {
    private final M_PrePostTestQCategoryRepository mPrePostTestQCategoryRepository;

    @Autowired
    public M_PrePostTestQCategoryService(M_PrePostTestQCategoryRepository mPrePostTestQCategoryRepository) {
        this.mPrePostTestQCategoryRepository = mPrePostTestQCategoryRepository;
    }

    public List<M_PrePostTestQCategory> getAllPrePostTestQuestionCategories() {
        try {
            return mPrePostTestQCategoryRepository.getAllPrePostTestQuestionCategories();
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Failed to fetch all pre post test question categories", ex);
        }
    }

    @Transactional(readOnly = true)
    public boolean checkPrePostTestQCategoryExists(@NotNull @NotBlank String newCategory) {
        try {
            Optional<M_PrePostTestQCategory> categoryOpt = mPrePostTestQCategoryRepository.findByCategoryName(newCategory);
            return categoryOpt.isPresent() &&
                    categoryOpt.get().getPptqcategoryname().equalsIgnoreCase(newCategory);
        } catch (Exception e) {
            throw new DataAccessResourceFailureException("Error checking category existence", e);
        }
    }

    @Transactional
    public boolean saveNewPrePostTestQCategory(@NotNull M_PrePostTestQCategory qc) {
        try {
            Integer maxId = mPrePostTestQCategoryRepository.findMaxCategoryCode();
            int newId = (maxId == null) ? 1 : maxId + 1;

            qc.setPptqcategorycode(String.valueOf(newId));
            mPrePostTestQCategoryRepository.save(qc);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error saving Pre/Post Test Q Category", e);
        }
    }
}
