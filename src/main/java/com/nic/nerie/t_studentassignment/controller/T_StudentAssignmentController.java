package com.nic.nerie.t_studentassignment.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.nic.nerie.audittrail.service.AudittrailService;
import com.nic.nerie.exceptions.MyAuthenticationCredentialsNotFoundException;
import com.nic.nerie.exceptions.MyAuthorizationDeniedException;
import com.nic.nerie.m_processes.service.M_ProcessesService;
import com.nic.nerie.m_subjects.model.M_Subjects;
import com.nic.nerie.m_subjects.service.M_SubjectService;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.mt_userlogin.service.MT_UserloginService;
import com.nic.nerie.t_assignmenttest.model.T_Assignmenttest;
import com.nic.nerie.t_assignmenttest.service.T_AssignmenttestService;
import com.nic.nerie.t_studentassignment.dto.StudentBySubDTO;
import com.nic.nerie.t_studentassignment.model.T_StudentAssignment;
import com.nic.nerie.t_studentassignment.service.T_StudentAssignmentService;
import com.nic.nerie.utils.ExceptionUtil;
import com.nic.nerie.utils.UtilCommon;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/assignments")
public class T_StudentAssignmentController {
    private final T_StudentAssignmentService tStudentAssignmentService;
    private final MT_UserloginService mtUserloginService;
    private final T_AssignmenttestService tAssignmenttestService;
    private final M_SubjectService mSubjectService;
    private final M_ProcessesService mProcessesService;
    private final AudittrailService audittrailService;
    private static final Logger logger = LoggerFactory.getLogger(T_StudentAssignmentController.class);
    private static final Logger persistenceLogger = LoggerFactory.getLogger("DATA_PERSISTENCE_LOGGER");

