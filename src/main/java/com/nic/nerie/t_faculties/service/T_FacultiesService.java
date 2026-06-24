package com.nic.nerie.t_faculties.service;

import com.nic.nerie.mt_userlogin.repository.MT_UserloginRepository;
import com.nic.nerie.t_faculties.model.T_Faculties;
import com.nic.nerie.t_faculties.repository.T_FacultiesRepository;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.List;

@Service
@Validated
public class T_FacultiesService {
    private final T_FacultiesRepository tFacultiesRepository;
    private final MT_UserloginRepository mtUserloginRepository;

    public T_FacultiesService(T_FacultiesRepository tFacultiesRepository, MT_UserloginRepository mtUserloginRepository) {
        this.tFacultiesRepository = tFacultiesRepository;
        this.mtUserloginRepository = mtUserloginRepository;
    }

    public List<Object[]> getDeptAndFacultyDetails(String usercode) {
        return tFacultiesRepository.getDeptAndFacultyDetails(usercode);
    }

    public T_Faculties getFaculty(@NotNull @NotBlank String usercode) {
        try {
            return tFacultiesRepository.getFacultyByUsercode(usercode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving faculty bu usercode: " + usercode, ex);
        }
    }

    public List<Object[]> getFacultySubjectsListByUser(@NotNull @NotBlank String usercode, @NotNull @NotBlank String officecode) {
        try {
            return tFacultiesRepository.findFacultySubjectsAndCoursesByUsercode(usercode, officecode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException(String.format("Error retrieving getFacultySubjectsListByUser by usercode: %s and officecode: %s", usercode, officecode), ex);
        }
    }

    public List<Object[]> getFacultySubjectsList(@NotBlank String officecode) {
        try {
            return tFacultiesRepository.findAllFacultySubjectsAndCourses(officecode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving getFacultySubjectsList with office code: " + officecode, ex);
        }
    }

    public List<Object[]> getFacultyDetails(@NotNull @NotBlank String usercode) {
        try {
            return tFacultiesRepository.findFacultyDetailsByUsercode(usercode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving faculty details by usercode: " + usercode, ex);
        }
    }

    @Transactional
    public String createFaculty(@NotNull T_Faculties fac) {
        try {
            if (fac.getFacultyid() == null || fac.getFacultyid().isEmpty()) {
                Integer maxId = tFacultiesRepository.findMaxFacultyId();
                int nextId = (maxId == null) ? 1 : maxId + 1;
                fac.setFacultyid(String.valueOf(nextId));
            }

            // Save or update faculty
            T_Faculties saved = tFacultiesRepository.save(fac);

            // Update mt_userlogin.isfaculty
            mtUserloginRepository.updateIsFaculty(String.valueOf(saved.getUsercode()));

            return saved.getFacultyid();
        } catch (Exception e) {
            e.printStackTrace();
            return "-1";
        }
    }

    @Transactional
    public String saveFacultySubjects(@NotNull @NotBlank String usercode, @NotNull String[] subjects) {
        try {
            // Delete existing entries
            tFacultiesRepository.deleteFacultySubjectByUsercode(usercode);

            // Insert new subjects
            for (String subjectCode : subjects) {
                tFacultiesRepository.insertFacultySubject(usercode, subjectCode);
            }

            return "1";
        } catch (Exception e) {
            e.printStackTrace();

            return "-1";
        }
    }

    @Transactional
    public String saveFacultyCourses(@NotNull @NotBlank String usercode, @NotNull String[] courses) {
        try {
            // Delete existing entries
            tFacultiesRepository.deleteFacultyCoursesByUsercode(usercode);

            // Insert new courses
            for (String courseCode : courses) {
                tFacultiesRepository.insertFacultyCourse(usercode, courseCode);
            }

            return "1";
        } catch (Exception e) {
            e.printStackTrace();

            return "-1";
        }
    }

    public T_Faculties getFacultyByFacultyID(@NotNull @NotBlank String facultyid) {
        try {
            return tFacultiesRepository.findFacultyByFacultyId(facultyid);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving faculty by id: " + facultyid, ex);
        }
    }

    public List<Object[]> getFacultyResearchList(String officecode) {
        try {
            return tFacultiesRepository.findFacultyResearchList(officecode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving faculty research list with office code: " + officecode);
        }
    }
}
