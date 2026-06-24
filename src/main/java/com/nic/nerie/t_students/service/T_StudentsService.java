package com.nic.nerie.t_students.service;

import com.nic.nerie.m_course_academics.model.M_Course_Academics;
import com.nic.nerie.m_course_academics.service.M_Course_AcademicsService;
import com.nic.nerie.m_offices.model.M_Offices;
import com.nic.nerie.m_semesters.model.M_Semesters;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.t_students.model.T_Students;
import com.nic.nerie.t_students.repository.T_StudentsRepository;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Service
@Validated
public class T_StudentsService {
    private final T_StudentsRepository tStudentsRepository;
    private final M_Course_AcademicsService mCourseAcademicsService;

    @Autowired
    public T_StudentsService(T_StudentsRepository tStudentsRepository, M_Course_AcademicsService mCourseAcademicsService) {
        this.tStudentsRepository = tStudentsRepository;
        this.mCourseAcademicsService = mCourseAcademicsService;
    }

    public Boolean existsByStudentid(@NotNull @NotBlank String studentid) {
        studentid = studentid.trim();

        try {
            return tStudentsRepository.existsByStudentid(studentid);
        } catch (Exception ex) {
            throw new RuntimeException("Error checking existence of student with ID: " + studentid, ex);
        }
    }

    public T_Students findByUsercode(MT_Userlogin usercode) {
        Optional<T_Students> tStudents = tStudentsRepository.findByUsercode(usercode);

        if (tStudents.isPresent())
            return tStudents.get();

        return null;
    }

    @Transactional(readOnly = true)
    public T_Students findByUsercode(@NotNull @NotBlank String usercode) {
        try {
            Optional<T_Students> tStudents = tStudentsRepository.findByUsercode(usercode);
            return tStudents.isPresent() ? tStudents.get() : null;
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error fetching T_Students entity by usercode", ex);
        }
    }

    public T_Students findByStudentid(@NotNull @NotBlank String studentid, @NotNull @NotBlank String officecode) {
        try {
            Optional<T_Students> studentOptional = tStudentsRepository.findByStudentid(studentid, officecode);
            return studentOptional.isPresent() ? studentOptional.get() : null;
        } catch (Exception ex) {
            throw new RuntimeException("Error fetching T_Students entity", ex);
        }
    }

    /*
     * This method retrieves all the rollno of students in a list
     */
    public List<String> getRollnoList(@NotBlank String officecode) {
        try {
            return tStudentsRepository.getRollnoList(officecode);
        } catch (Exception ex) {
            throw new RuntimeException("Error retrieving T_Students rollno list", ex);
        }
    }

    public List<Object[]> getMyHomePageAttendance(M_Semesters semesterscode,
                                                  M_Course_Academics coursecode,
                                                  String studentid) {
        return tStudentsRepository.getMyHomePageAttendance(semesterscode.getSemestercode(), coursecode.getCoursecode(), studentid);
    }

    public List<Object[]> getStudentList(@NotBlank String officecode) {
        return tStudentsRepository.getStudentList(officecode);
    }

    /*
     * This method is used to configure rollno & student id and save a new T_Students entity.
     * @param newStudent The T_Students entity to be configured and saved.
     * @return The saved T_Students entity with configured rollno and studentid.
     * @throws RuntimeException if there is an error during the configuration or save operation.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public T_Students configureAndSaveTStudentsEntity(@NotNull T_Students newStudent,
                                                      @NotNull M_Offices office
    ) {
        String temporaryRollno = "";

        // setting rollno for new student based on isshortterm and courseAcademics
        try {
            Integer maxId = null;
            M_Course_Academics courseAcademics = mCourseAcademicsService.getCourseAcademicsByCoursecode(newStudent.getCoursecode().getCoursecode());
            if (newStudent.getIsshortterm().equals("1")) {
                newStudent.setSemestercode(null);
                temporaryRollno = office.getShorttermcoursecode() + courseAcademics.getCourseid() + newStudent.getAcademicyear().substring(2, 4);
                maxId = tStudentsRepository.findMaxRollNumberSuffixForShortTermCourse(temporaryRollno);
            } else if (newStudent.getIsshortterm().equals("0")) {
                newStudent.setSphaseid(null);
                temporaryRollno = newStudent.getAcademicyear().substring(2, 4) + office.getLongtermcoursecode() + courseAcademics.getCourseid();
                maxId = tStudentsRepository.findMaxRollNumberSuffix(temporaryRollno);
            } else
                throw new RuntimeException("Invalid ishortterm flag");

            // assigning suffix to rollno and setting it to studentid and rollno
            try {

                int nextId = (maxId == null) ? 1 : maxId + 1;
                String suffix;

                if (newStudent.getIsshortterm().equals("1"))
                    suffix = String.format("%02d", nextId);
                else
                    suffix = String.format("%03d", nextId);

                String finalId = temporaryRollno + suffix;
                newStudent.setStudentid(finalId);
                newStudent.setRollno(finalId);
            } catch (Exception ex) {
                throw new RuntimeException("Error fetching max roll number suffix for prefix: " + temporaryRollno, ex);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error configuring roll number for new student - " + ex.getMessage(), ex);
        }

        // saving student instance
        try {
            return tStudentsRepository.save(newStudent);
        } catch (RuntimeException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    /*
     * This method is used to update or save a T_Students entity.
     * @param newStudent The T_Students entity to be updated or saved.
     * @return The updated or saved T_Students entity.
     * @throws IllegalArgumentException if the newStudent is null.
     * @throws RuntimeException if there is an error during the save operation.
    */
    @Transactional(propagation = Propagation.REQUIRED)
    public T_Students updateOrSaveTStudentsEntity(@NotNull T_Students newStudent) {
        try {
            return tStudentsRepository.save(newStudent);
        } catch (Exception ex) {
            throw new RuntimeException("Error saving T_Students entity", ex);
        }
    }

