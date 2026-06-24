package com.nic.nerie.mt_resourcepersons.service;

import com.nic.nerie.m_programs.model.M_Programs;
import com.nic.nerie.mt_resourcepersons.dto.ResourcePersonsDTO;
import com.nic.nerie.mt_resourcepersons.model.MT_ResourcePersons;
import com.nic.nerie.mt_resourcepersons.repository.MT_ResourcePersonsRepository;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.validation.constraints.NotBlank;

import org.apache.commons.collections.functors.ExceptionPredicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MT_ResourcePersonsService {
    private final MT_ResourcePersonsRepository mtResourcePersonsRepository;

    @Autowired
    public MT_ResourcePersonsService(MT_ResourcePersonsRepository mtResourcePersonsRepository) {
        this.mtResourcePersonsRepository = mtResourcePersonsRepository;
    }

    public List<MT_ResourcePersons> getAllResourcePersons() {
        try {
            return mtResourcePersonsRepository.findAll();
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving All Resource Persons, ", ex);
        }
    }

    public List<MT_ResourcePersons> getAllResourcePersonsByOfficecode(@NotNull @NotBlank String officecode) {
        try {
            return mtResourcePersonsRepository.findAllByOfficecode(officecode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving Resource Person with officecode: " + officecode, ex);
        }
    }

    @Transactional(readOnly = true)
    public boolean checkEmailAvailability(@NotNull @NotBlank String emailid, String rpslno) {
        try {
            if (rpslno == null) {
                // CREATE MODE: Check if email exists anywhere
                return !mtResourcePersonsRepository.existsByRpemailid(emailid.trim());
            } else {
                // EDIT MODE: Check if email exists for ANY OTHER user
                return !mtResourcePersonsRepository.existsByRpemailidAndNotId(emailid.trim(), rpslno);
            }
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error checking email availability for " + emailid, ex);
        }
    }

    @Transactional(readOnly = true)
    public List<Object[]> getAllResourcePersonsWithPhase(@NotNull @NotBlank String phaseid) {
        try {
            return mtResourcePersonsRepository.getAllResourcePersonsWithPhase(phaseid);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving Resource persons with phaseid " + phaseid, ex);
        }
    }

    @Transactional(readOnly = true)
    public List<Object[]> getResourcePersonsByPhaseid(@NotNull @NotBlank String phaseid) {
        try {
            return mtResourcePersonsRepository.getResourcePersonsPhaseid(phaseid);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving Resource person with phaseid " + phaseid, ex);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public MT_ResourcePersons saveResourcePersons(@NotNull MT_ResourcePersons newResourcePersons) {
        if (newResourcePersons.getRpslno() == null || newResourcePersons.getRpslno().isBlank())
            newResourcePersons.setRpslno(generateNextRpslno());
        
        try {
            return mtResourcePersonsRepository.save(newResourcePersons);
        } catch (Exception ex) {
            throw new RuntimeException("Error saving MT_ResourcePersons entity | Exception = " + ex);
        }
    }

    @Transactional
    public Boolean saveResourcePersonsCourseMap(@NotNull M_Programs program, @NotNull @NotBlank String phaseid, @NotNull List<String> resourcePersons) {
        try {
            mtResourcePersonsRepository.deleteResourcePersonCourseEntryByPhaseid(phaseid);
        } catch (Exception ex) {
            throw new RuntimeException("Error deleting mt_resourcepersoncoursemap entry | Exception = " + ex);
        }

        for (String resourcePerson : resourcePersons) {
            try {
                mtResourcePersonsRepository.createResourcePersonCourseEntry(phaseid, resourcePerson);
            } catch (Exception ex) {
                throw new RuntimeException("Error creating mt_resourcepersoncoursemap entry | Exception = " + ex);
            }
        }

        return true;
    }

    public Page<ResourcePersonsDTO> getResourcePersons(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);

        if (search == null || search.isBlank()) {
            return mtResourcePersonsRepository.getResourcePersons(pageable);
        }
        return mtResourcePersonsRepository.searchResourcePersons(search, pageable);
    }

    private String generateNextRpslno() {
        Integer lastUsedRpslno = mtResourcePersonsRepository.getLastUsedRpslno();
        return lastUsedRpslno == null ? "1" : String.valueOf(lastUsedRpslno + 1);
    }
}
