package com.nic.nerie.t_alumni.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.nic.nerie.t_alumni.model.T_Alumni;
import com.nic.nerie.t_alumni.repository.T_AlumniRepository;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.validation.constraints.NotBlank;

@Service
@Validated
public class T_AlumniService {

    private final T_AlumniRepository tAlumniRepository;

    @Autowired
    public T_AlumniService(T_AlumniRepository tAlumniRepository) {
        this.tAlumniRepository = tAlumniRepository;
    }
    
    public List<Object[]> getAlumniList(@NotBlank String officecode) {
        try {
            return tAlumniRepository.findAlumniList(officecode);
        } catch (Exception ex) {
            throw new RuntimeException("Error retrieving T_Alumni list", ex);
        }
    }

    public List<Object[]> getAlumniDetails(@NotNull @NotBlank String alumniid) {
        try {
            return tAlumniRepository.findAlumniDetailsByAlumniid(alumniid.trim());
        } catch (Exception ex) {
            throw new RuntimeException("Error retrieving alumni details for alumniid = " + alumniid, ex);
        }
    }

    public Boolean existsByRollno(@NotNull @NotBlank String rollno) {
        rollno = rollno.toUpperCase();

        try {
            return tAlumniRepository.existsByRollno(rollno);
        } catch (Exception ex) {
            throw new RuntimeException("Error check T_Alumni existence by rollno = " + rollno, ex);
        }
    }

    public T_Alumni configureAndSaveTAlumni(@NotNull T_Alumni newAlumni) {
        try {
            if (newAlumni.getAlumniid() == null || newAlumni.getAlumniid().isBlank())
                newAlumni.setAlumniid(generateNextAlumniid());
            return saveOrUpdateTAlumni(newAlumni);
        } catch (Exception ex) {
            throw new RuntimeException("Error configuring and saving alumni. Reasons = " + ex.getMessage(), ex);
        }
    }

    @Transactional(readOnly = false)
    public T_Alumni saveOrUpdateTAlumni(@NotNull T_Alumni newOrUpdatedAlumni) {
        try {
            return tAlumniRepository.save(newOrUpdatedAlumni);
        } catch (Exception ex) {
            throw new RuntimeException("Error saving T_Alumni entity", ex);
        }
    }

    @Transactional(rollbackFor = Exception.class, readOnly = false)
    public String createAlumni(@NotNull T_Alumni alumni) {
        try {
            if (alumni.getAlumniid() == null || alumni.getAlumniid().trim().isEmpty())
                alumni.setAlumniid(generateNextAlumniid());

            tAlumniRepository.save(alumni); // saveOrUpdate equivalent in JPA
            return alumni.getAlumniid();
        } catch (Exception e) {
            e.printStackTrace();
            return "-1";
        }
    }

    @Transactional(readOnly = true)
    private String generateNextAlumniid() {
        try {
            Integer lastUsedAlumniid = tAlumniRepository.findLastUsedAlumniid();
            return lastUsedAlumniid == null ? "1" : String.valueOf(lastUsedAlumniid + 1);
        } catch (Exception ex) {
            throw new RuntimeException("Error fetching last used alumniid", ex);
        }
    }
}


