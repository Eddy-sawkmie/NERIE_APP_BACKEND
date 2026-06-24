package com.nic.nerie.mt_programdetails.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import com.nic.nerie.m_coursecategories.model.M_CourseCategories;
import com.nic.nerie.m_coursecategories.service.M_CourseCategoriesService;
import com.nic.nerie.m_offices.model.M_Offices;
import com.nic.nerie.m_phasemoredetails.service.M_PhaseMoreDetailsService;
import com.nic.nerie.t_notifications.model.T_Notifications;
import com.nic.nerie.t_notifications.service.T_NotificationsService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.nic.nerie.audittrail.service.AudittrailService;
import com.nic.nerie.exceptions.MyAuthenticationCredentialsNotFoundException;
import com.nic.nerie.exceptions.MyAuthorizationDeniedException;
import com.nic.nerie.m_processes.service.M_ProcessesService;
import com.nic.nerie.m_programs.model.M_Programs;
import com.nic.nerie.m_programs.service.M_ProgramsService;
import com.nic.nerie.mt_program_members.model.MT_ProgramMembers;
import com.nic.nerie.mt_program_members.service.MT_ProgramMembersService;
import com.nic.nerie.mt_programdetails.model.MT_ProgramDetails;
import com.nic.nerie.mt_programdetails.service.MT_ProgramDetailsService;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.mt_userlogin.service.MT_UserloginService;
import com.nic.nerie.utils.ExceptionUtil;
import com.nic.nerie.utils.UtilCommon;

import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/program-details")
public class MT_ProgramDetailsController {

    private final DataSource dataSource;
    private final MT_ProgramDetailsService mtProgramDetailsService;
    private final M_ProgramsService mProgramsService;
    private final MT_ProgramMembersService mtProgramMembersService;
    private final MT_UserloginService mtUserloginService;
    private final M_ProcessesService mProcessesService;
    private final M_CourseCategoriesService mCourseCategoriesService;
    private final M_PhaseMoreDetailsService mPhaseMoreDetailsService;
    private final AudittrailService audittrailService;
    private final T_NotificationsService tNotificationsService;
    private static final Logger genericLogger = LoggerFactory.getLogger(MT_ProgramDetailsController.class);
    private static final Logger persistenceLogger = LoggerFactory.getLogger("DATA_PERSISTENCE_LOGGER");

    @Autowired
    public MT_ProgramDetailsController(
            DataSource dataSource,
            MT_ProgramDetailsService mtProgramDetailsService,
            M_ProgramsService mProgramsService,
            MT_ProgramMembersService mtProgramMembersService,
            MT_UserloginService mtUserloginService,
            M_ProcessesService mProcessesService,
            M_CourseCategoriesService mCourseCategoriesService,
            M_PhaseMoreDetailsService mPhaseMoreDetailsService,
            AudittrailService audittrailService, T_NotificationsService tNotificationsService) {
        this.dataSource = dataSource;
        this.mtProgramDetailsService = mtProgramDetailsService;
        this.mProgramsService = mProgramsService;
        this.mtProgramMembersService = mtProgramMembersService;
        this.mtUserloginService = mtUserloginService;
        this.mProcessesService = mProcessesService;
        this.mCourseCategoriesService = mCourseCategoriesService;
        this.mPhaseMoreDetailsService = mPhaseMoreDetailsService;
        this.audittrailService = audittrailService;
        this.tNotificationsService = tNotificationsService;
    }

    /*
     * Public endpoint
     */
    @PostMapping("/list")
    public ResponseEntity<?> getProgramDaysByPhaseid(@RequestParam("phaseid") String phaseid) {
        // validating phaseid
        if (phaseid == null || phaseid.isEmpty())
            return ResponseEntity.badRequest().body("phaseid cannot be null or empty");

        if (phaseid.trim().length() > 3)
            return ResponseEntity.badRequest().body("Invalid phaseid");

        return ResponseEntity.ok(mtProgramDetailsService.getProgramDaysByPhaseid(phaseid));
    }

