package com.nic.nerie.m_coursecategories.controller;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.nic.nerie.audittrail.service.AudittrailService;
import com.nic.nerie.exceptions.MyAuthenticationCredentialsNotFoundException;
import com.nic.nerie.exceptions.MyAuthorizationDeniedException;
import com.nic.nerie.m_course_academics.model.M_Course_Academics;
import com.nic.nerie.m_coursecategories.model.M_CourseCategories;
import com.nic.nerie.m_coursecategories.service.M_CourseCategoriesService;
import com.nic.nerie.m_departments.model.M_Departments;
import com.nic.nerie.m_departments.service.M_DepartmentsService;
import com.nic.nerie.m_processes.service.M_ProcessesService;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.mt_userlogin.service.MT_UserloginService;
import com.nic.nerie.utils.ExceptionUtil;
import com.nic.nerie.utils.UtilCommon;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/course-categories")
public class M_CourseCategoriesController {
    private final M_CourseCategoriesService mCourseCategoriesService;
    private final MT_UserloginService mtUserloginService;
    private final M_DepartmentsService mDepartmentsService;
    private final M_ProcessesService mProcessesService;
    private final AudittrailService audittrailService;

    private static final Logger persistenceLogger = LoggerFactory.getLogger("DATA_PERSISTENCE_LOGGER");

    @Autowired
    public M_CourseCategoriesController(
        M_CourseCategoriesService mCourseCategoriesService,
        MT_UserloginService mtUserloginService,
        M_DepartmentsService mDepartmentsService,
        M_ProcessesService mProcessesService,
        AudittrailService audittrailService) {
        this.mCourseCategoriesService = mCourseCategoriesService;
        this.mtUserloginService = mtUserloginService;
        this.mDepartmentsService = mDepartmentsService;
        this.mProcessesService = mProcessesService;
        this.audittrailService = audittrailService;
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role A (Local-admin) & U (Co-ordinator faculty)
     * 'Manage Courses' process (processcode = 29)
     */
    @GetMapping("/create-academic-courses")
    public String renderCreateAcademicCoursesPage(@ModelAttribute("macademiccourse") M_Course_Academics mcourseacademic,
                                                  Model model) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (RuntimeException ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage("Manage Courses [/course-categories/create-academic-courses]"), "page");
        } 
        String userRole = user.getRole().getRoleCode().toUpperCase();
        String officeCode = user.getMoffices().getOfficecode();

        if (!(
            List.of("A", "U").contains(userRole) &&
            mProcessesService.isProcessGranted(user.getUsercode(), 29)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage("Manage Courses [/course-categories/create-academic-courses]", user.getUserid()), "page");
        }

        model.addAttribute("mdepartmentList", mDepartmentsService.getDepartmentList(officeCode));

        if("A".equals(user.getUserrole())) {
            model.addAttribute("layoutPath", "layouts/local-admin-layout");
        } else if ("U".equals(user.getUserrole())) {
            model.addAttribute("layoutPath", "layouts/coordinator-faculty-layout");
        }
        
