package com.nic.nerie.t_programtimetable.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
import com.nic.nerie.m_phases.model.M_Phases;
import com.nic.nerie.m_phases.service.M_PhasesService;
import com.nic.nerie.m_processes.service.M_ProcessesService;
import com.nic.nerie.m_venues.model.M_Venues;
import com.nic.nerie.m_venues.service.M_VenuesService;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.mt_userlogin.service.MT_UserloginService;
import com.nic.nerie.mt_venuerooms.model.MT_VenueRooms;
import com.nic.nerie.mt_venuerooms.service.MT_VenueRoomsService;
import com.nic.nerie.t_programtimetable.model.T_ProgramTimeTable;
import com.nic.nerie.t_programtimetable.service.T_ProgramTimeTableService;
import com.nic.nerie.utils.ExceptionUtil;
import com.nic.nerie.utils.UtilCommon;

import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/timetable")
public class T_ProgramTimeTableController {
    private final MT_UserloginService userloginService;
    private final T_ProgramTimeTableService programTimeTableService;
    private final M_PhasesService mPhasesService;
    private final MT_VenueRoomsService mtVenueRoomsService;
    private final M_VenuesService mVenuesService;
    private final M_ProcessesService mProcessesService;
    private final AudittrailService audittrailService;
    private static final Logger persistenceLogger = LoggerFactory.getLogger("DATA_PERSISTENCE_LOGGER");

    @Autowired
    public T_ProgramTimeTableController(
        MT_UserloginService userloginService, 
        T_ProgramTimeTableService programTimeTableService, 
        M_PhasesService mPhasesService, 
        MT_VenueRoomsService mtVenueRoomsService, 
        M_VenuesService mVenuesService, 
        M_ProcessesService mProcessesService,
        AudittrailService audittrailService
    ) {
        this.userloginService = userloginService;
        this.programTimeTableService = programTimeTableService;
        this.mPhasesService = mPhasesService;
        this.mtVenueRoomsService = mtVenueRoomsService;
        this.mVenuesService = mVenuesService;
        this.mProcessesService = mProcessesService;
        this.audittrailService = audittrailService;
    }

    /*
     * Secured endpoint
     */
    //Participant Program Time Table
    @GetMapping("/program-timetable")
    @ResponseBody
    public ResponseEntity<?> getProgramTimetable( // Use wildcard or Object for flexibility
                                                  @RequestParam("phaseid") String phaseId,
                                                  @RequestParam("applicationcode") String applicationCode,
                                                  @AuthenticationPrincipal UserDetails userDetails,
                                                  HttpServletRequest request) {
        if (userDetails == null) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        }

