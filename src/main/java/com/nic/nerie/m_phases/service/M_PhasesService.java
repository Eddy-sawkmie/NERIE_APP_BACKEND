package com.nic.nerie.m_phases.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nic.nerie.m_phases.model.M_Phases;
import com.nic.nerie.m_phases.repository.M_PhasesRepository;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class M_PhasesService {
    private final M_PhasesRepository mPhasesRepository;

    @Autowired
    public M_PhasesService(M_PhasesRepository mPhasesRepository) {
        this.mPhasesRepository = mPhasesRepository;
    }

    @Transactional(readOnly = true)
    public Optional<M_Phases> findById(@NotNull @NotBlank String phaseId) {
        try {
            return mPhasesRepository.findById(phaseId);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving M_Phases entity with phaseId: " + phaseId, ex);
        }
    }

    @Transactional(readOnly = true)
    public Boolean existsById(@NotNull @NotBlank String phaseId) {
        try {
            return mPhasesRepository.existsById(phaseId);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error checking existence of M_Phases entity with phaseId: " + phaseId, ex);
        }
    }

    public List<Object[]> getDashboardRecentlyCompletedPhasesList(final Integer coursetype, final Integer limit, final String officecode) {
        return mPhasesRepository.getDashboardRecentlyCompletedPhasesList(coursetype, limit, officecode);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public M_Phases savePhaseDetails(@NotNull M_Phases newPhase) {
        try {
            if (newPhase.getPhaseid() == null || newPhase.getPhaseid().isBlank())
                newPhase.setPhaseid(generateNextPhaseid());
            return mPhasesRepository.save(newPhase);
        } catch (Exception ex) {
            throw new RuntimeException("Error saving M_Phases entity", ex);
        }
    }
    public String getPhasenoByPhaseid(String phaseid) {
        Integer phaseno = mPhasesRepository.getPhasenoByPhaseid(phaseid);

        return phaseno != null ? String.valueOf(phaseno) : null;
    }

    @Transactional(readOnly = true)
    public String generateNextPhaseid() {
        try {
            Integer lastUsedPhaseid = mPhasesRepository.getLastUsedPhaseid();
            return lastUsedPhaseid == null ? "1" : String.valueOf(lastUsedPhaseid + 1);
        } catch (Exception ex) {
            throw new RuntimeException("Error generating next phaseid", ex);
        }
    }

    public String generateNextPhaseno(String programcode) {
        Integer lastUsedPhaseno = mPhasesRepository.getLastUsedPhaseno(programcode);
        Integer nextPhaseno = 1;

        if (lastUsedPhaseno != null)
            nextPhaseno = lastUsedPhaseno + 1;

        return String.valueOf(nextPhaseno);
    }

    public List<Object[]> getUnClosePhasesList(@NotNull @NotBlank String programcode) {
        return mPhasesRepository.getUnClosePhasesList(programcode);
    }

    public List<Object[]> getPhasesBasedOnProgramCode(@NotNull @NotBlank String programCode) {
        try {
            return mPhasesRepository.getPhasesBasedOnProgramCode(programCode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error getting phases based on program code: " + programCode, ex);
        }
    }

    public List<Object[]> getPhasesByProgramcode(@NotNull @NotBlank String programcode) {
        return mPhasesRepository.getPhasesByProgramcode(programcode);
    }

    @Transactional(readOnly = true)
    public Long getPhasesCountByProgramcode(@NotNull @NotBlank String programcode) {
        try {
            return mPhasesRepository.getPhasesCountByProgramcode(programcode.trim());
        } catch (Exception ex) {
            throw new RuntimeException("Error deleting M_Phases entity by programcode " + programcode, ex);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void deleteByProgramcode(@NotNull @NotBlank String programcode) {
        try {
            if (mPhasesRepository.deleteByProgramcode(programcode.trim()) == 0)
                throw new Exception();
        } catch (Exception ex) {
            throw new RuntimeException("Error deleting M_Phases entity by programcode " + programcode, ex);
        }
    }

    @Transactional(readOnly = true)
    public M_Phases getPhaseByPhaseId(@NotNull @NotBlank String phaseId) {
        try {
            return mPhasesRepository.getPhaseByPhaseId(phaseId);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error getting Phase By Phase Id" + phaseId, ex);
        }
    }

    public List<Object[]> getPhaseDetailsForFeedbackByPhaseId(@NotNull @NotBlank String phaseId) {
        try {
            return mPhasesRepository.findPhaseDetailsForFeedbackByPhaseId(phaseId);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Failed to fetch phase details for feedback by phase id: " + phaseId, ex);
        }
    }

    public List<Object[]> getPhasesBasedOnProgramCodeWithoutPrePostTest(String pcode) {
        try {
            return mPhasesRepository.findPhasesBasedOnProgramCodeWithoutPrePostTest(pcode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error fetching phases based on program code without pre post test: " + pcode, ex);
        }
    }
}
