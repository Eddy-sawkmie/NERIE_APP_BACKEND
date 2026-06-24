package com.nic.nerie.m_qualificationsubjects.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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

import com.nic.nerie.audittrail.service.AudittrailService;
import com.nic.nerie.exceptions.MyAuthenticationCredentialsNotFoundException;
import com.nic.nerie.exceptions.MyAuthorizationDeniedException;
import com.nic.nerie.m_processes.service.M_ProcessesService;
import com.nic.nerie.m_qualificationcategories.service.M_QualificationCategoriesService;
import com.nic.nerie.m_qualifications.service.M_QualificationsService;
import com.nic.nerie.m_qualificationsubjects.model.M_QualificationSubjects;
import com.nic.nerie.m_qualificationsubjects.service.M_QualificationSubjectsService;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.mt_userlogin.service.MT_UserloginService;
import com.nic.nerie.utils.ExceptionUtil;
import com.nic.nerie.utils.UtilCommon;

import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/qualification-subjects")
public class M_QualificationSubjectsController {
    private final MT_UserloginService mtUserloginService;
    private final M_QualificationsService mQualificationsService;
    private final M_QualificationCategoriesService mQualificationCategoriesService;
    private final M_QualificationSubjectsService qualificationSubjectsService;
    private final M_ProcessesService mProcessesService;
    private final AudittrailService audittrailService;
    private static final Logger persistenceLogger = LoggerFactory.getLogger("DATA_PERSISTENCE_LOGGER");

