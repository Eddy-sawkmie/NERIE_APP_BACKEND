package com.nic.nerie.m_designations.service;

import com.nic.nerie.m_designations.model.M_Designations;
import com.nic.nerie.m_designations.repository.M_DesignationsRepository;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional; // Import Optional

@Service
@Validated
public class M_DesignationsService {
    private final M_DesignationsRepository designationsRepository;

    @Autowired
    public M_DesignationsService(M_DesignationsRepository designationsRepository) {
        this.designationsRepository = designationsRepository;
    }

    /*
     * This method retrieves M_Designations list by isparticipantdesignation and ordered by designationname
     * @params isparticipantdesignation Of M_Designations to perform matching
     * @returns List of M_Designations matched
     * @throws DataAccessResourceFailureException for db access errors
     */
    @Transactional(readOnly = true)
    public List<M_Designations> getDesignationList(@NotNull @NotBlank String isparticipantdesignation) {
        try {
            return designationsRepository.findByIsparticipantdesignationOrderByDesignationname(isparticipantdesignation);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving M_Designations list by isparticipantdesignation " + isparticipantdesignation, ex);
        }
    }

    public Optional<M_Designations> findById(String designationCode) {
        return designationsRepository.findById(designationCode);
    }

    public M_Designations findByDesignationname(@NotNull @NotBlank String designationname) {
        Optional<M_Designations> designation = designationsRepository.findByDesignationname(designationname.toUpperCase());
        return designation.isPresent() ? designation.get() : null;
    }

    /*
     * This method checks M_Designations entity existence by designationcode
     * @params designationcode Of target entity to perform matching
     * @returns A Boolean specifying the existence
     * @throws DataAccessResourceFailureException for db access errors
     */
    @Transactional(readOnly = true)
    public Boolean existsByDesignationcode(@NotNull @NotBlank String designationcode) {
        try {
            return designationsRepository.existsById(designationcode.trim());
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error checking M_Designations existence by designationcode", ex);
        }
    }

    /*
     * This method retrieves M_Designations entity by designationcode from db
     * @params designationcode Of target entity to perform matching
     * @returns M_Designations if found otherwise null
     * @throws DataAccessResourceFailureException for db access errors
     */
    @Transactional(readOnly = true)
    public M_Designations getDesignation(String designationcode) {
        try {
            return designationsRepository.findByDesignationcode(designationcode.trim());
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving M_Designations entity by designationcode", ex);
        } 
    }

    /*
     * This method sets designationcode for M_Designations (creation) and save it
     * @params designation The M_Designations to create or update
     * @returns The newly saved M_Designations
     * @throws RuntimeException for any errors
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public M_Designations saveDesignation(@NotNull M_Designations designation) {
        try {
            if (designation.getDesignationcode() == null || designation.getDesignationcode().isBlank()) {
                Integer maxCode = designationsRepository.findMaxDesignationCode();
                int newCode = (maxCode == null) ? 1 : maxCode + 1;
                designation.setDesignationcode(String.valueOf(newCode));
            }
            return designationsRepository.save(designation);
        } catch (Exception ex) {
            throw new RuntimeException("Error saving M_Designations entity | Exception: "  + ex);
        }
    }
    
    /*
     * This method sets designationcode for M_Designations (creation) and save it
     * @params designation The M_Designations to create or update
     * @returns The designationcode of the newly saved M_Designations
     * @throws RuntimeException for any errors
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public M_Designations saveDesignationDetails(@NotNull M_Designations designation) {
        try {
            // generate and set new designationcode (if creating...)
            if (designation.getDesignationcode() == null || designation.getDesignationcode().isBlank()) {
                Integer maxCode = designationsRepository.findMaxDesignationCode();
                int newCode = (maxCode == null) ? 1 : maxCode + 1;
                designation.setDesignationcode(String.valueOf(newCode));
            }
            
            return designationsRepository.save(designation);
        } catch (Exception ex) {
            throw new RuntimeException("Error saving M_Designations entity", ex);
        }
    }

    /*
     * This method returns a list of M_Designations ordered by isparticipantdesignation and designationname
     * @returns An ordered list of M_Designations
     * @throws DataAccessResourceFailureException for data access exceptions
     */
    @Transactional(readOnly = true)
    public List<M_Designations> getAllDesignationList() {
        try {
            return designationsRepository.findAllOrderByIsparticipantdesignationAndDesignationname();
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retreiving M_Designations list", ex);
        }
    }

    public boolean checkDesignationExistByDesignationname(@NotNull @NotBlank String designationame) {
        return designationsRepository.existsByDesignationname(designationame);
    }

    /*
     * This method checks M_Designations existence by designationname, isparticipantdesignation and designationcode
     * @params md The M_Designations entity for matching with
     * @returns A boolean specifying the existence
     * @throws DataAccessResourceFailureException
     */
    @Transactional(readOnly = true)
    public boolean checkDesignationExist(@NotNull M_Designations md) {
        try {
            String designationname = md.getDesignationname().toUpperCase();
            String isparticipantdesignation = md.getIsparticipantdesignation();
            String designationcode = (md.getDesignationcode() == null || md.getDesignationcode().isEmpty()) ? null : md.getDesignationcode();
    
            return designationsRepository.existsByDesignationnameAndIsparticipantdesignationAndNotDesignationcode(
                    designationname, isparticipantdesignation, designationcode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error checking M_Designations existence", ex);
        }
    }

    public List<M_Designations> getDesignations() {
        try {
            return designationsRepository.findAllOrderByIsparticipantdesignationAndDesignationname();
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving getDesignations", ex);
        }
    }

    public String saveDesignationDetails2(M_Designations u) {
        String res = "";

        try {
            if (u.getDesignationcode() == null || u.getDesignationcode().isBlank()) {
                Integer maxCode = designationsRepository.findMaxDesignationCode();
                int newCode = (maxCode == null) ? 1 : maxCode + 1;
                u.setDesignationcode(String.valueOf(newCode));
            }

            designationsRepository.save(u);
            res = u.getDesignationcode();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }
}
