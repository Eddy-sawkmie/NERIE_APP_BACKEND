package com.nic.nerie.m_activities.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nic.nerie.audittrail.service.AudittrailService;
import com.nic.nerie.exceptions.MyAuthenticationCredentialsNotFoundException;
import com.nic.nerie.exceptions.MyAuthorizationDeniedException;
import com.nic.nerie.m_activities.model.M_Activities;
import com.nic.nerie.m_activities.service.M_ActivitiesService;
import com.nic.nerie.m_phases.model.M_Phases;
import com.nic.nerie.m_phases.service.M_PhasesService;
import com.nic.nerie.m_processes.service.M_ProcessesService;
import com.nic.nerie.m_programs.model.M_Programs;
import com.nic.nerie.m_programs.service.M_ProgramsService;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.mt_userlogin.service.MT_UserloginService;
import com.nic.nerie.utils.ExceptionUtil;
import com.nic.nerie.utils.UtilCommon;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/activities")
public class M_ActivitiesController {
    private final M_ActivitiesService mActivitiesService;
    private final MT_UserloginService mtUserloginService;
    private final M_PhasesService mPhasesService;
    private final M_ProgramsService mProgramsService;
    private final M_ProcessesService mProcessesService;
    private final AudittrailService audittrailService;

    private static final Logger persistenceLogger = LoggerFactory.getLogger("DATA_PERSISTENCE_LOGGER");

    @Autowired
    public M_ActivitiesController(
        M_ActivitiesService mActivitiesService, 
        MT_UserloginService mtUserloginService,
        M_PhasesService mPhasesService,
        M_ProgramsService mProgramsService,
        M_ProcessesService mProcessesService,
        AudittrailService audittrailService
    ) {
        this.mActivitiesService = mActivitiesService;
        this.mtUserloginService = mtUserloginService;
        this.mPhasesService = mPhasesService;
        this.mProgramsService = mProgramsService;
        this.mProcessesService = mProcessesService;
        this.audittrailService = audittrailService;
    }

    /*
     * Public endpoint
     */
    @GetMapping("/getActivityBasedOnPhaseId")
    @ResponseBody
    public ResponseEntity<List<M_Activities>> getActivityBasedOnPhaseId(
            @RequestParam String phaseid) {

        try {
            List<M_Activities> acts = mActivitiesService.getActivitiesByPhaseId(phaseid);

            return new ResponseEntity<>(
                    acts == null ? List.of() : acts,
                    HttpStatus.OK
            );

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
     * Secured endpoint
     * Endpoint exclusive to role A (Local-admin) and U (Coordinator-faculty)
     * Endpoint tied to Add/Edit Program process
     */
    @PostMapping("/save")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<String> saveActivity(
        @RequestParam("activityProgcode") String pcode,
        @RequestParam("activityPhaseID") String phaseid,
        @RequestParam("activitystartdate") String startdate,
        @RequestParam("activityenddate") String enddate,
        @RequestParam("expenditure") String expenditure,
        @RequestParam("activityname") String activityname,
        @RequestParam("activitydescription") String activitydescription,
        HttpServletRequest request
    ) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!(
            List.of("A", "U").contains(userRole) &&
            mProcessesService.isProcessGranted(user.getUsercode(), 2)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }

        if (
            pcode == null || pcode.isBlank() ||
            phaseid == null || phaseid.isBlank() ||
            startdate == null || startdate.isBlank() ||
            enddate == null || enddate.isBlank() ||
            //expenditure == null || expenditure.isBlank() ||
            activityname == null || activityname.isBlank() ||
            activitydescription == null || activitydescription.isBlank()
        )
            return ResponseEntity.badRequest().body("Required fields are missing or blank");

        Date startdate2 = null;
        Date enddate2 = null;
        try {
            startdate2 = new SimpleDateFormat("dd-MM-yyyy").parse(startdate);
            enddate2 = new SimpleDateFormat("dd-MM-yyyy").parse(enddate);
        } catch (ParseException e) {
            return ResponseEntity.badRequest().body("Invalid start or end date");
        }
        
        M_Phases phase = mPhasesService.findById(phaseid).get();
        if (phase == null)
            return ResponseEntity.badRequest().body("Phase with phaseid " + phaseid + " does not exist");
        
        M_Programs program = mProgramsService.getProgram(pcode);
        if (program == null)
            return ResponseEntity.badRequest().body("Program with programcodee " + pcode + " does not exist");

        M_Activities newActivities = new M_Activities();
        newActivities.setActivitydescription(activitydescription);
        newActivities.setActivityname(activityname);

        if(expenditure != null && !expenditure.isBlank())
            newActivities.setExpenditure(expenditure);

        newActivities.setPhaseid(phase);
        newActivities.setProgramcode(program);
        newActivities.setActivitystartdate(startdate2);
        newActivities.setActivityenddate(enddate2);
       
        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
       
        try {
            if ((newActivities = mActivitiesService.saveActivities(newActivities)) != null) {
                persistenceLogger.info("M_Activities with activityid {} saved successfully by userid {}", newActivities.getActivityid(), user.getUserid());
                audittrailService.logAuditTrail(auditMap, user.getUserid(), "m_activities with activityid " + newActivities.getActivityid() + " saved successfully");

                return ResponseEntity.ok("2");  // activities successfully saved
            }
        } catch (Exception ex) {
            persistenceLogger.error("M_Activities save failed.\nMessage: {}\nUserid: {}", ex.getMessage(), user.getUserid(), ex);
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "m_activities save failed");
        }
        
        return ResponseEntity.ok("4");  // something went wrong...
    }
}
