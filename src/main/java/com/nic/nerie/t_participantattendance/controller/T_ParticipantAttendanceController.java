package com.nic.nerie.t_participantattendance.controller;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.nic.nerie.m_processes.service.M_ProcessesService;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.mt_userlogin.service.MT_UserloginService;
import com.nic.nerie.t_participantattendance.service.T_ParticipantAttendanceService;
import com.nic.nerie.utils.ExceptionUtil;
import com.nic.nerie.utils.UtilCommon;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/participant/attendance")
public class T_ParticipantAttendanceController {
    private final MT_UserloginService mtUserloginService;
    private final T_ParticipantAttendanceService participantAttendanceService;
    private final M_ProcessesService mProcessesService;
    private final AudittrailService audittrailService;
    private static final Logger persistenceLogger = LoggerFactory.getLogger("DATA_PERSISTENCE_LOGGER");

    public T_ParticipantAttendanceController(MT_UserloginService mtUserloginService,
                                             T_ParticipantAttendanceService participantAttendanceService,
                                             M_ProcessesService mProcessesService,
                                             AudittrailService audittrailService) {
        this.mtUserloginService = mtUserloginService;
        this.participantAttendanceService = participantAttendanceService;
        this.mProcessesService = mProcessesService;
        this.audittrailService = audittrailService;
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to roles A (Local Admin) and U (Coordinator/Faculty).
     * 'Attendance' process (processcode = 12)
     */
    @GetMapping("/manage")
    public String renderParticipantAttendancePage(Model model, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "Attendance, " + request.getMethod()), "page");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!(
            List.of("A", "U").contains(userRole) &&
            mProcessesService.isProcessGranted(user.getUsercode(), 12)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "Attendance, " + request.getMethod(), user.getUserid()), "page");
        }

        switch (user.getRole().getRoleCode().toUpperCase()) {
            case "A":
                model.addAttribute("layoutPath", "layouts/local-admin-layout");
                break;
            case "U":
                model.addAttribute("layoutPath", "layouts/coordinator-faculty-layout");
                break;
        }

        model.addAttribute("programlistP", participantAttendanceService.getProgramsForParticipantAttendance(user.getUsercode(),
            user.getMoffices().getOfficecode(), user.getRole().getRoleCode()));

        return "pages/participant-attendance";
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to roles A (Local Admin) and U (Coordinator/Faculty).
     * Endpoint tied with 'Attendance' process (processcode = 12)
     */
    @PostMapping("/save")
    public ResponseEntity<String> saveParticipantAttendance(
            @RequestParam("phaseid") String phaseid,
            @RequestParam("programtimetablecode") String programtimetablecode,
            @RequestParam(value = "p_applicationcode", required = false) List<String> applicationCodes,
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
            mProcessesService.isProcessGranted(user.getUsercode(), 12)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage("/participant/attendance/save", user.getUserid()), "json");
        }

        // validation
        if (phaseid == null || phaseid.isBlank() || programtimetablecode == null || programtimetablecode.isBlank()) {
            return ResponseEntity.badRequest().body("Required parameters (phaseid, programtimetablecode) are missing.");
        }

        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
        try {
            participantAttendanceService.saveOrUpdateAttendance(
                    user.getUsercode(),
                    phaseid,
                    programtimetablecode,
                    applicationCodes
            );
            persistenceLogger.info("T_ParticipantAttendance saved successfully by userid {}", user.getUserid());
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_participantattendance saved successfully");

            return ResponseEntity.ok("2");
        } catch (Exception ex) {
            persistenceLogger.error("T_ParticipantAttendance save failed.\nMessage: {}\nUserid: {}", ex.getMessage(), user.getUserid(), ex);
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_participantattendance save failed");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while saving attendance.");
        }
    }

    /*
     * Secured endpoint
     */
    // Get Attendance
    @GetMapping("/program-attendance")
    @ResponseBody
    public ResponseEntity<List<Object[]>> getProgramAttendance(@RequestParam("phaseid") String phaseId, HttpServletRequest request) {
        MT_Userlogin user = null;

        try {
            user = mtUserloginService.getUserloginFromAuthentication(SecurityContextHolder.getContext().getAuthentication());
            if (user == null)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            List<Object[]> attendance = participantAttendanceService.getAttendance(phaseId, user.getUsercode());

            return ResponseEntity.ok(attendance);
        } catch (AuthenticationCredentialsNotFoundException ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
