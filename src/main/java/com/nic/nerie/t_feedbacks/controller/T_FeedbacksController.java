package com.nic.nerie.t_feedbacks.controller;
import com.nic.nerie.audittrail.service.AudittrailService;
import com.nic.nerie.exceptions.MyAuthenticationCredentialsNotFoundException;
import com.nic.nerie.exceptions.MyAuthorizationDeniedException;
import com.nic.nerie.m_phases.model.M_Phases;
import com.nic.nerie.m_phases.service.M_PhasesService;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.t_feedbacks.service.T_FeedbackService;
import com.nic.nerie.t_feedbacksday.model.T_Feedbacksday;
import com.nic.nerie.t_feedbacksday.service.T_FeedbacksdayService;
import com.nic.nerie.t_participantfeedbacks.model.T_ParticipantFeedbacks;
import com.nic.nerie.t_participantfeedbacks.service.T_ParticipantFeedbacksService;
import com.nic.nerie.t_programtimetable.service.T_ProgramTimeTableService;
import com.nic.nerie.utils.ExceptionUtil;
import com.nic.nerie.utils.UtilCommon;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import com.nic.nerie.mt_userlogin.service.MT_UserloginService;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/participant/feedback")
public class T_FeedbacksController  {
    private final T_ParticipantFeedbacksService participantFeedbacksService;
    private final MT_UserloginService userloginService;
    private final T_FeedbackService feedbackService;
    private final M_PhasesService phasesService;
    private final AudittrailService audittrailService;
    private final T_ProgramTimeTableService tProgramTimeTableService;
    private final T_FeedbacksdayService tFeedbacksdayService;
    private final T_ParticipantFeedbacksService tParticipantFeedbacksService;

