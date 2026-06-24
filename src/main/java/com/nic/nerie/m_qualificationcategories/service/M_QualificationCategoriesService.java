package com.nic.nerie.m_qualificationcategories.service;

import com.nic.nerie.m_qualificationcategories.model.M_QualificationCategories;
import com.nic.nerie.m_qualificationcategories.repository.M_QualificationCategoriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class M_QualificationCategoriesService {
    private final M_QualificationCategoriesRepository mQualificationCategoriesRepository;

    @Autowired
    public M_QualificationCategoriesService(M_QualificationCategoriesRepository mQualificationCategoriesRepository) {
        this.mQualificationCategoriesRepository = mQualificationCategoriesRepository;
    }

    /*
     * This method returns all M_QualifcationCategories ordered by qualificationcategorycode
     * @returns A ordered list of M_QualificatoinCategories
     * @throws DataAccessResourceFailureException for data access errors
     */
    @Transactional(readOnly = true)
    public List<M_QualificationCategories> getAllQualificationCategories() {
        try {
            return mQualificationCategoriesRepository.findAllOrdered();
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error fetcing M_QualifcationCategories list", ex);
        }
    }

    public List<M_QualificationCategories> getAllOrderedByQualificationcategoryname() {
        return mQualificationCategoriesRepository.findAllOrderedByQualificationcategoryname();
    }

    /*
     * This method retrieves a M_QualificationCategories entity by qualificationcategorycode
     * @params qualificationcategorycode For matching
     * @returns M_QualificationCategories if found otherwise null
     * @throws 
     */
    @Transactional(readOnly = true)
    public M_QualificationCategories findByQualificationcategorycode(String qualificationcategorycode) {
        try {
            Optional<M_QualificationCategories> qualificationCategories = mQualificationCategoriesRepository.findByQualificationcategorycode(qualificationcategorycode);
            return qualificationCategories.isPresent() ? qualificationCategories.get() : null;
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving M_QualificationCategories entity", ex);
        }
    }
}