    /*
     * Public endpoint
     */
    @PostMapping("/list/timetable")
    public ResponseEntity<?> getprogramtimetable(@RequestParam("phaseid") String phaseid,
            @RequestParam("programday") String programday) {
        // validating phaseid
        if (phaseid == null || phaseid.isEmpty())
            return ResponseEntity.badRequest().body("phaseid cannot be null or empty");
        if (phaseid.trim().length() > 3)
            return ResponseEntity.badRequest().body("Invalid phaseid");

        // validating programday
        if (programday == null || programday.isEmpty())
            return ResponseEntity.badRequest().body("programday cannot be null or empty");
        try {
            Integer.parseInt(programday.trim());
        } catch (NumberFormatException ex) {
            return ResponseEntity.badRequest().body("Invalid programday");
        }

        return ResponseEntity
                .ok(mtProgramDetailsService.getProgramTimetableDetailsByPhaseidAndProgramday(phaseid, programday));
    }

    /*
     * Public endpoint
     */
    @GetMapping("/getAllProgramDetailsBasedOnProgramCode")
    @ResponseBody
    @SuppressWarnings("unchecked")
    public ResponseEntity<JSONArray> getAllProgramDetailsBasedOnProgramCode(
            @RequestParam("programcode") String programcode) {
        JSONArray res = new JSONArray();
        try {

            SimpleDateFormat outputFormat = new SimpleDateFormat("d MMMM yyyy");

            M_Programs mpg = mProgramsService.getProgram(programcode);
            if (mpg == null) {
                // return a 404
            }
            if (mpg != null) {
                res.add(mpg);
            }

            List<MT_ProgramDetails> pdet = mtProgramDetailsService.getProgramDetailsByProgramCode(programcode);
            JSONArray programDetailsArray = new JSONArray(); // Renamed to avoid confusion with outer res

            for (MT_ProgramDetails p : pdet) {
                JSONObject obj = new JSONObject();
                obj.put("phase", p.getPhaseid() != null ? p.getPhaseid().getPhaseid() : null);
                obj.put("programDetailid", p.getProgramdetailid());

                String formattedStartDate = "";
                if (p.getStartdate() != null) {
                    formattedStartDate = outputFormat.format(p.getStartdate());
                }
                obj.put("startDate", formattedStartDate);

                String formattedEndDate = "";
                if (p.getEnddate() != null) {
                    formattedEndDate = outputFormat.format(p.getEnddate());
                }
                obj.put("endDate", formattedEndDate);

                obj.put("finalized", p.getFinalized());
                obj.put("closed", p.getClosed());

                List<MT_ProgramMembers> mems = mtProgramMembersService.getProgramMembers(
                        p.getProgramcode().getProgramcode(),
                        p.getPhaseid().getPhaseid());
                JSONArray coos = new JSONArray();
                for (MT_ProgramMembers m : mems) {
                    coos.add(m.getMtuserlogin().getUsername());
                }
                obj.put("coordinator", coos);

                List<Object[]> venues = mProgramsService.getProgramVenuesAndRP(
                        p.getProgramcode().getProgramcode(),
                        p.getPhaseid().getPhaseid());
                JSONArray vens = new JSONArray();
                for (Object[] venue : venues) {
                    JSONObject venueObj = new JSONObject();
                    // venue[2] was officename, venue[4] was venuename, venue[5] was rpname
                    // Based on your snippet:
                    venueObj.put("venueNames", venue[4] != null ? venue[4].toString() : "");
                    venueObj.put("RPNames", venue[5] != null ? venue[5].toString() : "");
                    vens.add(venueObj);
                }
                obj.put("VenuesAndRP", vens);
                programDetailsArray.add(obj);
            }
            res.add(programDetailsArray);

            return ResponseEntity.ok(res);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new JSONArray());
        }
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role A (Local-admin) & Z (Principal-director)
     * Endpoint tied with 'Close Program' process (processcode = 17)
     */
    @PostMapping("/phase/close")
    public ResponseEntity<String> closePhase(
            @RequestParam("phaseid") String phaseId,
            @RequestParam("closingreport") String closingReport,
            HttpServletRequest request
        ) {
        MT_Userlogin user = null;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (RuntimeException ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!(
            List.of("A", "Z").contains(userRole) &&
            mProcessesService.isProcessGranted(user.getUsercode(), 17)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }

        // validating required parameters
        if (phaseId == null || phaseId.isEmpty())
            return ResponseEntity.badRequest().body("phaseid cannot be null or empty");
        if (phaseId.trim().length() > 3)
            return ResponseEntity.badRequest().body("Invalid phaseid");
        if (!mtProgramDetailsService.existsByPhaseid(phaseId))
            return ResponseEntity.badRequest().body("Phase with phaseid " + phaseId + " does not exist");

        // validating closingReport
        if (closingReport == null || closingReport.isEmpty())
            return ResponseEntity.badRequest().body("closingReport cannot be null or empty");
        if (closingReport.trim().length() > 300)
            return ResponseEntity.badRequest().body("closingreport must not exceed 300 characters");


        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
        try {
            if (mtProgramDetailsService.closePhase(phaseId, closingReport)) {
                persistenceLogger.info("M_Phases with phaseid {} closed successfully by userid {}", phaseId, user.getUserid());
                audittrailService.logAuditTrail(auditMap, user.getUserid(), "M_Phases with phaseid " + phaseId + " closed successfully");

                return ResponseEntity.ok("1");
            }

            throw new PersistenceException();
        } catch (Exception ex) {
            persistenceLogger.info("M_Phases with phaseid {} close failed by userid {}", phaseId, user.getUserid());
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "M_Phases with phaseid " + phaseId + " close failed");


            return ResponseEntity.ok("-1");
        }
    }