        return "pages/create-academic-courses";
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role A (Local-admin) & U (Co-ordinator faculty)
     * Program Categories
     */
    @GetMapping("/manage")
    public String renderCourseCategoriesListPage(Model model, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "Program Categories, " + request.getMethod()), "page");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!(
            List.of("A", "U").contains(userRole) &&
            mProcessesService.isProcessGranted(user.getUsercode(), 4)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "Program Categories, " + request.getMethod(), user.getUserid()), "page");
        }

        switch (user.getRole().getRoleCode().toUpperCase()) {
            case "A":
                model.addAttribute("layoutPath", "layouts/local-admin-layout");
                break;
            case "U":
                model.addAttribute("layoutPath", "layouts/coordinator-faculty-layout");
                break;
        }

        model.addAttribute("mcoursecategoryList", mCourseCategoriesService.getAllCourseCategories());

        return "pages/program-categories";
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role A (Local-admin) & U (Co-ordinator faculty)
     * Endpoint tied to Program Categories process
     */
    @PostMapping("/save")
    public ResponseEntity<String> saveCourseCategoriesDetails(@RequestParam("coursetype") String coursetype,
                                              @RequestParam(value = "coursecategorycode", required = false) String coursecategorycode,
                                              @RequestParam("coursecategoryname") String coursecategoryname,
                                              HttpServletRequest request) {
        // TODO @Abanggi: Add process-code based authorization
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (RuntimeException ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!(
            List.of("A", "U").contains(userRole) &&
            mProcessesService.isProcessGranted(user.getUsercode(), 4)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }

        // validating Required parameters
        // validating coursecategorycode (if updating...)
        if (coursecategorycode != null && !coursecategorycode.isBlank()) {
            if (coursecategorycode.trim().length() > 3)
                return ResponseEntity.badRequest().body("Invalid coursecategorycode");

            if (!mCourseCategoriesService.existsByCoursecategorycode(coursecategorycode))
                return ResponseEntity.badRequest().body("Program category does not exist. Update failed.");
        }

        // validating coursecategoryname
        if (coursecategoryname == null)
            return ResponseEntity.ok("3");
        else if (coursecategoryname.isBlank() || coursecategoryname.length() > 50)
            return ResponseEntity.ok("4");

        // validating coursetype
        if (coursetype == null || coursetype.isBlank())
            return ResponseEntity.badRequest().body("Program Type cannot be null or empty");
        else if (!coursetype.trim().toUpperCase().equals("S") && !coursetype.trim().toUpperCase().equals("L"))
            return ResponseEntity.badRequest().body("Invalid Progam Type");

        M_CourseCategories courseCategories = new M_CourseCategories();
        courseCategories.setCoursecategorycode(coursecategorycode != null ? coursecategorycode.trim() : "");    // optional field, is generated new for create
        courseCategories.setCoursecategoryname(coursecategoryname.trim());
        courseCategories.setCoursetype(coursetype.trim());

        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
        try {
            if (mCourseCategoriesService.checkCourseCategoryExist(courseCategories)) {
                audittrailService.logAuditTrail(auditMap, user.getUserid(), "m_coursecategories save failed");

                return ResponseEntity.ok("1");
            }

            if ((courseCategories = mCourseCategoriesService.saveCourseCategoriesDetails(courseCategories)) != null) {
                persistenceLogger.info("M_CourseCategories with coursecategorycode {} saved successfully by userid {}", courseCategories.getCoursecategorycode(), user.getUserid());
                audittrailService.logAuditTrail(auditMap, user.getUserid(), "m_coursecategories with coursecategorycode " + courseCategories.getCoursecategorycode() + " saved successfully");

                return ResponseEntity.ok("2");            
            }
        } catch (Exception ex) {
            persistenceLogger.error("M_CourseCategories save failed.\nMessage: {}\nUserid: {}", ex.getMessage(), user.getUserid(), ex);
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "m_coursecategories save failed");
        }

        return ResponseEntity.ok("");
    }
    /*
     * API endpoint — Flutter department dropdown
     */
    @GetMapping("/departments/list")
    public ResponseEntity<List<M_Departments>> getDepartmentsJson() {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
            String officeCode = user.getMoffices().getOfficecode();
            List<M_Departments> departments =
                    mDepartmentsService.getDepartmentList(officeCode);
            return ResponseEntity.ok(departments);
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }

    /*
     * API endpoint — Flutter course list
     */
    @GetMapping("/getAllJson")
    @ResponseBody
    public List<M_CourseCategories> getAllCourseCategoriesJson(HttpServletRequest request) {
        MT_Userlogin user = mtUserloginService.getUserloginFromAuthentication();

        System.out.println("Role: "      + user.getRole().getRoleCode());
        System.out.println("Usercode: "  + user.getUsercode());
        System.out.println("Process 4: " + mProcessesService.isProcessGranted(user.getUsercode(), 4));

        return mCourseCategoriesService.getAllCourseCategories();
    }

}
