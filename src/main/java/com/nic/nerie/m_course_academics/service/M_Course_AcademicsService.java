package com.nic.nerie.m_course_academics.service;

import com.nic.nerie.m_course_academics.model.M_Course_Academics;
import com.nic.nerie.m_course_academics.repository.M_Course_AcademicsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.validation.constraints.NotBlank;

import javax.xml.crypto.Data;

@Service
public class M_Course_AcademicsService {
    private final M_Course_AcademicsRepository courseAcademicsRepository;

    @Autowired
    public M_Course_AcademicsService(M_Course_AcademicsRepository courseAcademicsRepository) {
        this.courseAcademicsRepository = courseAcademicsRepository;
    }

    public M_Course_Academics getCourseAcademicsByCoursecode(@NotNull @NotBlank String coursecode) {
        coursecode = coursecode.trim();

        try {
            Optional<M_Course_Academics> courseAcademicsOptional = courseAcademicsRepository.findById(coursecode);
            return courseAcademicsOptional.isPresent() ? courseAcademicsOptional.get() : null;
        } catch (Exception ex) {
            throw new RuntimeException("Error fetching M_Course_Academics entity", ex);
        }
    }

    public List<Object[]> getCourseAcademicsByDepartmentcode(@NotNull @NotBlank String departmentcode) {
        departmentcode = departmentcode.trim();

        try {
            return courseAcademicsRepository.getByDepartmentcodeOrderByCoursecodeCoursenameAsc(departmentcode);
        } catch (Exception ex) {
            throw new RuntimeException("Error retrieving M_Course_Academics list by departmentcode = " + departmentcode, ex);
        }
    }

    public Boolean checkCourseExists(@NotNull @NotBlank String coursecode) {
        return courseAcademicsRepository.existsById(coursecode);
    }

    public List<M_Course_Academics> getcoursesbasedondepartment(String departmentcode, String isshortterm) {
        if (!isshortterm.equals("0") && !isshortterm.equals("1")) {
            isshortterm = null;
        }
        return courseAcademicsRepository.findByDepartmentCodeAndShortTerm(departmentcode, isshortterm);
    }
    
    public List<M_Course_Academics> getListOfCoursesForDept(String departmentcode) {
        return courseAcademicsRepository.findByDepartmentCode(departmentcode);
    }
    
    public List<Object[]> getCoursesBasedOnDepartment(@NotNull @NotBlank String departmentcode,
                                                      @NotNull @NotBlank String isshortterm) {
        try {
            String shortTermParam = ("0".equals(isshortterm) || "1".equals(isshortterm)) ? isshortterm : null;
            return courseAcademicsRepository.findCoursesByDepartmentAndShortTerm(departmentcode, shortTermParam);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException(String.format("Error retrieving course based on department: departmentcode: '%s', isshortterm: '%s'", departmentcode, isshortterm), ex);
        }

    }

    public List<Object[]> getCourseList(@NotNull @NotBlank String departmentCode) {
        try {
            return courseAcademicsRepository.findCoursesWithDepartmentDetails(departmentCode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving course list based on department code: " + departmentCode, ex);
        }
    }

    public List<M_Course_Academics> getCourseList2() {
        return courseAcademicsRepository.findAllOrderedByCourseCodeAndName();
    }

    public boolean checkAcademicCourseExist(@NotNull M_Course_Academics mcourse) {
        try {
            return courseAcademicsRepository.existsByCourseNameAndDepartmentCode(
                    mcourse.getCoursename().trim(),
                    mcourse.getDepartmentcode().getDepartmentcode()
            );
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error checking academic course exist", ex);
        }
    }

    public String saveOrUpdateCourse(@NotNull M_Course_Academics course) {
        try {
            if (course.getCoursecode() == null || course.getCoursecode().trim().isEmpty()) {
                Integer maxCode = courseAcademicsRepository.getMaxCourseCode();
                int newCode = (maxCode == null) ? 1 : maxCode + 1;
                course.setCoursecode(String.valueOf(newCode));
            }

            courseAcademicsRepository.save(course);

            return "2"; // Success
        } catch (Exception ex) {
            return "1"; // Failure
        }
    }
    public M_Course_Academics getCourseByCode(@NotNull @NotBlank String coursecode) {
        try {
            return courseAcademicsRepository.getCourseByCode(coursecode)
                    .orElseThrow(() -> new EntityNotFoundException("Course not found with code: " + coursecode));
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieveing course by coursecode: " + coursecode, ex);
        }
    }

    public boolean isCourseNameTakenByOtherCourse(@NotNull @NotBlank String courseName,
                                                  @NotNull @NotBlank String departmentCode,
                                                  @NotNull @NotBlank String courseCode) {
        try {
            return courseAcademicsRepository.isCourseNameTakenByOtherCourse(courseName, departmentCode, courseCode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException(
                    String.format("Error checking if course name is taken by other course: courseName: %s, departmentCode: %s, courseCode: %s. Cause: %s",
                            courseName, departmentCode, courseCode, ex.getMessage())
            );
        }
    }

    public List<Object[]> getCoursesBasedOnDepartmentFaculty(@NotNull @NotBlank String departmentcode) {
        try {
            return courseAcademicsRepository.getCoursesBasedOnDepartmentFaculty(departmentcode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error getting courses based on department faculty: departmentcode: " + departmentcode, ex);
        }
    }

    public List<M_Course_Academics> getAllCourseAcademics() {
        return courseAcademicsRepository.findAllCoursesAcademics();
    }

    public List<M_Course_Academics> findCoursesByOfficeCode(String officecode) {
        try {
            return courseAcademicsRepository.findCoursesByOfficeCode(officecode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving courses by office code: " + officecode, ex);
        }
    }
}
