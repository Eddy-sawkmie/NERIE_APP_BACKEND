package com.nic.nerie.t_students.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nic.nerie.audittrail.service.AudittrailService;
import com.nic.nerie.exceptions.MyAuthenticationCredentialsNotFoundException;
import com.nic.nerie.exceptions.MyAuthorizationDeniedException;
import com.nic.nerie.m_course_academics.model.M_Course_Academics;
import com.nic.nerie.m_course_academics.service.M_Course_AcademicsService;
import com.nic.nerie.m_departments.model.M_Departments;
import com.nic.nerie.m_departments.service.M_DepartmentsService;
import com.nic.nerie.m_offices.model.M_Offices;
import com.nic.nerie.m_processes.service.M_ProcessesService;
import com.nic.nerie.m_semesters.model.M_Semesters;
import com.nic.nerie.m_semesters.service.M_SemestersService;
import com.nic.nerie.m_shortterm_phases.model.M_ShortTerm_Phases;
import com.nic.nerie.m_shortterm_phases.service.M_ShortTerm_PhasesService;
import com.nic.nerie.m_subjects.model.M_Subjects;
import com.nic.nerie.m_subjects.service.M_SubjectService;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.mt_userlogin.service.MT_UserloginService;
import com.nic.nerie.mt_userloginrole.model.MT_UserloginRole;
import com.nic.nerie.mt_userloginrole.service.MT_UserloginRoleService;
import com.nic.nerie.t_alumni.model.T_Alumni;
import com.nic.nerie.t_alumni.service.T_AlumniService;
import com.nic.nerie.t_student_subject.model.T_Student_Subject;
import com.nic.nerie.t_student_subject.service.T_Student_SubjectService;
import com.nic.nerie.t_students.model.T_Students;
import com.nic.nerie.t_students.service.T_StudentsService;
import com.nic.nerie.utils.EmailValidator;
import com.nic.nerie.utils.ExceptionUtil;
import com.nic.nerie.utils.ImageUtil;
import com.nic.nerie.utils.RandomPasswordGenerator;
import com.nic.nerie.utils.UtilCommon;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/students")
public class T_StudentsController {
    private final MT_UserloginService mtUserloginService;
    private final M_SubjectService mSubjectService;
    private final T_StudentsService tStudentsService;
    private final AudittrailService audittrailService;
    private final M_DepartmentsService mDepartmentsService;
    private final M_SemestersService mSemestersService;
    private final M_Course_AcademicsService mCourseAcademicsService;
    private final M_ShortTerm_PhasesService mShortTermPhasesService;
    private final T_AlumniService tAlumniService;
    private final T_Student_SubjectService tStudentSubjectService;
    private final M_ProcessesService mProcessesService;
    private final MT_UserloginRoleService mtUserloginRoleService; 

    private static final Logger logger = LoggerFactory.getLogger(T_StudentsController.class);
    private static final Logger dataAccessLogger = LoggerFactory.getLogger("DATA_ACCESS_LOGGER");
    private static final Logger persistenceLogger = LoggerFactory.getLogger("DATA_PERSISTENCE_LOGGER");

