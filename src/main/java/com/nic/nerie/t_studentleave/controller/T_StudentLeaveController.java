package com.nic.nerie.t_studentleave.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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
import com.nic.nerie.m_course_academics.service.M_Course_AcademicsService;
import com.nic.nerie.m_processes.service.M_ProcessesService;
import com.nic.nerie.mt_la_usermapping.service.MT_LeaveApplication_UserMappingService;
import com.nic.nerie.mt_programdetails.service.MT_ProgramDetailsService;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.mt_userlogin.service.MT_UserloginService;
import com.nic.nerie.t_notifications.model.T_Notifications;
import com.nic.nerie.t_notifications.service.T_NotificationsService;
import com.nic.nerie.t_participantattendance.model.T_ParticipantAttendance;
import com.nic.nerie.t_studentleave.model.T_StudentLeave;
import com.nic.nerie.t_studentleave.service.T_StudentLeaveService;
import com.nic.nerie.t_students.model.T_Students;
import com.nic.nerie.t_students.service.T_StudentsService;
import com.nic.nerie.utils.ExceptionUtil;
import com.nic.nerie.utils.UtilCommon;

import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/student-leaves")
public class T_StudentLeaveController {
    private final MT_UserloginService mtUserloginService;
    private final T_StudentsService tStudentsService;
    private final T_StudentLeaveService tStudentLeaveService;
    private final MT_LeaveApplication_UserMappingService mtLeaveApplicationUserMappingService;
    private final MT_ProgramDetailsService mtProgramDetailsService;
    private final M_Course_AcademicsService mCourseAcademicsService;
    private final T_NotificationsService tNotificationsService;
    private final M_ProcessesService mProcessesService;
    private final AudittrailService audittrailService;
    private static final Logger persistenceLogger = LoggerFactory.getLogger("DATA_PERSISTENCE_LOGGER");
    
