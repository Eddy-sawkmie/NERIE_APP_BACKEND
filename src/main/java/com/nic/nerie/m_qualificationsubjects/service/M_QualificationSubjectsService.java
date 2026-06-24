// M_QualificationSubjectsService.java
package com.nic.nerie.m_qualificationsubjects.service;

import com.nic.nerie.m_qualificationsubjects.model.M_QualificationSubjects;
import com.nic.nerie.m_qualificationsubjects.repository.M_QualificationSubjectsRepository;

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
public class M_QualificationSubjectsService {
    private final M_QualificationSubjectsRepository qualificationSubjectsRepository;

    @Autowired
    public M_QualificationSubjectsService(M_QualificationSubjectsRepository qualificationSubjectsRepository) {
        this.qualificationSubjectsRepository = qualificationSubjectsRepository;
    }

    public List<Object[]> getQualificationSubject(@NotNull @NotBlank String qualificationcode) {
        try {
            return qualificationSubjectsRepository.findSubjectsByQualificationCode(qualificationcode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving qualification subject", ex);
        }
    }

    /*
     * This method checks the existence of M_QualificationSubjects entity by qualificationsubjectcode
     * @params subjectCode The subjectcode of the target M_QualificationSubjects entity
     * @returns A boolean specifying the existence 
     * @throws DataAccessResourceFailureException for data access errors
     */
    @Transactional(readOnly = true)
    public Boolean existsByQualificationsubjectcode(@NotNull @NotBlank String qualificationsubjectcode) {
        try {
            return qualificationSubjectsRepository.existsById(qualificationsubjectcode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error checking existence of M_QualificationSubjects entity", ex);
        }
    }
    
    /*
     * This method retrieves M_QualificationSubjects entity by subjectcode
     * @params subjectCode The subjectcode of the target M_QualificationSubjects entity
     * @returns M_QualificationSubjects wrapped in Optional
     * @throws DataAccessResourceFailureException for data access errors
     */
    @Transactional(readOnly = true)
    public Optional<M_QualificationSubjects> findById(@NotNull @NotBlank String subjectCode) {
        try {
            return qualificationSubjectsRepository.findById(subjectCode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving M_QualificationSubjects entity by subjectcode", ex);
        }
    }

    /*
     * This method returns M_QualificationSubjects ordered by qualificationsubjectname
     * @returns An ordered List of M_QualificationSubjects
     * @throws DataAccessResourceFailureException For data access errors
     */
    @Transactional(readOnly = true)
    public List<M_QualificationSubjects> getAllQualificationSubjects() {
        try {
            return qualificationSubjectsRepository.findAllOrderedByQualificationSubjectName();
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving M_QualificationSubjects list", ex);
        }
    }

    /*
     * This method checks M_QualificationSubjects existence
     * @params mqsubjects The M_QualificationSubjects to compare with
     * @returns A boolean specifying the existence
     * @throws DataAccessResourceFailureException For data access errors
     */
    @Transactional(readOnly = true)
    public boolean checkSubjectExist(M_QualificationSubjects mqsubjects) {
        try {
            String subjectName = mqsubjects.getQualificationsubjectname().toUpperCase();
            String subjectCode = mqsubjects.getQualificationsubjectcode();
            return qualificationSubjectsRepository.existsByNameAndNotCode(subjectName, subjectCode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error checking M_QualificationSubjects existence", ex);
        }
    }

    /*
     * This method sets qualificationsubjectcode for new M_QualificationSubjects (creation) and save it
     * @params subjects The M_QualificationSubjects entity for saving/updating
     * @returns The newly saved M_QualificationSubjects entity
     * @throws RuntimeException For any errors
     */
    @Transactional(readOnly = false)
    public M_QualificationSubjects saveSubjectDetails(@NotNull M_QualificationSubjects subject) {
        try {
            // generate and assign new subjectcode only for creation
            if (subject.getQualificationsubjectcode() == null || subject.getQualificationsubjectcode().isBlank())
                subject.setQualificationsubjectcode(generateNewSubjectCode());
            return qualificationSubjectsRepository.save(subject);
        } catch (Exception ex) {
            throw new RuntimeException("Error saving M_QualificationSubjects entity", ex);
        }
    }

    /*
     * This method generates a new subjectcode for M_QualificationSubjects and returns as string
     * @returns The new subjectcode as String
     * @throws RuntimeException For any errors
     */
    @Transactional(readOnly = true)
    private String generateNewSubjectCode() {
        try {
            Integer lastSubjectCodeUsed = qualificationSubjectsRepository.findMaxSubjectCode();
            return lastSubjectCodeUsed == null ? "1" : String.valueOf(lastSubjectCodeUsed + 1);
        } catch (Exception ex) {
            throw new RuntimeException("Error generating new subjectcode", ex);
        }
    }

    /*
     * This method initially deletes and saves new qualification-subject mappings
     * @params qualificationcode The code representing the qualification
     * @params subjectCodes The List of subject codes to be added
     * @returns void
     * @throws RuntimeException For persistence errors
     */
    @Transactional(readOnly = false)
    public void saveQualificationSubjectMap(@NotNull @NotBlank String qualificationcode, List<String> subjectCodes) {
        // Removing existing qualification-subject mappings
        try {
            qualificationSubjectsRepository.removeQualificationSubjects(qualificationcode);
        } catch (Exception ex) {
            throw new RuntimeException("Error removing existing mt_qualificationssubjectsmap entries", ex);
        }

        // Adding new qualification-subject mapping
        for (String subjectCode : subjectCodes) {
            try {
                qualificationSubjectsRepository.saveQualificationSubject(qualificationcode, subjectCode);
            } catch (Exception ex) {
                throw new RuntimeException("Error saving mt_qualificationsubjectsmap tuple", ex);
            }
        }
    }

    public List<Object[]> getMappedSubjects(String qualificationcode) {
        return qualificationSubjectsRepository.findSubjectsWithQualificationMapping(qualificationcode);
    }
}