    @Autowired
    public T_FeedbacksController(MT_UserloginService userloginService, T_ProgramTimeTableService programTimeTableService, M_PhasesService phasesService, T_ParticipantFeedbacksService participantFeedbacksService, T_FeedbackService feedbackService, AudittrailService audittrailService, T_ProgramTimeTableService tProgramTimeTableService, T_FeedbacksdayService tFeedbacksdayService, T_ParticipantFeedbacksService tParticipantFeedbacksService) {
        this.userloginService = userloginService;
        this.participantFeedbacksService = participantFeedbacksService;
        this.feedbackService = feedbackService;
        this.phasesService = phasesService;
        this.audittrailService = audittrailService;
        this.tProgramTimeTableService = tProgramTimeTableService;
        this.tFeedbacksdayService = tFeedbacksdayService;
        this.tParticipantFeedbacksService = tParticipantFeedbacksService;
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive only to P (Participant)
     */
    @PostMapping("/daily-feedback")
    @ResponseBody
    public ResponseEntity<List<Object[]>> getDailySubjectsForFeedback(
            @RequestParam("programcode") String programCode,
            @RequestParam("phaseid") String phaseId,
            HttpServletRequest request) {

        MT_Userlogin user;

        try {
            user = userloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        }

        if (!"P".equalsIgnoreCase(user.getUserrole())) {
            throw new MyAuthorizationDeniedException(
                    ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }

        List<Object[]> subjects = feedbackService.getDayPrograms(new Date(), phaseId, user.getUsercode());
        return ResponseEntity.ok(subjects);
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive only to P (Participant)
     */
    @PostMapping("/save-daily-feedback")
    @ResponseBody
    public ResponseEntity<String> saveDailyFeedback(
            @RequestParam("programtimetablecode") String programTimetableCode,
            @RequestParam("feedback") String feedbackText,
            HttpServletRequest request) {

        MT_Userlogin user;
        try {
            user = userloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        }

        if (!"P".equalsIgnoreCase(user.getUserrole())) {
            throw new MyAuthorizationDeniedException(
                    ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }

        if (!StringUtils.hasText(feedbackText)) {
            return ResponseEntity.badRequest().body("Feedback cannot be empty.");
        }

        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
        auditMap.put("userid", user.getUsername());

        boolean success = feedbackService.saveDailyFeedback(programTimetableCode, user.getUsercode(), feedbackText);

        if (success) {
            auditMap.put("actiontaken", "Save Daily Feedback Success");
            audittrailService.saveAuditTrail(auditMap);
            return ResponseEntity.ok("1");
        } else {
            auditMap.put("actiontaken", "Save Daily Feedback Failed (Service Layer)");
            audittrailService.saveAuditTrail(auditMap);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("-1");
        }
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive only to U (Coordinator-Faculty)
     */
    @GetMapping("/daily-feedback/list")
    public String getDailySubjectsForFeedbackList(@RequestParam("phaseid") String phaseid, Model model,
                                                  HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = userloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        }

        if (!"U".equalsIgnoreCase(user.getUserrole())) {
            throw new MyAuthorizationDeniedException(
                    ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }

        model.addAttribute("subjectslist", tProgramTimeTableService.getSubjectDaysByPhaseId(phaseid));
        model.addAttribute("program", phasesService.getPhaseByPhaseId(phaseid));

        // This line sets the active menu for child page that belong to the "Add/Edit Programs" section.
        model.addAttribute("activeMenuItem", "/program/manage");

        return "pages/list-daily-feedback";
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive only to U (Coordinator-Faculty)
     */
    @GetMapping("/daily-feedback/get")
    @ResponseBody
    public ResponseEntity<List<T_Feedbacksday>> getDayFeedbacks(@RequestParam("programtimetablecode") String programtimetablecode,
                                                                HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = userloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        }

        if (!"U".equalsIgnoreCase(user.getUserrole())) {
            throw new MyAuthorizationDeniedException(
                    ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }

        List<T_Feedbacksday> feedbacks = tFeedbacksdayService.getDayFeedbacksByProgramTimeTableCode(programtimetablecode);
        return ResponseEntity.ok(feedbacks);
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to P (Participant) & U (Coordinator-Faculty)
     */
    @GetMapping("/view-overall-feedback")
    public String renderViewOverallFeedbackPage(@RequestParam("aid") String phaseid,
                                                @RequestParam(value = "usercode", required = false) String usercode,
                                                HttpServletRequest request,
                                                Model model) {
        MT_Userlogin user;
        try {
            user = userloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        }

        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!"P".equalsIgnoreCase(userRole) && !"U".equalsIgnoreCase(userRole)) {
            throw new MyAuthorizationDeniedException(
                    ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }

        switch (userRole) {
            case "U":
                // This line sets the active menu for child page that belong to the "Add/Edit Programs" section.
                model.addAttribute("activeMenuItem", "/program/manage");
                model.addAttribute("layoutPath", "layouts/coordinator-faculty-layout");
                break;
            case "P":
                // This line sets the active menu for child page that belong to the "My Program" section.
                model.addAttribute("activeMenuItem", "/program/my-programs");
                model.addAttribute("layoutPath", "layouts/participant-layout");
                break;
        }

        T_ParticipantFeedbacks tpfeedback = participantFeedbacksService
                .getFeedbackByPhaseIdAndUserCode(phaseid, (usercode != null && !usercode.isEmpty()) ? usercode : user.getUsercode());

        if (tpfeedback == null) {
            tpfeedback = new T_ParticipantFeedbacks();
        }

        model.addAttribute("tpfeedback", tpfeedback);
        model.addAttribute("phaseid", phaseid);
        model.addAttribute("mprogramlist", phasesService.getPhaseDetailsForFeedbackByPhaseId(phaseid));
        model.addAttribute("view", true);
        model.addAttribute("login", user);

        return "pages/t_participants/overall-feedback";
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive only to P (Participant)
     */
    @GetMapping("/write-overall-feedback")
    public String renderWriteOverallFeedbackPage(@RequestParam("aid") String phaseid, Model model,
                                                 HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = userloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        }

        if (!"P".equalsIgnoreCase(user.getUserrole())) {
            throw new MyAuthorizationDeniedException(
                    ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }

        T_ParticipantFeedbacks tpfeedback = new T_ParticipantFeedbacks();

        model.addAttribute("tpfeedback", tpfeedback);
        model.addAttribute("phaseid", phaseid);
        model.addAttribute("mprogramlist", phasesService.getPhaseDetailsForFeedbackByPhaseId(phaseid));
        model.addAttribute("view", false);
        model.addAttribute("login", user);
        model.addAttribute("layoutPath", "layouts/participant-layout");

        // This line sets the active menu for child page that belong to the "My Program" section.
        model.addAttribute("activeMenuItem", "/program/my-programs");

        return "pages/t_participants/overall-feedback";
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive only to P (Participant)
     */
    @PostMapping("/save-overall-feedback")
    public ResponseEntity<String> saveOverallFeedback(
            @RequestParam("phid") String phaseid,
            @ModelAttribute("tpfeedback") T_ParticipantFeedbacks tpfeedback,
            HttpServletRequest request) {

        MT_Userlogin user;

        try {
            user = userloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        }

        if (!"P".equalsIgnoreCase(user.getUserrole())) {
            throw new MyAuthorizationDeniedException(
                    ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }

        M_Phases phase = phasesService.getPhaseByPhaseId(phaseid);
        if (phase == null) {
            throw new IllegalArgumentException("Invalid phase ID: " + phaseid);
        }

        tpfeedback.setMtuserlogin(user);
        tpfeedback.setEntrydate(new Date());
        tpfeedback.setPhaseid(phase);

        String feedbackNo = participantFeedbacksService.saveOverallFeedback(tpfeedback);
        return ResponseEntity.ok(feedbackNo);
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to U (Coordinator-Faculty)
     */
    @GetMapping("/overall-feedback/list")
    public String renderOverallFeedbackListPage(@RequestParam("aid") String phaseid, Model model,
                                                HttpServletRequest request) {
        MT_Userlogin user;

        try {
            user = userloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        }

        if (!"U".equalsIgnoreCase(user.getUserrole())) {
            throw new MyAuthorizationDeniedException(
                    ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }

        List<T_ParticipantFeedbacks> feedbackList = tParticipantFeedbacksService.getFeedbacksByPhaseId(phaseid);

        model.addAttribute("feedbackslist", feedbackList);
        model.addAttribute("program", phasesService.getPhaseByPhaseId(phaseid));

        // This line sets the active menu for child page that belong to the "Add/Edit Programs" section.
        model.addAttribute("activeMenuItem", "/program/manage");

        return "pages/list-overall-feedback";
    }
}
