package com.nic.nerie.m_offices.controller;

import com.nic.nerie.audittrail.service.AudittrailService;
import com.nic.nerie.exceptions.MyAuthenticationCredentialsNotFoundException;
import com.nic.nerie.exceptions.MyAuthorizationDeniedException;
import com.nic.nerie.m_offices.model.M_Offices;
import com.nic.nerie.m_offices.service.M_OfficesService;
import com.nic.nerie.m_states.model.M_States;
import com.nic.nerie.m_states.service.M_StatesService;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.mt_userlogin.service.MT_UserloginService;
import com.nic.nerie.utils.ExceptionUtil;
import com.nic.nerie.utils.UtilCommon;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/offices")
public class M_OfficesController {
    private final M_OfficesService mOfficesService;
    private final M_StatesService mStatesService;
    private final MT_UserloginService mtUserloginService;
    private final AudittrailService audittrailService;

    @Autowired
    public M_OfficesController(M_OfficesService mOfficesService, M_StatesService mStatesService, MT_UserloginService mtUserloginService, AudittrailService audittrailService) {
        this.mOfficesService = mOfficesService;
        this.mStatesService = mStatesService;
        this.mtUserloginService = mtUserloginService;
        this.audittrailService = audittrailService;
    }

    /*
     * Secured endpoint
     * Exclusive to role S (Admin)
     * Management > Create Office
     */
    // Admin
    @GetMapping("/createoffice")
    public String showCreateOfficePage(@ModelAttribute("moffices") M_Offices moffices, Model model, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "Create Office, " + request.getMethod()), "page");
        }

        if (!user.getRole().getRoleCode().equalsIgnoreCase("S")) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "Create Office, " + request.getMethod(), user.getUserid()), "page");
        }

        List<M_Offices> officeList = mOfficesService.getOfficeList();
        List<M_States> stateList = mStatesService.getAllStates();

        model.addAttribute("officelist", officeList);
        model.addAttribute("statelist", stateList);

        return "pages/admin/create-office";
    }

    /*
     * Secured endpoint
     * Exclusive to role S (Admin)
     * Management > Create Office
     */
    // Admin
    @PostMapping("/saveOfficeDetails")
    @ResponseBody
    public ResponseEntity<String> saveOfficeDetails(
            @ModelAttribute("moffices") M_Offices moffices,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest request) {

        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
        String responseBody = "";

        if (userDetails == null) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        }

        String username = userDetails.getUsername();
        MT_Userlogin currentUser = mtUserloginService.findByUserId(username);
        
        if (currentUser == null) {
            throw new DataAccessResourceFailureException("Error retrieving MT_Userlogin by username " + username);
        } else if (!currentUser.getRole().getRoleCode().equalsIgnoreCase("S")) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), currentUser.getUserid()), "json");
        }

        try {
            M_Offices savedOffice = mOfficesService.saveOffice(moffices);
            if (savedOffice == null) {
                logAuditTrail(auditMap, currentUser.getUserid(), "Save Office Failed (Null)");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
            }
            responseBody = "2";
            logAuditTrail(auditMap, currentUser.getUserid(), "Save Office Success");
            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            logAuditTrail(auditMap, currentUser.getUserid(), "Save Office Failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }
    }

    @GetMapping("/check-coursecodes-exists")
    @ResponseBody
    public boolean checkCourseCodeExists(
        @RequestParam String code, 
        @RequestParam String type,
        @RequestParam(required = false) String excludeOfficeCode) {
        
        return mOfficesService.existsByCode(code, type, excludeOfficeCode);
    }
    
    private void logAuditTrail(HashMap<String, String> auditMap, String userId, String actionTaken) {
        if (auditMap != null) {
            auditMap.put("userid", userId);
            auditMap.put("actiontaken", actionTaken);
            audittrailService.saveAuditTrail(auditMap);
        }
    }
    //flutter api
    @GetMapping("/createoffice-metadata")
    @ResponseBody
    public Map<String, List<?>> getCreateOfficeMetadata(HttpServletRequest request) {
        // Auth & role check exactly as in showCreateOfficePage()
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(),
                            "Create Office Metadata, " + request.getMethod()), "json");
        }

        if (!user.getRole().getRoleCode().equalsIgnoreCase("S")) {
            throw new MyAuthorizationDeniedException(
                    ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(),
                            "Create Office Metadata, " + request.getMethod(), user.getUserid()), "json");
        }

        Map<String, List<?>> map = new HashMap<>();
        map.put("officelist", mOfficesService.getOfficeList());
        map.put("statelist", mStatesService.getAllStates());
        return map;
    }


}