    // Unused method
    @PostMapping("/course/reopen")
    public ResponseEntity<String> reopenCourse(@RequestParam("coursecode") String programdetailid) {
        if (mtProgramDetailsService.reopenPhase(programdetailid))
            return ResponseEntity.ok("1");
        return ResponseEntity.ok("-1");
    }

    @PostMapping("/getMoreOngoingProgramList")
    @ResponseBody
    public List<Object[]> getMoreOngoingProgramList(Model model) {
        List<Object[]> districtlist = null;
        districtlist = mtProgramDetailsService.getMoreOngoingProgramList(0);
        return districtlist;
    }

    @PostMapping("/getMoreUpcomingProgramList")
    @ResponseBody
    public List<Object[]> getMoreUpcomingProgramList(Model model) {
        List<Object[]> districtlist = null;
        districtlist = mtProgramDetailsService.getMoreUpcomingProgramList(0);
        return districtlist;
    }

    @PostMapping("/getMoreCompletedProgramList")
    @ResponseBody
    public ResponseEntity<List<Object[]>> getmoreCompletedProgramList(Model model) {
        return ResponseEntity.ok(mtProgramDetailsService.getMoreCompletedProgramList(0));
    }

    /*
     * Secured endpoint
     * Endpoint exclusive to role Z (Principal-Director)
     * Endpoint tied with 'Manage Program' process (processcode = 7)
     */
    @PostMapping("/principal-director/accept")
    public ResponseEntity<String> acceptProgram(
            @RequestParam(value = "file1", required = false) MultipartFile file,
            @RequestParam("aprogramdetailid") String programdetailid,
            HttpServletRequest request) {

        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!(userRole.equalsIgnoreCase("Z") &&
                mProcessesService.isProcessGranted(user.getUsercode(), 7))) {
            throw new MyAuthorizationDeniedException(
                    ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }

        if (programdetailid == null || programdetailid.isBlank())
            return ResponseEntity.badRequest().body("aprogramdetailid field missing");

        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);

        try {

            MT_ProgramDetails programDetails = mtProgramDetailsService.getProgramDetails(programdetailid.trim());

            if (programDetails == null) {
                return ResponseEntity.ok("3");
            }

            // Get current phase number
            int currentPhaseNo = Integer.parseInt(programDetails.getPhaseid().getPhaseno());

            // Check if file is present
            boolean isFilePresent = (file != null && !file.isEmpty());

            // Only return error "1" if it is Phase 1 AND File is missing
            if (currentPhaseNo == 1 && !isFilePresent) {
                return ResponseEntity.ok("1");
            }

            // UPDATE FIELDS
            programDetails.setApprovaldate(new Date());
            programDetails.setFinalized("Y");
            programDetails.setMtuserloginapproval(user);

            // Only set file bytes if the file actually exists
            if (isFilePresent) {
                programDetails.setApprovalletter(file.getBytes());
            }

            // SAVE TO DATABASE
            mtProgramDetailsService.approveProgram(programDetails);

            persistenceLogger.info("MT_ProgramDetails with programdetailid {} approved successfully by userid {}", programdetailid, user.getUserid());
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "mt_programdetails with programdetailid " + programdetailid + " approved successfully");

