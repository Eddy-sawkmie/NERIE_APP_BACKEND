package com.nic.nerie.t_facultyprofile.service;

import com.nic.nerie.t_facultyprofile.model.T_FacultyProfile;
import com.nic.nerie.t_facultyprofile.repository.T_FacultyProfileRepository;

import java.util.Collections;
import java.util.List;

import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class T_FacultyProfileService {

    private final T_FacultyProfileRepository tFacultyProfileRepository;

    public T_FacultyProfileService(T_FacultyProfileRepository tFacultyProfileRepository) {
        this.tFacultyProfileRepository = tFacultyProfileRepository;
    }

    @Transactional(readOnly = true)
    public T_FacultyProfile getFacultyProfileByUsercode(String usercode) {
        try {
            T_FacultyProfile profile = tFacultyProfileRepository.findFacultyProfileByUsercode(usercode);

            if (profile != null && profile.getResearchpapers() != null) {
                profile.getResearchpapers().size();
            }

            return profile;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DataAccessResourceFailureException("Error retrieving faculty profile for usercode: " + usercode, ex);
        }
    }

    @Transactional
    public boolean saveFacultyResearchProfile(T_FacultyProfile profile) {
        try {
            if (profile.getFacultyprofileid() == null) {
                Integer maxId = tFacultyProfileRepository.findMaxFacultyProfileId();
                int newId = (maxId == null) ? 1 : maxId + 1;
                profile.setFacultyprofileid(String.valueOf(newId));
            }
            tFacultyProfileRepository.save(profile);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Transactional(readOnly = true)
    public List<Object[]> findProgramDetailsForFacultyProfile(String usercode) {

        try {
            if (usercode == null || usercode.trim().isEmpty()) {
                throw new IllegalArgumentException("Usercode cannot be null or empty");
            }

            List<Object[]> programDetails = tFacultyProfileRepository
                    .findProgramDetailsForFacultyProfile(usercode);

            if (programDetails == null || programDetails.isEmpty()) {
                return Collections.emptyList();
            }

            return programDetails;

        } catch (IllegalArgumentException e) {
            throw e; // rethrow validation errors
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error while fetching program details for usercode: " + usercode, e);
        }
    }
}
