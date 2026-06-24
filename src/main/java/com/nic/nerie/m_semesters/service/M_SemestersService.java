package com.nic.nerie.m_semesters.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.nic.nerie.m_semesters.model.M_Semesters;
import com.nic.nerie.m_semesters.repository.M_SemestersRepository;
import com.nic.nerie.m_shortterm_phases.repository.M_ShortTerm_PhasesRepository;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.validation.constraints.NotBlank;

@Service
public class M_SemestersService {
    private final M_SemestersRepository mSemestersRepository;
    private final M_ShortTerm_PhasesRepository mShorttermPhasesRepository;

    @Autowired
    public M_SemestersService(M_SemestersRepository mSemestersRepository, M_ShortTerm_PhasesRepository mShorttermPhasesRepository) {
        this.mSemestersRepository = mSemestersRepository;
        this.mShorttermPhasesRepository = mShorttermPhasesRepository;
    }

    public M_Semesters getSemesterBySemestercode(@NotNull @NotBlank String semestercode) {
        semestercode = semestercode.trim();

        try {
            Optional<M_Semesters> semesterOptional = mSemestersRepository.findById(semestercode);
            return semesterOptional.isPresent() ? semesterOptional.get() : null;
        } catch (Exception ex) {
            throw new RuntimeException("Error fetching M_Semesters entity", ex);
        }
    }

    public Boolean checkSemesterExists(@NotNull @NotBlank String semestercode) {
        return mSemestersRepository.existsById(semestercode);
    }

    public List<M_Semesters> getSemesterList() {
        try {
            return mSemestersRepository.findAllByOrderBySemestercodeAscSemesternameAsc();
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error fetching Semester List", ex);
        }
    }

    public List<Object[]> getMasterSemesters() {
        try {
            return mSemestersRepository.getMasterSemesters();
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving masters semesters" ,ex);
        }
    }

    public List<Map<String,String>> getSemPhaseList() {
        List<Map<String,String>> result = new ArrayList<>();
        // semesters
        List<Object[]> sems = mSemestersRepository.getMasterSemesters();
        for(Object[] s : sems){
            Map<String,String> item = new HashMap<>();
            String code = "S" + s[0].toString();
            item.put("value", code);
            item.put("label", s[1].toString().replace("Semester","") + " Semester");
            result.add(item);
        }
        // phases
        List<Object[]> phases = mShorttermPhasesRepository.getMasterPhases();
        for(Object[] p : phases){
            Map<String,String> item = new HashMap<>();
            String code = "P" + p[0].toString();
            item.put("value", code);
            item.put("label", p[1].toString());
            result.add(item);
        }
        return result;
    }
}