    @Autowired
    public T_StudentsController(
        MT_UserloginService mtUserloginService, 
        T_StudentsService tStudentsService, 
        M_SubjectService mSubjectService, 
        AudittrailService audittrailService, 
        M_DepartmentsService mDepartmentsService, 
        M_SemestersService mSemestersService, 
        M_Course_AcademicsService mCourseAcademicsService, 
        M_ShortTerm_PhasesService mShortTermPhasesService, 
        T_AlumniService tAlumniService, 
        T_Student_SubjectService tStudentSubjectService,
        M_ProcessesService mProcessesService,
        MT_UserloginRoleService mtUserloginRoleService
    ) {
        this.mtUserloginService = mtUserloginService;
        this.mSubjectService = mSubjectService;
        this.tStudentsService = tStudentsService;
        this.audittrailService = audittrailService;
        this.mDepartmentsService = mDepartmentsService;
        this.mSemestersService = mSemestersService;
        this.mCourseAcademicsService = mCourseAcademicsService;
        this.mShortTermPhasesService = mShortTermPhasesService;
        this.tAlumniService = tAlumniService;
        this.tStudentSubjectService = tStudentSubjectService;
        this.mProcessesService = mProcessesService;
        this.mtUserloginRoleService = mtUserloginRoleService;
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role T (Student)     
     * View Profile
     */
    @GetMapping("/profile")
    @SuppressWarnings("unchecked")
    public String renderUserProfilePage(Model model, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "Add/Edit Designation, " + request.getMethod()), "page");
        }

        model.addAttribute("profilepic", ImageUtil.convertToBase64(user.getUserphotograph()));
        T_Students student = tStudentsService.findByUsercode(user.getUsercode());

        List<Object[]> subjectList = new ArrayList<>();
        JSONArray sublist = new JSONArray();
        if (student.getIsshortterm().equals("0"))
            subjectList = mSubjectService.getGeneralStudentFacultySubjectListLongterm(student.getSemestercode().getSemestercode(),
                    student.getCoursecode().getCoursecode(), student.getStudentid());
        else if (student.getIsshortterm().equals("1"))
            subjectList = mSubjectService.getGeneralStudentFacultySubjectListShortterm(student.getSphaseid().getSphaseid(),
                    student.getCoursecode().getCoursecode(), student.getStudentid());
        List<Object[]> opt = mSubjectService.getoptionalstudentfacultysubjectlist(student.getUsercode().getUsercode(),
                student.getStudentid());

        // Loop for compulsory subjects
        for (Object[] c : subjectList) {
            JSONObject sub = new JSONObject();
            sub.put("sname", c[0]);
            sub.put("fname", c[1]);
            sub.put("fcode", c[2]);
            sub.put("subcode", c[3]);
            sub.put("present", c[4]);
            sub.put("absent", c[5]);
            sub.put("total", c[6]);
            sub.put("stype", "Compulsory Subject");
            sublist.add(sub);
        }

        // loop for optional subjects
        for (Object[] c : opt) {
            JSONObject sub = new JSONObject();
            sub.put("sname", c[0]);   // subjectname
            sub.put("fname", c[1]);   // aggregated teachers' names
            sub.put("subcode", c[2]); // subjectcode
            sub.put("present", c[3]); // present_count
            sub.put("absent", c[4]);  // absent_count
            sub.put("total", c[5]);   // total_count

            // sub.put("fcode", "N/A"); // Optional: if frontend needs a value

            sub.put("stype", "Optional Subject");
            sublist.add(sub);
        }

        model.addAttribute("sublist", sublist);

        return "pages/t_students/profile";
    }

    /*
     * Secured endpoint
     */
    @GetMapping("/info")
    @SuppressWarnings("unchecked")
    public ResponseEntity<JSONObject> getStudentInfo(HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (RuntimeException ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        }

        T_Students student = tStudentsService.findByUsercode(user.getUsercode());
        JSONObject studentJSON = new JSONObject();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            studentJSON.put("student", objectMapper.writeValueAsString(student));
        } catch (Exception e) {
            studentJSON.put("error", "Failed to serialize student data");
        }

        return ResponseEntity.ok(studentJSON);
    }

    // TODO: Change the routes to this endpoint
    @GetMapping("/getmyhomepageattendance")
    public ResponseEntity<List<Object[]>> getMyHomePageAttendance(HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (RuntimeException ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        }

        T_Students student = tStudentsService.findByUsercode(user);

        if (student.getSemestercode() == null || student.getCoursecode() == null)
            return ResponseEntity.ok(Collections.emptyList());

        List<Object[]> attendanceList = tStudentsService.getMyHomePageAttendance(student.getSemestercode(),
                student.getCoursecode(), student.getStudentid());

        return ResponseEntity.ok(attendanceList);
    }

    /*Change Student password*/
    @GetMapping("/change-password")
    public String showChangePasswordPage(Model model, @AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request) {
        if (userDetails == null) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "page");
        }

        String currentUserId = userDetails.getUsername();
        if (currentUserId == null || currentUserId.trim().isEmpty()) {
            return "redirect:/login";
        }

        MT_Userlogin currentUserLogin = mtUserloginService.findByUserId(currentUserId);

        if (currentUserLogin == null) {
            return "redirect:/login?msg=user+not+found";
        }

        model.addAttribute("userlogin", currentUserLogin);

        return "pages/t_students/change-password";
    }

    @PostMapping("/check-old-password")
    @ResponseBody
    public ResponseEntity<String> checkOldPasswordMatch(
            @RequestParam(value = "olduserpassword", required = true) String oldPasswordInput, // Make required=true?
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest request) {

        // Authentication check
        if (userDetails == null) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        }
        String currentUserId = userDetails.getUsername();

        // Basic Input validation
        if (!StringUtils.hasText(oldPasswordInput)) {
            return ResponseEntity.badRequest().body("InputError"); // Or let service handle via IllegalArgumentException
        }
        if (oldPasswordInput.length() > 512) {
            return ResponseEntity.badRequest().body("LengthError");
        }

        // Call the verification service
        try {
            boolean passwordMatches = mtUserloginService.verifyPassword(currentUserId, oldPasswordInput);
            return ResponseEntity.ok(passwordMatches ? "1" : "0"); // 1 match, 0 mismatch

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("InputError"); // Bad request due to invalid input
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("UserNotFound");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ServerError");
        }
    }

    @PostMapping("/change-password")
    @ResponseBody
    public ResponseEntity<String> changeParticipantPassword(
            @RequestParam("olduserpassword") String oldPassword,
            @RequestParam("newuserpassword") String newPassword,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest request) {

        // Validate inputs
        if (userDetails == null) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        }

        if (isInvalidPassword(oldPassword) || isInvalidPassword(newPassword)) {
            return ResponseEntity.badRequest().body("0");
        }

        String userId = userDetails.getUsername();
        MT_Userlogin user = mtUserloginService.findByUserId(userId);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("0");
        }

        // Prepare audit trail
        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);

        // Verify old password
        if (!mtUserloginService.verifyPassword(userId, oldPassword)) {
            logAuditTrail(auditMap, userId,"Change password Failed - Wrong Old Password");
            return ResponseEntity.ok("0");
        }

        // Check if new password is same as old
        if (mtUserloginService.verifyPassword(userId, newPassword)) {
            logAuditTrail(auditMap, userId,"Change password Failed - New Same as Old");
            return ResponseEntity.ok("3");
        }

        // Check if password contains user ID
        if (user.getUserid() != null && newPassword.toUpperCase().contains(user.getUserid().toUpperCase())) {
            logAuditTrail(auditMap, userId, "Change password Failed - Contains User ID");
            return ResponseEntity.ok("5");
        }

        // Update password
        MT_Userlogin updatedUser = mtUserloginService.updateUserPassword(user, newPassword);
        if (updatedUser == null) {
            throw new RuntimeException("Failed to update participant profile details.");
        }

        logAuditTrail(auditMap, userId, "Change password Success");
        return ResponseEntity.ok("2");
    }

    @GetMapping("/getStudentsList")
    @ResponseBody
    public List<Object[]> getStudentsList(@RequestParam("subjectcode") String subjectcode,
                                          @RequestParam("testid") String testid) {
        return tStudentsService.getStudentsList(subjectcode, testid);
    }

    private boolean isInvalidPassword(String password) {
        return password == null || password.isBlank() || password.length() < 8 || password.length() > 512;
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role A (Local-admin) & U (Coordinator-faculty)
     * 'Promote Students' process (processcode = 34)
     */
    @GetMapping("/promotion-student-list")
    public String renderPromotionStudentListPage(@ModelAttribute("msubjects") M_Subjects msubjects, Model model, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "Promote Students, " + request.getMethod()), "page");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();
        String officeCode = user.getMoffices().getOfficecode();

        if (!(
            List.of("A", "U").contains(userRole) &&
            mProcessesService.isProcessGranted(user.getUsercode(), 34)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "Promote Students, " + request.getMethod(), user.getUserid()), "page");
        }

        model.addAttribute("mdepartmentList", mDepartmentsService.getDepartmentList(officeCode));
        model.addAttribute("semesterList", mSemestersService.getSemesterList());
        model.addAttribute("courseList", mCourseAcademicsService.getCourseList2());

        if ("A".equals(user.getUserrole())) {
            model.addAttribute("layoutPath", "layouts/local-admin-layout");
        } else {
            model.addAttribute("layoutPath", "layouts/coordinator-faculty-layout");
        }

        return "pages/promotion-student-list";
    }

    @GetMapping("/getStudentPromotionData")
    @ResponseBody
    @SuppressWarnings("unchecked")
    public JSONObject getStudentPromotionData(String depcode, String spcode, String isstc) {
        JSONObject res = new JSONObject();
        JSONArray sublist = new JSONArray();
        JSONArray semphaselist = new JSONArray();

        try {
            List<M_Subjects> slist = null;
            List<M_Semesters> selist = null;
            List<M_ShortTerm_Phases> phlist = null;

            // Get next code by incrementing current value
            String nextCode = String.valueOf(Integer.parseInt(spcode) + 1);

            if ("0".equals(isstc)) {
                slist = mSubjectService.getNextSemesterOptionalSubjects(depcode, nextCode);
                selist = mSemestersService.getSemesterList();
            } else {
                slist = mSubjectService.getNextPhaseOptionalSubjects(depcode, nextCode);
                phlist = mShortTermPhasesService.getSPhaseList();
            }

            // Add subjects to JSON array
            if (slist != null) {
                for (M_Subjects subject : slist) {
                    sublist.add(subject);
                }
            }

            // Add semester or phase info
            if ("0".equals(isstc) && selist != null) {
                for (M_Semesters semester : selist) {
                    JSONObject temp = new JSONObject();
                    temp.put("semphaseid", semester.getSemestercode());
                    temp.put("semphasename", semester.getSemestername());
                    semphaselist.add(temp);
                }
            } else if (phlist != null) {
                for (M_ShortTerm_Phases phase : phlist) {
                    JSONObject temp = new JSONObject();
                    temp.put("semphaseid", phase.getSphaseid());
                    temp.put("semphasename", phase.getSphasename());
                    semphaselist.add(temp);
                }
            }

            res.put("subjects", sublist);
            res.put("semphases", semphaselist);

        } catch (Exception e) {
            e.printStackTrace();
            res.put("error", "An error occurred while processing your request.");
        }

        return res;
    }

    @GetMapping("/getStudentOnStudentid")
    @ResponseBody
    public T_Students getStudentOnStudentid(String studentid) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (RuntimeException ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage("Promote Students [/students/getStudentOnStudentid"), "page");
        }
        String officeCode = user.getMoffices().getOfficecode();
        return tStudentsService.findByStudentid(studentid, officeCode);
    }

    @PostMapping("/getSemPhaseBasedOnCourses")
    @ResponseBody
    public List<Object[]> getSemPhaseBasedOnCourses(@RequestParam("sp") String sp) {
        List<Object[]> clist = null;
        if(sp.equals("0")) {
            clist = mSemestersService.getMasterSemesters();
        } else {
            clist = mShortTermPhasesService.getMasterPhases();
        }
        return clist;
    }

    @PostMapping("/getListOfStudents")
    @ResponseBody
    public List<Object[]> getListOfStudents(
            @RequestParam("departmentcode") String dcode,
            @RequestParam("coursecode") String ccode,
            @RequestParam("semphase") String semphase,
            @RequestParam("isshortterm") String isshortterm) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (RuntimeException ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage("Promote Students [/students/promotion-student-list"), "page");
        }
        String officeCode = user.getMoffices().getOfficecode();

        List<Object[]> list = null;
        if (isshortterm.equals("0")) {
            list = tStudentsService.getSubjectListOfStudentsSemester(dcode, ccode, semphase, officeCode);
        } else {
            list = tStudentsService.getSubjectListOfStudentsPhase(dcode, ccode, semphase, officeCode);
        }
        return list;
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role A (Local-admin) & U (Coordinator-faculty)
     * Endpoint tied with 'Promote Students' process (processcode = 34)
     */
    @PostMapping("/promoteStudent")
    @ResponseBody
    public String promoteStudent(String studentid, String[] elective, HttpServletRequest request) {
        String res = "-1";

        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
        try {
            MT_Userlogin user = mtUserloginService.getUserloginFromAuthentication();
            String userRole = user.getRole().getRoleCode();
            String officeCode = user.getMoffices().getOfficecode();

            if (!(
                List.of("A", "U").contains(userRole) &&
                mProcessesService.isProcessGranted(user.getUsercode(), 34)
            )) {
                throw new AuthorizationDeniedException(user.getUserid());    
            }

            // Fetch student by ID
            T_Students stu = tStudentsService.findByStudentid(studentid, officeCode);
            if (stu == null) {
                return "-1";
            }

            String isstc = stu.getIsshortterm();
            int spcount = 0;
            int currentsp = 0;
            String check = "-1";

            // Determine current semester/phase and total count
            if ("0".equals(isstc)) {
                currentsp = Integer.parseInt(stu.getSemestercode().getSemestercode());
                spcount = mSemestersService.getSemesterList().size();
            } else {
                currentsp = Integer.parseInt(stu.getSphaseid().getSphaseid());
                spcount = mShortTermPhasesService.getSPhaseList().size();
            }

            // Promote to next semester or phase
            if (currentsp < spcount) {
                if ("0".equals(isstc)) {
                    M_Semesters sems = new M_Semesters();
                    sems.setSemestercode(String.valueOf(currentsp + 1));
                    stu.setSemestercode(sems);
                } else {
                    M_ShortTerm_Phases ph = new M_ShortTerm_Phases();
                    ph.setSphaseid(String.valueOf(currentsp + 1));
                    stu.setSphaseid(ph);
                }

                check = tStudentsService.updateStudent2(stu);
                if ("1".equals(check)) {
                    persistenceLogger.info("T_Students with studentid {} promoted successfully by userid {}", stu.getStudentid(), user.getUserid());
                    audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_students with studentid " + stu.getStudentid() + " promoted successfully");

                    check = "2"; // Promotion successful
                    System.out.println("Promotion successful");
                } else {
                    persistenceLogger.error("T_Students promote failed.\nuserid {}", user.getUserid());
                    audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_students promote failed");
                }
            } else {
                // Student has completed all phases/semesters - mark as alumni
                stu.setIscurrent("0");
                check = tStudentsService.updateStudent2(stu);

                if (!"-1".equals(check)) {
                    persistenceLogger.info("T_Students with studentid {} promoted successfully by userid {}", stu.getStudentid(), user.getUserid());
                    audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_students with studentid " + stu.getStudentid() + " promoted successfully");

                    T_Alumni alumni = new T_Alumni();
                    alumni.setBatch(stu.getAcademicyear());
                    alumni.setCoursecode(stu.getCoursecode());
                    alumni.setDepartmentcode(stu.getDepartmentcode());
                    alumni.setMoffices(stu.getOfficecode()); // Copy office from student to alumni
                    alumni.setEmail(stu.getEmail());
                    alumni.setFname(stu.getFname());
                    alumni.setMname(stu.getMname());
                    alumni.setLname(stu.getLname());
                    alumni.setRollno(stu.getStudentid());
                    alumni.setGender(stu.getGender());
                    check = tAlumniService.createAlumni(alumni);
                    
                    if (!check.equals("-1")) {
                        persistenceLogger.info("T_Alumni for studentid {} created successfully by userid {}", stu.getStudentid(), user.getUserid());
                        audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_alumni with studentid " + stu.getStudentid() + " created successfully");

                        // After successfully creating the alumni record, deactivate the user's login account.
                        try {
                            boolean isToggled = mtUserloginService.toggleUserStatus(stu.getUsercode().getUsercode());
                            if (isToggled) {
                                persistenceLogger.info("User account for usercode {} was deactivated upon promotion to alumni. Action by userid {}", stu.getUsercode().getUsercode(), user.getUserid());
                                audittrailService.logAuditTrail(auditMap, user.getUserid(), "User account for usercode " + stu.getUsercode().getUsercode() + " deactivated successfully.");
                            } else {
                                persistenceLogger.warn("Failed to deactivate user account for usercode {} upon promotion to alumni. Action by userid {}", stu.getUsercode().getUsercode(), user.getUserid());
                                audittrailService.logAuditTrail(auditMap, user.getUserid(), "Failed to deactivate user account for usercode " + stu.getUsercode().getUsercode());
                            }
                        } catch (Exception e) {
                            persistenceLogger.error("Exception while deactivating user account for usercode {}. Action by userid {}", stu.getUsercode().getUsercode(), user.getUserid(), e);
                            audittrailService.logAuditTrail(auditMap, user.getUserid(), "Exception deactivating user account for usercode " + stu.getUsercode().getUsercode());
                        }
                    } else {
                        persistenceLogger.error("T_Alumni for studentid {} create failed.\nuserid {}", stu.getStudentid(), user.getUserid());
                        audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_alumni create failed");
                    }
                } else {
                    persistenceLogger.error("T_Students promote failed.\nuserid {}", user.getUserid());
                    audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_students promote failed");
                }
            }

            // Handle optional subjects if promotion was successful
            if (!"-1".equals(check)) {
                if ("2".equals(check)) {
                    String re = tStudentSubjectService.falsifyStudentSubject(stu.getUsercode().getUsercode());

                    if ("1".equals(re)) {
                        if (elective != null && elective.length > 0) {
                            for (String ele : elective) {
                                String scd = "";
                                if (ele.length() == 3) {
                                    scd = ele;
                                } else if (ele.length() == 2) {
                                    scd = "0" + ele;
                                } else if (ele.length() == 1) {
                                    scd = "00" + ele;
                                }

                                T_Student_Subject tss = new T_Student_Subject();
                                M_Subjects sc = new M_Subjects();
                                sc.setSubjectcode(ele);

                                tss.setUsercode(stu.getUsercode());
                                tss.setSubjectcode(sc);
                                tss.setIsactive("1");
                                tss.setTssid(stu.getStudentid() + (currentsp + 1) + scd);

                                res = tStudentSubjectService.saveStudentSubject(tss);
                                if (!res.equals("-1")) {
                                    persistenceLogger.info("T_Student_Subject for usercode {} created successfully by userid {}", stu.getUsercode().getUsercode(), user.getUserid());
                                    audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_student_subject for usercode " + stu.getUsercode().getUsercode() + " created successfully");
                                } else {
                                    persistenceLogger.error("T_Student_Subject for usercode {} create failed.\nuserid {}", stu.getUsercode().getUsercode(), user.getUserid());
                                    audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_student_subject for usercode " + stu.getUsercode().getUsercode() + " create failed");
                                }
                            }
                        } else {
                            res = "1";
                        }
                    }
                } else {
                    res = "1"; // Already graduated
                }
            }
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

    private void logAuditTrail(HashMap<String, String> auditMap,String userId, String actionTaken) {
        if (auditMap != null) {
            auditMap.put("userid", userId);
            auditMap.put("actiontaken", actionTaken);
            audittrailService.saveAuditTrail(auditMap);
        }
    }

    /*
     * TODO @Abanggi: Just use @RequestParam brother. Why so fancy?
     */
    @GetMapping("/student/usercode={usercode}")
    public ResponseEntity<?> getTStudentsByUsercode(@PathVariable("usercode") String usercode) {
        try {
            T_Students student = tStudentsService.findByUsercode(usercode);
            return student != null ? ResponseEntity.ok(student) : ResponseEntity.notFound().build();
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            return ResponseEntity.internalServerError().body("Error fetching student with usercode " + usercode);
        }
    }

    @GetMapping("/student/studentid={studentid}")
    public ResponseEntity<?> getTStudentsByStudentid(@PathVariable("studentid") String studentid) {
        try {
            MT_Userlogin user = mtUserloginService.getUserloginFromAuthentication();
            String officeCode = user.getMoffices().getOfficecode();
            T_Students student = tStudentsService.findByStudentid(studentid, officeCode);
            return student != null ? ResponseEntity.ok(student) : ResponseEntity.notFound().build();
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.internalServerError().body("Error fetching student with studentid " + studentid);
        }
    }

    @GetMapping("/student/promotion")
    @SuppressWarnings("unchecked")
    public ResponseEntity<JSONObject> getTStudentsPromotionDetails(@RequestParam("depcode") String depcode,
        @RequestParam("spcode") String spcode, @RequestParam("isstc") String isstc) {
        JSONObject response = new JSONObject();
        JSONArray subjects = new JSONArray();
        JSONArray phases = new JSONArray();

        try {
            List<M_Subjects> mSubjectsList = new ArrayList<>();
            List<M_Semesters> mSemestersList = new ArrayList<>();
            List<M_ShortTerm_Phases> mShortTermPhasesList = new ArrayList<>();

            if (isstc.trim().equals("0")) {
                mSubjectsList = mSubjectService.getNextSemesterOptionalSubjects(depcode, String.valueOf(Integer.valueOf(spcode) + 1));
                mSemestersList = mSemestersService.getSemesterList();
            } else {
                mSubjectsList = mSubjectService.getNextPhaseOptionalSubjects(depcode, String.valueOf(Integer.valueOf(spcode) + 1));
                mShortTermPhasesList = mShortTermPhasesService.getSPhaseList();
            }

            for (M_Subjects mSubject : mSubjectsList)
                subjects.add(mSubject);

            if (isstc.trim().equals("0")) {
                for (M_Semesters mSemester : mSemestersList) {
                    JSONObject json = new JSONObject();
                    json.put("semphaseid", mSemester.getSemestercode());
                    json.put("semphasename", mSemester.getSemestername());
                    phases.add(json);
                }
            } else {
                for (M_ShortTerm_Phases mShortTermPhase : mShortTermPhasesList) {
                    JSONObject json = new JSONObject();
                    json.put("semphaseid", mShortTermPhase.getSphaseid());
                    json.put("semphasename", mShortTermPhase.getSphasename());
                    phases.add(json);
                }
            }

            response.put("subjects", subjects);
            response.put("semphases", phases);

            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role A (Local-admin) & U (Coordinator-faculty)
     * 'Manage Students' process (processcode = 31)
     */
    @GetMapping("/manage")
    public String renderManageStudentsPage(Model model, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "Manage Students, " + request.getMethod()), "page");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();
        String officeCode = user.getMoffices().getOfficecode();

        if (!(
            List.of("A", "U").contains(userRole) &&
            mProcessesService.isProcessGranted(user.getUsercode(), 31)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "Manage Students, " + request.getMethod(), user.getUserid()), "page");
        }

        switch (user.getRole().getRoleCode().toUpperCase()) {
            case "A":
                model.addAttribute("layoutname", "layouts/local-admin-layout");
                break;
            case "U":
                model.addAttribute("layoutname", "layouts/coordinator-faculty-layout");
                break;
        }

        model.addAttribute("semesterList", mSemestersService.getSemesterList());
        model.addAttribute("departments", mDepartmentsService.getDepartmentList(officeCode));
        model.addAttribute("allstudents", tStudentsService.getStudentList(officeCode));
        model.addAttribute("allsubjects", mSubjectService.getAllSubjectList(officeCode));
        model.addAttribute("sphaseList", mShortTermPhasesService.getSPhaseList());

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int[] ay = new int[2];
        for (int i = 0; i < 2; i++)
            ay[i] = year - i;
        model.addAttribute("ay", ay);

        return "pages/create-student";
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role A (Local-admin) & U (Coordinator-faculty)
     * Endpoint tied with 'Manage Students' process (processcode = 31)
     */
    @PostMapping("/save")
    @SuppressWarnings("unchecked")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<JSONObject> saveStudentDetails(
        @RequestParam(value = "studentid", required = false) String studentid,
        @RequestParam(value = "usercode", required = false) String usercode,
        @RequestParam(value = "rollno", required = false) String rollno,
        @RequestParam("username") String username,
        @RequestParam("fname") String fname,
        @RequestParam(value = "mname", required = false) String mname,
        @RequestParam("lname") String lname,
        @RequestParam("gender") String gender,
        @RequestParam("email") String email,
        @RequestParam("mobileno") String mobileno,
        @RequestParam("departmentcode") String departmentcode,
        @RequestParam("isshortterm") String isshortterm,
        @RequestParam(value = "semestercode", required = false) String semestercode,
        @RequestParam(value = "sphaseid", required = false) String sphaseid,
        @RequestParam("coursecode") String coursecode,
        @RequestParam("optionalsubjects") List<String> optionalsubjects,
        @RequestParam("academicyear") String academicyear,
        HttpServletRequest request
    ) {
        JSONObject response = new JSONObject();
        MT_Userlogin currentUser;
        try {
            currentUser = mtUserloginService.getUserloginFromAuthentication();
        } catch (RuntimeException ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        } 
        String userRole = currentUser.getRole().getRoleCode().toUpperCase();

        if (!(
            List.of("A", "U").contains(userRole) &&
            mProcessesService.isProcessGranted(currentUser.getUsercode(), 31)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), currentUser.getUserid()), "json");
        }

        // TODO @Abanggi: Return informative badRequest and internalservererror body.
        if (
            username == null || username.isBlank() ||   // username is only utilized for creating new userlogin
            fname == null || fname.isBlank() ||
            lname == null || lname.isBlank() ||
            gender == null || gender.isBlank() ||
            email == null || email.isBlank() ||
            mobileno == null || mobileno.isBlank() ||
            departmentcode == null || departmentcode.isBlank() ||
            isshortterm == null || isshortterm.isBlank() ||
            ((semestercode == null || semestercode.isBlank()) && (sphaseid == null || sphaseid.isBlank())) || // either semestercode or sphaseid should be present
            coursecode == null || coursecode.isBlank() ||
            optionalsubjects == null || optionalsubjects.isEmpty() ||
            academicyear == null || academicyear.isBlank()
        ) {
            response.put("status", "Failed");
            response.put("msg", "Required Parameters are missing");
            return ResponseEntity.badRequest().body(response);
        }

        // Validating gender
        if (!gender.trim().equals("M") && !gender.trim().equals("F") && !gender.trim().equals("O")) {
            response.put("status", "Failed");
            response.put("msg", "Invalid gender option");
            return ResponseEntity.badRequest().body(response);
        }

        // Validating email
        if (!EmailValidator.isEmailValid(email)) {
            response.put("status", "Failed");
            response.put("msg", "Invalid email address");
            return ResponseEntity.badRequest().body(response);
        }

        // Validating mobileno
        // TODO @Abanggi: Create MobilenoValidator to Validatingusername mobile numbers
        if (mobileno.trim().length() < 10) {
            response.put("status", "Failed");
            response.put("msg", "Invalid phone number");
            return ResponseEntity.badRequest().body(response);
        }

        // Validating isshortterm
        if (!isshortterm.trim().equals("0") && !isshortterm.trim().equals("1")) {
            response.put("status", "Failed");
            response.put("msg", "Invalid isshortterm option");
            return ResponseEntity.badRequest().body(response);
        }

        // for student-details update
        // Validating studentid
        if (studentid != null && !studentid.isBlank()) {
            try {
                if (!tStudentsService.existsByStudentid(studentid.trim())) {
                    response.put("status", "Failed");
                    response.put("msg", "Student with studentid " + studentid.trim() + " already exists");
                    return ResponseEntity.badRequest().body(response);
                }
            } catch (RuntimeException ex) {
                ex.printStackTrace();
                response.put("status", "Failed");
                response.put("msg", "Something went wrong");
                return ResponseEntity.internalServerError().body(response);
            }
        }

        // for student-details update
        // Validating usercode
        if (usercode != null && !usercode.isBlank()) {
            try {
                if (!mtUserloginService.existsByUsercode(usercode)) {
                    response.put("status", "Failed");
                    response.put("msg", "User with usercode " + usercode.trim() + " does not exist");
                    return ResponseEntity.badRequest().body(response);
                }
            } catch (RuntimeException ex) {
                ex.printStackTrace();
                response.put("status", "Failed");
                response.put("msg", "Something went wrong");
                return ResponseEntity.internalServerError().body(response);
            }
        }

        // Student entity to save or update
        T_Students newStudent = new T_Students();

        // Validating department existence by departmentcode
        M_Departments studentDepartment;
        try {
            studentDepartment = mDepartmentsService.getDepartmentByDepartmentcode(departmentcode);
            if (studentDepartment == null) {
                response.put("status", "Failed");
                response.put("msg", "Department with departmentcode " + departmentcode.trim() + " does not exist");

                return ResponseEntity.badRequest().body(response);
            }
            newStudent.setDepartmentcode(studentDepartment);
        } catch (RuntimeException ex) {
            dataAccessLogger.error(ex.toString());
            response.put("status", "Failed");
            response.put("msg", "Something went wrong");
            
            return ResponseEntity.internalServerError().body(response);
        }

        // Validating semester existence by semestercode
        // semestercode is optional parameter
        if (semestercode != null && !semestercode.isBlank()) {
            M_Semesters studentSemester;
            try {
                studentSemester = mSemestersService.getSemesterBySemestercode(semestercode);
                if (studentSemester == null) {
                    response.put("status", "Failed");
                    response.put("msg", "Semester with semestercode " + semestercode.trim() + " does not exist");

                    return ResponseEntity.badRequest().body(response);
                }
                newStudent.setSemestercode(studentSemester);
            } catch (RuntimeException ex) {
                dataAccessLogger.error(ex.toString());
                response.put("status", "Failed");
                response.put("msg", "Something went wrong");

                return ResponseEntity.internalServerError().body(response);
            }
        } else
            newStudent.setSemestercode(null);

        // Validating short-term phase existence by sphaseid
        // sphaseid is optional parameter
        if (sphaseid != null && !sphaseid.isBlank()) {
            M_ShortTerm_Phases studentShortTermPhase;
            try {
                studentShortTermPhase = mShortTermPhasesService.getShortTermPhaseBySphaseid(sphaseid);
                if (studentShortTermPhase == null) {
                    response.put("status", "Failed");
                    response.put("msg", "Phase with phaseid " + sphaseid.trim() + " does not exist");

                    return ResponseEntity.badRequest().body(response);
                }
                newStudent.setSphaseid(studentShortTermPhase);
            } catch (RuntimeException ex) {
                dataAccessLogger.error(ex.toString());
                response.put("status", "Failed");
                response.put("msg", "Something went wrong");

                return ResponseEntity.internalServerError().body(response);
            }
        } else
            newStudent.setSphaseid(null);

        // Validating course existence by coursecode
        M_Course_Academics studentCourse;
        try {
            studentCourse = mCourseAcademicsService.getCourseAcademicsByCoursecode(coursecode);
            if (studentCourse == null) {
                response.put("status", "Failed");
                response.put("msg", "Course with coursecode " + coursecode.trim() + " does not exist");
                return ResponseEntity.badRequest().body(response);
            }
            newStudent.setCoursecode(studentCourse);
        } catch (RuntimeException ex) {
            dataAccessLogger.error(ex.toString());
            response.put("status", "Failed");
            response.put("msg", "Something went wrong");
            return ResponseEntity.internalServerError().body(response);
        }

        // Checking if user already exists
        // Only check if the student is new and not updating
        if (studentid == null || studentid.isBlank()) {
            MT_Userlogin user = new MT_Userlogin();
            user.setEmailid(email.trim());
            user.setUsermobile(mobileno.trim());
            try {
                if (mtUserloginService.checkUserExists(user)) {
                    response.put("status", "Failed");
                    response.put("msg", "Email or mobile number is already taken");
                    return ResponseEntity.badRequest().body(response);
                }
            } catch (RuntimeException ex) {
                ex.printStackTrace();
                response.put("status", "Failed");
                response.put("msg", "Something went wrong");
                return ResponseEntity.internalServerError().body(response);
            }
        }

        // Handling Usercode for existing students
        if (usercode != null && !usercode.isBlank()) {
            try {
                MT_Userlogin existingUser = mtUserloginService.findByUsercode(usercode.trim());
                if (existingUser.getRole() == null) {
                    existingUser.setRole(mtUserloginRoleService.findByRoleCode(existingUser.getUserrole()));
                    if ((existingUser = mtUserloginService.save(existingUser)) == null)
                        throw new Exception("Error saving MT_Userlogin for For pre-migrated users");
                }
                newStudent.setUsercode(existingUser);
            } catch (Exception ex) {
                logger.error(ex.getMessage());
                response.put("status", "Failed");
                response.put("msg", "Something went wrong");
                return ResponseEntity.internalServerError().body(response);
            }
        } else {
            newStudent.setUsercode(null);
        }

        // setting mandatory and optional student parameters
        newStudent.setStudentid(studentid != null ? studentid.trim() : "");
        newStudent.setRollno(rollno != null ? rollno.trim() : "");
        newStudent.setFname(fname.trim());
        newStudent.setMname(mname != null ? mname.trim() : "");
        newStudent.setLname(lname.trim());
        newStudent.setGender(gender.trim());
        newStudent.setEmail(email.trim());
        newStudent.setMobileno(mobileno.trim());
        newStudent.setIsshortterm(isshortterm);
        newStudent.setAcademicyear(academicyear.trim());

        M_Offices currentUserOffice = new M_Offices();
        currentUserOffice.setOfficecode(currentUser.getMoffices().getOfficecode());
        newStudent.setOfficecode(currentUserOffice);

        // for new student
        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
        if (newStudent.getStudentid() == null || newStudent.getStudentid().isBlank()) {
            MT_Userlogin newStudentUser = null;
            String newStudentUserPassword = RandomPasswordGenerator.generateRandomPassword();

            try {
                if ((newStudentUser = mtUserloginService.saveUserDetailsForStudent(
                        username.trim(),
                        fname.trim(),
                        newStudentUserPassword,
                        currentUserOffice // <--- Added Office Parameter
                )) == null)
                    throw new RuntimeException("Failed to save MT_Userlogin entity for T_Students entity");

                persistenceLogger.info("MT_Userlogin with usercode {} saved successfully by userid {}", newStudentUser.getUsercode(), currentUser.getUserid());
                audittrailService.logAuditTrail(auditMap, currentUser.getUserid(), "mt_userlogin with usercode " + newStudentUser.getUsercode() + " saved successfully");

                newStudent.setUsercode(newStudentUser);
                newStudent.setIscurrent("1");

                T_Students savedStudent;
                if ((savedStudent = tStudentsService.configureAndSaveTStudentsEntity(newStudent,currentUser.getMoffices())) == null)
                    throw new RuntimeException("Failed to save T_Students entity");

                persistenceLogger.info("T_Students with studentid {} saved successfully by userid {}", savedStudent.getStudentid(), currentUser.getUserid());
                audittrailService.logAuditTrail(auditMap, currentUser.getUserid(), "T_Students with studentid " + savedStudent.getStudentid() + " saved successfully");

                List<T_Student_Subject> savedStudentSubjectList = tStudentSubjectService.saveTStudentSubjectEntityList(optionalsubjects, savedStudent);
                if (savedStudentSubjectList ==  null || savedStudentSubjectList.isEmpty()) {
                    throw new RuntimeException("Failed to save T_Student_Subject entity list");
                }
                persistenceLogger.info("T_Student_Subject saved successfully by userid {}", currentUser.getUserid());
                audittrailService.logAuditTrail(auditMap, currentUser.getUserid(), "t_student_subject saved successfully");

                response.put("status", "Success");
                response.put("msg", "Successfully Created Student");
                response.put("flg", 1);
                response.put("userid", username);
                response.put("password", newStudentUserPassword);
            } catch (Exception ex) {
                persistenceLogger.error(ex.getMessage() + " by userid {}", currentUser.getUserid());
                audittrailService.logAuditTrail(auditMap, currentUser.getUserid(), ex.getMessage());

                response.put("status", "Failed");
                response.put("msg", "Failed to Create Student");
            }
        }
        // for student-details update
        else {
            try {
                newStudent.setIscurrent("1");

                // Note: officecode is already set above before the if/else block now

                T_Students updatedStudent;
                if ((updatedStudent = tStudentsService.updateOrSaveTStudentsEntity(newStudent)) == null)
                    throw new RuntimeException("Failed to update T_Students entity");

                persistenceLogger.info("T_Students with studentid {} updated successfully by userid {}", updatedStudent.getStudentid(), currentUser.getUserid());
                audittrailService.logAuditTrail(auditMap, currentUser.getUserid(), "t_students with studentid " + updatedStudent.getStudentid() + " saved successfully");

                List<T_Student_Subject> savedStudentSubjectList = tStudentSubjectService.saveTStudentSubjectEntityList(optionalsubjects, updatedStudent);

                if (savedStudentSubjectList ==  null || savedStudentSubjectList.isEmpty())
                    throw new RuntimeException("Failed to update T_Student_Subject entity list");

                persistenceLogger.info("T_Student_Subject updated successfully by userid {}", currentUser.getUserid());
                audittrailService.logAuditTrail(auditMap, currentUser.getUserid(), "t_student_subject updated successfully");

                response.put("msg", "Successfully Updated Student Information");
                response.put("flg", 2);
            } catch (Exception ex) {
                persistenceLogger.error(ex.getMessage() + " by userid {}", currentUser.getUserid());
                audittrailService.logAuditTrail(auditMap, currentUser.getUserid(), ex.getMessage());

                response.put("status", "Failed");
                response.put("msg", "Failed to Update Student Information");
            }
        }

        return ResponseEntity.ok(response);
    }

    //flutter endpoint
    // Get all students
    @GetMapping("/all")
    public ResponseEntity<List<Object[]>> getAllStudents(HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (RuntimeException ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(
                            request.getRequestURI(), request.getMethod()), "json");
        }

        String officecode = user.getMoffices().getOfficecode();
        List<Object[]> students = tStudentsService.getAllStudentsWithSubjects(officecode);
        return ResponseEntity.ok(students);
    }
}
