package com.nic.nerie.t_internalevaluationmarks.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.nic.nerie.t_students.service.T_StudentsService;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.nic.nerie.audittrail.service.AudittrailService;
import com.nic.nerie.exceptions.MyAuthenticationCredentialsNotFoundException;
import com.nic.nerie.exceptions.MyAuthorizationDeniedException;
import com.nic.nerie.m_processes.service.M_ProcessesService;
import com.nic.nerie.m_subjects.model.M_Subjects;
import com.nic.nerie.m_subjects.service.M_SubjectService;
import com.nic.nerie.mt_test.model.MT_Test;
import com.nic.nerie.mt_test.service.MT_TestService;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.mt_userlogin.service.MT_UserloginService;
import com.nic.nerie.t_faculties.model.T_Faculties;
import com.nic.nerie.t_faculties.service.T_FacultiesService;
import com.nic.nerie.t_internalevaluationmarks.model.T_InternalEvaluationMarks;
import com.nic.nerie.t_internalevaluationmarks.service.T_InternalEvaluationMarksService;
import com.nic.nerie.t_students.model.T_Students;
import com.nic.nerie.utils.ExceptionUtil;
import com.nic.nerie.utils.UtilCommon;

import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/internal-evaluation-marks")
public class T_InternalEvaluationMarksController {
    private final MT_UserloginService mtUserloginService;
    private final M_SubjectService mSubjectService;
    private final MT_TestService mtTestService;
    private final T_FacultiesService tFacultiesService;
    private final T_InternalEvaluationMarksService tInternalEvaluationMarksService;
    private final M_ProcessesService mProcessesService;
    private final AudittrailService audittrailService;
    private final T_StudentsService tStudentsService;
    private static final Logger persistenceLogger = LoggerFactory.getLogger("DATA_PERSISTENCE_LOGGER");

