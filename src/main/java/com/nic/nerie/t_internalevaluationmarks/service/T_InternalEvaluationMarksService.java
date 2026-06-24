package com.nic.nerie.t_internalevaluationmarks.service;

import com.nic.nerie.t_internalevaluationmarks.model.T_InternalEvaluationMarks;
import com.nic.nerie.t_internalevaluationmarks.repository.T_InternalEvaluationMarksRepository;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.crypto.Data;
import java.util.List;

@Service
public class T_InternalEvaluationMarksService {

    private final T_InternalEvaluationMarksRepository repository;

    @Autowired
    public T_InternalEvaluationMarksService(T_InternalEvaluationMarksRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public String saveStudentInternalEvaluationMarks(@NotNull T_InternalEvaluationMarks ie) {
        String res = "-1";
        try {
            // Check if internalevaluationid is null or empty
            if (ie.getInternalevaluationid() == null || ie.getInternalevaluationid().trim().isEmpty() || "null".equalsIgnoreCase(ie.getInternalevaluationid())) {
                Integer maxId = repository.findMaxInternalEvaluationId();
                Integer newId = (maxId == null || maxId == 0) ? 1 : maxId + 1;
                ie.setInternalevaluationid(newId.toString());
            }

            repository.save(ie);

            res = ie.getInternalevaluationid();

        } catch (Exception e) {
            e.printStackTrace();
            res = "-1"; // error
        }
        return res;
    }

    @Transactional
    public void deleteMarkById(String id) {
        if (id != null && !id.trim().isEmpty()) {
            try {
                repository.deleteById(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public List<Object[]> getMarksByStudentAndSubject(@NotNull @NotBlank String studentid, @NotNull @NotBlank String subjectcode) {
        try {
            return repository.findMarksByStudentAndSubject(studentid, subjectcode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving getMarksByStudentAndSubject by studentid: " + studentid + ", and subjectcode: " + subjectcode, ex);
        }
    }
}