    public List<Object[]> getOptionalPhaseSubjectStudents(String subjectcode) {
        return tStudentsRepository.getOptionalPhaseSubjectStudents(subjectcode);
    }

    public List<Object[]> getGeneralPhaseSubjectStudents(String subjectcode) {
        return tStudentsRepository.getGeneralPhaseSubjectStudents(subjectcode);
    }

    public List<Object[]> getOptionalSemesterSubjectStudents(String subjectcode) {
        return tStudentsRepository.getOptionalSemesterSubjectStudents(subjectcode);
    }

    public List<Object[]> getGeneralSemesterSubjectStudents(String subjectcode) {
        return tStudentsRepository.getGeneralSemesterSubjectStudents(subjectcode);
    }

    public List<Object[]> getStudentsList(@NotNull @NotBlank String subjectcode, @NotNull @NotBlank String testid) {
        try {
            return tStudentsRepository.getStudentsList(subjectcode, testid);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving getStudentsList by subjectcode: " + subjectcode + ", testid: " + testid, ex);
        }
    }

    public List<Object[]> getSubjectListOfStudentsSemester(@NotNull @NotBlank String dcode,
                                                           @NotNull @NotBlank String ccode,
                                                           @NotNull @NotBlank String semphase,
                                                           @NotNull @NotBlank String officecode) {
        try {
            return tStudentsRepository.getSubjectListOfStudentsSemester(dcode, ccode, semphase, officecode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException(String.format("Error retrieving subject list of student semester: departmentcode: %s, coursecode: %s, semphase: %s, officecode: %s", dcode, ccode, semphase, officecode), ex);
        }
    }

    public List<Object[]> getSubjectListOfStudentsPhase(@NotNull @NotBlank String dcode,
                                                        @NotNull @NotBlank String ccode,
                                                        @NotNull @NotBlank String semphase,
                                                        @NotNull @NotBlank String officecode) {
        try {
            return tStudentsRepository.getSubjectListOfStudentsPhase(dcode, ccode, semphase, officecode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException(String.format("Error retrieving subject list of student phase: departmentcode: %s, coursecode: %s, semphase: %s, officecode: %s", dcode, ccode, semphase, officecode), ex);
        }
    }

    @Transactional(readOnly = false)
    public String updateStudent2(@NotNull T_Students student) {
        try {
            tStudentsRepository.save(student); // saveOrUpdate equivalent in JPA
            return "1"; // Success
        } catch (Exception e) {
            e.printStackTrace();
            return "-1"; // Error
        }
    }

    public String getStudentIdByUsercode(@NotNull @NotBlank String usercode) {
        try {
            return tStudentsRepository.findStudentIdByUsercode(usercode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving getStudentIdByUsercode by usercode: " + usercode, ex);
        }
    }
    // Get all students
    public List<T_Students> getAllStudents() {
        return tStudentsRepository.findAll();
    }

    public List<Object[]> getAllStudentsWithSubjects(@NotBlank String officecode) {
        try {
            return tStudentsRepository.getAllStudentsWithSubjects(officecode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException(
                    "Error retrieving getAllStudentsWithSubjects for officecode: " + officecode, ex);
        }
    }
}