    @Autowired
    public T_StudentAssignmentController(
        T_StudentAssignmentService tStudentAssignmentService, 
        MT_UserloginService mtUserloginService, 
        T_AssignmenttestService tAssignmenttestService, 
        M_SubjectService mSubjectService,
        M_ProcessesService mProcessesService,
        AudittrailService audittrailService
    ) {
        this.tStudentAssignmentService = tStudentAssignmentService;
        this.mtUserloginService = mtUserloginService;
        this.tAssignmenttestService = tAssignmenttestService;
        this.mSubjectService = mSubjectService;
        this.mProcessesService = mProcessesService;
        this.audittrailService = audittrailService;
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role A (Local-admin) & U (Coordinator-faculty)
     * isfaculty = 1
     * 'Assignments' process (processcode = 40)
     */
    @GetMapping("/upload-assignment")
    public String renderUploadAssignmentPage(@ModelAttribute("assignment") T_Assignmenttest assignmenttest, Model model, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "Assignments, " + request.getMethod()), "page");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!(
            List.of("A", "U").contains(userRole) &&
            mProcessesService.isProcessGranted(user.getUsercode(), 40) &&
            user.getIsfaculty().equals("1")
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "Assignments, " + request.getMethod(), user.getUserid()), "page");
        }

        List<Object[]> subs = mSubjectService.getSubjectsList(user.getUsercode());
        List<T_Assignmenttest> assignments = tAssignmenttestService.getAssignmentList(user.getUsercode());

        for (T_Assignmenttest assignment : assignments) {
            if ("LINK".equals(assignment.getSubmissiontype()) && assignment.getReldoc() != null) {
                assignment.setReldocAsString(new String(assignment.getReldoc(), StandardCharsets.UTF_8));
            }
        }
        
        model.addAttribute("alist", assignments);
        model.addAttribute("subs", subs);

        return "pages/upload-assignment";
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role A (Local-admin) & U (Coordinator-faculty)
     * isfaculty = 1
     * Endpont tied with 'Assignments' process (processcode = 40)
     */
    @PostMapping("/upload-assignment")
    @ResponseBody
    public ResponseEntity<String> uploadAssignment(
            @ModelAttribute("assignment") T_Assignmenttest assignment,
            @RequestParam(name = "file1", required = false) MultipartFile file1,
            @RequestParam(name = "submissiontype", required = false) String submissionType,
            @RequestParam(name = "submissionLink", required = false) String submissionLink,
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
            mProcessesService.isProcessGranted(user.getUsercode(), 40) &&
            user.getIsfaculty().equals("1")
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }
        
        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
        try {
            // Set faculty who is uploading
            assignment.setUsercode(user);

            // Handle submission type: FILE or LINK
            if ("FILE".equalsIgnoreCase(submissionType) && file1 != null && !file1.isEmpty()) {
                assignment.setReldoc(file1.getBytes());
                assignment.setSubmissiontype("FILE");
            } else if ("LINK".equalsIgnoreCase(submissionType) && submissionLink != null && !submissionLink.trim().isEmpty()) {
                assignment.setReldoc(submissionLink.getBytes(StandardCharsets.UTF_8));
                assignment.setSubmissiontype("LINK");
            } else if("NONE".equalsIgnoreCase(submissionType)){
                String defBase64String ="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";
                assignment.setSubmissiontype("FILE");
                byte[] fileBytes = Base64.getDecoder().decode(defBase64String);
                assignment.setReldoc(fileBytes);
            }
            
            else {
                return ResponseEntity.badRequest().body("-1");
            }

            // Save the assignment
            String result = tAssignmenttestService.uploadAssignment(assignment);
            
            if (result.equals("-1"))
                throw new PersistenceException();
            
            persistenceLogger.info("T_StudentAssignment saved successfully by userid {}", user.getUserid());
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_studentassignment saved successfully");

            return ResponseEntity.ok(result);
        } catch (NullPointerException ex) {
            logger.error(ex.toString());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("-1");
        } catch (Exception ex) {
            persistenceLogger.error("T_StudentAssignment save failed.\nMessage: {}\nUserid: {}", ex.getMessage(), user.getUserid(), ex);
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_studentassignment save failed");
            
            return ResponseEntity.ok("-1");
        }
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role A (Local-admin) & U (Coordinator-faculty)
     * isfaculty = 1
     * Endpont tied with 'Assignments' process (processcode = 40)
     */
    @PostMapping("/editAssignment")
    @ResponseBody
    public ResponseEntity<String> editAssignment(
            @RequestParam("assignmenttestid") String assignmentId,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("subjectcode") String subjectCode,
            @RequestParam("uploaddate") @DateTimeFormat(pattern = "dd-MM-yyyy") Date uploadDate,
            @RequestParam("submissiondate") @DateTimeFormat(pattern = "dd-MM-yyyy") Date submissionDate,
            @RequestParam("fullmark") int fullMark,
            @RequestParam("passmark") int passMark,
            @RequestParam("submissiontype") String submissionType,
            @RequestParam(name = "submissionLink", required = false) String submissionLink,
            @RequestParam(name = "file1", required = false) MultipartFile file1,
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
                        mProcessesService.isProcessGranted(user.getUsercode(), 40) &&
                        user.getIsfaculty().equals("1")
        )) {
            throw new MyAuthorizationDeniedException(
                    ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }

        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
        try {
            // Fetch existing assignment
            T_Assignmenttest ed = tAssignmenttestService.getAssignmentDetails(assignmentId);
            if (ed == null) {
                persistenceLogger.warn("Attempt to edit non-existent assignment with id: {}", assignmentId);
                return ResponseEntity.badRequest().body("-1");
            }

            ed.setTitle(title);
            ed.setDescription(description);
            ed.setFullmark(fullMark);
            ed.setPassmark(passMark);
            ed.setSubmissiondate(submissionDate);
            ed.setUploaddate(uploadDate);

            M_Subjects subject = mSubjectService.getSubjectBySubjectCode(subjectCode);
            if (subject == null) {
                persistenceLogger.warn("Invalid subject code provided during edit: {}", subjectCode);
                return ResponseEntity.badRequest().body("-1");
            }
            ed.setSubjectcode(subject);

            if ("FILE".equalsIgnoreCase(submissionType)) {
                ed.setSubmissiontype("FILE");
                // Only update the file if a new one is actually uploaded
                if (file1 != null && !file1.isEmpty()) {
                    ed.setReldoc(file1.getBytes());
                }
                // If no new file is provided, the existing ed.reldoc is preserved.

            } else if ("LINK".equalsIgnoreCase(submissionType)) {
                ed.setSubmissiontype("LINK");
                // Only update the link if a new one is provided
                if (submissionLink != null && !submissionLink.trim().isEmpty()) {
                    ed.setReldoc(submissionLink.getBytes(StandardCharsets.UTF_8));
                }
                // If no new link is provided, the existing ed.reldoc is preserved.
            } else {
                persistenceLogger.warn("Invalid submission type during edit: {}", submissionType);
                return ResponseEntity.badRequest().body("-1"); // Invalid submission type
            }

            ed.setUsercode(user);

            String result = tAssignmenttestService.uploadAssignment(ed);

            if ("-1".equals(result)) {
                throw new PersistenceException("Service layer failed to save the assignment.");
            }

            persistenceLogger.info("T_Assignmenttest with id {} edited successfully by userid {}", assignmentId, user.getUserid());
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_assignmenttest edited successfully with id: " + assignmentId);

            return ResponseEntity.ok(result);

        } catch (Exception ex) {
            persistenceLogger.error("T_Assignmenttest edit failed for id: {}.\nMessage: {}\nUserid: {}", assignmentId, ex.getMessage(), user.getUserid(), ex);
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_assignmenttest edit failed for id: " + assignmentId);

            return ResponseEntity.internalServerError().body("-1");
        }
    }

    @GetMapping("/view-submitted-assignment")
    public String renderViewSubmittedAssignmentPage(@ModelAttribute("studentasssignments") T_StudentAssignment tsa, String aid, Model model, HttpServletRequest request) {
        try {
            mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        }

        T_Assignmenttest assignmentDetails = tAssignmenttestService.getAssignmentDetails(aid);
        List<T_StudentAssignment> submittedAssignments = tStudentAssignmentService.getSubmittedAssignments(aid);
        List<Object[]>  studentAssignmentsNames = tStudentAssignmentService.getSubmittedAssignmentsStudentsName(aid);

        model.addAttribute("assignmentdetails", assignmentDetails);
        model.addAttribute("studentasssignments", submittedAssignments);
        model.addAttribute("studentasssignmentsnames", studentAssignmentsNames);

        // This line sets the active menu for child page that belong to the "Assignment" section.
        model.addAttribute("activeMenuItem", "/assignments/upload-assignment");

        return "pages/view-submitted-assignment";
    }

    @GetMapping("/viewAssignmentDocument")
    public void viewOriginalAssignmentDocument(HttpServletResponse response, @RequestParam("fid") String fid) throws IOException {
        T_Assignmenttest ta = tAssignmenttestService.getAssignmentDetails(fid);

        if (ta != null && ta.getReldoc() != null) {
            byte[] fileContent = ta.getReldoc();
            // Set response headers
            response.reset();
            response.setContentType("application/pdf");
            response.setContentLength(fileContent.length);

            // Write the file content to the response output stream
            try (OutputStream out = response.getOutputStream()) {
                out.write(fileContent);
                out.flush(); // Ensure all data is sent
            } catch (IOException e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error streaming file.");
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found for assignment ID: " + fid);
        }
    }

    @GetMapping("/viewStudentUploadAssignmentDocument")
    public void viewStudentUploadedAssignmentDocument(HttpServletResponse response,
                                                      @RequestParam("fid") String assignmentTestId,
                                                      @RequestParam("sid") String studentUserCode)
                                                      throws IOException {

        T_StudentAssignment sa = tStudentAssignmentService.getStudentAssignmentDocument(assignmentTestId, studentUserCode);

        if (sa != null && sa.getReldoc() != null) {
            byte[] fileContent = sa.getReldoc();
            response.reset();
            response.setContentType("application/pdf");
            response.setContentLength(fileContent.length);

            try (OutputStream out = response.getOutputStream()) {
                out.write(fileContent);
                out.flush();
            } catch (IOException e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error streaming file.");
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Submitted file not found for student: " + studentUserCode + " and assignment: " + assignmentTestId);
        }
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role A (Local-admin) & U (Coordinator-faculty)
     * isfaculty = 1
     * Endpont tied with 'Assignments' process (processcode = 40)
     */
    @PostMapping("/saveStudentAssignmentMarks")
    @ResponseBody
    public ResponseEntity<String> saveStudentAssignmentMarks(
            @RequestParam(name = "assignmentmarks", required = false) String[] assignmentmarks,
            @RequestParam(name = "stdids", required = false) String[] stdids,
            @RequestParam(name = "idsToDelete", required = false) String[] idsToDelete,
            HttpServletRequest request) {

        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "Assignments, " + request.getMethod()), "page");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!(List.of("A", "U").contains(userRole) &&
                mProcessesService.isProcessGranted(user.getUsercode(), 40) &&
                user.getIsfaculty().equals("1"))) {
            throw new MyAuthorizationDeniedException(
                    ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "Assignments, " + request.getMethod(), user.getUserid()), "page");
        }

        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
        String finalResult = "1";

        try {
            // Handle deletions
            if (idsToDelete != null && idsToDelete.length > 0) {
                for (String id : idsToDelete) {
                    if (id != null && !id.isEmpty()) {
                        tStudentAssignmentService.deleteAssignmentMarkById(id);
                        persistenceLogger.info("Deleted assignment mark with ID {} by userid {}", id, user.getUserid());
                        audittrailService.logAuditTrail(auditMap, user.getUserid(),
                                "Deleted assignment mark with ID " + id);
                    }
                }
            }

            // Save or update marks
            if (assignmentmarks != null && stdids != null && assignmentmarks.length == stdids.length) {
                for (int i = 0; i < stdids.length; i++) {
                    String result = tStudentAssignmentService.saveStudentAssignmentMarks(stdids[i], assignmentmarks[i]);
                    if ("-1".equals(result)) {
                        finalResult = "-1";
                        persistenceLogger.error("Failed to save assignment mark for student ID {} by userid {}", stdids[i], user.getUserid());
                        audittrailService.logAuditTrail(auditMap, user.getUserid(),
                                "Failed to save assignment mark for student ID " + stdids[i]);
                    } else {
                        persistenceLogger.info("Saved assignment mark for student ID {} by userid {}", stdids[i], user.getUserid());
                        audittrailService.logAuditTrail(auditMap, user.getUserid(),
                                "Saved assignment mark for student ID " + stdids[i]);
                    }
                }
            }

            return ResponseEntity.ok(finalResult);

        } catch (Exception ex) {
            finalResult = "-1";
            persistenceLogger.error("saveStudentAssignmentMarks operation failed.\nMessage: {}\nUserid: {}",
                    ex.getMessage(), user.getUserid(), ex);
            audittrailService.logAuditTrail(auditMap, user.getUserid(),
                    "saveStudentAssignmentMarks operation failed");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(finalResult);
        }
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role T (Student)     
     */
