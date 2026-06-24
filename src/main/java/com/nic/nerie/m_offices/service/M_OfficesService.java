package com.nic.nerie.m_offices.service;

import com.nic.nerie.m_offices.model.M_Offices;
import com.nic.nerie.m_offices.repository.M_OfficesRepository;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;


@Service
public class M_OfficesService {
    private  final Logger logger = LoggerFactory.getLogger(M_OfficesService.class);
    private final M_OfficesRepository mOfficesRepository;

    @Autowired
    public M_OfficesService(M_OfficesRepository mOfficesRepository) {
        this.mOfficesRepository = mOfficesRepository;
    }

    /*
     * This method checks M_Offices existence by officecode 
     * @params officecode Of M_Offices for matching
     * @returns Boolean specifying existence
     * @throws DataAccessResourceFailureException for db access errors
     */
    @Transactional(readOnly = true)
    public Boolean existsByOfficecode(@NotNull @NotBlank String officecode) {
        try {
            return mOfficesRepository.existsById(officecode.trim());
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error checking M_Offices existence by officecode " + officecode, ex);
        }
    }

    public List<M_Offices> getOfficeList() {
        return mOfficesRepository.findAllByOrderByOfficenameAsc();
    }

    /*
     * This method retrieves M_Offices list ordered by officename
     * @returns List of all M_Offices
     * @throws DataAccessResourceFailureException for db access errors
     */
    @Transactional(readOnly = true)
    public List<M_Offices> getOfficesList() {
        try {
            return mOfficesRepository.findAllOrderByOfficename();
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving M_Offices list", ex);
        }
    }

    public M_Offices saveOffice(M_Offices mOffices) {
        try {
            // Handle State and District Objects
            if (mOffices.getMstates() == null ||
                    !StringUtils.hasText(mOffices.getMstates().getStatecode())) {
                mOffices.setMstates(null);
            }

            if (mOffices.getMdistricts() == null ||
                    !StringUtils.hasText(mOffices.getMdistricts().getDistrictcode())) {
                mOffices.setMdistricts(null);
            }

            // Generate Office Code if not provided
            if (!StringUtils.hasText(mOffices.getOfficecode())) {
                Integer maxCode = mOfficesRepository.findMaxOfficeCodeAsInteger();
                int nextCode = (maxCode == null) ? 1 : maxCode + 1;
                mOffices.setOfficecode(String.valueOf(nextCode));
            }

            // Save
            M_Offices savedOffice = mOfficesRepository.save(mOffices);
            return savedOffice;

        } catch (NumberFormatException e) {
            throw new RuntimeException("Failed to generate office code due to non-numeric existing codes.", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save office details.", e);
        }
    }

    @Transactional(readOnly = true)
    public String getOfficeFacultiesCount(@NotNull @NotBlank String officecode) {
        try {
            return String.valueOf(mOfficesRepository.getOfficeFacultiesCount(officecode.trim()));
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving faculty count for office " + officecode, ex);
        }
    }

    @Transactional(readOnly = true)
    public String getOfficeStudentsCount(@NotNull @NotBlank String officecode) {
        try {
            return String.valueOf(mOfficesRepository.getOfficeStudentsCount(officecode.trim()));
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving students count for office " + officecode, ex);
        }
    }

    @Transactional(readOnly = true)
    public String getOfficeAlumniCount(@NotNull @NotBlank String officecode) {
        try {
            return String.valueOf(mOfficesRepository.getOfficeAlumniCount(officecode.trim()));
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving alumni count for office " + officecode, ex);
        }
    }

    @Transactional(readOnly = true)
    public String getOfficeParticipantsCount(String officecode) {
        try {
            return String.valueOf(mOfficesRepository.getOfficeParticipantsCount(officecode));
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving participants count", ex);
        }
    }

    public boolean existsByCode(String code, String type, String excludeOfficeCode) {
        boolean isEditMode = (excludeOfficeCode != null && !excludeOfficeCode.isEmpty());

        if ("shorttermcoursecode".equals(type)) {
            if (isEditMode) {
                // Edit: Check others, skip self
                return mOfficesRepository.existsByShorttermcoursecodeAndOfficecodeNot(code, excludeOfficeCode);
            }
            // Create: Check everyone
            return mOfficesRepository.existsByShorttermcoursecode(code);
        } 
        
        else {
            if (isEditMode) {
                return mOfficesRepository.existsByLongtermcoursecodeAndOfficecodeNot(code, excludeOfficeCode);
            }
            return mOfficesRepository.existsByLongtermcoursecode(code);
        }
    }
}