    @Autowired
    public T_StudentLeaveController(
        MT_UserloginService mtUserloginService, 
        T_StudentsService tStudentsService,
        T_StudentLeaveService tStudentLeaveService, 
        MT_LeaveApplication_UserMappingService mtLeaveApplicationUserMappingService, 
        MT_ProgramDetailsService mtProgramDetailsService, 
        M_Course_AcademicsService mCourseAcademicsService, 
        T_NotificationsService tNotificationsService,
        M_ProcessesService mProcessesService,
        AudittrailService audittrailService
        ) {
        this.mtUserloginService = mtUserloginService;
        this.tStudentsService = tStudentsService;
        this.tStudentLeaveService = tStudentLeaveService;
        this.mtLeaveApplicationUserMappingService = mtLeaveApplicationUserMappingService;
        this.mtProgramDetailsService = mtProgramDetailsService;
        this.mCourseAcademicsService = mCourseAcademicsService;
        this.tNotificationsService = tNotificationsService;
        this.mProcessesService = mProcessesService;
        this.audittrailService = audittrailService;
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role T (Student)     
     * Apply For Leave
     */
    @GetMapping("/application")
    public String renderLeaveApplicationPage(Model model, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "Apply For Leave, " + request.getMethod()), "page");
        }

        if (!user.getRole().getRoleCode().equalsIgnoreCase("T")) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "Apply For Leave, " + request.getMethod(), user.getUserid()), "page");
        }

        T_Students student = tStudentsService.findByUsercode(user.getUsercode());
        List<T_StudentLeave> studentLeaves = tStudentLeaveService.getOwnLeaveApplications(student.getStudentid());
        model.addAttribute("student", student);
        model.addAttribute("leavelist", studentLeaves);

        return "pages/t_students/leave-application";
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role A (Local-admin), U (Coordinator-faculty), Z (Principal-Director), T (Student)
     * Student Leave Applications
     */
    // TODO @Abanggi: remove optionality in approvaltype after presentation
    @GetMapping("/view-approval")
    public ResponseEntity<byte[]> getApprovalDocument(@RequestParam("studentleaveid") String studentleaveid, @RequestParam(value = "approvaltype", required = false) String approval) {
        T_StudentLeave studentLeave = tStudentLeaveService.findByStudentleaveid(studentleaveid);
        byte[] document = null;
        /**
            Commented because the entity class was updated
        */
        //if (approval.equals("guardian"))
            document = studentLeave.getGapprovalletter();
        //else if (approval.equals("warden"))

            //document = studentLeave.getWapprovalletter();

        if (document != null)
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
                    .contentLength(document.length).body(document);
        else
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(document);
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role T (Student)     
     * Apply For Leave
     */
    @PostMapping(value = "/submit-application", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> saveLeaveApplication(
            @RequestParam("leavestation") String leaveStation,
            @RequestParam("reasonforleave") String reasonForLeave,
            @RequestParam(value = "buildingno", required = false) String buildingno,    // optional for day scholar
            @RequestParam(value = "roomno", required = false) String roomno,    // optional for day scholar
            @RequestParam("nameofguardian") String nameOfGuardian,
            @RequestParam("guardianrelationship") String guardianRelationship,
            @RequestParam("phnoguardian") String guardianPhoneNumber,
            @RequestParam("requestedfrom") String requestedFrom,
            @RequestParam("requestedto") String requestedTo,
            @RequestParam("file1") MultipartFile guardianApprovalLetter,
            @RequestParam("ds") Boolean isDayScholar,
            HttpServletRequest request
    ) {
        MT_Userlogin user;
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

        if (
                leaveStation == null || leaveStation.isBlank() ||
                        reasonForLeave == null || reasonForLeave.isBlank() ||
                        nameOfGuardian == null || nameOfGuardian.isBlank() ||
                        guardianRelationship == null || guardianRelationship.isBlank() ||
                        guardianPhoneNumber == null || guardianPhoneNumber.isBlank() ||
                        requestedFrom == null || requestedFrom.isBlank() ||
                        requestedTo == null || requestedTo.isBlank() ||
                        guardianApprovalLetter == null || guardianApprovalLetter.isEmpty() ||
                        isDayScholar == null
        )
            return ResponseEntity.status(400).body("Required fields are missing or blank");

        T_Students student = tStudentsService.findByUsercode(user.getUsercode());
        if (student == null) return ResponseEntity.internalServerError().build();

        // preparing T_StudentLeave for persistence
        T_StudentLeave studentLeave = new T_StudentLeave();
        studentLeave.setLeavestation(leaveStation.trim());
        studentLeave.setReasonforleave(reasonForLeave.trim());

        // Only for non-day scholars
        if (isDayScholar != null && !isDayScholar) {
            if (buildingno != null && !buildingno.isBlank() && roomno != null && !roomno.isBlank()) {
                studentLeave.setBuildingno(buildingno.trim());
                studentLeave.setRoomno(roomno.trim());
            } else
                return ResponseEntity.badRequest().body("buildingno and roomno fields are missing");
        }

        studentLeave.setNameofguardian(nameOfGuardian.trim());
        studentLeave.setGuardianrelationship(guardianRelationship.trim());
        studentLeave.setPhnoguardian(guardianPhoneNumber.trim());

        try {
            SimpleDateFormat parser = new SimpleDateFormat("dd-MM-yyyy");
            studentLeave.setRequestedfrom(parser.parse(requestedFrom));
            studentLeave.setRequestedto(parser.parse(requestedTo));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Invalid Date format");
        }

        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
        try {
            studentLeave.setGapprovalletter(guardianApprovalLetter.getBytes());
        } catch (IOException ex) {
            return ResponseEntity.badRequest().body("Invalid guarding approval letter");
        }

        // Day scholars don't need warden approval
        if (isDayScholar)
            studentLeave.setIswardenapproved("2");  // For day scholar don't display the warden button

        studentLeave.setStudentid(student);
        studentLeave.setApplicationdate(new Date());

        try {
            if ((studentLeave = tStudentLeaveService.saveLeaveApplication(studentLeave)) != null) {
                persistenceLogger.info("T_StudentLeave with studentleaveid {} saved successfully by userid {}", studentLeave.getStudentleaveid(), user.getUserid());
                audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_studentleave with studentleaveid " + studentLeave.getStudentleaveid() + " saved successfully");

                return ResponseEntity.ok("0");
            } else
                throw new PersistenceException();
        } catch (Exception ex) {
            persistenceLogger.error("T_StudentLeave save failed.\nMessage: {}\nUserid: {}", ex.getMessage(), user.getUserid(), ex);
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_studentLeave save failed");
        }

        return ResponseEntity.ok("-1"); // something went wrong...
    }

    @GetMapping("/ReportStudentLeaveList")
    @ResponseBody
    public List<Object[]> getReportStudentLeaveList(
            @RequestParam("fystart") String fystart,
            @RequestParam("fyend") String fyend,
            @RequestParam("status") String status,
            @RequestParam("course") String course,
            @RequestParam(value = "semester", required = false) String semester,
            @RequestParam(value = "sphaseid", required = false) String sphaseid,
            @RequestParam("approvedstatus") String approvedstatus
    ) {
        List<Object[]> t1 = new ArrayList<>();
        t1 = tStudentLeaveService.getFilteredStudentLeaveList(status, fystart, fyend, sphaseid, semester, course, approvedstatus);
        return t1;
    }

    @PostMapping("/getFYCourseList")
    @ResponseBody
    public List<Object[]> getFYCourseList(@RequestParam(value = "fystart", required = false) String fystart, @RequestParam(value = "fyend", required = false) String fyend) {
        List<Object[]> ilist = null;
        MT_Userlogin user = mtUserloginService.getUserloginFromAuthentication(
                SecurityContextHolder.getContext().getAuthentication());
        ilist = mtProgramDetailsService.listMcoursesforRPFY(user.getUsercode(), user.getMoffices().getOfficecode(), fystart, fyend, user.getUserrole());
        return ilist;
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role A (Local-admin), U (Coordinator-faculty) & Z (Principal-director)
     * 'Student Leave Applications' process (processcode = 44)
     */
    @GetMapping("/approve-student-leave")
    public String renderApproveLeavePage(@ModelAttribute("tparticipantattendance") T_ParticipantAttendance tpattendance, Model model, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "Student Leave Applications, " + request.getMethod()), "page");
        }

        String userRole = user.getRole().getRoleCode().toUpperCase();
        String officeCode = user.getMoffices().getOfficecode();

        if (!(
            List.of("A", "U", "Z").contains(userRole) &&
            mProcessesService.isProcessGranted(user.getUsercode(), 44)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "Student Leave Applications, " + request.getMethod(), user.getUserid()), "page");
        }
        
        Integer rolecode = mtLeaveApplicationUserMappingService.getLAUserMapRolecode(user.getUsercode());
        model.addAttribute("rolecode", rolecode); // Leave Application Role
        model.addAttribute("userRole", userRole); // User Role

        if(user.getUserrole().equals("Z"))
            model.addAttribute("leavelist", tStudentLeaveService.getPStudentLeaveApplications(officeCode)); //PRINCIPAL
        else if(rolecode == 1)
            model.addAttribute("leavelist", tStudentLeaveService.getPMStudentLeaveApplications(officeCode)); //PRINCIPAL MALE
            //model.addAttribute("leavelist", tStudentLeaveService.getMStudentLeaveApplications()); //MALE WARDEN
        else if(rolecode == 2)
            model.addAttribute("leavelist", tStudentLeaveService.getPFStudentLeaveApplications(officeCode)); //PRINCIPAL FEMALE
            //model.addAttribute("leavelist", tStudentLeaveService.getFStudentLeaveApplications()); //FEMALE WARDEN
        else if(rolecode == 3)
            model.addAttribute("leavelist", tStudentLeaveService.getPStudentLeaveApplications(officeCode)); //PRINCIPAL
            //model.addAttribute("leavelist", tStudentLeaveService.getDStudentLeaveApplications()); //DEAN
        else if(rolecode == 4)
            model.addAttribute("leavelist", tStudentLeaveService.getPStudentLeaveApplications(officeCode)); //PRINCIPAL
            //model.addAttribute("leavelist", tStudentLeaveService.getCWStudentLeaveApplications()); //CHIEF WARDEN

        if("A".equals(user.getUserrole())) {
            model.addAttribute("layoutPath", "layouts/local-admin-layout");
        } else if ("U".equals(user.getUserrole())) {
            model.addAttribute("layoutPath", "layouts/coordinator-faculty-layout");
        } else {
            model.addAttribute("layoutPath", "layouts/principal-director-layout");
        }

        return "pages/approve-student-leave";
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role A (Local-admin), U (Coordinator-faculty) & Z (Principal-director)
     * 'Student Leave Applications' process (processcode = 44)
     */
    @GetMapping("/view-student-leave-history")
    public String viewStudentLeaveHistory(@RequestParam(value = "aid") String sid, Model model, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "Student Leave History, " + request.getMethod()), "page");
        }

        String userRole = user.getRole().getRoleCode().toUpperCase();
        String officeCode = user.getMoffices().getOfficecode();

        if (!(
                List.of("A", "U", "Z").contains(userRole) &&
                        mProcessesService.isProcessGranted(user.getUsercode(), 44)
        )) {
            throw new MyAuthorizationDeniedException(
                    ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "Student Leave Applications History, " + request.getMethod(), user.getUserid()), "page");
        }

        // STUDENT DETAILS
        T_Students students = tStudentsService.findByStudentid(sid, officeCode);

        String semPhaseId;
        if ("1".equals(students.getIsshortterm())) {
            semPhaseId = students.getSphaseid().getSphaseid();
        } else {
            semPhaseId = students.getSemestercode().getSemestercode();
        }

        // LEAVE DATA
        List<T_StudentLeave> leavelist = tStudentLeaveService.getOwnLeaveApplicationsCurrentSem(students.getStudentid(), students.getIsshortterm(), semPhaseId);

        List<T_StudentLeave> allleaves = tStudentLeaveService.getOwnLeaveApplications(students.getStudentid());

        // DISPLAY NAME
        String name = "";
        if (!leavelist.isEmpty()) {
            T_Students s = leavelist.get(0).getStudentid();
            name = s.getFname() + " " + s.getLname() + "'s ";
        }

        // MODEL ATTRIBUTES
        model.addAttribute("leavelist", leavelist);
        model.addAttribute("allleaves", allleaves);
        model.addAttribute("name", name);
        model.addAttribute("student", students);
        model.addAttribute("leavecount", tStudentLeaveService.countLeaveDaysWithoutWeekends(students.getStudentid(), students.getIsshortterm(), semPhaseId));

        if("A".equals(user.getUserrole())) {
            model.addAttribute("layoutPath", "layouts/local-admin-layout");
        } else if ("U".equals(user.getUserrole())) {
            model.addAttribute("layoutPath", "layouts/coordinator-faculty-layout");
        } else {
            model.addAttribute("layoutPath", "layouts/principal-director-layout");
        }

        // This line sets the active menu for child page that belong to the "Student Leaves" section.
        model.addAttribute("activeMenuItem", "/student-leaves/approve-student-leave");

        return "pages/student-leave-history";
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role A (Local-admin), U (Coordinator-faculty) & Z (Principal-director)
     * 'Delete Student Leave Applications' process (processcode = 44)
     */
    @PostMapping("/deleteStudentLeave")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteStudentLeave(@RequestParam("leaveid") String leaveid, Model model, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();

        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "Student Leave History, " + request.getMethod()), "page");
        }

        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!(
                List.of("A", "U", "Z").contains(userRole) &&
                        mProcessesService.isProcessGranted(user.getUsercode(), 44)
        )) {
            throw new MyAuthorizationDeniedException(
                    ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "Student Leave Applications History, " + request.getMethod(), user.getUserid()), "page");
        }

        boolean deleted = tStudentLeaveService.deleteStudentLeaveApplication(leaveid);

        if (deleted) {
            response.put("status", "success");
            response.put("message", "Student leave deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "failure");
            response.put("message", "Unable to delete student leave");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role A (Local-admin), U (Coordinator-faculty)
     * 'Student Leave Report' process (processcode = 45)
     */
    @GetMapping("/student-leave-reports")
    public String renderStudentLeaveReports(Model model, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "Student Leave Report, " + request.getMethod()), "page");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!(
            List.of("A", "U").contains(userRole) &&
            mProcessesService.isProcessGranted(user.getUsercode(), 45)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "Student Leave Report, " + request.getMethod(), user.getUserid()), "page");
        }

        model.addAttribute("leavelist", tStudentLeaveService.getAllStudentLeaveApplications());
        model.addAttribute("fylist", mtProgramDetailsService.getFinancialYearsByOfficeCode(user.getMoffices().getOfficecode()));
        model.addAttribute("programlist", mCourseAcademicsService.getAllCourseAcademics());

        if("A".equals(user.getUserrole())) {
            model.addAttribute("layoutPath", "layouts/local-admin-layout");
        } else {
            model.addAttribute("layoutPath", "layouts/coordinator-faculty-layout");
        }
        
        return "pages/student-leave-reports";
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role A (Local-admin), U (Coordinator-faculty) & Z (Principal-Director)
     * Endpoint tied with 'Student Leave Applications' process (processcode = 44)
     */
    @GetMapping("/view-leave-application-details")
    public String renderViewLeaveApplicationDetailsPage(@ModelAttribute("studentleave") T_StudentLeave sl,
                                                        Model model, String aid,
                                                        @RequestParam(value = "origin") String origin,
                                                        HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "page");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (
            !List.of("A", "U", "Z").contains(userRole)
            && mProcessesService.isProcessGranted(user.getUsercode(), 44)
        ) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "page");
        }

        model.addAttribute("leavedetails", tStudentLeaveService.getStudentLeaveDetails(aid));
        // model.addAttribute("role", user.getUserrole().equals("Z"));
        model.addAttribute("role", user.getUserrole());
        Integer rolecode = mtLeaveApplicationUserMappingService.getLAUserMapRolecode(user.getUsercode());
        model.addAttribute("rolecode", rolecode);

        if ("A".equals(user.getUserrole())) {
            model.addAttribute("layoutPath", "layouts/local-admin-layout");
        } else if ("U".equalsIgnoreCase(user.getUserrole())) {
            model.addAttribute("layoutPath", "layouts/coordinator-faculty-layout");
        } else {
            model.addAttribute("layoutPath", "layouts/principal-director-layout");
        }

        if ("report".equals(origin)) {
            // This line sets the active menu for child page that belong to the "Student Leave Report" section.
            model.addAttribute("activeMenuItem", "/student-leaves/student-leave-reports");
        } else {
            // This line sets the active menu for child page that belong to the "Student Leave Applications" section.
            model.addAttribute("activeMenuItem", "/student-leaves/approve-student-leave");
        }

        return "pages/view-leave-application-details";
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role A (Local-admin), U (Coordinator-faculty) & Z (Principal-Director)
     * Endpoint tied with 'Student Leave Applications' process (processcode = 44)
     */
    @PostMapping("/approveLeaveApplication")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public String approveLeaveApplication(@RequestParam("studentleaveid") String slid,
                                          @RequestParam("rolecode") Integer rolecode,
                                          HttpServletRequest request) {
        String res = "-1";
        try {
            MT_Userlogin user = mtUserloginService.getUserloginFromAuthentication();
            T_StudentLeave sl = tStudentLeaveService.getStudentLeaveDetails(slid);
            if(user.getUserrole().equals("Z") && rolecode==9){ // cause principal can also be dean, same id with 2 roles(so approve button can approve as a dean or princi)
                // User Role "Z" & RoleCode "9" -> Principal // 9 is From html page not from table LA Mapping, So Principal can approve as Dean or Principal
                sl.setIsapproved("1");
                sl.setUsercode(user);
                sl.setActiontakendate(new java.util.Date());

                T_Notifications noti = new T_Notifications();
                noti.setNotification("Your Leave Application from "+ sl.getRequestedfrom() +" to "+ sl.getRequestedto() +" has been Approved!!");
                noti.setReceivertype("SPECIFIC");
                noti.setUsercode(user);
                noti.setOfficecode(user.getMoffices());
                noti.setEntrydate(new java.util.Date());
                //noti.setReceiverusercode(sl.getStudentid().getUsercode()); -> no longer in use

                // Get the student user
                MT_Userlogin studentUser = sl.getStudentid().getUsercode();

                if (studentUser != null) {
                    Set<MT_Userlogin> receivers = new HashSet<>();
                    receivers.add(studentUser);
                    noti.setReceivers(receivers);
                }

                res = tNotificationsService.addNotifications(noti);
                if(res.equalsIgnoreCase("-1"))
                    System.out.println("SOMETHING WENT WRONG approveleaveapplication noti");
            }
            else if(rolecode == 1 || rolecode == 2 || rolecode == 4) {
                // 1 -> Male Warden
                // 2 -> Female Warden
                // 4 -> Chief Warden
                sl.setIswardenapproved("1");
            }
            else if(rolecode == 3) {
                // 3 -> Dean
                sl.setIsdeanapproved("1");
            } else {
                throw new AuthorizationDeniedException(user.getUserid());
            }

            res = tStudentLeaveService.uploadStudentLeaveApplication(sl);
        } catch (AuthenticationCredentialsNotFoundException ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        } catch (AuthorizationDeniedException ex) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), ex.getMessage()), "json");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return res;
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role A (Local-admin), U (Coordinator-faculty) & Z (Principal-Director)
     * Endpoint tied with 'Student Leave Applications' process (processcode = 44)
     */
    @PostMapping("/rejectLeaveApplication")
    @ResponseBody
    public ResponseEntity<String> rejectLeaveApplication(
            @RequestParam(value = "studentleaveid", required = false) String slid,
            @RequestParam(value = "rejectionreason", required = false) String rejectionReason,
            @RequestParam(value = "rolecode", required = false) Integer rolecode,
            HttpServletRequest request) {

        // Check if rejectionReason is empty
        if (rejectionReason == null || rejectionReason.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("-2");
        }

        try {
            MT_Userlogin user = mtUserloginService.getUserloginFromAuthentication();
            T_StudentLeave sl = tStudentLeaveService.getStudentLeaveDetails(slid);

            if ((user.getUserrole().equals("Z")) && rolecode == 9) { // Also Principal acting as dean
                sl.setIsapproved("0");
            } else if (rolecode == 1 || rolecode == 2 || rolecode == 4) {
                sl.setIswardenapproved("0");
            } else if (rolecode == 3) {
                sl.setIsdeanapproved("0");
            } else {
                throw new AuthorizationDeniedException(user.getUserid());
            }

            sl.setActiontakendate(new java.util.Date());
            sl.setUsercode(user);
            sl.setRejectionreason(rejectionReason);
            String res = tStudentLeaveService.uploadStudentLeaveApplication(sl);
            return ResponseEntity.ok(res);

        } catch (AuthenticationCredentialsNotFoundException ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        } catch (AuthorizationDeniedException ex) {
            throw new MyAuthorizationDeniedException(
                    ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), ex.getMessage()), "json");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request.");
        }
    }

}
