package com.nic.nerie.t_feedbackstudent.controller;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nic.nerie.audittrail.service.AudittrailService;
import com.nic.nerie.exceptions.MyAuthenticationCredentialsNotFoundException;
import com.nic.nerie.exceptions.MyAuthorizationDeniedException;
import com.nic.nerie.m_processes.service.M_ProcessesService;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.mt_userlogin.service.MT_UserloginService;
import com.nic.nerie.t_feedbackstudent.model.T_FeedbackStudent;
import com.nic.nerie.t_feedbackstudent.service.T_FeedbackStudentService;
import com.nic.nerie.utils.ExceptionUtil;
import com.nic.nerie.utils.UtilCommon;

import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;

// ✅ REMOVED: import static com.nic.nerie.mt_test.controller.MT_TestController.safeStr;
// safeStr() was private in MT_TestController so it cannot be imported.
// A local private safeStr() method is added at the bottom of this class instead.

@Controller
@RequestMapping("/feedbacks")
public class T_FeedbackStudentController {
    private final T_FeedbackStudentService tFeedbackStudentService;
    private final MT_UserloginService mtUserloginService;
    private final M_ProcessesService mProcessesService;
    private final AudittrailService audittrailService;
    private static final Logger persistenceLogger = LoggerFactory.getLogger("DATA_PERSISTENCE_LOGGER");

    @Autowired
    public T_FeedbackStudentController(
            T_FeedbackStudentService tFeedbackStudentService,
            MT_UserloginService mtUserloginService,
            M_ProcessesService mProcessesService,
            AudittrailService audittrailService) {
        this.tFeedbackStudentService = tFeedbackStudentService;
        this.mtUserloginService = mtUserloginService;
        this.mProcessesService = mProcessesService;
        this.audittrailService = audittrailService;
    }

    /*
     * Secured endpoint
     * Endpoint exclusive to role A (Local-admin) & U (Coordinator-Faculty)
     * 'View Feedbacks' process (proecsscode = 41)
     */
    @GetMapping("/view-student-feedback")
    public String renderStudentFeedback(Model model, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "View Feedbacks, " + request.getMethod()), "page");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!(
                List.of("A", "U").contains(userRole) &&
                        mProcessesService.isProcessGranted(user.getUsercode(), 41) &&
                        user.getIsfaculty().equals("1")
        )) {
            throw new MyAuthorizationDeniedException(
                    ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "View Feedbacks, " + request.getMethod(), user.getUserid()), "page");
        }

        List<Object[]> subjectsListFeed = tFeedbackStudentService.getSubjectsListFeed(user.getUsercode());

        model.addAttribute("subjectlist", subjectsListFeed);

        return "pages/view-student-feedback";
    }

    @GetMapping("/getFeebackListBasedOnSubjectCode")
    @ResponseBody
    public List<Object[]> getFeebackListBasedOnSubjectCode(@RequestParam("subjectcode") String subjectcode, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        }

        return tFeedbackStudentService.getStudentsFeedbackList(subjectcode, user.getUsercode());
    }

