package com.nic.nerie.t_studymaterials.service;

import com.nic.nerie.t_studymaterials.model.T_StudyMaterials;
import com.nic.nerie.t_studymaterials.repository.T_StudyMaterialsRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class T_StudyMaterialsService {
    private final T_StudyMaterialsRepository tStudyMaterialsRepository;

    @Autowired
    public T_StudyMaterialsService(T_StudyMaterialsRepository tStudyMaterialsRepository) {
        this.tStudyMaterialsRepository = tStudyMaterialsRepository;
    }

    @Transactional(readOnly = true)
    public List<T_StudyMaterials> getStudyMaterialsListSubject(String subjectcode) {
        try {
            return tStudyMaterialsRepository.findStudyMaterialsBySubjectcode(subjectcode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error fetching study materials for subject: " + subjectcode, ex);
        }
    }

    public List<T_StudyMaterials> getAllStudyMaterials(@NotNull @NotBlank String facultyid) {
        try {
            return tStudyMaterialsRepository.findByFacultyIdOrderByUploadDateDesc(facultyid);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving getAllStudyMaterials by facultyid: " + facultyid, ex);
        }
    }

    public T_StudyMaterials getStudyMaterialDocument(@NotNull @NotBlank String sid) {
        try {
            return tStudyMaterialsRepository.findByStudyMaterialId(sid);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving getStudyMaterialDocument by sid: " + sid, ex);
        }
    }

    public List<Object[]> getStudyMaterialsListSubjectFaculty(@NotNull @NotBlank String subjectcode,
                                                              @NotNull @NotBlank String facultyid) {
        try {
            if ("-1".equals(subjectcode))
                subjectcode = "All";

            return tStudyMaterialsRepository.findStudyMaterialsBySubjectAndFaculty(subjectcode, facultyid);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException(String.format("Error retrieving getStudyMaterialsListSubjectFaculty : subjectcode: %s, facultyid: %s", subjectcode, facultyid), ex);
        }
    }

    @Transactional
    public String uploadStudyMaterial(@NotNull T_StudyMaterials materials) {
        try {
            // Generate ID if not present
            if (materials.getStudymaterialid() == null || materials.getStudymaterialid().isEmpty()) {
                Integer maxId = tStudyMaterialsRepository.getMaxStudyMaterialId();
                int nextId = (maxId == null) ? 1 : maxId + 1;
                materials.setStudymaterialid(String.valueOf(nextId));
            }

            tStudyMaterialsRepository.saveAndFlush(materials);
            return materials.getStudymaterialid();
        } catch (Exception e) {
            e.printStackTrace();
            return "-1";
        }
    }
}