        MT_Userlogin login = userloginService.findByUserId(userDetails.getUsername());
        if (login == null || login.getUsercode() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"error\":\"User details not found\"}");
        }
        String userCode = login.getUsercode();

        try {
            List<Object[]> timetable = programTimeTableService.getParticipantTimetable(phaseId, userCode); // Pass correct userCode

            if (timetable == null) {
                return ResponseEntity.ok(Collections.emptyList());
            }
            return ResponseEntity.ok(timetable);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\":\"Server error fetching timetable\"}"); // Return JSON error
        }
    }

    /*
     * Public endpoint
     */
    @PostMapping("/program-timetable")
    public ResponseEntity<List<Object[]>> getProgramSessions(@RequestParam("phaseid") String phaseId, @RequestParam("programday") String programDay) {
        // validating required fields
        if (
            phaseId == null || phaseId.isBlank() || phaseId.trim().length() > 6 || !mPhasesService.existsById(phaseId) || 
            programDay == null || programDay.isBlank()
        )
            return ResponseEntity.badRequest().body(Collections.emptyList());

        try {
            return ResponseEntity.ok(programTimeTableService.getProgramSessions(phaseId, programDay));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    /*
     * Secured endpoint
     * Endpoint exclusive to role A (Local-admin) & U (Co-ordinator Faculty)
     * 'Program Schedule' process (processcode = 16)
     */
    @GetMapping("/program-timetable/create")
    public String renderProgramTimetableCreatePage(Model model, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = userloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "Program Schedule, " + request.getMethod()), "page");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!(
            List.of("A", "U").contains(userRole) &&
            mProcessesService.isProcessGranted(user.getUsercode(), 16)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "Program Schedule, " + request.getMethod(), user.getUserid()), "page");
        }

        switch (user.getRole().getRoleCode().toUpperCase()) {
            case "A":
                model.addAttribute("layoutPath", "layouts/local-admin-layout");
                break;
            case "U":
                model.addAttribute("layoutPath", "layouts/coordinator-faculty-layout");
                break;
        }

        model.addAttribute("programlist", programTimeTableService.getProgramsTimetableByUserrole(user.getUsercode(), 
            user.getMoffices().getOfficecode(), user.getRole().getRoleCode()));
    
        return "pages/create-program-timetable";        
    }

    /*
     * Secured endpoint
     * Endpoint exclusive to role A (Local-admin) & U (Co-ordinator Faculty)
     * 'Program Schedule' process (processcode = 16)
     */
    @PostMapping("/program-timetable/save")
    public ResponseEntity<String> saveProgramTimetable(
            @RequestParam(value = "programtimetablecode", required = false) String programTimeTableCode,
            @RequestParam("programcode") String programCode,
            @RequestParam("phaseid") String phaseId,
            @RequestParam("programdate") String programDate,
            @RequestParam("programday") String programDay,  
            @RequestParam("venuecode") String venueCode,
            @RequestParam("roomcode") String roomCode,
            @RequestParam("starttime") String startTime,
            @RequestParam("endtime") String endTime,
            @RequestParam("breakclass") String breakClass,
            @RequestParam("subject") String subject,
            @RequestParam(value = "rpslno", required = false) List<String> resourcePersons,
            HttpServletRequest request) {

        MT_Userlogin user;
        try {
            user = userloginService.getUserloginFromAuthentication();
        } catch (RuntimeException ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        } 
        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!(
            List.of("A", "U").contains(userRole) &&
            mProcessesService.isProcessGranted(user.getUsercode(), 16)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }

        T_ProgramTimeTable newProgramTimeTable = new T_ProgramTimeTable();
        newProgramTimeTable.setMtuserlogin(user);
        newProgramTimeTable.setProgramtimetablecode(programTimeTableCode != null ? programTimeTableCode.trim() : "");

        if (startTime == null || startTime.isBlank() || endTime == null || endTime.isBlank())
            return ResponseEntity.ok("1");
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            
            if (startTime.split(":")[0].length() != 2)
                startTime = "0" + startTime;
            
            if (endTime.split(":")[0].length() != 2)
                endTime = "0" + endTime;

            newProgramTimeTable.setStarttime(LocalTime.parse(startTime, formatter));
            newProgramTimeTable.setEndtime(LocalTime.parse(endTime, formatter));
        } catch (DateTimeParseException ex) {
            return ResponseEntity.badRequest().body("Improper start or end date format");
        }

        if (subject == null || subject.isBlank())
            return ResponseEntity.ok("4");
        else if (subject.length() > 70)
            return ResponseEntity.ok("5");            
        newProgramTimeTable.setSubject(subject.trim());

        if (programCode == null || programCode.isBlank() ||
            phaseId == null || phaseId.isBlank() ||
            programDay == null || programDay.isBlank() || 
            venueCode == null || venueCode.isBlank() ||
            roomCode == null || roomCode.isBlank() ||
            breakClass == null || breakClass.isBlank())
            return ResponseEntity.badRequest().build();

        newProgramTimeTable.setEntrydate(new Date());

        Optional<M_Phases> existingPhase = mPhasesService.findById(phaseId.trim());
        if (existingPhase.isEmpty())
            return ResponseEntity.badRequest().body("Phase does not exist");
        newProgramTimeTable.setPhaseid(existingPhase.get());

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localDate = LocalDate.parse(programDate, formatter);
            newProgramTimeTable.setProgramdate(Date.from(
                localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));   
        } catch (DateTimeParseException ex) {
            ex.printStackTrace();
            return ResponseEntity.badRequest().body("Improper date format");
        }

        // programDay has abitrary values sent from client-side
        // if (Integer.valueOf(programDay) < 1 || Integer.valueOf(programDay) > 7)
        //     return ResponseEntity.badRequest().body("Improper program day value");
        newProgramTimeTable.setProgramday(Short.valueOf(programDay));
        
        M_Venues existingVenues = mVenuesService.findByVenuecode(venueCode.trim());
        MT_VenueRooms existingVenueRooms = mtVenueRoomsService.findByRoomcode(roomCode.trim());        
        if (existingVenues == null)
            return ResponseEntity.badRequest().body("Venue does not exist");
        if (existingVenueRooms == null)
            return ResponseEntity.badRequest().body("Venue room does not exist");
        if (existingVenueRooms.getMvenues() != existingVenues)
            return ResponseEntity.badRequest().body("Venue room does not exist for the venue");
        newProgramTimeTable.setMtvenuerooms(existingVenueRooms);
       
        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
        if (programTimeTableService.checkProgramTimetableExistence(newProgramTimeTable)) {
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_programtimetable save failed");

            return ResponseEntity.ok("6");
        }
        
        if (programTimeTableService.checkProgramTimetableClashExistence(newProgramTimeTable)) {
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_programtimetable save failed");

            return ResponseEntity.ok("7");
        }

        try {
            if (programTimeTableService.saveProgramTimeTable(newProgramTimeTable, resourcePersons)) {
                persistenceLogger.info("T_ProgramTimeTable saved successfully by userid {}", user.getUserid());
                audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_programtimetable saved successfully");

                return ResponseEntity.ok("2");
            } else 
                throw new PersistenceException();
        } catch (RuntimeException ex) {
            persistenceLogger.error("T_ProgramTimeTable save failed.\nMessage: {}\nUserid: {}", ex.getMessage(), user.getUserid(), ex);
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_programtimetable save failed");
            
            return ResponseEntity.ok("10");
        }
    }
}
