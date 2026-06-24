package com.nic.nerie.m_coursecategories.service;

import com.nic.nerie.m_coursecategories.model.M_CourseCategories;
import com.nic.nerie.m_coursecategories.repository.M_CourseCategoriesRepository;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Service
@Validated
public class M_CourseCategoriesService {
    private final M_CourseCategoriesRepository mCourseCategoriesRepository;

    @Autowired
    public M_CourseCategoriesService(M_CourseCategoriesRepository mCourseCategoriesRepository) {
        this.mCourseCategoriesRepository = mCourseCategoriesRepository;
    }

    @Transactional(readOnly = true)
    public M_CourseCategories getByCoursecategorycode(@NotNull @NotBlank String coursecategorycode) {
        try {
            Optional<M_CourseCategories> courseCategoriesOptional = mCourseCategoriesRepository.findById(coursecategorycode.trim());
            return courseCategoriesOptional.isPresent() ? courseCategoriesOptional.get() : null;
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving M_CourseCategories by coursecategorycode " + coursecategorycode, ex);
        }
    }

    @Transactional(readOnly = true)
    public List<M_CourseCategories> getAllCourseCategories() {
        try {
            return mCourseCategoriesRepository.findAllOrderByCourseCategoryName();
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving all M_CourseCategories", ex);
        }
    }

    /*
     * This method checks M_CourseCategories entity existence by coursecategorycode
     * @params coursecategorycode Of target entity to perform matching
     * @returns Boolean specifying existence
     * @throws DataAccessResourceFailureException for db access errors
     */
    @Transactional(readOnly = true)
    public Boolean existsByCoursecategorycode(@NotNull @NotBlank String coursecategorycode) {
        try {
            return mCourseCategoriesRepository.existsById(coursecategorycode.trim());
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error checking M_CourseCategories entity existence by coursecategorycode", ex);
        }
    }

    /*
     * This method checks if M_CourseCategories exists by name and code
     * @param mCourseCategories The M_CourseCategories to match with
     * @return A boolean specifying it's existence
     * @throws DataAccessResourceFailureException for db access errors
     */
    @Transactional(readOnly = true)
    public boolean checkCourseCategoryExist(@NotNull M_CourseCategories mCourseCategories) {
        String courseCategoryName = mCourseCategories.getCoursecategoryname();
        String courseCategoryCode = mCourseCategories.getCoursecategorycode().isEmpty() ? null : mCourseCategories.getCoursecategorycode();

        try {
            return mCourseCategoriesRepository.existsByCourseCategoryNameAndNotCourseCategoryCode(courseCategoryName, courseCategoryCode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error checking M_CourseCategories existence by name and code", ex);
        }
    }

    /*
     * This method sets coursecategorycode for new M_CourseCategories entity and save it
     * @params courseCategories The new M_CourseCategories entity to be saved
     * @returns The saved M_CourseCategories entity
     * @throws RuntimeException for any errors
     */
    @Transactional
    public M_CourseCategories saveCourseCategoriesDetails(@NotNull M_CourseCategories courseCategories) {
        try {
            if (courseCategories.getCoursecategorycode() == null || courseCategories.getCoursecategorycode().isBlank())
                courseCategories.setCoursecategorycode(generateNewCourseCategoryCode());
            return mCourseCategoriesRepository.save(courseCategories);
        } catch (Exception ex) {
            throw new RuntimeException("Error saving M_CourseCategories entity", ex);
        }
    }

    /*
     * This method generates the new coursecategorycode for M_CourseCategory creation
     * @returns The new coursecategorycode as String
     * @throws RuntimeException for any errors
     */
    @Transactional(readOnly = true)
    private String generateNewCourseCategoryCode() {
        try {
            Integer lastCategoryCodeUsed = mCourseCategoriesRepository.findMaxCourseCategoryCode();
            return lastCategoryCodeUsed == null ? "1" : String.valueOf(lastCategoryCodeUsed + 1);
        } catch (Exception ex) {
            throw new RuntimeException("Error generating next coursecategorycode", ex);
        }
    }

    @Transactional(readOnly = true)
    public M_CourseCategories getCourseCategoryById(String coursecategorycode) {
        try {
            return mCourseCategoriesRepository.findByCodeNative(coursecategorycode.trim())
                    .orElse(null);
        } catch (Exception e) {
            throw new DataAccessResourceFailureException("Error retrieving course category with code: " + coursecategorycode, e) {};
        }
    }
}
