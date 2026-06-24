package com.nic.nerie.mt_venuerooms.controller;

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
import com.nic.nerie.m_venues.model.M_Venues;
import com.nic.nerie.m_venues.service.M_VenuesService;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.mt_userlogin.service.MT_UserloginService;
import com.nic.nerie.mt_venuerooms.model.MT_VenueRooms;
import com.nic.nerie.mt_venuerooms.service.MT_VenueRoomsService;
import com.nic.nerie.utils.ExceptionUtil;
import com.nic.nerie.utils.UtilCommon;

import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/venue-rooms")
public class MT_VenueRoomsController {
    private final MT_VenueRoomsService mtVenueRoomsService;
    private final M_VenuesService mVenuesService;
    private final MT_UserloginService mtUserloginService;
    private final M_ProcessesService mProcessesService;
    private final AudittrailService audittrailService;
    private static final Logger persistenceLogger = LoggerFactory.getLogger("DATA_PERSISTENCE_LOGGER");

    @Autowired
    public MT_VenueRoomsController(
        MT_VenueRoomsService mtVenueRoomsService, 
        M_VenuesService mVenuesService, 
        MT_UserloginService mtUserloginService,
        M_ProcessesService mProcessesService,
        AudittrailService audittrailService
    ) {
        this.mtVenueRoomsService = mtVenueRoomsService;
        this.mVenuesService = mVenuesService;
        this.mtUserloginService = mtUserloginService;
        this.mProcessesService = mProcessesService;
        this.audittrailService = audittrailService;
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role A (Local-admin) & U (Co-ordinator faculty)
     * 'Add/Edit Rooms' process (processcode = 18)
     */
    @GetMapping("/manage")
    public String renderVenueRoomsListPage(Model model, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "Add/Edit Rooms, " + request.getMethod()), "page");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!(
            List.of("A", "U").contains(userRole) &&
            mProcessesService.isProcessGranted(user.getUsercode(), 18)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "Add/Edit Rooms, " + request.getMethod(), user.getUserid()), "page");
        }

        switch (user.getRole().getRoleCode().toUpperCase()) {
            case "A":
                model.addAttribute("layoutPath", "layouts/local-admin-layout");
                break;
            case "U":
                model.addAttribute("layoutPath", "layouts/coordinator-faculty-layout");
                break;
        }

        model.addAttribute("mvenueList", mVenuesService.getAllOfficeVenues(user.getMoffices().getOfficecode()));
        model.addAttribute("mvenueroomList", mtVenueRoomsService.getAllOfficeVenueRooms(user.getMoffices().getOfficecode()));

        return "pages/venue-rooms-list";
    }

    /*
     * Public endpoint
     */
    @PostMapping("/list")
    public ResponseEntity<?> getRoomcodeRoomnameListByVenuecode(@RequestParam("venuecode") String venuecode) {
        if (venuecode == null || venuecode.isBlank())
            return ResponseEntity.badRequest().body("venuecode cannot be null or empty");
        if (venuecode.trim().length() > 6)
            return ResponseEntity.badRequest().body("Invalid venuecode");

        return ResponseEntity.ok(mtVenueRoomsService.getRoomcodeRoomnameByVenuecode(venuecode));
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role A (Local-admin) & U (Co-ordinator faculty)
     * Endpoint tied with 'Add/Edit Rooms' process (processcode = 18)
     */
    @PostMapping("/save")
    public ResponseEntity<String> saveVenueRoomsDetails(@RequestParam("venuecode") String venuecode,
                                                        @RequestParam(value = "roomcode", required = false) String roomcode,
                                                        @RequestParam("capacity") Integer capacity,
                                                        @RequestParam("roomname") String roomname,
                                                        HttpServletRequest request) {
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
            mProcessesService.isProcessGranted(user.getUsercode(), 18)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }
        
        // validate venuecode
        if (venuecode == null || venuecode.isBlank())
            return ResponseEntity.badRequest().body("Required parameters cannot be null or empty");

        M_Venues associatedVenue = mVenuesService.findByVenuecode(venuecode);   
        if (associatedVenue == null)
            return ResponseEntity.badRequest().body("Venue with venuecode " + venuecode + " does not exist");
        
        // validate roomcode (if updating...)
        if (roomcode != null && !roomcode.isBlank())
            if (!mtVenueRoomsService.existsByRoomcode(roomcode))
                return ResponseEntity.badRequest().body("Venue Room with roomcode " + roomcode + " does not exist");

        // validate capacity
        // TODO @Abanggi: Add upper-bound on capacity
        if (capacity == null || capacity < 0)
            return ResponseEntity.badRequest().body("Invalid capacity");

        // validate roomname
        if (roomname == null)
            return ResponseEntity.ok("3");
        else if (roomname.isBlank() || roomname.trim().length() > 50)
            return ResponseEntity.ok("4");

        // preparing MT_VenueRooms entity for persisting
        MT_VenueRooms newVenueRoom = new MT_VenueRooms();
        newVenueRoom.setMvenues(associatedVenue);
        newVenueRoom.setRoomcode(roomcode != null ? roomcode.trim() : "");  // optional parameter
        newVenueRoom.setCapacity(capacity);
        newVenueRoom.setRoomname(roomname.trim());
        
        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
        if (mtVenueRoomsService.checkVenueRoomExist(newVenueRoom)) {
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "mt_venuerooms saved failed");
            
            return ResponseEntity.ok("1");
        }

        try {
            if ((newVenueRoom = mtVenueRoomsService.saveVenueRoomsDetails(newVenueRoom)) != null) {
                persistenceLogger.info("MT_VenueRooms with roomcode {} saved successfully by userid {}", newVenueRoom.getRoomcode(), user.getUserid());
                audittrailService.logAuditTrail(auditMap, user.getUserid(), "mt_venuerooms with roomcode " + newVenueRoom.getRoomcode() + " saved successfully");
                
                return ResponseEntity.ok("2");
            } else 
                throw new PersistenceException();
        } catch (RuntimeException ex) {
            persistenceLogger.error("MT_VenueRooms save failed.\nMessage: {}\nUserid: {}", ex.getMessage(), user.getUserid(), ex);
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "mt_venuerooms save failed");

           return ResponseEntity.ok("");
        }
    }
}