    @Autowired
    public T_InternalEvaluationMarksController(
            MT_UserloginService mtUserloginService,
            M_SubjectService mSubjectService,
            MT_TestService mtTestService,
            T_FacultiesService tFacultiesService,
            T_InternalEvaluationMarksService tInternalEvaluationMarksService,
            M_ProcessesService mProcessesService,
            AudittrailService audittrailService, T_StudentsService tStudentsService) {
        this.mtUserloginService = mtUserloginService;
        this.mSubjectService = mSubjectService;
        this.mtTestService = mtTestService;
        this.tFacultiesService = tFacultiesService;
        this.tInternalEvaluationMarksService = tInternalEvaluationMarksService;
        this.mProcessesService = mProcessesService;
        this.audittrailService = audittrailService;
        this.tStudentsService = tStudentsService;
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role A (Local-admin) & U (Coordinator-faculty)
     * isfaculty = 1
     * 'Internal Evaluation' process (processcode = 39)
     */
    @GetMapping("/upload-internal-evaluation-marks")
    public String renderInternalEvaluationMarksPage(Model model, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "Internal Evaluation, " + request.getMethod()), "page");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!(
                List.of("A", "U").contains(userRole) &&
                        mProcessesService.isProcessGranted(user.getUsercode(), 39) &&
                        user.getIsfaculty().equals("1")
        )) {
            throw new MyAuthorizationDeniedException(
                    ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "Internal Evaluation, " + request.getMethod(), user.getUserid()), "page");
        }

        List<Object[]> subjectList = mSubjectService.getSubjectsList(user.getUsercode());

        model.addAttribute("subs", subjectList);

        return "pages/upload-internal-evaluation-marks";
    }

    @GetMapping("/by-subject")
    public ResponseEntity<List<Object[]>> getTestsForSubject(@RequestParam String subjectcode) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            return ResponseEntity.status(401).build();
        }
        List<Object[]> tests = mtTestService.getTestsBySubjectAndUser(user.getUsercode(), subjectcode);
        return ResponseEntity.ok(tests);
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role A (Local-admin) & U (Coordinator-faculty)
     * isfaculty = 1
     * Endpoint tied with 'Internal Evaluation' process (processcode = 39)
     */
    @PostMapping("/saveInternalEvaluation")
    @ResponseBody
    public String saveInternalEvaluation(
            @RequestParam(name = "studentids", required = false) String[] studentids,
            @RequestParam(name = "studentmarks", required = false) String[] studentmarks,
            @RequestParam(name = "internalevaluationids", required = false) String[] internalevaluationids,
            @RequestParam(name = "idsToDelete", required = false) String[] idsToDelete,
            @RequestParam("subjectcode") String subjectcode,
            @RequestParam("testid") String testid,
            HttpServletRequest request) {

        String res = "1"; // Default to success
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
                        mProcessesService.isProcessGranted(user.getUsercode(), 39) &&
                        user.getIsfaculty().equals("1")
        )) {
            throw new MyAuthorizationDeniedException(
                    ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }

        Date entryDate = new Date();
        T_Faculties faculty = tFacultiesService.getFaculty(user.getUsercode());
        M_Subjects subject = new M_Subjects();
        subject.setSubjectcode(subjectcode);
        MT_Test test = new MT_Test();
        test.setTestid(testid);

        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
        try {
            //  Handle Deletions First
            if (idsToDelete != null && idsToDelete.length > 0) {
                for (String idToDelete : idsToDelete) {
                    if (idToDelete != null && !idToDelete.isEmpty()) {
                        tInternalEvaluationMarksService.deleteMarkById(idToDelete);
                        persistenceLogger.info("T_InternalEvaluationMarks with id {} deleted successfully by userid {}", idToDelete, user.getUserid());
                        audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_internalevaluationmarks with id " + idToDelete + " deleted successfully");
                    }
                }
            }

            // Handle Saves/Updates
            if (studentids != null && studentmarks != null) {
                for (int i = 0; i < studentids.length; i++) {
                    T_InternalEvaluationMarks ie = new T_InternalEvaluationMarks();
                    ie.setEntrydate(entryDate);
                    ie.setMarks(new BigDecimal(studentmarks[i]));
                    ie.setSubjectcode(subject);
                    ie.setFacultyid(faculty);

                    T_Students student = new T_Students();
                    student.setStudentid(studentids[i]);
                    ie.setStudentid(student);

                    ie.setTestid(test);

                    if (internalevaluationids != null && i < internalevaluationids.length &&
                            internalevaluationids[i] != null && !internalevaluationids[i].isEmpty()) {
                        ie.setInternalevaluationid(internalevaluationids[i]);
                    }

                    String saveResult = tInternalEvaluationMarksService.saveStudentInternalEvaluationMarks(ie);
                    if (saveResult.equals("-1")) {
                        throw new PersistenceException("Failed to save mark for student id: " + studentids[i]);
                    }

                    persistenceLogger.info("T_InternalEvaluationMarks saved/updated successfully by userid {}", user.getUserid());
                    audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_internalevaluationmarks saved/updated successfully");
                }
            }
        } catch (Exception ex) {
            persistenceLogger.error("T_InternalEvaluationMarks save/delete failed.\nMessage: {}\nUserid: {}", ex.getMessage(), user.getUserid(), ex);
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_internalevaluationmarks save/delete failed");
            return "-1";
        }

        return res;
    }

    @GetMapping("/viewmarks")
    public String renderStudentInternalMarksPage(Model model, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "page");
        }

        if (!user.getRole().getRoleCode().equalsIgnoreCase("T")) {
            throw new MyAuthorizationDeniedException(
                    ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "page");
        }

        T_Students student = tStudentsService.findByUsercode(user.getUsercode());

        List<Object[]> allSubjects = new ArrayList<>();

        // Get compulsory subjects based on long-term or short-term course
        List<Object[]> compulsorySubjects;
        if (student.getIsshortterm().equals("0")) { // Long-term student
            compulsorySubjects = mSubjectService.getCompulsorySubjectsLongTerm(
                    student.getSemestercode().getSemestercode(),
                    student.getCoursecode().getCoursecode());
        } else { // Short-term student
            compulsorySubjects = mSubjectService.getCompulsorySubjectsShortTerm(
                    student.getSphaseid().getSphaseid(),
                    student.getCoursecode().getCoursecode());
        }

        // Get the optional subjects
        List<Object[]> optionalSubjects = mSubjectService.getStudentSubjectsList(user.getUsercode());

        // Combine both lists
        allSubjects.addAll(compulsorySubjects);
        allSubjects.addAll(optionalSubjects);

        model.addAttribute("subs", allSubjects);

        return "pages/t_students/internal-marks-list";
    }

    @GetMapping("/my-internal-marks")
    @ResponseBody
    public List<Object[]> getMyInternalMarksForSubject(@RequestParam("subjectcode") String subjectcode, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "Add/Edit Designation, " + request.getMethod()), "page");
        }

        String studentId = tStudentsService.getStudentIdByUsercode(user.getUsercode());
        if (studentId == null) {
            return new ArrayList<>();
        }

        return tInternalEvaluationMarksService.getMarksByStudentAndSubject(studentId, subjectcode);
    }
}
