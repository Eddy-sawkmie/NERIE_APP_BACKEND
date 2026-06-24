package com.nic.nerie.t_studentsattendance.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import com.nic.nerie.m_course_academics.model.M_Course_Academics;
import com.nic.nerie.m_course_academics.service.M_Course_AcademicsService;
import com.nic.nerie.m_processes.service.M_ProcessesService;
import com.nic.nerie.m_semesters.service.M_SemestersService;
import com.nic.nerie.m_subjects.model.M_Subjects;
import com.nic.nerie.m_subjects.service.M_SubjectService;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.mt_userlogin.service.MT_UserloginService;
import com.nic.nerie.t_faculties.service.T_FacultiesService;
import com.nic.nerie.t_students.model.T_Students;
import com.nic.nerie.t_students.service.T_StudentsService;
import com.nic.nerie.t_studentsattendance.model.T_StudentsAttendance;
import com.nic.nerie.t_studentsattendance.service.T_StudentsAttendanceService;
import com.nic.nerie.utils.ExceptionUtil;
import com.nic.nerie.utils.UtilCommon;

import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/attendance")
public class T_StudentsAttendanceController {
    private final T_StudentsAttendanceService tStudentsAttendanceService;
    private final M_SubjectService mSubjectService;
    private final MT_UserloginService mtUserloginService;
    private final T_StudentsService tStudentsService;
    private final T_FacultiesService tFacultiesService;
    private final M_ProcessesService mProcessesService;
    private final AudittrailService audittrailService;
    private final M_Course_AcademicsService mCourseAcademicsService;
    private final M_SemestersService mSemestersService;
    private static final Logger persistenceLogger = LoggerFactory.getLogger("DATA_PERSISTENCE_LOGGER");

