package com.nic.nerie.m_course_academics.controller;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nic.nerie.audittrail.service.AudittrailService;
import com.nic.nerie.exceptions.MyAuthenticationCredentialsNotFoundException;
import com.nic.nerie.exceptions.MyAuthorizationDeniedException;
import com.nic.nerie.m_course_academics.model.M_Course_Academics;
import com.nic.nerie.m_course_academics.service.M_Course_AcademicsService;
import com.nic.nerie.m_departments.model.M_Departments;
import com.nic.nerie.m_departments.service.M_DepartmentsService;
import com.nic.nerie.m_processes.service.M_ProcessesService;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.mt_userlogin.service.MT_UserloginService;
import com.nic.nerie.utils.ExceptionUtil;
import com.nic.nerie.utils.UtilCommon;

import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/course-academics")
public class M_Course_AcademicsController {
    private final M_Course_AcademicsService mCourseAcademicsService;
    private final MT_UserloginService mtUserloginService;
    private final M_DepartmentsService mDepartmentsService;
    private final M_ProcessesService mProcessesService;
    private final AudittrailService audittrailService;
    private static final Logger persistenceLogger = LoggerFactory.getLogger("DATA_PERSISTENCE_LOGGER");
    
    @Autowired
    public M_Course_AcademicsController(
        M_Course_AcademicsService mCourseAcademicsService, 
        MT_UserloginService mtUserloginService, 
        M_DepartmentsService mDepartmentsService,
        M_ProcessesService mProcessesService,
        AudittrailService audittrailService) {
        this.mCourseAcademicsService = mCourseAcademicsService;
        this.mtUserloginService = mtUserloginService;
        this.mDepartmentsService = mDepartmentsService;
        this.mProcessesService = mProcessesService;
        this.audittrailService = audittrailService;
    }

    @PostMapping("/coursesbasedondepartment")
    public ResponseEntity<List<M_Course_Academics>> getCoursesBasedOnDepartment(@RequestParam("departmentcode") String departmentcode, @RequestParam("isshortterm") String isshortterm) {
        if (departmentcode == null || departmentcode.isBlank() || isshortterm == null || isshortterm.isBlank()) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
        
        return ResponseEntity.ok(mCourseAcademicsService.getcoursesbasedondepartment(departmentcode, isshortterm));
    }

    @PostMapping("/list-by-departmentcode")
    public ResponseEntity<List<Object[]>> getCoursesBasedOnDepartmentCode(@RequestParam("departmentcode") String departmentcode) {
        MT_Userlogin user = null;

        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        } finally {
            if (user == null)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            else if (!(user.getRole().getRoleCode().toUpperCase().equals("A") ||
                       user.getRole().getRoleCode().toUpperCase().equals("U")))
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        try {
            return ResponseEntity.ok(mCourseAcademicsService.getCourseAcademicsByDepartmentcode(departmentcode));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.internalServerError().body(Collections.emptyList());
        }
    }

    @PostMapping("/generateacademicyearbyduration")
    @SuppressWarnings("unchecked")
    public ResponseEntity<JSONArray> generateAcademicYearByDuration(@RequestParam("duration") String duration) {
        JSONArray res = new JSONArray();
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR) + 2;

        for (int i = 0; i < 10; i++) {
            String ay = ((year - i) - Integer.valueOf(duration)) + "-" + (year - i);
            res.add(ay);
        }

