package com.nic.nerie.t_phase_preposttest.service;

import com.nic.nerie.t_phase_preposttest.model.T_Phase_PrePostTest;
import com.nic.nerie.t_phase_preposttest.repository.T_Phase_PrePostTestRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class T_Phase_PrePostTestService {
    private final T_Phase_PrePostTestRepository tPhasePrePostTestRepository;

    @Autowired
    public T_Phase_PrePostTestService(T_Phase_PrePostTestRepository tPhasePrePostTestRepository) {
        this.tPhasePrePostTestRepository = tPhasePrePostTestRepository;
    }

    @Transactional
    public boolean saveNewPhasePrePostTest(@NotNull T_Phase_PrePostTest ptest) {
        try {
            Integer maxId = tPhasePrePostTestRepository.findMaxTestId();
            String newId = (maxId == null ? 1 : maxId + 1) + "";
            ptest.setTestid(newId);

            tPhasePrePostTestRepository.save(ptest);
            return true;
        } catch (Exception ex) {
            throw new RuntimeException("Error saving New Phase Pre Post Test", ex);
        }
    }

    public boolean testExistsForPhase(@NotNull @NotBlank String phaseId) {
        try {
            return tPhasePrePostTestRepository.countByPhaseId(phaseId) > 0;
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error getting the count by phase id: " + phaseId, ex);
        }
    }

    public T_Phase_PrePostTest getPhasePrePostTestByPhaseId(@NotNull @NotBlank String phid) {
        try {
            return tPhasePrePostTestRepository.findPhasePrePostTestByPhaseId(phid);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error getting phase pre post test by phase id: " + phid, ex);
        }
    }

    public T_Phase_PrePostTest getPhasePrePostTestById(@NotNull @NotBlank String testid) {
        try {
            T_Phase_PrePostTest pppt = tPhasePrePostTestRepository.findPhasePrePostTestById(testid);
            return pppt;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