    @Autowired
    public T_StudentsAttendanceController(T_StudentsAttendanceService tStudentsAttendanceService,
                                          M_SubjectService mSubjectService,
                                          MT_UserloginService mtUserloginService, 
                                          T_StudentsService tStudentsService, 
                                          T_FacultiesService tFacultiesService,
                                          M_ProcessesService mProcessesService,
                                          AudittrailService audittrailService,
                                        M_Course_AcademicsService mCourseAcademicsService,
                                        M_SemestersService mSemestersService) {
        this.tStudentsAttendanceService = tStudentsAttendanceService;
        this.mSubjectService = mSubjectService;
        this.mtUserloginService = mtUserloginService;
        this.tStudentsService = tStudentsService;
        this.tFacultiesService = tFacultiesService;
        this.mProcessesService = mProcessesService;
        this.audittrailService = audittrailService;
        this.mCourseAcademicsService = mCourseAcademicsService;
        this.mSemestersService = mSemestersService;
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role A (Local-admin) & U (Coordinator-faculty)
     * isfaculty = 1
     * 'Upload Attendance' process (processcode = 35)
     */
    @GetMapping("/upload-attendance")
    public String renderUploadAttendancePage(Model model, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "Upload Attendance, " + request.getMethod()), "page");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!(
            List.of("A", "U").contains(userRole) &&
            mProcessesService.isProcessGranted(user.getUsercode(), 35) &&
            user.getIsfaculty().equals("1")
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "Upload Attendance, " + request.getMethod(), user.getUserid()), "page");
        }

        // List<Object[]> subscodelist = mSubjectService.getSubjectsList(user.getUsercode());
        // model.addAttribute("subs", subscodelist);
        model.addAttribute("semphases",mSemestersService.getSemPhaseList());
        return "pages/upload-attendance";
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role A (Local-admin) & U (Coordinator-faculty)
     * isfaculty = 1
     * Endpoint tied with 'Upload Attendance' process (processcode = 35)
     */
    @PostMapping("/upload-attendance")
    @ResponseBody
    public String uploadAttendance(
            @RequestParam("subjectcode") String subjectcode,
            @RequestParam("dateselect") String dateselect,
            @RequestParam("starttime") String starttime,
            @RequestParam("endtime") String endtime,
            @RequestParam("attendancejsonstring") String attendancejsonstring,
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
                        mProcessesService.isProcessGranted(user.getUsercode(), 35) &&
                        user.getIsfaculty().equals("1")
        )) {
            throw new MyAuthorizationDeniedException(
                    ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }

        String res;
        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
        try {
            JSONParser parser = new JSONParser();
            JSONArray qdata = (JSONArray) parser.parse(attendancejsonstring);

            M_Subjects subject = mSubjectService.getSubjectBySubjectCode(subjectcode);
            if (subject == null) {
                persistenceLogger.warn("Attempt to upload attendance for non-existent subjectcode: {}", subjectcode);
                return "-1"; // Subject not found
            }

            Date attendancedate = new SimpleDateFormat("dd-MM-yyyy").parse(dateselect);
            DateFormat formatter = new SimpleDateFormat("HH:mm");
            Date starttime2 = formatter.parse(starttime);
            Date endtime2 = formatter.parse(endtime);

            for (Object o : qdata) {
                JSONObject jo = (JSONObject) o;

                String studentId = jo.get("studentid").toString();
                String attendanceStatus = jo.get("pora").toString();

                T_Students student = new T_Students();
                //student.setStudentid(studentId);
                student = tStudentsService.findByStudentid(studentId, user.getMoffices().getOfficecode());

                res = tStudentsAttendanceService.saveOrUpdateStudentAttendance(
                        student, subject, attendanceStatus, attendancedate, starttime2, endtime2, user);

                if ("-1".equals(res)) {
                    throw new PersistenceException("Failed to process attendance for student ID: " + studentId);
                } else {
                    persistenceLogger.info("Attendance for studentid {} processed successfully by userid {}", studentId, user.getUserid());
                    audittrailService.logAuditTrail(auditMap, user.getUserid(), "Attendance for studentid " + studentId + " processed successfully");
                }
            }
        } catch (Exception ex) {
            persistenceLogger.error("Attendance upload process failed.\nMessage: {}\nUserid: {}", ex.getMessage(), user.getUserid(), ex);
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "Attendance upload process failed");
            return "-1";
        }

        return "1";
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role A (Local-admin) & U (Coordinator-faculty)
     * isfaculty = 1
     * 'View Student Attendance' process (processcode = 36)
     */ 
    @GetMapping("/view-student-attendance")
    public String renderViewStudentAttendance(Model model, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "View Student Attendance, " + request.getMethod()), "page");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!(
            List.of("A", "U").contains(userRole) &&
            mProcessesService.isProcessGranted(user.getUsercode(), 36) &&
            user.getIsfaculty().equals("1")
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "View Student Attendance, " + request.getMethod(), user.getUserid()), "page");
        }

        int startYear = 2022;
        int currentYear = Year.now().getValue();
        List<Integer> yearlist = new ArrayList<>();
        for (int y = startYear; y <= currentYear; y++) {
            yearlist.add(y);
        }

        //List<Object[]> subscodelist = mSubjectService.getSubjectsList(user.getUsercode());
        List<M_Course_Academics> courselist = mCourseAcademicsService.findCoursesByOfficeCode(user.getMoffices().getOfficecode());
        List<String> timelist = tStudentsAttendanceService.getTimeList();
        List<Object[]> deptAndFaculty = tFacultiesService.getDeptAndFacultyDetails(user.getUsercode()); // You'll need to create this method

        //model.addAttribute("subs", subscodelist);
        model.addAttribute("courselist", courselist);
        model.addAttribute("time", timelist);
        model.addAttribute("deptandfacultyname", deptAndFaculty);
        model.addAttribute("yearlist", yearlist);
        
        return "pages/view-student-attendance";
    }

    //OLD CODE USING TIME AS A PARAM
    // @GetMapping("/getStudentAttendance")
    // @ResponseBody
    // public List<Object[]> getStudentAttendance(@RequestParam("subjectcode") String subjectcode,
    //                                            @RequestParam("month") String month,
    //                                            @RequestParam("time") String time) {
    //     MT_Userlogin user = mtUserloginService.getUserloginFromAuthentication(
    //             SecurityContextHolder.getContext().getAuthentication());

    //     if (user == null) {
    //         throw new RuntimeException("User not authenticated");
    //     }
    //     return tStudentsAttendanceService.getStudentAttendanceDetails(user.getUsercode(), subjectcode, month, time);
    // }

    @GetMapping("/getStudentAttendance")
    @ResponseBody
    public List<Object[]> getStudentAttendance(@RequestParam("subjectcode") String subjectcode,
                                               @RequestParam("month") Integer month,
                                               @RequestParam("year") Integer year, 
                                               @RequestParam("semflg") String sem) {
        MT_Userlogin user = mtUserloginService.getUserloginFromAuthentication(
                SecurityContextHolder.getContext().getAuthentication());

        if (user == null) {
            throw new RuntimeException("User not authenticated");
        }
        return tStudentsAttendanceService.getStudentAttendanceDetails(user.getUsercode(), subjectcode, month, year, sem);
    }

    @GetMapping("/getStudentsListBasedOnSubjectCode")
    @ResponseBody
    public List<Object[]> getStudentsListBasedOnSubjectCode(@RequestParam("subjectcode") String subjectcode) {
        M_Subjects subject = mSubjectService.getSubjectBySubjectCode(subjectcode);

        if ("1".equals(subject.getIsshortterm())) {
            if ("1".equals(subject.getIsoptional())) {
                return tStudentsService.getOptionalPhaseSubjectStudents(subjectcode); // <--- NEW
            } else {
                return tStudentsService.getGeneralPhaseSubjectStudents(subjectcode);
            }
        } else if ("0".equals(subject.getIsshortterm())) {
            if ("1".equals(subject.getIsoptional())) {
                return tStudentsService.getOptionalSemesterSubjectStudents(subjectcode);
            }
        }

        return tStudentsService.getGeneralSemesterSubjectStudents(subjectcode);
    }

    @GetMapping("/getSubjectsBySemPhase")
    @ResponseBody
    public List<Object[]> getSubjectsBySemPhase(
            @RequestParam String semphase) {

        MT_Userlogin user = mtUserloginService.getUserloginFromAuthentication(
                SecurityContextHolder.getContext().getAuthentication());

        return mSubjectService
                .getSubjectsListBySemPhaseAndUsercode(user.getUsercode(), semphase);
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role T (Student)     
     * View My Attendance
     */
    @GetMapping("/viewattendance")
    public String renderAttendancePage(Model model, HttpServletRequest request) {
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

        model.addAttribute("sublist", allSubjects);

        return "pages/t_students/attendance-record";
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role T (Student)     
     * View My Attendance
     */
    @GetMapping("/getattendance")
    public ResponseEntity<List<Object[]>> getAttendance(@RequestParam("subjectcode") String subjectcode,
                                                        @RequestParam("month") String month,
                                                        HttpServletRequest request) {
        MT_Userlogin user;

        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (RuntimeException ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        }

        if (!user.getRole().getRoleCode().equalsIgnoreCase("T")) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }

        List<Object[]> attendance = tStudentsAttendanceService.getStudentAttendanceList(user.getUsercode(),
                subjectcode, month);

        return ResponseEntity.ok(attendance);
    }
    /*
     * Secured endpoint
     * Exclusive to role T (Student)
     * Returns subject list for the logged-in student
     */
    @GetMapping("/subjects")
    @ResponseBody
    public ResponseEntity<List<Object[]>> getStudentSubjects(HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (RuntimeException ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        }

        if (!user.getRole().getRoleCode().equalsIgnoreCase("T")) {
            throw new MyAuthorizationDeniedException(
                    ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }

        T_Students student = tStudentsService.findByUsercode(user.getUsercode());

        List<Object[]> allSubjects = new ArrayList<>();

        List<Object[]> compulsorySubjects;
        if (student.getIsshortterm().equals("0")) {
            compulsorySubjects = mSubjectService.getCompulsorySubjectsLongTerm(
                    student.getSemestercode().getSemestercode(),
                    student.getCoursecode().getCoursecode());
        } else {
            compulsorySubjects = mSubjectService.getCompulsorySubjectsShortTerm(
                    student.getSphaseid().getSphaseid(),
                    student.getCoursecode().getCoursecode());
        }

        List<Object[]> optionalSubjects = mSubjectService.getStudentSubjectsList(user.getUsercode());

        allSubjects.addAll(compulsorySubjects);
        allSubjects.addAll(optionalSubjects);

        return ResponseEntity.ok(allSubjects);
    }


    @GetMapping("/faculty-subjects")
    @ResponseBody
    public ResponseEntity<List<Object[]>> getFacultySubjects(HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (RuntimeException ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(
                            request.getRequestURI(), request.getMethod()), "json");
        }
        if (!List.of("A", "U").contains(user.getRole().getRoleCode().toUpperCase())) {
            throw new MyAuthorizationDeniedException(
                    ExceptionUtil.generateAuthorizationDeniedMessage(
                            request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }
        return ResponseEntity.ok(mSubjectService.getSubjectsList(user.getUsercode()));
    }

}
