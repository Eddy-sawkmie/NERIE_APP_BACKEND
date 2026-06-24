package com.nic.nerie.m_qualifications.controller;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.nic.nerie.m_qualificationcategories.model.M_QualificationCategories;
import com.nic.nerie.m_qualificationcategories.service.M_QualificationCategoriesService;
import com.nic.nerie.m_qualifications.model.M_Qualifications;
import com.nic.nerie.m_qualifications.service.M_QualificationsService;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.mt_userlogin.service.MT_UserloginService;
import com.nic.nerie.utils.ExceptionUtil;
import com.nic.nerie.utils.UtilCommon;

import io.jsonwebtoken.lang.Collections;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/qualifications")
public class M_QualificationsController {
    private final MT_UserloginService mtUserloginService;
    private final M_QualificationCategoriesService mQualificationCategoriesService;
    private final M_QualificationsService mQualificationsService;
    private final M_ProcessesService mProcessesService;
    private final AudittrailService audittrailService;
    private static final Logger persistenceLogger = LoggerFactory.getLogger("DATA_PERSISTENCE_LOGGER");

    @Autowired
    public M_QualificationsController(MT_UserloginService mtUserloginService,
                                      M_QualificationCategoriesService mQualificationCategoriesService,
                                      M_QualificationsService mQualificationsService,
                                      M_ProcessesService mProcessesService,
                                      AudittrailService audittrailService) {
        this.mtUserloginService = mtUserloginService;
        this.mQualificationCategoriesService = mQualificationCategoriesService;
        this.mQualificationsService = mQualificationsService;
        this.mProcessesService = mProcessesService;
        this.audittrailService = audittrailService;
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role S (Admin), A (Local-admin) & U (Co-ordinator faculty)
     * 'Add/Edit Qualifications' process (processcode = 13)
     */
    @GetMapping("/manage")
    public String renderQualificationsListPage(Model model, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "Add/Edit Qualifications, " + request.getMethod()), "page");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!(
            List.of("A", "S", "U").contains(userRole) &&
            mProcessesService.isProcessGranted(user.getUsercode(), 13)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "Add/Edit Qualifications, " + request.getMethod(), user.getUserid()), "page");
        }

        switch (user.getRole().getRoleCode().toUpperCase()) {
            case "S":
                model.addAttribute("layoutPath", "layouts/admin-layout");
                break;
            case "A":
                model.addAttribute("layoutPath", "layouts/local-admin-layout");
                break;
            case "U":
                model.addAttribute("layoutPath", "layouts/coordinator-faculty-layout");
                break;
        }

        model.addAttribute("qualificationcategoryList", mQualificationCategoriesService.getAllQualificationCategories());
        model.addAttribute("mqualificationList", mQualificationsService.getQualificationList());

        return "pages/qualification-list";
    }

    /*
     * Public endpoint
     */
    // TODO @Abanggi: Find out if this endpoint is role exclusive
    @PostMapping("/list")
    public ResponseEntity<List<M_Qualifications>> getQualificationsByQualificationcategorycode(@RequestParam("qualificationcategorycode") String qualificationcategorycode) {
        if (qualificationcategorycode == null || qualificationcategorycode.isBlank())
            return ResponseEntity.badRequest().body(Collections.emptyList());

        return ResponseEntity.ok(mQualificationsService.getQualificationByQualificationcategorycode(qualificationcategorycode));
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role S (Admin), A (Local-admin) & U (Co-ordinator faculty)
     */
    @PostMapping("/save")
    public ResponseEntity<String> saveQualificationDetails(@RequestParam(value = "qualificationcode", required = false) String qualificationcode,
                                                           @RequestParam("qualificationcategorycode") String qualificationcategorycode,
                                                           @RequestParam("qualificationname") String qualificationname,
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
            List.of("A", "S", "U").contains(userRole) &&
            mProcessesService.isProcessGranted(user.getUsercode(), 13)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }

        // validating required parameters
        // validating qualificationcode (if updating...)
        if (qualificationcode != null && !qualificationcode.isBlank()) {
            if (qualificationcode.trim().length() > 3)
                return ResponseEntity.badRequest().body("Invalid qualificationcode");

            if (!mQualificationsService.existsByQualificationcode(qualificationcode))       
                return ResponseEntity.badRequest().body("Qualification does not exist. Update failed.");
        }

        // validating qualificationcategorycode
        if (qualificationcategorycode == null || qualificationcategorycode.isBlank())
            return ResponseEntity.badRequest().body("Required parameters missing");

        // validating qualificationname
        if (qualificationname == null)
            return ResponseEntity.ok("3");
        else if (qualificationname.isBlank() || qualificationname.trim().length() > 100)
            return ResponseEntity.ok("4");

        // validating qualificationcode
        M_QualificationCategories qualificationCategories = mQualificationCategoriesService.findByQualificationcategorycode(qualificationcategorycode);
        if (qualificationCategories == null)
            return ResponseEntity.badRequest().body("Invalid qualificationcode");

        // preparing M_Qualifications for persisting
        M_Qualifications qualification = new M_Qualifications();
        qualification.setQualificationcode(qualificationcode != null ? qualificationcode.trim() : "");
        qualification.setMqualificationcategories(qualificationCategories);
        qualification.setQualificationname(qualificationname != null ? qualificationname.trim() : "");

        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
        try {
            if (mQualificationsService.checkQualificationExist(qualification)) {
                audittrailService.logAuditTrail(auditMap, user.getUserid(), "m_qualifications save failed");
                
                return ResponseEntity.ok("1");  // qualification already exists
            }

            if ((qualification = mQualificationsService.saveQualificationDetails(qualification)) != null) {
                persistenceLogger.info("M_Qualifications with qualificationcode {} saved successfully by userid {}", qualification.getQualificationcode(), user.getUserid());
                audittrailService.logAuditTrail(auditMap, user.getUserid(), "m_qualifications with qualificationcode " + qualification.getQualificationcode() + " saved successfully");

                return ResponseEntity.ok("2");  // successfully saved or updated
            } else 
                throw new PersistenceException();
        } catch (Exception ex) {
            persistenceLogger.error("M_Qualifications save failed.\nMessage: {}\nUserid: {}", ex.getMessage(), user.getUserid(), ex);
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "m_qualifications save failed");
            
            return ResponseEntity.ok(""); // something went wrong...
        }
    }
}
