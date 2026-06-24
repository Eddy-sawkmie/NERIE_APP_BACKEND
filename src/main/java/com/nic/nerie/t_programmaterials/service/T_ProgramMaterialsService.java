package com.nic.nerie.t_programmaterials.service;

import com.nic.nerie.t_programmaterials.model.T_ProgramMaterials;
import com.nic.nerie.t_programmaterials.repository.T_ProgramMaterialsRepository;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class T_ProgramMaterialsService {
    private final T_ProgramMaterialsRepository repository;

    @Autowired
    public T_ProgramMaterialsService(T_ProgramMaterialsRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public T_ProgramMaterials getByProgrammaterialid(@NotNull @NotBlank String programmaterilid) {
        try {
            Optional<T_ProgramMaterials> programMaterials = repository.findById(programmaterilid.trim());
            return programMaterials.isPresent() ? programMaterials.get() : null;
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving T_ProgramMaterials by programmaterialid " + programmaterilid, ex);
        }
    }

    public List<Object[]> getMaterialsForPhase(@NotNull @NotBlank String phaseid) {
        return repository.findMaterialsByPhaseId(phaseid);
    }

    @Transactional
    public T_ProgramMaterials saveOrUpdateProgramMaterial(@NotNull T_ProgramMaterials newProgramMaterial) {
        try {
            if (newProgramMaterial.getProgrammaterialid() == null || newProgramMaterial.getProgrammaterialid().isBlank())
                newProgramMaterial.setProgrammaterialid(generateNextProgrammaterialid());
            
            return repository.save(newProgramMaterial);
        } catch (Exception ex) {
            throw new RuntimeException("Error saving T_ProgramMaterials entity", ex);
        }
    }

    private String generateNextProgrammaterialid() {
        try {
            Integer lastUsedProgrammaterialid = repository.findLastUsedProgrammaterialid();
            return lastUsedProgrammaterialid == null ? "1" : String.valueOf(lastUsedProgrammaterialid + 1);
        } catch (Exception ex) {
            throw new RuntimeException("Error generating next programmaterialid", ex);
        }
    }

    @Transactional(readOnly = false)
    public void deleteProgramMaterial(@NotNull @NotBlank String programmaterialid) {
        try {
            repository.deleteById(programmaterialid.trim());
        } catch (Exception ex) {
            throw new RuntimeException("Error deleting T_ProgramMaterials entity with programmaterialid " + programmaterialid, ex);
        }
    }

    public T_ProgramMaterials getCourseMaterial(@NotNull @NotBlank String pmid) {
        try {
            return repository.findCourseMaterial(pmid);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Failed fetching course material by pmid: " + pmid, ex);
        }

    }
}