    @Autowired
    public M_QualificationSubjectsController(MT_UserloginService mtUserloginService,
                                             M_QualificationsService mQualificationsService,
                                             M_QualificationCategoriesService mQualificationCategoriesService,
                                             M_QualificationSubjectsService qualificationSubjectsService,
                                             M_ProcessesService mProcessesService,
                                             AudittrailService audittrailService) {
        this.mtUserloginService = mtUserloginService;
        this.mQualificationsService = mQualificationsService;
        this.mQualificationCategoriesService = mQualificationCategoriesService;
        this.qualificationSubjectsService = qualificationSubjectsService;
        this.mProcessesService = mProcessesService;
        this.audittrailService = audittrailService;
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role A (Local-admin) & U (Co-ordinator faculty)
     * 'Add/Edit Subjects' process (processcode = 14)
     */
    @GetMapping("/manage")
    public String renderQualificationSubjectList(Model model, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "Add/Edit Subjects, " + request.getMethod()), "page");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!(
            List.of("A", "U").contains(userRole) &&
            mProcessesService.isProcessGranted(user.getUsercode(), 14)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "Add/Edit Subjects, " + request.getMethod(), user.getUserid()), "page");
        }

        switch (user.getRole().getRoleCode().toUpperCase()) {
            case "A":
                model.addAttribute("layoutPath", "layouts/local-admin-layout");
                break;
            case "U":
                model.addAttribute("layoutPath", "layouts/coordinator-faculty-layout");
                break;
        }

        model.addAttribute("mqsubjectList", qualificationSubjectsService.getAllQualificationSubjects());

        return "pages/qualification-subject-list";
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role A (Local-admin) & U (Co-ordinator faculty)
     * Endpoint tied with 'Add/Edit Subjects' process (processcode = 14)
     */
    @PostMapping("/save")
    public ResponseEntity<String> saveSubjectDetails(@RequestParam(value = "qualificationsubjectcode", required = false) String qualificationsubjectcode,
                                                     @RequestParam("qualificationsubjectname") String qualificationsubjectname,
                                                     HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (RuntimeException e) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        } 
        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!(
            List.of("A", "U").contains(userRole) &&
            mProcessesService.isProcessGranted(user.getUsercode(), 14)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }

        // validating required parameters
        // validating qualificationsubjectcode (if updating...)
        if (qualificationsubjectcode != null && !qualificationsubjectcode.isBlank()) {
            if (qualificationsubjectcode.trim().length() > 3)
                return ResponseEntity.badRequest().body("Invalid qualificationsubjectcode");

            if (!qualificationSubjectsService.existsByQualificationsubjectcode(qualificationsubjectcode))
                return ResponseEntity.badRequest().body("Qualification subject does not exist. Update failed.");
        }

        // validating qualificationsubjectname
        if (qualificationsubjectname == null)
            return ResponseEntity.ok("3");
        else if (qualificationsubjectname.isBlank() || qualificationsubjectname.trim().length() > 50)
            return ResponseEntity.ok("4");

        // preparing M_QualificationSubject for persisting
        M_QualificationSubjects subject = new M_QualificationSubjects();
        subject.setQualificationsubjectcode(qualificationsubjectcode != null ? qualificationsubjectcode.trim() : "");
        subject.setQualificationsubjectname(qualificationsubjectname != null ? qualificationsubjectname.trim() : "");

        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
        try {
            if (qualificationSubjectsService.checkSubjectExist(subject)) {
                audittrailService.logAuditTrail(auditMap, user.getUserid(), "m_qualificationsubjects save failed");

                return ResponseEntity.ok("1"); // subject already exists
            }

            if ((subject = qualificationSubjectsService.saveSubjectDetails(subject)) != null) {
                persistenceLogger.info("M_QualificationSubjects with qualificationsubjectcode {} saved successfully by userid {}", subject.getQualificationsubjectcode(), user.getUserid());
                audittrailService.logAuditTrail(auditMap, user.getUserid(), "m_qualificationsubjects with qualificationsubjectcode " + subject.getQualificationsubjectcode() + " saved successfully");

                return ResponseEntity.ok("2"); // subject successfully saved
            } else  
                throw new PersistenceException();
        } catch (Exception ex) {
            persistenceLogger.error("M_QualificationSubjects save failed.\nMessage: {}\nUserid: {}", ex.getMessage(), user.getUserid(), ex);
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "m_qualificationsubjects save failed");
            
            return ResponseEntity.ok(""); // something went wrong...
        }
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role A (Local-admin) & U (Co-ordinator faculty)
     * 'Map Qualification Subject' process (processcode = 15)
     */
    @GetMapping("/map")
    public String renderMapQualificationSubjectPage(Model model, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "Map Qualification Subject, " + request.getMethod()), "page");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!(
            List.of("A", "U").contains(userRole) &&
            mProcessesService.isProcessGranted(user.getUsercode(), 15)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "Map Qualification Subject, " + request.getMethod(), user.getUserid()), "page");
        }

        switch (user.getRole().getRoleCode().toUpperCase()) {
            case "A":
                model.addAttribute("layoutPath", "layouts/local-admin-layout");
                break;
            case "U":
                model.addAttribute("layoutPath", "layouts/coordinator-faculty-layout");
                break;
        }

        M_QualificationSubjects mqsubjects = new M_QualificationSubjects();
        model.addAttribute("mqualificationList", mQualificationsService.getQualificationList());
        model.addAttribute("mqsubjects", mqsubjects);
        model.addAttribute("qualificationcategoryList", mQualificationCategoriesService.getAllQualificationCategories());

        return "pages/map-qualific-sub";
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role A (Local-admin) & U (Co-ordinator faculty)
     * Endpoint tied with 'Map Qualification Subject' process (processcode = 15)
     */
    @PostMapping("/map/save")
    public ResponseEntity<String> saveQualificationSubjectMap(@RequestParam("qualificationcode") String qualificationcode,
                                                              @RequestParam("subjects") List<String> subjects,
                                                              HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (RuntimeException e) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        } 
        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!(
            List.of("A", "U").contains(userRole) &&
            mProcessesService.isProcessGranted(user.getUsercode(), 15)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }

        // validating required parameters
        if (qualificationcode == null || qualificationcode.isBlank() || subjects == null || subjects.size() == 0)
            return ResponseEntity.badRequest().body("Required parameters cannot be null or empty");

        // skipping validation for qualificationcode and subjects list codes
        // if either doesn't exist, catch exception and return with response ""

        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
        try {
            qualificationSubjectsService.saveQualificationSubjectMap(qualificationcode, subjects);
            persistenceLogger.info("M_QualificationSubjects mapped successfully with M_Qualifications with qualificationcode {} by userid {}", qualificationcode, user.getUserid());
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "m_qualificationsubjects mapped successfully with m_qualifications with qualificationcode " + qualificationcode);

            return ResponseEntity.ok("2");
        } catch (RuntimeException ex) {
            persistenceLogger.error("M_QualificationSubjects mapping failed with M_Qualifications of qualificationcode {}.\nMessage: {}\nUserid: {}", qualificationcode, ex.getMessage(), user.getUserid(), ex);
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "m_qualificationsubjects mapping failed with m_qualifications of qualificationcode " + qualificationcode);
        }

        return ResponseEntity.ok("");   // something went wrong...
    }

    /*
     * Public endpoint
     */
    @GetMapping("/get-subjects")
    public ResponseEntity<List<Object[]>> getSubjectsForQualification(@RequestParam("qualificationcode") String qualificationcode) {
        if (qualificationcode == null || qualificationcode.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
        }
        try {
            List<Object[]> subjects = qualificationSubjectsService.getQualificationSubject(qualificationcode);
            return ResponseEntity.ok(subjects);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
        }
    }

    /*
     * Public endpoint
     */
    @GetMapping("/get-mp-subjects")
    public ResponseEntity<List<Object[]>> getMPSubjectsForQualification(
            @RequestParam("qualificationcode") String qualificationcode) {
        if (qualificationcode == null || qualificationcode.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
        }
        try {
            List<Object[]> subjects = qualificationSubjectsService.getMappedSubjects(qualificationcode);
            return ResponseEntity.ok(subjects);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());
        }
    }
}
