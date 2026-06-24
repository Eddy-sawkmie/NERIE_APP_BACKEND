package com.nic.nerie.m_designations.controller;

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
import com.nic.nerie.m_designations.model.M_Designations;
import com.nic.nerie.m_designations.service.M_DesignationsService;
import com.nic.nerie.m_processes.service.M_ProcessesService;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.mt_userlogin.service.MT_UserloginService;
import com.nic.nerie.utils.ExceptionUtil;
import com.nic.nerie.utils.UtilCommon;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/designations")
public class M_DesignationsController {
    private final MT_UserloginService mtUserloginService;
    private final M_DesignationsService mDesignationsService;
    private final M_ProcessesService mProcessesService;
    private final AudittrailService audittrailService;
    private static final Logger persistenceLogger = LoggerFactory.getLogger("DATA_PERSISTENCE_LOGGER");

    @Autowired
    public M_DesignationsController(
        MT_UserloginService mtUserloginService, 
        M_DesignationsService mDesignationsService,
        M_ProcessesService mProcessesService,
        AudittrailService audittrailService) {
        this.mtUserloginService = mtUserloginService;
        this.mDesignationsService = mDesignationsService;
        this.mProcessesService = mProcessesService;
        this.audittrailService = audittrailService;
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role S (Admin), A (Local-admin)
     * 'Add/Edit Designation' process (processcode = 5)
     */
    @GetMapping("/manage")
    public String renderDesignationListPage(Model model, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "Add/Edit Designation, " + request.getMethod()), "page");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!(
            List.of("A", "S").contains(userRole) &&
            userRole.equalsIgnoreCase("A") ? mProcessesService.isProcessGranted(user.getUsercode(), 5) : true   // S (Admin) is granted logically
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "Add/Edit Designation, " + request.getMethod(), user.getUserid()), "page");
        }

        switch (user.getRole().getRoleCode().toUpperCase()) {
            case "A":
                model.addAttribute("layoutPath", "layouts/local-admin-layout");
                break;
            case "S":
                model.addAttribute("layoutPath", "layouts/admin-layout");
                break;
        }

        model.addAttribute("mdesignationList", mDesignationsService.getAllDesignationList());

        return "pages/designation-list";
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role S (Admin) & A (Local-admin)
     * Endpoint tied with 'Add/Edit Designation' process (processcode = 5)
     */
    @PostMapping("/save")
    public ResponseEntity<String> saveDesignationDetails(@RequestParam(value = "designationcode", required = false) String designationcode,
                                                         @RequestParam("designationname") String designationname,
                                                         @RequestParam("isparticipantdesignation") String isparticipantdesignation,
                                                         HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();
        
        if (!(
            List.of("A", "S").contains(userRole) &&
            userRole.equalsIgnoreCase("A") ? mProcessesService.isProcessGranted(user.getUsercode(), 5) : true   // S (Admin) is granted logically
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }

        // validating Required parameters
        // validating designationcode (if updating...)
        if (designationcode != null && !designationcode.isBlank()) {
            if (designationcode.trim().length() > 3)
                return ResponseEntity.badRequest().body("Invalid designationcode");

            if (!mDesignationsService.existsByDesignationcode(designationcode))
                return ResponseEntity.badRequest().body("Designation does not exist. Update failed.");
        }

        // validating designationname
        if (designationname == null)
            return ResponseEntity.ok("3");
        else if (designationname.isBlank() || designationname.length() > 100)
            return ResponseEntity.ok("4");

        // validating isparticipantdesignation
        if (isparticipantdesignation == null || isparticipantdesignation.isBlank())
            return ResponseEntity.badRequest().body("Designation type cannot be null or empty");
        else if (!isparticipantdesignation.trim().toUpperCase().equals("Y") && !isparticipantdesignation.trim().toUpperCase().equals("N"))
            return ResponseEntity.badRequest().body("Invalid Designation type");

        // preparing M_Designations for persisting 
        M_Designations designations = new M_Designations();
        designations.setDesignationcode(designationcode != null ? designationcode.trim() : "");
        designations.setDesignationname(designationname.trim());
        designations.setIsparticipantdesignation(isparticipantdesignation.trim());

        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
        try {
            if (mDesignationsService.checkDesignationExist(designations)) {
                audittrailService.logAuditTrail(auditMap, user.getUserid(), "m_designations save failed");
                
                return ResponseEntity.ok("1");  // designation already exists
            }
            
            if ((designations = mDesignationsService.saveDesignationDetails(designations)) != null) {
                persistenceLogger.info("M_Designations with designationcode {} saved successfully by userid {}", designations.getDesignationcode(), user.getUserid());
                audittrailService.logAuditTrail(auditMap, user.getUserid(), "m_designations with designationcode " + designations.getDesignationcode() + " saved successfully");

                return ResponseEntity.ok("2");  // designation successfully saved
            }
        } catch (Exception ex) {
            persistenceLogger.error("M_Designations save failed.\nMessage: {}\nUserid: {}", ex.getMessage(), user.getUserid(), ex);
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "m_designations save failed");
        }

        return ResponseEntity.ok(""); // something went wrong
    }


    /*
     * API Endpoint for Flutter/Mobile Clients
     * Returns a JSON list of all designations.
     */
    @GetMapping("/list-api")
    @ResponseBody
    public List<M_Designations> getDesignationListApi(HttpServletRequest request) {
        return mDesignationsService.getAllDesignationList();
    }

}
