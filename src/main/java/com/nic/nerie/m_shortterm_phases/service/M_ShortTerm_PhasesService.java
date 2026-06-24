package com.nic.nerie.m_shortterm_phases.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;

import com.nic.nerie.m_shortterm_phases.model.M_ShortTerm_Phases;
import com.nic.nerie.m_shortterm_phases.repository.M_ShortTerm_PhasesRepository;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.validation.constraints.NotBlank;

@Service
public class M_ShortTerm_PhasesService {
    private final M_ShortTerm_PhasesRepository mShortTermPhasesRepository;

    @Autowired
    public M_ShortTerm_PhasesService(M_ShortTerm_PhasesRepository mShortTermPhasesRepository) {
        this.mShortTermPhasesRepository = mShortTermPhasesRepository;
    }

    public M_ShortTerm_Phases getShortTermPhaseBySphaseid(@NotNull @NotBlank String sphaseid) {
        sphaseid = sphaseid.trim();

        try {
            Optional<M_ShortTerm_Phases> shortTermPhaseOptional = mShortTermPhasesRepository.findById(sphaseid);
            return shortTermPhaseOptional.isPresent() ? shortTermPhaseOptional.get() : null;
        } catch (Exception ex) {
            throw new RuntimeException("Error fetching M_ShortTerm_Phases entity", ex);
        }
    }

    public Boolean checkSPhaseExists(@NotNull @NotBlank String sphaseid) {
        return mShortTermPhasesRepository.existsById(sphaseid);
    }

    public List<M_ShortTerm_Phases> getSPhaseList() {
        try {
            return mShortTermPhasesRepository.findAllOrderBySphaseidAscSphasenameAsc();
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving get SPhaseList", ex);
        }
    }

    // public List<M_ShortTerm_Phases> getSPhaseList() {
    //     return mShortTermPhasesRepository.getSPhaseList();
    // }

    public List<Object[]> getMasterPhases() {
        try {
            return mShortTermPhasesRepository.getMasterPhases();
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving Master Phases: ", ex);
        }
    }
}
