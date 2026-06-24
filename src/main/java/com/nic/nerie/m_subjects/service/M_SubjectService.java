package com.nic.nerie.m_subjects.service;

import com.nic.nerie.m_subjects.model.M_Subjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.nic.nerie.m_subjects.repository.M_SubjectRepository;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.validation.constraints.NotBlank;

@Service
@Validated
public class M_SubjectService {
    private final M_SubjectRepository mSubjectRepository;

    @Autowired
    public M_SubjectService(M_SubjectRepository mSubjectRepository) {
        this.mSubjectRepository = mSubjectRepository;
    }

    public M_Subjects getSubjectBySubjectcode(@NotNull @NotBlank String subjectcode) {
        subjectcode = subjectcode.trim();

        try {
            Optional<M_Subjects> subject = mSubjectRepository.findById(subjectcode);
            return subject.isPresent() ? subject.get() : null;
        } catch (Exception ex) {
            throw new RuntimeException("Error fetching M_Subjects entity", ex);
        }
    }

    @Transactional(readOnly = true) 
    public List<Object[]> getGeneralStudentFacultySubjectListLongterm(@NotNull @NotBlank String semestercode,
                                                                      @NotNull @NotBlank String coursecode,
                                                                      @NotNull @NotBlank String studentid) {
        try {
            return mSubjectRepository.getGeneralStudentFacultySubjectListLongterm(semestercode.trim(), coursecode.trim(), studentid.trim());
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error fetching general student faculty subject list for long term", ex);
        }
    }

    @Transactional(readOnly = true)
    public List<Object[]> getGeneralStudentFacultySubjectListShortterm(@NotNull @NotBlank String sphaseid,
                                                                       @NotNull @NotBlank String coursecode,
                                                                       @NotNull @NotBlank String studentid) {
        try {
            return mSubjectRepository.getGeneralStudentFacultySubjectListShortterm(sphaseid.trim(), coursecode.trim(), studentid.trim());
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error fetching general student faculty subject list for short term", ex);
        }
    }

    public List<Object[]> getoptionalstudentfacultysubjectlist(String usercode, String studentid) {
        return mSubjectRepository.getoptionalstudentfacultysubjectlist(usercode, studentid);
    }

    public List<Object[]> getStudentSubjectsList(String usercode) {
        return mSubjectRepository.getStudentSubjectsList(usercode);
    }

    public List<Object[]> getCompulsorySubjectsLongTerm(String semestercode, String coursecode) {
        return mSubjectRepository.getCompulsorySubjectsLongTerm(semestercode, coursecode);
    }

    public List<Object[]> getCompulsorySubjectsShortTerm(String sphaseid, String coursecode) {
        return mSubjectRepository.getCompulsorySubjectsShortTerm(sphaseid, coursecode);
    }

