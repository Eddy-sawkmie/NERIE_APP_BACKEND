package com.nic.nerie.t_student_subject.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.nic.nerie.m_subjects.model.M_Subjects;
import com.nic.nerie.m_subjects.service.M_SubjectService;
import com.nic.nerie.t_student_subject.model.T_Student_Subject;
import com.nic.nerie.t_student_subject.repository.T_Student_SubjectRepository;
import com.nic.nerie.t_students.model.T_Students;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotBlank;

@Service
@Validated
public class T_Student_SubjectService {
    private final T_Student_SubjectRepository studentSubjectRepository;
    private final M_SubjectService mSubjectService;
    private final T_Student_SubjectRepository tStudentSubjectRepository;

    @Autowired
    public T_Student_SubjectService(T_Student_SubjectRepository studentSubjectRepository,
                                    M_SubjectService mSubjectService,
                                    T_Student_SubjectRepository tStudentSubjectRepository) {
        this.studentSubjectRepository = studentSubjectRepository;
        this.mSubjectService = mSubjectService;
        this.tStudentSubjectRepository = tStudentSubjectRepository;
    }

    @Transactional
    public String falsifyStudentSubject(@NotNull @NotBlank String usercode) {
        try {
            int rowsUpdated = tStudentSubjectRepository.falsifyStudentSubject(usercode);
            return (rowsUpdated > 0) ? "1" : "0"; // 1 = success
        } catch (Exception e) {
            e.printStackTrace();
            return "-1";
        }
    }

    @Transactional
    public String saveStudentSubject(@NotNull T_Student_Subject studentSubject) {
        try {
            tStudentSubjectRepository.save(studentSubject); // Handles insert/update
            return "1"; // Success
        } catch (Exception e) {
            e.printStackTrace();
            return "-1"; // Error
        }
    }

    @Transactional(readOnly = true)
    public List<T_Student_Subject> getTStudentSubjectByUsercode(@NotNull @NotBlank String usercode) {
        try {
            return studentSubjectRepository.findByUsercode(usercode.trim());
        } catch (Exception ex) {
            throw new RuntimeException("Error fetching T_Student_Subject by usercode", ex);
        }
    }

    /*
     * Saves a list of T_Student_Subject entities for a given student.
     * @param subjectCodeList List of subject codes to be saved.
     * @param student The T_Students entity for which the subjects are being saved.
     * @return List of saved T_Student_Subject entities.
     * @throws RuntimeException if there is an error during any of the save operation.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public List<T_Student_Subject> saveTStudentSubjectEntityList(@NotNull List<String> subjectCodeList, @NotNull T_Students student) {
        T_Student_Subject savedStudentSubject = null;
        List<T_Student_Subject> savedStudentSubjectList = new ArrayList<>();

        try {
            String scd;

            for (String subjectCode : subjectCodeList) {
                T_Student_Subject newStudentSubject = new T_Student_Subject();
                M_Subjects existingSubject;
                scd = String.format("%03d", Integer.parseInt(subjectCode));

                // Check if the subject code is valid
                existingSubject = mSubjectService.getSubjectBySubjectcode(subjectCode);
                if (existingSubject == null)
                    throw new EntityNotFoundException("M_Subject entity with subjectcode = " + subjectCode + " does not exist.");

                newStudentSubject.setSubjectcode(existingSubject);
                
                System.out.println("usercode = " + student.getUsercode());

                newStudentSubject.setUsercode(student.getUsercode());
                newStudentSubject.setIsactive("1");

                // Old-code: newStudentSubject.setTssid(student.getStudentid() + "1" + scd);
                // Changed because of [ERROR: value too long for type character varying(15)]
                newStudentSubject.setTssid(student.getStudentid() + scd);


                if ((savedStudentSubject = saveTStudentSubjectEntity(newStudentSubject)) != null)
                    savedStudentSubjectList.add(savedStudentSubject);
                else
                    throw new RuntimeException("Failed to save T_Student_Subject entity for subject code: " + subjectCode);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }

        return savedStudentSubjectList;
    }

    /*
     * Saves a new T_Student_Subject entity.
     * @param newStudentSubject The T_Student_Subject entity to be saved.
     * @return The saved T_Student_Subject entity.
     * @throws RuntimeException if there is an error during the save operation.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public T_Student_Subject saveTStudentSubjectEntity(@NotNull T_Student_Subject newStudentSubject) {
        try {
            return studentSubjectRepository.save(newStudentSubject);
        } catch (Exception ex) {
            throw new RuntimeException("Error saving T_Student_Subject entity", ex);
        }
    }
}