//    /*
//     * Secured endpoint
//     * This endpoint is exclusive to role T (Student)
//     * Endpoint tied with Feedback process
//     */
//    @PostMapping(value = "/postsubjectfeedback", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<String> saveFeedbackStudent(@RequestBody T_FeedbackStudent tFeedbackStudent, HttpServletRequest request) {
//        MT_Userlogin user = null;
//        try {
//            user = mtUserloginService.getUserloginFromAuthentication();
//        } catch (Exception ex) {
//            throw new MyAuthenticationCredentialsNotFoundException(
//                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
//        }
//
//        if (!user.getRole().getRoleCode().equalsIgnoreCase("T")) {
//            throw new MyAuthorizationDeniedException(
//                    ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
//        }
//
//        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
//        try {
//            T_FeedbackStudent savedTFeedbackStudent = tFeedbackStudentService.saveFeedbackStudent(tFeedbackStudent);
//            if (savedTFeedbackStudent != null) {
//                persistenceLogger.info("T_FeedbackStudent with feedbackid {} saved successfully by userid {}", savedTFeedbackStudent.getFeedbackid(), user.getUserid());
//                audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_feedbackstudent with feedbackid " + savedTFeedbackStudent.getFeedbackid() + " saved successfully");
//
//                return ResponseEntity.ok().build();
//            }
//
//            throw new PersistenceException();
//        } catch (Exception ex) {
//            persistenceLogger.error("T_FeedbackStudent save failed.\nMessage: {}\nUserid: {}", ex.getMessage(), user.getUserid(), ex);
//            audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_feedbackstudent save failed");
//
//            return ResponseEntity.internalServerError().build();
//        }
//    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role T (Student)
     * Endpoint tied with Feedback process
     */
    @PostMapping(value = "/postsubjectfeedback", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> saveFeedbackStudent(@RequestBody T_FeedbackStudent tFeedbackStudent, HttpServletRequest request) {
        MT_Userlogin user = null;

        System.out.println("=== [FLUTTER REQUEST] JSON: " + tFeedbackStudent);
        System.out.println("=== [FLUTTER] subject class: " + tFeedbackStudent.getSubjectcode());
        System.out.println("=== [FLUTTER] faculty class: " + tFeedbackStudent.getFacultyid());
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        }

        if (!user.getRole().getRoleCode().equalsIgnoreCase("T")) {
            throw new MyAuthorizationDeniedException(
                    ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }

        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
        try {
            T_FeedbackStudent savedTFeedbackStudent = tFeedbackStudentService.saveFeedbackStudent(tFeedbackStudent, user);
            if (savedTFeedbackStudent != null) {
                persistenceLogger.info("T_FeedbackStudent with feedbackid {} saved successfully by userid {}", savedTFeedbackStudent.getFeedbackid(), user.getUserid());
                audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_feedbackstudent with feedbackid " + savedTFeedbackStudent.getFeedbackid() + " saved successfully");

                return ResponseEntity.ok().build();
            }

            throw new PersistenceException();
        } catch (Exception ex) {
            persistenceLogger.error("T_FeedbackStudent save failed.\nMessage: {}\nUserid: {}", ex.getMessage(), user.getUserid(), ex);
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_feedbackstudent save failed");

            return ResponseEntity.internalServerError().build();
        }
    }


    @GetMapping("/getStudentSubjectList")
    @ResponseBody
    public List<Object[]> getStudentSubjectList(HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        }
        return tFeedbackStudentService.getSubjectsListForStudent(user.getUsercode());
    }

    //Flutter endpoint
    // GET /feedbacks/getFeedbackListJson?subjectcode=

    @GetMapping("/getFeedbackListJson")
    @ResponseBody
    public List<Map<String, Object>> getFeedbackListJson(
            @RequestParam("subjectcode") String subjectcode,
            HttpServletRequest request) {
        MT_Userlogin user = requireAuth(request);
        requireFacultyAccess(request, user);

        List<Object[]> rows = tFeedbackStudentService
                .getStudentsFeedbackList(subjectcode, user.getUsercode());

        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("feedback",    safeStr(row, 0));  // "good"
            map.put("date",        safeStr(row, 1));  // "03/21/2026 10:42 PM"
            map.put("studentid",   safeStr(row, 2));  // "26NERTES002"
            map.put("studentname", safeStr(row, 3));  // "Wanlam Kharkongor"
            result.add(map);
        }
        return result;
    }

    //Flutter endpoint
    // GET /feedbacks/getSubjectsListFeedJson
    // Returns the faculty's subject list as named JSON for Flutter.
    @GetMapping("/getSubjectsListFeedJson")
    @ResponseBody
    public List<Map<String, Object>> getSubjectsListFeedJson(
            HttpServletRequest request) {
        MT_Userlogin user = requireAuth(request);
        requireFacultyAccess(request, user);

        List<Object[]> rows = tFeedbackStudentService
                .getSubjectsListFeed(user.getUsercode());

        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : rows) {
            System.out.println("=== [FEED_SUBJ] length=" + row.length);
            for (int i = 0; i < row.length; i++) {
                System.out.println("=== [FEED_SUBJ][" + i + "] = \""
                        + row[i] + "\" ("
                        + (row[i] == null ? "null"
                        : row[i].getClass().getSimpleName())
                        + ")");
            }
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("subjectcode", safeStr(row, 0));
            map.put("subjectname", safeStr(row, 1));
            result.add(map);
        }
        return result;
    }

    //helper methods required by the Flutter endpoints above
    // requireAuth() and requireFacultyAccess() avoid repeating try/catch boilerplate.
    // safeStr() safely reads an Object[] element by index without ArrayIndexOutOfBoundsException.
    private MT_Userlogin requireAuth(HttpServletRequest request) {
        try {
            return mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(
                            request.getRequestURI(), request.getMethod()), "json");
        }
    }

    private void requireFacultyAccess(HttpServletRequest request, MT_Userlogin user) {
        String role = user.getRole().getRoleCode().toUpperCase();
        if (!(List.of("A", "U").contains(role)
                && mProcessesService.isProcessGranted(user.getUsercode(), 41)
                && user.getIsfaculty().equals("1"))) {
            throw new MyAuthorizationDeniedException(
                    ExceptionUtil.generateAuthorizationDeniedMessage(
                            request.getRequestURI(), request.getMethod(),
                            user.getUserid()), "json");
        }
    }

    private static String safeStr(Object[] row, int idx) {
        return (idx < row.length && row[idx] != null) ? row[idx].toString() : "";
    }
}