    public List<M_Subjects> getAllSubjectList(@NotBlank String officecode) {
        try {
            return mSubjectRepository.findAllByOfficeCode(officecode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving getAllSubjectList: ", ex);
        }
    }

    public List<M_Subjects> getSubjectsByCourseCodes(List<String> courseCodes) {
        if (courseCodes == null || courseCodes.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return mSubjectRepository.findSubjectsByCourseCodes(courseCodes);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving subjects for courses: " + courseCodes, ex);
        }
    }

    public List<M_Subjects> getSubjectsDepartmentSemester(String departmentcode, String semestercode, String coursecode) {
        return mSubjectRepository.findByDepartmentAndSemesterAndCourse(departmentcode, semestercode, coursecode);
    }

    public List<M_Subjects> getSubjectsDepartmentPhase(String departmentcode, String sphaseid, String coursecode) {
        return mSubjectRepository.findByDepartmentAndPhaseAndCourse(departmentcode, sphaseid, coursecode);
    }

    public List<M_Subjects> getNextSemesterOptionalSubjects(@NotNull @NotBlank String departmentcode, @NotNull @NotBlank String spcode) {
        try {
            return mSubjectRepository.findNextSemesterOptionalSubjects(departmentcode.trim(), spcode.trim());
        } catch (Exception ex) {
            throw new RuntimeException("Error fetching optional subjects for next semester", ex);
        }
    }
    
    public List<M_Subjects> getNextPhaseOptionalSubjects(@NotNull @NotBlank String departmentcode, @NotNull @NotBlank String spcode) {
        try {
            return mSubjectRepository.findNextPhaseOptionalSubjects(departmentcode.trim(), spcode.trim());
        } catch (Exception ex) {
            throw new RuntimeException("Error fetching optional subjects for next phase", ex);
        }
    }

    @Transactional(readOnly = true)
    public List<Object[]> getSubjectsList(@NotNull @NotBlank String usercode) {
        try {
            return mSubjectRepository.getSubjectsList(usercode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving M_Subject list by usercode " + usercode, ex);
        } 
    }

    @Transactional(readOnly = true)
    public List<Object[]> getSubjectsListBySemPhaseAndUsercode(
            @NotNull @NotBlank String usercode,
            @NotNull @NotBlank String semphase) {

        try {
            return mSubjectRepository
                    .getSubjectsListBySemPhaseAndUsercode(usercode, semphase);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException(
                    "Error retrieving M_Subject list by usercode "
                            + usercode + " and semphase " + semphase, ex);
        }
    }

    public M_Subjects getSubjectBySubjectCode(String subjectcode) {
        return mSubjectRepository.getSubjectBySubjectCode(subjectcode);
    }

    public List<Object[]> getSubjectListByPhaseid(@NotNull @NotBlank String departmentCode,
                                                  @NotNull @NotBlank String shortTermPhaseId,
                                                  @NotNull @NotBlank String courseCode) {
        try {
            return mSubjectRepository.getSubjectListByPhaseid(departmentCode, shortTermPhaseId, courseCode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving subject list by phaseid: departmentCode: " + departmentCode + ", shortTermPhaseId: " + shortTermPhaseId + ", courseCode: " + courseCode, ex);
        }
    }

    public List<Object[]> getSubjectListBySemestercode(@NotNull @NotBlank String departmentCode,
                                                       @NotNull @NotBlank String semesterCode,
                                                       @NotNull @NotBlank String courseCode) {
        try {
            return mSubjectRepository.getSubjectListBySemestercode(departmentCode, semesterCode, courseCode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving subject list by semesterCode: departmentCode: " + departmentCode + ", semesterCode: " + semesterCode + ", courseCode: " + courseCode, ex);
        }
    }

    public String saveNewSubject(@NotNull M_Subjects subject) {
        try {
            if (subject.getSubjectcode() == null || subject.getSubjectcode().isEmpty()) {
                Integer maxCode = mSubjectRepository.getMaxSubjectCode();
                int newCode = (maxCode == null) ? 1 : maxCode + 1;
                subject.setSubjectcode(String.valueOf(newCode));
            }

            mSubjectRepository.save(subject);

            return "2"; // Success
        } catch (Exception ex) {
            return "1"; // Failure
        }
    }

    public List<Object[]> getSubjectsListByFaculty(@NotNull @NotBlank String usercode) {
        try {
            return mSubjectRepository.findSubjectsByFacultyUsercode(usercode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving subject list by faculty: usercode: " + usercode, ex);
        }
    }

    public List<M_Subjects> findSubjectsByCourseCodeFacultyWise(String coursecode ,@NotNull @NotBlank String usercode) {
        try {
            return mSubjectRepository.findSubjectsByCourseCodeFacultyWise(coursecode,usercode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving subject list by faculty: usercode: " + usercode, ex);
        }
    }



    @Transactional(readOnly = true)
    public Boolean doesSubjectExists(@NotNull @NotBlank String departmentCode, @NotNull @NotBlank String courseCode, @NotNull @NotBlank String subjectName) {
        try {
            return mSubjectRepository.existsByDepartmentAndCourseAndSubjectName(departmentCode.trim(), courseCode.trim(), subjectName.trim());
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error checking if subject exists", ex);
        }
    }

    @Transactional(readOnly = true)
    public Boolean doesSubjectExistWithDifferentCode(@NotNull @NotBlank String departmentCode, @NotNull @NotBlank String courseCode, @NotNull @NotBlank String subjectName, @NotNull @NotBlank String subjectCode) {
        try {
            // Calls the new repository method
            return mSubjectRepository.existsWithDifferentCode(departmentCode.trim(), courseCode.trim(), subjectName.trim(), subjectCode.trim());
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error checking if subject exists with a different code", ex);
        }
    }
}