            // NOTIFICATION LOGIC (Only for Phase 1)
            if (currentPhaseNo == 1) {
                try {
                    Object[] info = mtProgramDetailsService.getProgramInfoForNotification(programdetailid);

                    if (info != null) {
                        String progName = (String) info[0];
                        String phaseNo = (String) info[1];
                        String progCode = (String) info[2];
                        String phaseId = (String) info[3];

                        T_Notifications noti = new T_Notifications();
                        noti.setNotification("Please Add The Program Details for " + progName + ", Phase " + phaseNo + "!!");
                        noti.setReceivertype("SPECIFIC");
                        noti.setUsercode(user);
                        noti.setOfficecode(user.getMoffices());
                        noti.setEntrydate(new java.util.Date());
                        noti.setLink("/program/manage");

                        MT_ProgramMembers headCoorMember = mtProgramMembersService.getHeadCoordinator(progCode, phaseId);

                        if (headCoorMember != null) {
                            Set<MT_Userlogin> receivers = new HashSet<>();
                            receivers.add(headCoorMember.getMtuserlogin());
                            noti.setReceivers(receivers);

                            String res = tNotificationsService.addNotifications(noti);

                            persistenceLogger.info("Notification sent to Head Coordinator for Program {} Phase 1", progCode);
                        }
                    }
                } catch (Exception notiEx) {
                    persistenceLogger.error("Approval successful, but notification failed: {}", notiEx.getMessage());
                }
            }