        return ResponseEntity.ok(res);
    }

    @PostMapping("/getListOfCourses")
    @ResponseBody
    public List<Object[]> getlistofcourses(@RequestParam(value = "departmentcode", required = false) String dcode) {
        return mCourseAcademicsService.getCourseList(dcode);
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role A (Local-admin) & U (Co-ordinator faculty)
     * Endpoint tied with 'Manage Courses' process (processcode = 29)
    */
    @PostMapping("/saveMapDepartmentCourse")
    @ResponseBody
    public String saveMapDepartmentCourse(
            @RequestParam("coursecode") String coursecode,
            @RequestParam("coursename") String coursename,
            @RequestParam("departmentcode") String departmentcode,
            @RequestParam("isshortterm") String isshortterm,
            @RequestParam("courseid") String courseid,
            @RequestParam("courseduration") String courseduration,
            Model model, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (RuntimeException ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage("/course-academics/saveMapDepartmentCourse"), "json");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!(
            List.of("A", "U").contains(userRole) &&
            mProcessesService.isProcessGranted(user.getUsercode(), 29)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage("/course-academics/saveMapDepartmentCourse", user.getUserid()), "json");
        }

        M_Course_Academics mcourse = new M_Course_Academics();
        mcourse.setCourseid(courseid);
        mcourse.setCoursecode(coursecode);
        mcourse.setCoursename(coursename);
        mcourse.setIsshortterm("1".equals(isshortterm) ? "1" : "0");
        mcourse.setDuration(courseduration);

        M_Departments department = mDepartmentsService.getDepartmentByCode(departmentcode);
        if (department == null) {
            return "4";
        }
        mcourse.setDepartmentcode(department);

        // Check if course already exists
        if (mCourseAcademicsService.checkAcademicCourseExist(mcourse)) {
            return "3"; // Course already exists
        }

        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
        try {
            // Save or update course
            String response = mCourseAcademicsService.saveOrUpdateCourse(mcourse);

            if (response.equals("2")) {
                persistenceLogger.info("M_Course_Academics saved successfully by userid {}", user.getUserid());
                audittrailService.logAuditTrail(auditMap, user.getUserid(), "m_course_academics saved successfully");

                return response;    // successfully saved
            } else 
                throw new PersistenceException();
        } catch (Exception ex) {
            persistenceLogger.error("M_Course_Academics save failed.\nMessage: {}\nUserid: {}", ex.getMessage(), user.getUserid(), ex);
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "m_course_academics save failed");

            return "1"; // something went wrong        
        }
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role A (Local-admin) & U (Co-ordinator faculty)
     * Endpoint tied with 'Manage Courses' process (processcode = 29)
    */
    @PostMapping("/updateDepartmentCourse")
    @ResponseBody
    public String updateDepartmentCourse(
            @RequestParam("coursecode") String coursecode,
            @RequestParam("coursename") String coursename,
            @RequestParam("departmentcode") String departmentcode,
            @RequestParam("isshortterm") String isshortterm,
            @RequestParam("courseid") String courseid,
            @RequestParam("courseduration") String courseduration,
            HttpServletRequest request) {
        
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (RuntimeException ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage("/course-academics/updateDepartmentCourse"), "json");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!(
            List.of("A", "U").contains(userRole) &&
            mProcessesService.isProcessGranted(user.getUsercode(), 29)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage("/course-academics/updateDepartmentCourse", user.getUserid()), "json");
        }

        // Get existing course
        M_Course_Academics existingCourse = mCourseAcademicsService.getCourseByCode(coursecode);
        if (existingCourse == null) {
            return "5"; // Course not found
        }

        // Verify department exists
        M_Departments department = mDepartmentsService.getDepartmentByCode(departmentcode);
        if (department == null) {
            return "4"; // Department not found
        }

        // Check if the new course name conflicts with other courses (excluding current course)
        if (mCourseAcademicsService.isCourseNameTakenByOtherCourse(coursename, departmentcode, coursecode)) {
            return "3"; // Course name already used by another course
        }

        // Update course details
        existingCourse.setCourseid(courseid);
        existingCourse.setCoursename(coursename);
        existingCourse.setDepartmentcode(department);
        existingCourse.setIsshortterm("1".equals(isshortterm) ? "1" : "0");
        existingCourse.setDuration(courseduration);

        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
        try {
            // Perform update
            String response = mCourseAcademicsService.saveOrUpdateCourse(existingCourse);

            if (response.equals("2")) {
                persistenceLogger.info("M_Course_Academics updated successfully by userid {}", user.getUserid());
                audittrailService.logAuditTrail(auditMap, user.getUserid(), "m_course_academics updated successfully");

                return response;
            } else
                throw new PersistenceException();
        } catch (Exception ex) {
            persistenceLogger.error("M_Course_Academics update failed.\nMessage: {}\nUserid: {}", ex.getMessage(), user.getUserid(), ex);
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "m_course_academics update failed");

            return "6"; // General update error
        }
    }

    @PostMapping("/getCoursesBasedOnDepartment")
    @ResponseBody
    public List<Object[]> getCoursesBasedOnDepartment(@RequestParam("departmentcode") String departmentcode,
                                                      @RequestParam("isshortterm") String isshortterm,
                                                      Model model) {
        return mCourseAcademicsService.getCoursesBasedOnDepartment(departmentcode, isshortterm);
    }

    @PostMapping("/getCoursesBasedOnDepartment2")
    @ResponseBody
    public List<Object[]> getCoursesBasedOnDepartment2(@RequestParam("departmentcode") String departmentcode) {
        return mCourseAcademicsService.getCoursesBasedOnDepartmentFaculty(departmentcode);
    }

    @GetMapping("/getCoursesBasedOnDepartmentFaculty")
    @ResponseBody
    public List<Object[]> getCoursesBasedOnDepartmentFaculty(@RequestParam("departmentcode") String departmentcode) {
        return mCourseAcademicsService.getCoursesBasedOnDepartmentFaculty(departmentcode);
    }
}
