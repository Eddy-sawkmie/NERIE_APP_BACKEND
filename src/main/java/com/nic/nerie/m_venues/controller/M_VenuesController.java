package com.nic.nerie.m_venues.controller;

import com.nic.nerie.audittrail.service.AudittrailService;
import com.nic.nerie.exceptions.MyAuthenticationCredentialsNotFoundException;
import com.nic.nerie.exceptions.MyAuthorizationDeniedException;
import com.nic.nerie.m_offices.model.M_Offices;
import com.nic.nerie.m_processes.service.M_ProcessesService;
import com.nic.nerie.m_venues.model.M_Venues;
import com.nic.nerie.m_venues.service.M_VenuesService;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.mt_userlogin.service.MT_UserloginService;
import com.nic.nerie.utils.ExceptionUtil;
import com.nic.nerie.utils.UtilCommon;

import jakarta.servlet.http.HttpServletRequest;

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

@Controller
@RequestMapping("/venues")
public class M_VenuesController {
    private final M_VenuesService mVenuesService;
    private final MT_UserloginService mtUserloginService;
    private final M_ProcessesService mProcessesService;
    private final AudittrailService audittrailService;
    private static final Logger persistenceLogger = LoggerFactory.getLogger("DATA_PERSISTENCE_LOGGER");

    @Autowired
    public M_VenuesController(
        M_VenuesService mVenuesService, 
        MT_UserloginService mtUserloginService,
        M_ProcessesService mProcessesService,
        AudittrailService audittrailService) {
        this.mVenuesService = mVenuesService;
        this.mtUserloginService = mtUserloginService;
        this.mProcessesService = mProcessesService;
        this.audittrailService = audittrailService;
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role S (admin), A (Local-admin) & U (Co-ordinator faculty)
     * Add/Edit Venues
     */
    @GetMapping("/manage")
    public String renderVenueListPage(Model model, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "Add/Edit Venues, " + request.getMethod()), "page");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!(
            List.of("A", "S", "U").contains(userRole) &&
            mProcessesService.isProcessGranted(user.getUsercode(), 3)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "Add/Edit Venues, " + request.getMethod(), user.getUserid()), "page");
        }
        
        switch (userRole) {
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

        model.addAttribute("mvenueList", mVenuesService.getAllOfficeVenues(user.getMoffices()
                .getOfficecode()));

        return "pages/venue-list";
    }

    /*
     * Public endpoint
     */
    @PostMapping("/list")
    public ResponseEntity<List<Object[]>> getVenueListByPhaseid(@RequestParam("phaseid") String phaseid) {
        return ResponseEntity.ok(mVenuesService.getByPhaseid(phaseid));                
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role S (admin), A (Local-admin) & U (Co-ordinator faculty)
     * Endpoint tied to Add/Edit Venues process
     */
    @PostMapping("/save")
    public ResponseEntity<String> saveVenueDetails(@RequestParam(value = "venuecode", required = false) String venuecode,
                                                   @RequestParam("venuename") String venuename,
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
            List.of("A", "S", "U").contains(userRole) &&
            mProcessesService.isProcessGranted(user.getUsercode(), 3)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }

        // validating Required parameters
        // validating venuecode (if updating...)
        if (venuecode != null && !venuecode.isBlank()) {
            if (venuecode.trim().length() > 6)
                return ResponseEntity.badRequest().body("Invalid venuecode");
            
            if (!mVenuesService.existsByVenuecode(venuecode))
                return ResponseEntity.badRequest().body("Venue does not exist");
        }

        // validating venuename 
        if (venuename == null)
            return ResponseEntity.ok("3");
        else if (venuename.isBlank() || venuename.trim().length() > 100)
            return ResponseEntity.ok("4");

        // instantiating venue's office
        // when a venue is created or updated it's officecode gets overwritten
        // by the current user's office
        M_Offices offices = new M_Offices();
        offices.setOfficecode(user.getMoffices().getOfficecode());
        M_Venues venue = new M_Venues(venuecode != null ? venuecode.trim() : "", venuename.trim(), offices);
        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);

        if (mVenuesService.checkVenueExist(venue.getVenuename(), venue.getMoffices().getOfficecode(), venue.getVenuecode())) {
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "m_venues save failed");
            return ResponseEntity.ok("1");  
        }
        
        try {
            if ((venue = mVenuesService.saveVenueDetails(venue)) != null) {
                persistenceLogger.info("M_Venues with venuecode {} saved successfully by userid {}", venue.getVenuecode(), user.getUserid());
                audittrailService.logAuditTrail(auditMap, user.getUserid(), "m_venues with venuecode " + venue.getVenuecode() + " saved successfully");

                return ResponseEntity.ok("2");  // successfully saved or updated
            }
        } catch (Exception ex) {
            persistenceLogger.error("M_Venues save failed.\nMessage: {}\nUserid: {}", ex.getMessage(), user.getUserid(), ex);
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "m_venues save failed");
        }

        return ResponseEntity.ok("");   // something went wrong...
    }
}