            return ResponseEntity.ok("2"); // Success

        } catch (IOException ex) {
            genericLogger.error(ex.toString());
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "approve failed (IO Error)");
            return ResponseEntity.badRequest().body("Invalid file");
        } catch (Exception ex) {
            persistenceLogger.error("MT_ProgramDetails approve failed.\nMessage: {}\nUserid: {}", ex.getMessage(), user.getUserid(), ex);
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "approve failed");
            return ResponseEntity.ok("3"); // General failure
        }
    }

    /*
     * Secured endpoint
     * Endpoint exclusive to role Z (Principal-Director)
     * Endpoint tied with 'Manage Program' process (processcode = 7)
     */
    @PostMapping("/principal-director/reject")
    public ResponseEntity<String> rejectProgram(
            @RequestParam("file2") MultipartFile file,
            @RequestParam("rprogramdetailid") String programdetailid,
            @RequestParam("rejectremark") String rejectremark,
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
            userRole.equalsIgnoreCase("Z") &&
            mProcessesService.isProcessGranted(user.getUsercode(), 7)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }

        // Validating fields
        if (file == null || file.isEmpty())
            return ResponseEntity.ok("1");

        if (programdetailid == null || programdetailid.isBlank() || rejectremark == null || rejectremark.isBlank())
            return ResponseEntity.badRequest().body("Required fields are missing");

        MT_ProgramDetails programDetails = new MT_ProgramDetails();
        programDetails.setProgramdetailid(programdetailid.trim());
        programDetails.setRejectiondate(new Date());
        programDetails.setFinalized("R");
        programDetails.setMtuserloginrejection(user);
        programDetails.setRejectionremarks(rejectremark.trim());

        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
        try {
            programDetails.setRejectionletter(file.getBytes());
            mtProgramDetailsService.rejectProgram(programDetails);
            persistenceLogger.info("MT_ProgramDetails with programdeatailid {} rejected successfully by userid {}", programdetailid, user.getUserid());
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "mt_programdetails with programdetailid " + programdetailid + " rejected successfully");

            return ResponseEntity.ok("2");
        } catch (IOException ex) {
            genericLogger.error(ex.toString());
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "mt_programdetails with programdetailid " + programdetailid + " reject failed");
            
            return ResponseEntity.badRequest().body("Invalid file");
        } catch (Exception ex) {
            persistenceLogger.error("MT_ProgramDetails reject failed.\nMessage: {}\nUserid: {}", ex.getMessage(), user.getUserid(), ex);
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "mt_programdetails with programdetailid " + programdetailid + " reject failed");
            
            return ResponseEntity.ok("");
        }
    }

    /*
     * Secured endpoint
     * Endpoint exclusive to role Z (Principal-Director)
     * Endpoint tied with 'Manage Program' process (processcode = 7)
     */
    @GetMapping("/principal-director/delete")
    public ResponseEntity<String> deleteProgram(@RequestParam("programcode") String programcode, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!(
            userRole.equalsIgnoreCase("Z") &&
            mProcessesService.isProcessGranted(user.getUsercode(), 7)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }

        // Validating fields
        if (programcode == null || programcode.isBlank() || !mProgramsService.existsByProgramcode(programcode.trim()))
            return ResponseEntity.badRequest().body("Invalid programcode or Program does not exist");

        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
        try {
            mtProgramDetailsService.deleteProgramAndRelatedEntities(programcode.trim());
            persistenceLogger.info("MT_ProgramDetails with programcode {} deleted successfully by userid {}", programcode, user.getUserid());
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "mt_programdetails with programcode " + programcode + " deleted successfully");

            return ResponseEntity.ok("1");
        } catch (Exception ex) {
            persistenceLogger.error("MT_ProgramDetails delete failed.\nMessage: {}\nUserid: {}", ex.getMessage(), user.getUserid(), ex);
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "mt_programdetails with programcode " + programcode + " delete failed");

            return ResponseEntity.ok("");
        }
    }

    @GetMapping("/view-approval")
    public void viewApprovalLetter(@RequestParam("pdid") String programdetailid, HttpServletResponse response)
            throws IOException {
        MT_ProgramDetails programDetails = mtProgramDetailsService.getProgramDetailsByProgramdetailid(programdetailid);

        byte[] approvalLetter = programDetails.getApprovalletter();

        if (approvalLetter != null) {
            response.reset();
            response.setContentType("application/pdf"); // Adjust based on the file type
            response.setContentLength(approvalLetter.length);

            try (OutputStream out = response.getOutputStream()) {
                out.write(approvalLetter);
                out.flush(); // Ensure all data is sent
            } catch (IOException e) {
                response.sendRedirect("/error/500");
            }
        } else {
            response.sendRedirect("/error/404");
        }
    }

    // TODO @Abanggi: Handle IOException in ControllerAdvice
    // TODO @Abanggi: Change from NOT FOUND to NO CONTENT
    @GetMapping("/view-rejection")
    public void viewRejectionLetter(@RequestParam("pdid") String programdetailid, HttpServletResponse response) throws IOException {
        MT_ProgramDetails programDetails = mtProgramDetailsService.getProgramDetailsByProgramdetailid(programdetailid);

        byte[] rejectionLetter = programDetails.getRejectionletter();

        if (rejectionLetter != null) {
            response.reset();
            response.setContentType("application/pdf"); // Adjust based on the file type
            response.setContentLength(rejectionLetter.length);

            try (OutputStream out = response.getOutputStream()) {
                out.write(rejectionLetter);
                out.flush(); // Ensure all data is sent
            } catch (IOException e) {
                response.sendRedirect("/error/500");
            }
        } else {
            response.sendRedirect("/error/404");
        }
    }

    /*
     * Secured endpoint
     * Endpoint exclusive to role A and U (Local Admin and Coordinator/Faculty)
     * Endpoint tied to Add/Edit Program process
     */
    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<String> updateProgramDetails(
            // Program Basics
            @RequestParam(value = "programcode") String programcode,
            @RequestParam(value = "programname") String programname,
            @RequestParam(value = "programid", required = false) String programid,
            @RequestParam(value = "programdescription") String programdescription,
            @RequestParam(value = "programcategory", required = false) String programcategory,
            // Approved Params
            @RequestParam(value = "evenues", required = false) String[] venues,
            @RequestParam(value = "estartdate", required = false) String startdate,
            @RequestParam(value = "eenddate", required = false) String enddate,
            @RequestParam(value = "elastdate", required = false) String lastdate,
            @RequestParam(value = "ecourseclosedate", required = false) String courseclosedate,
            @RequestParam(value = "ecoordinators", required = false) String[] coordinatorsApproved,
            // Submitted Params
            @RequestParam(value = "ecoordinators111", required = false) String[] coordinatorsSubmitted,
            // Common Params
            @RequestParam(value = "ephasedescription", required = false) String phasedescription,
            @RequestParam(value = "ephaseid") String phaseid,
            HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (AuthenticationCredentialsNotFoundException ex) {
            throw new MyAuthenticationCredentialsNotFoundException("Auth Failed", "json");
        }

        // Authorization Check
        String userRole = user.getRole().getRoleCode().toUpperCase();
        if (!(List.of("A", "U").contains(userRole) &&
                mProcessesService.isProcessGranted(user.getUsercode(), 2))) {
            throw new MyAuthorizationDeniedException("Access Denied", "json");
        }

        // Validation
        if (programname == null || programname.isEmpty()) {
            return ResponseEntity.badRequest().body("3");
        }

        // 1. Setup M_Programs Object
        M_Programs mprogram = new M_Programs();
        mprogram.setProgramcode(programcode);
        mprogram.setProgramname(programname);
        mprogram.setProgramid(programid);
        mprogram.setProgramdescription(programdescription);
        mprogram.setClosed("N");

        // 2. Set User & Office Info
        M_Offices mo = new M_Offices();
        mo.setOfficecode(user.getMoffices().getOfficecode());
        mprogram.setMoffices(mo);

        MT_Userlogin log = new MT_Userlogin();
        log.setUsercode(user.getUsercode());
        mprogram.setUsercode(log);

        // 3. Handle Program Category (Restored from Legacy Code)
        // If programcategory is provided (Approved flow) or if you want a default
        if (programcategory != null && !programcategory.isEmpty()) {
            M_CourseCategories cc = new M_CourseCategories();
            cc.setCoursecategorycode(programcategory);
            // Note: If you need to check if it exists or default to "1" like legacy code:
            // if(!repo.existsById(programcategory)) { cc.setCoursecategorycode("1"); }
            mprogram.setMcoursecategories(cc);
        } else {
            // In legacy "Submitted" flow, category wasn't touched.
            // If you need to preserve existing category, you might need to fetch the entity from DB first
            // instead of creating 'new M_Programs()'.
            // For now, assuming null is safe or handled by DB default.
        }

        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
        boolean res = false;

        try {
            // 4. Determine Flow based on Start Date presence
            if (startdate == null || startdate.trim().isEmpty()) {

                // --- SUBMITTED FLOW ---
                // In submitted flow, usually 'ecoordinators111' is sent
                String[] coordsToUse = (coordinatorsSubmitted != null) ? coordinatorsSubmitted : coordinatorsApproved;

                res = mtProgramDetailsService.updateProgramDetailsSubmitted(
                        mprogram, coordsToUse, phasedescription, phaseid);

                persistenceLogger.info("Submitted Program updated by {}", user.getUserid());

            } else {

                // --- APPROVED FLOW ---
                // In approved flow, usually 'ecoordinators' (or ecoordinators111 in legacy) is sent
                // Use the one that is not null
                String[] coordsToUse = (coordinatorsApproved != null) ? coordinatorsApproved : coordinatorsSubmitted;

                res = mtProgramDetailsService.updateProgramDetailsApproved(
                        mprogram, venues, coordsToUse, phasedescription,
                        startdate, enddate, lastdate, courseclosedate, phaseid);

                persistenceLogger.info("Approved Program updated by {}", user.getUserid());
            }

            if (res) {
                audittrailService.logAuditTrail(auditMap, user.getUserid(), "MT_ProgramDetails updated successfully");
            }

            return ResponseEntity.ok(res ? "1" : "2");

        } catch (Exception e) {
            e.printStackTrace();
            // Log error
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "MT_ProgramDetails update failed");
            // Return "2" so the frontend shows "Failed to update" message instead of crashing
            return ResponseEntity.ok("2");
        }
    }

    /*
     * Secured endpoint
     * Endpoint exclusive to role A and U (Local Admin and Coordinator/Faculty)
     * Endpoint tied to Add/Edit Program process
     */
    @PostMapping("/save")
    @ResponseBody
    public ResponseEntity<String> saveProgramDetails(
            @RequestParam(value = "programcattwo") String programcattwo,
            @RequestParam(value = "programDetailProgcode") String pcode,
            @RequestParam(value = "programDetailPhaseID") String phaseid,
            @RequestParam(value = "venuesDET", required = false) String[] venues,
            @RequestParam(value = "startdateDET") String startdate,
            @RequestParam(value = "enddateDET") String enddate,
            @RequestParam(value = "lastdateDET") String lastdate,
            @RequestParam(value = "courseclosedateDET") String courseclosedate,
            @RequestParam(value = "programcategory") String programcategory,
            @RequestParam(value = "focusareas", required = false) String[] focusareas,
            @RequestParam(value = "targetgroup", required = false) String[] targetgroup,
            @RequestParam(value = "stages", required = false) String[] stages,
            @RequestParam(value = "budget") String budget,
            @RequestParam(value = "objectives") String objectives,
            @RequestParam(value = "methodology") String methodology,
            @RequestParam(value = "tools") String tools,
            @RequestParam(value = "kpindicators") String kpindicators,
            @RequestParam(value = "outcomes") String outcomes,
            @RequestParam(value = "localcoordinator", required = false) List<String> localcoordinator,
            @RequestParam(value = "otherfocusarea", required = false) String otherfocusarea,
            @RequestParam(value = "othertargetgroup", required = false) String othertargetgroup,
            @RequestParam(value = "otherstages", required = false) String otherstages,
            HttpServletRequest request) {
        String response = "";
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (AuthenticationCredentialsNotFoundException ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!(List.of("A", "U").contains(userRole) &&
                mProcessesService.isProcessGranted(user.getUsercode(), 2))) {
            throw new MyAuthorizationDeniedException(
                    ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }

        try {
            HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);

            M_CourseCategories cc = mCourseCategoriesService.getCourseCategoryById(programcategory);
            boolean res = mtProgramDetailsService.saveProgramDetails(pcode,phaseid,venues, startdate, enddate, lastdate, courseclosedate,cc,programcattwo);

            if (res) {
                persistenceLogger.info("MT_ProgramDetails saved successfully by userid {}", user.getUserid());
                audittrailService.logAuditTrail(auditMap, user.getUserid(), "MT_ProgramDetails saved successfully");

                boolean res2 = mPhaseMoreDetailsService.savePhaseMoreDetails(focusareas, targetgroup, stages, budget,
                        objectives, methodology, tools, kpindicators, outcomes, pcode, phaseid,
                        otherfocusarea, othertargetgroup, otherstages);

                if(res2) {
                    persistenceLogger.info("M_PhasesMoreDetails saved successfully by userid {}", user.getUserid());
                    audittrailService.logAuditTrail(auditMap, user.getUserid(), "M_PhasesMoreDetails saved successfully");
                    response = "2";
                }
                else {
                    persistenceLogger.error("M_PhasesMoreDetails save unsuccessful by userid {}", user.getUserid());
                    audittrailService.logAuditTrail(auditMap, user.getUserid(), "M_PhasesMoreDetails save failed");
                    response = "4";
                }
            } else {
                persistenceLogger.error("MT_ProgramDetails save unsuccessful by userid {}", user.getUserid());
                audittrailService.logAuditTrail(auditMap, user.getUserid(), "MT_ProgramDetails save failed");
                response = "4";
            }

            // Logic to handle List of coordinators
            if(res && localcoordinator != null && !localcoordinator.isEmpty()){
                // Filter out any empty strings or nulls that might come from select2
                List<String> validCoordinators = localcoordinator.stream()
                        .filter(id -> id != null && !id.trim().isEmpty())
                        .collect(Collectors.toList());

                if (!validCoordinators.isEmpty()) {
                    if(mtProgramMembersService.setCoordinatorAsLocalCoordinator(validCoordinators).equals("1")) {
                        persistenceLogger.info("Successfully set coordinators as local coordinator by userid {}", user.getUserid());
                        audittrailService.logAuditTrail(auditMap, user.getUserid(), "Successfully set coordinators as local coordinator");
                        if(response.equals("2")) response = "2";
                    }
                    else {
                        persistenceLogger.info("Failed setting coordinator as local coordinator by userid {}", user.getUserid());
                        audittrailService.logAuditTrail(auditMap, user.getUserid(), "Failed setting coordinator as local coordinator");
                        response = "4";
                    }
                }
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            persistenceLogger.error("Error happened while attempting to save MT_ProgramDetails.\nMessage: {}\nUserid: {}", e.getMessage(), e, user.getUserid());
            throw new RuntimeException(e);
        }
    }

    /*
     * Secured endpoint
     * Endpoint exclusive to role A and U (Local Admin and Coordinator/Faculty)
     */
    @PostMapping("/form-data")
    @ResponseBody
    List<Object[]> getProgramDetailsBasedOnCodeToPopulateForm(@RequestParam(value = "programcode", required = false) String pcode) {
        return mtProgramDetailsService.getProgramDetailsBasedOnCodeToPopulateForm(pcode);
    }
}