package com.nic.nerie.m_qualifications.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nic.nerie.m_qualifications.model.M_Qualifications;
import com.nic.nerie.m_qualifications.repository.M_QualificationsRepository;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Service
public class M_QualificationsService {
    private final M_QualificationsRepository qualificationsRepository;

    @Autowired
    public M_QualificationsService(M_QualificationsRepository qualificationsRepository) {
        this.qualificationsRepository = qualificationsRepository;
    }

    /*
     * This method checks M_Qualifications existence by qualificationcode
     * @params qualificationcode The qualificationcode to perform matching 
     * @returns A Boolean specifying the existence
     * @throws DataAccessResourceFailureException for data access errors
     */
    @Transactional(readOnly = true)
    public Boolean existsByQualificationcode(@NotNull @NotBlank String qualificationcode) {
        try {
           return qualificationsRepository.existsById(qualificationcode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error checking M_Qualifications existence by qualificationcode", ex);
        }
    }

    /*
     * This method returns all M_QualifcationCategories ordered by qualificationcode and qualificationname
     * @returns An ordered list of M_Qualifications
     * @throws DataAccessResourceFailureException for data access errors
     */
    @Transactional(readOnly = true)
    public List<M_Qualifications> getQualificationList() {
        try {
            return qualificationsRepository.findAllOrdered();
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retreiving M_Qualifcations list", ex);
        } 
    }

    /*
     * This method returns all M_Qualifcations by qualificationcategorycode
     * @returns A list of M_Qualifications
     * @throws DataAccessResourceFailureException for data access errors
     */
    @Transactional(readOnly = true)
    public List<M_Qualifications> getQualificationByQualificationcategorycode(@NotNull @NotBlank String qualificationcategorycode) {
        try {
            return qualificationsRepository.getQualificationsByQualificationcategorycode(qualificationcategorycode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retreiving M_Qualifcations list by qualificationcategorycode", ex);
        } 
    }

    /*
     * This method retrieves M_Qualifications entity by qualificationcode from db
     * @params qualificaitonCode The qualificationcode to perform matching
     * @returns M_Qualifications entity wrapped in Optional
     * @throws DataAccessResourceFailureException for data access errors
     */
    @Transactional(readOnly = true)
    public Optional<M_Qualifications> findById(String qualificationCode) {
        try {
            return qualificationsRepository.findById(qualificationCode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retreiving M_Qualifcations entity by qualificationcode", ex);
        } 
    }

    /*
     * This method retrieves M_Qualifications entity by qualificationname & qualificationcategorycode from db
     * @params qualificaitonCode The qualificationname of target entity to perform matching
     * @params qualificaitoncategorycode The qualificationcode of target entity to perform matching
     * @returns The required M_Qualifications entity     
     * * @throws DataAccessResourceFailureException for data access errors
     */
    @Transactional(readOnly = true)
    public M_Qualifications findByQualificationnameAndQualificationcategorycode(@NotNull @NotBlank String qualificationname, @NotNull @NotBlank String qualificationcategorycode) {
        try {
            Optional<M_Qualifications> qualification = qualificationsRepository.findQualificationByNameAndCategory(qualificationname, qualificationcategorycode);
            return qualification.isPresent() ? qualification.get() : null;
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving M_Qualifications entity by qualificationname, qualificationcategorycode", ex);
        }
    }

    /*
     * This method sets new qualificationcode for creation and save
     * @params qualification The M_Qualifications entity to save or update
     * @returns The newly saved M_Qualifications entity
     * @throws RuntimeException for any errors
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public M_Qualifications saveQualificationDetails(@NotNull M_Qualifications qualification) {
        try {
            if (qualification.getQualificationcode() == null || qualification.getQualificationcode().isBlank())
                qualification.setQualificationcode(generateNewQualificationCode());

            return qualificationsRepository.save(qualification);
        } catch (Exception ex) {
            throw new RuntimeException("Error saving M_QualificationSubjects entity", ex);
        }
    }

    /*
     * This method checks if M_Qualifications already exists by qualificationame and qualificationcode
     * @params mQualifications The M_Qualifcations entity for matching
     * @returns A boolean specifying the existence
     * @throws DataAccessResourceFailureException For data access errors
     */
    @Transactional(readOnly = true)
    public boolean checkQualificationExist(M_Qualifications mQualifications) {
        try {
            String qualificationName = mQualifications.getQualificationname();
            String qualificationCode = mQualifications.getQualificationcode().isEmpty() ? null : mQualifications.getQualificationcode();
    
            return qualificationsRepository.existsByQualificationNameAndCode(qualificationName, qualificationCode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error checking M_Qualification existence", ex);
        }
    }

    /*
     * This method checks M_Qualifications existence by qualificationName & qualificationcategoryCode 
     * @params qualificationName The qualificationname of target entity to perform matching
     * @params qualificationCategoryCode The qualificationcategorycode of target entity to perform matching
     * @returns A Boolean specifying the existence
     * @throws DataAccessResourceFailureException For data access errors
     */
    @Transactional(readOnly = true)
    public Boolean checkQualificationExistByQualificationnameAndQualificationcategorycode(String qualificationName, String qualificationCategoryCode) {
        try {
            return qualificationsRepository.existsByQualificationNameAndCategoryCode(qualificationName.trim(), qualificationCategoryCode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error checking M_Qualification existence by qualificationname & qualificationcategorycode", ex);
        }
    }

    /*
     * This method generates a new qualificationcode and returns as String
     * @returns The new qualificationcode as String
     * @throws RuntimeException for any errors 
     */
    private String generateNewQualificationCode() {
        try {
            Integer lastQualificationCodeUsed = qualificationsRepository.findMaxQualificationCode();
            return lastQualificationCodeUsed == null ? "1" : String.valueOf(lastQualificationCodeUsed + 1);
        } catch (Exception ex) {
            throw new RuntimeException("Error generating new qualificationcode", ex);
        }
    }
}

