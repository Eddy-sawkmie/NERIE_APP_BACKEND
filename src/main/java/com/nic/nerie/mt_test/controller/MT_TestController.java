package com.nic.nerie.mt_test.controller;

import java.util.*;

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
import com.nic.nerie.m_processes.service.M_ProcessesService;
import com.nic.nerie.m_subjects.service.M_SubjectService;
import com.nic.nerie.mt_test.model.MT_Test;
import com.nic.nerie.mt_test.service.MT_TestService;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.mt_userlogin.service.MT_UserloginService;
import com.nic.nerie.utils.ExceptionUtil;
import com.nic.nerie.utils.UtilCommon;

import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/tests")
public class MT_TestController {
    private final MT_UserloginService mtUserloginService;
    private final M_SubjectService mSubjectService;
    private final MT_TestService mtTestService;
    private final M_ProcessesService mProcessesService;
    private final AudittrailService audittrailService;
    private static final Logger persistenceLogger = LoggerFactory.getLogger("DATA_PERSISTENCE_LOGGER");

    @Autowired
    public MT_TestController(
        MT_UserloginService mtUserloginService, 
        M_SubjectService mSubjectService, 
        MT_TestService mtTestService,
        M_ProcessesService mProcessesService,
        AudittrailService audittrailService) {
        this.mtUserloginService = mtUserloginService;
        this.mSubjectService = mSubjectService;
        this.mtTestService = mtTestService;
        this.mProcessesService = mProcessesService;
        this.audittrailService = audittrailService;
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role A (Local-admin) & U (Coordinator-faculty)
     * isfaculty = 1
     * 'Create Tests' process (processcode = 38)
     */
    @GetMapping("/create-tests")
    public String renderCreateTestsPage(@ModelAttribute("testdetails") MT_Test testdetail,
                                        Model model,
                                        HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "Create Tests, " + request.getMethod()), "page");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!(
            List.of("A", "U").contains(userRole) &&
            mProcessesService.isProcessGranted(user.getUsercode(), 38) &&
            user.getIsfaculty().equals("1")
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "Create Tests, " + request.getMethod(), user.getUserid()), "page");
        }

        // @Toiar stuff I don't know...
        // if ("A".equals(user.getUserrole())) {
        //     model.addAttribute("layoutPath", "layouts/local-admin-layout");
        // } else {
        //     model.addAttribute("layoutPath", "layouts/coordinator-faculty-layout");
        // }

        List<Object[]> subjectList = mSubjectService.getSubjectsList(user.getUsercode());
        List<Object[]> testList = mtTestService.getTestList(user.getUsercode());

        model.addAttribute("subs", subjectList);
        model.addAttribute("testlist", testList);

        return "pages/create-tests";
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role A (Local-admin) & U (Coordinator-faculty)
     * isfaculty = 1
     * Endpoint tied with 'Create Tests' process (processcode = 38)
     */
    @PostMapping("/saveTestDetails")
    @ResponseBody
    public String saveTestDetails(@ModelAttribute("testdetails") MT_Test testDetail,
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
            List.of("A", "U").contains(userRole) &&
            mProcessesService.isProcessGranted(user.getUsercode(), 38) &&
            user.getIsfaculty().equals("1")
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }

        testDetail.setUsercode(user);

        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
        String result = "-1"; // Should be "1" for success, "-1" for failure
        try {
            result = mtTestService.createtests(testDetail, user.getUsercode());
            
            if (result.equals("1")) {
                persistenceLogger.info("MT_Test saved successfully by userid {}", user.getUserid());
                audittrailService.logAuditTrail(auditMap, user.getUserid(), "mt_test saved successfully");
            } else
                throw new PersistenceException();
        } catch (Exception ex) {
            persistenceLogger.error("MT_Test save failed.\nMessage: {}\nUserid: {}", ex.getMessage(), user.getUserid(), ex);
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "mt_test save failed");
        }

        return result;
    }

    @GetMapping("/full-mark")
    public ResponseEntity<?> getFullmark(@RequestParam("testid") String testid) {
        if (testid == null || testid.isBlank())
            return ResponseEntity.badRequest().body("Required parameter testid is missing");
        
        return ResponseEntity.ok(mtTestService.getFullMark(testid));
    }
    //flutter api to get the test list
    @GetMapping("/getTestList")
    @ResponseBody
    public List<Map<String, Object>> getTestListJson(HttpServletRequest request) {
        MT_Userlogin user = requireAuth(request);
        requireFacultyAccess(request, user);

        List<Object[]> rows = mtTestService.getTestList(user.getUsercode());
        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] row : rows) {
            Map<String, Object> map = new LinkedHashMap<>();


            map.put("testid",      safeStr(row, 0));  // "102"
            map.put("testno",      safeStr(row, 1));  // "2"
            map.put("testdate",    safeStr(row, 2));  // "2026-03-21"
            map.put("testname",    safeStr(row, 3));  // "test"
            map.put("passmark",    safeStr(row, 4));  // "20"
            map.put("fullmark",    safeStr(row, 5));  // "40"
            // [6] subjectcode — not needed in Flutter UI
            map.put("subjectname", safeStr(row, 7));  // "Test Subject 1"
            map.put("semester",    safeStr(row, 8));  // "1 Semester"

            result.add(map);
        }
        return result;
    }

    // ──  DELETE /tests/deleteTest?testid= — Flutter delete endpoint ──────
    @DeleteMapping("/deleteTest")
    @ResponseBody
    public ResponseEntity<String> deleteTest(
            @RequestParam("testid") String testid,
            HttpServletRequest request) {
        MT_Userlogin user = requireAuth(request);
        requireFacultyAccess(request, user);

        if (testid == null || testid.isBlank())
            return ResponseEntity.badRequest().body("testid is required");

        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
        try {
            mtTestService.deleteTest(testid.trim());
            persistenceLogger.info("MT_Test {} deleted by {}", testid, user.getUserid());
            audittrailService.logAuditTrail(auditMap, user.getUserid(),
                    "mt_test " + testid + " deleted");
            return ResponseEntity.ok("1");
        } catch (Exception ex) {
            persistenceLogger.error("MT_Test delete failed: {}", ex.getMessage(), ex);
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "mt_test delete failed");
            return ResponseEntity.internalServerError().body("-1");
        }
    }

    private static String safeStr(Object[] row, int idx) {
        return (idx < row.length && row[idx] != null) ? row[idx].toString() : "";
    }

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
                && mProcessesService.isProcessGranted(user.getUsercode(), 38)
                && user.getIsfaculty().equals("1"))) {
            throw new MyAuthorizationDeniedException(
                    ExceptionUtil.generateAuthorizationDeniedMessage(
                            request.getRequestURI(), request.getMethod(),
                            user.getUserid()), "json");
        }
    }
}