//    @GetMapping("/viewassignments")
//    public String renderAssignmentListPage(Model model, HttpServletRequest request) {
//        MT_Userlogin user;
//        try {
//            user = mtUserloginService.getUserloginFromAuthentication();
//        } catch (Exception ex) {
//            throw new MyAuthenticationCredentialsNotFoundException(
//                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "page");
//        }
//        List<Object[]> assignmentList = tStudentAssignmentService.getSubmitAssignmentList(user.getUsercode());
//
//        if (!user.getRole().getRoleCode().equalsIgnoreCase("T")) {
//            throw new MyAuthorizationDeniedException(
//                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "page");
//        }
//
//        for (Object[] record : assignmentList) {
//            String submissionType = (String) record[11]; // Adjust index
//            byte[] reldoc = (byte[]) record[3]; // Adjust index
//
//            if ("LINK".equals(submissionType) && reldoc != null) {
//                String reldocAsString = new String(reldoc, StandardCharsets.UTF_8);
//                record[3] = reldocAsString; // Adjust index
//            }
//        }
//
//        model.addAttribute("subs", assignmentList);
//        model.addAttribute("usercode", user.getUsercode());
//
//        return "pages/t_students/assignment-list";
//    }
    @GetMapping("/viewassignments")
    public String renderAssignmentListPage(
            Model model,
            HttpServletRequest request,
            @RequestParam(defaultValue = "-1") String semphase) {

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

        List<Object[]> assignmentList = tStudentAssignmentService.getSubmitAssignmentList(user.getUsercode(), semphase);

        for (Object[] record : assignmentList) {
            String submissionType = (String) record[11];
            byte[] reldoc = (byte[]) record[3];
            if ("LINK".equals(submissionType) && reldoc != null) {
                record[3] = new String(reldoc, StandardCharsets.UTF_8);
            }
        }

        model.addAttribute("subs", assignmentList);
        model.addAttribute("usercode", user.getUsercode());
        model.addAttribute("semphase", semphase);

        return "pages/t_students/assignment-list";
    }
    @PostMapping("/uploadstudentassignment")
    @ResponseBody
    public String uploadAssignmentStudent(
            @RequestParam("assignmentfile") MultipartFile assignmentFile,
            @RequestParam("assignmentid") String assignmentId,
            @RequestParam("usercode") String usercode,
            HttpServletRequest request) {

        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        }

        if (!"T".equalsIgnoreCase(user.getRole().getRoleCode())) {
            throw new MyAuthorizationDeniedException(
                    ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }

        String result = "-1";

        try {
            if (assignmentFile != null && assignmentFile.getSize() > 0) {
                result =  tStudentAssignmentService.uploadStudentAssignment(assignmentFile, assignmentId, usercode);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return result;
    }

    @GetMapping("/viewassignmentsubmission")
    public void viewAssignmentDocument(HttpServletResponse response, @RequestParam("studentassignmentid") String studentassignmentid) throws IOException {
        T_StudentAssignment assignment = tStudentAssignmentService.getAssignmentSubmissionDetails(studentassignmentid);
        byte[] fileContent = assignment.getReldoc();

        if (fileContent != null) {
            response.reset();
            response.setContentType("application/pdf"); // Set the content type (adjust if needed)
            response.setContentLength(fileContent.length);

            try (OutputStream out = response.getOutputStream()) {
                out.write(fileContent);
                out.flush();
            }
        } else
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role T (Student)     
     * Assignments
     */
    @PostMapping("/edit-student-assignment")
    public ResponseEntity<String> editStudentAssignment(
        @RequestParam("eassignmentfile") MultipartFile eassignmentfile, 
        @RequestParam(value = "assignmentid", required = false) String assignmentid, 
        @RequestParam("usercode") String usercode, 
        HttpServletRequest request) {

        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        }

        if (!user.getRole().getRoleCode().toUpperCase().equalsIgnoreCase("T")) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }

        // validating required parameters
        if (eassignmentfile == null || eassignmentfile.isEmpty() || usercode == null || usercode.isBlank()) 
            return ResponseEntity.badRequest().body("Required parameters are missing or invalid");
        
        // validating assignmentid 
        // only for update operation
        if (assignmentid != null && !assignmentid.isBlank()) {
            if (!tStudentAssignmentService.existsByStudentassignmentid(assignmentid))
                return ResponseEntity.badRequest().body("Assignment with assignmentid " + assignmentid + " does not exist");
        }

        if (!mtUserloginService.existsByUsercode(usercode))
            return ResponseEntity.badRequest().body("User with usercode " + usercode + " does not exist");

        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
        try {
            T_StudentAssignment assignment = tStudentAssignmentService.getStudentAssignmentByAssignmentidAndUsercode(assignmentid, usercode);
            if (assignment == null)
                throw new EntityNotFoundException();
            assignment.setReldoc(eassignmentfile.getBytes());
        
            if ((assignment = tStudentAssignmentService.saveStudentAssignment(assignment)) != null) {
                persistenceLogger.info("T_StudentAssignment with studentassignmentid {} saved successfully by userid {}", assignment.getStudentassignmentid(), user.getUserid());
                audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_studentassignment with studentassignmentid " + assignment.getStudentassignmentid() + " saved successfully");
            
                return ResponseEntity.ok("1");
            } else 
                throw new PersistenceException();
        } catch (Exception ex) {
            persistenceLogger.error("T_StudentAssignment save failed.\nMessage: {}\nUserid: {}", ex.getMessage(), user.getUserid(), ex);
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_studentassignment save failed");
            
            if (ex.getClass().equals(EntityNotFoundException.class)) {
                return ResponseEntity.badRequest().body("-1");
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("-1");
        } 
    }

    @GetMapping("/students-by-subject")
    @ResponseBody
    public List<StudentBySubDTO> getStudentsBySubject(@RequestParam String subjectcode) {
        List<StudentBySubDTO> list = tStudentAssignmentService.getStudentsBySubject(subjectcode);
        return list;
    }


//    @GetMapping("/api/myassignments")
//    @ResponseBody
//    public ResponseEntity<?> getMyAssignmentsApi(HttpServletRequest request) {
//
//        MT_Userlogin user = mtUserloginService.getUserloginFromAuthentication();
//        List<Object[]> list = tStudentAssignmentService.getSubmitAssignmentList(user.getUsercode());
//
//        List<Map<String, Object>> response = new ArrayList<>();
//
//        for (Object[] obj : list) {
//            Map<String, Object> map = new HashMap<>();
//
//            map.put("assignmentId",   obj[0]);   // assignmenttestid
//            map.put("subjectCode",    obj[1]);   // subjectcode
//            map.put("subjectName",    obj[2]);   // subjectname  ← was wrongly used as title
//            map.put("title",          obj[4]);   // title        ← THE FIX
//            map.put("assignedDate",   obj[5]);   // uploaddate
//            map.put("lastDate",       obj[6]);   // submissiondate
//            map.put("marksSecured",   obj[7]);   // assignmentmarks
//            map.put("studentAssignmentId", obj[8]); // studentassignmentid
//            map.put("description",    obj[9]);   // description
//            map.put("fullMark",       obj[10]);  // fullmark
//            map.put("submissionType", obj[11]);  // submissiontype
//            map.put("isSubmitted",    obj[8] != null); // if studentassignmentid exists → submitted
//
//            // Encode fileData correctly
//            if (obj[3] instanceof byte[]) {
//                String subType = obj[11] != null ? obj[11].toString() : "";
//                if ("LINK".equalsIgnoreCase(subType)) {
//                    map.put("fileData", new String((byte[]) obj[3], StandardCharsets.UTF_8));
//                } else {
//                    map.put("fileData", Base64.getEncoder().encodeToString((byte[]) obj[3]));
//                }
//            } else {
//                map.put("fileData", obj[3] != null ? obj[3].toString() : null);
//            }
//
//            response.add(map);
//        }
//
//        return ResponseEntity.ok(response);
//    }
@GetMapping("/api/myassignments")
@ResponseBody
public ResponseEntity<?> getMyAssignmentsApi(
        HttpServletRequest request,
        @RequestParam(defaultValue = "-1") String semphase) {

    MT_Userlogin user = mtUserloginService.getUserloginFromAuthentication();
    List<Object[]> list = tStudentAssignmentService.getSubmitAssignmentList(user.getUsercode(), semphase);

    List<Map<String, Object>> response = new ArrayList<>();

    for (Object[] obj : list) {
        Map<String, Object> map = new HashMap<>();
        map.put("assignmentId",        obj[0]);
        map.put("subjectCode",         obj[1]);
        map.put("subjectName",         obj[2]);
        map.put("title",               obj[4]);
        map.put("assignedDate",        obj[5]);
        map.put("lastDate",            obj[6]);
        map.put("marksSecured",        obj[7]);
        map.put("studentAssignmentId", obj[8]);
        map.put("description",         obj[9]);
        map.put("fullMark",            obj[10]);
        map.put("submissionType",      obj[11]);
        map.put("isSubmitted",         obj[8] != null);

        if (obj[3] instanceof byte[]) {
            String subType = obj[11] != null ? obj[11].toString() : "";
            if ("LINK".equalsIgnoreCase(subType)) {
                map.put("fileData", new String((byte[]) obj[3], StandardCharsets.UTF_8));
            } else {
                map.put("fileData", Base64.getEncoder().encodeToString((byte[]) obj[3]));
            }
        } else {
            map.put("fileData", obj[3] != null ? obj[3].toString() : null);
        }

        response.add(map);
    }

    return ResponseEntity.ok(response);
}
    //flutter endpoint
    @GetMapping("/getAssignmentListJson")
    @ResponseBody
    public List<T_Assignmenttest> getAssignmentListJson(HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        }
        return tAssignmenttestService.getAssignmentList(user.getUsercode());
    }
    @GetMapping("/getSubmittedAssignmentsJson")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getSubmittedAssignmentsJson(
            @RequestParam("aid") String aid, HttpServletRequest request) {
        try {
            mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(
                            request.getRequestURI(), request.getMethod()), "json");
        }

        List<T_StudentAssignment> submissions =
                tStudentAssignmentService.getSubmittedAssignments(aid);
        List<Object[]> studentNames =
                tStudentAssignmentService.getSubmittedAssignmentsStudentsName(aid);

        List<Map<String, Object>> response = new ArrayList<>();

        for (int i = 0; i < submissions.size(); i++) {
            T_StudentAssignment sa = submissions.get(i);
            Map<String, Object> map = new HashMap<>();

            String usercode = sa.getUsercode() != null
                    ? sa.getUsercode().getUsercode() : "";

            String studentName = usercode;
            String rollNo = usercode; // fallback

            if (i < studentNames.size()) {
                Object[] row = studentNames.get(i);
                // row[0] = 26NERTES002 (roll number)
                // row[1] = firstname
                // row[2] = lastname
                rollNo       = row[0] != null ? row[0].toString().trim() : usercode;
                String firstName = row[1] != null ? row[1].toString().trim() : "";
                String lastName  = row[2] != null ? row[2].toString().trim() : "";
                studentName  = (firstName + " " + lastName).trim();
                if (studentName.isEmpty()) studentName = rollNo;
            }

            map.put("studentassignmentid", sa.getStudentassignmentid());
            //map.put("usercode",            rollNo);
            map.put("usercode",            rollNo);           // 26NERTES002 — display
            map.put("numericUsercode",     usercode); //← roll no e.g. 26NERTES002
            map.put("studentname",         studentName); // ← full name e.g. Wanlam Kharkongor
            map.put("submitteddate",       sa.getUploaddate());
            map.put("assignmentmarks",     sa.getAssignmentmark() != null
                    ? sa.getAssignmentmark().toString() : null);
            response.add(map);
        }

        return ResponseEntity.ok(response);
    }
}
