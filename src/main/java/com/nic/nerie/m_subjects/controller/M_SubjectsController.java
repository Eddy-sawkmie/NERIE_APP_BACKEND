package com.nic.nerie.m_subjects.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nic.nerie.audittrail.service.AudittrailService;
import com.nic.nerie.exceptions.MyAuthenticationCredentialsNotFoundException;
import com.nic.nerie.exceptions.MyAuthorizationDeniedException;
import com.nic.nerie.m_course_academics.model.M_Course_Academics;
import com.nic.nerie.m_course_academics.service.M_Course_AcademicsService;
import com.nic.nerie.m_departments.model.M_Departments;
import com.nic.nerie.m_departments.service.M_DepartmentsService;
import com.nic.nerie.m_processes.service.M_ProcessesService;
import com.nic.nerie.m_semesters.model.M_Semesters;
import com.nic.nerie.m_semesters.service.M_SemestersService;
import com.nic.nerie.m_shortterm_phases.model.M_ShortTerm_Phases;
import com.nic.nerie.m_shortterm_phases.service.M_ShortTerm_PhasesService;
import com.nic.nerie.m_subjects.model.M_Subjects;
import com.nic.nerie.m_subjects.service.M_SubjectService;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.mt_userlogin.service.MT_UserloginService;
import com.nic.nerie.t_students.model.T_Students;
import com.nic.nerie.t_students.service.T_StudentsService;
import com.nic.nerie.utils.ExceptionUtil;
import com.nic.nerie.utils.UtilCommon;

import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/subjects")
public class M_SubjectsController {
    private final MT_UserloginService mtUserloginService;
    private final T_StudentsService tStudentsService;
    private final M_SubjectService mSubjectService;
    private final M_DepartmentsService mDepartmentsService;
    private final M_SemestersService mSemestersService;
    private final M_ShortTerm_PhasesService mShortTermPhasesService;
    private final M_Course_AcademicsService mCourseAcademicsService;
    private final M_ProcessesService mProcessesService;
    private final AudittrailService audittrailService;
    private static final Logger persistenceLogger = LoggerFactory.getLogger("DATA_PERSISTENCE_LOGGER");

    @Autowired
    public M_SubjectsController(MT_UserloginService mtUserloginService,
                                T_StudentsService tStudentsService,
                                M_SubjectService mSubjectService, 
                                M_DepartmentsService mDepartmentsService, 
                                M_SemestersService mSemestersService, 
                                M_ShortTerm_PhasesService mShortTermPhasesService, 
                                M_Course_AcademicsService mCourseAcademicsService,
                                M_ProcessesService mProcessesService,
                                AudittrailService audittrailService) {
        this.mtUserloginService = mtUserloginService;
        this.tStudentsService = tStudentsService;
        this.mSubjectService = mSubjectService;
        this.mDepartmentsService = mDepartmentsService;
        this.mSemestersService = mSemestersService;
        this.mShortTermPhasesService = mShortTermPhasesService;
        this.mCourseAcademicsService = mCourseAcademicsService;
        this.mProcessesService = mProcessesService;
        this.audittrailService = audittrailService;
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role A (Local-Admin), U (Coordinator-Faculty), and S (Admin).
     * 'Manage Subjects' process (processcode = 28)
     */
    @GetMapping("/create-academic-subjects")
    public String renderCreateAcademicSubjectsPage(@ModelAttribute("macademicsubject") M_Departments mdepartments,
                                                   @RequestParam(value = "departmentcode", required = false) String dcode,
                                                   @RequestParam(value = "semestercode", required = false) String scode,
                                                   Model model,
                                                   HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "Manage Subjects, " + request.getMethod()), "page");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!(
            List.of("A", "S", "U").contains(userRole) &&
            mProcessesService.isProcessGranted(user.getUsercode(), 28)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "Manage Subjects, " + request.getMethod(), user.getUserid()), "page");
        }

        String officeCode = user.getMoffices().getOfficecode();

        List<M_Departments> departmentList = mDepartmentsService.getDepartmentList(officeCode);
        List<M_Semesters> semesterList = mSemestersService.getSemesterList();
        List<M_ShortTerm_Phases> shortTermPhasesList = mShortTermPhasesService.getSPhaseList();
        List<M_Course_Academics> courseAcademicsList = mCourseAcademicsService.getListOfCoursesForDept(dcode);

        model.addAttribute("mdepartmentList", departmentList);
        model.addAttribute("semesterList", semesterList);
        model.addAttribute("sphaseList", shortTermPhasesList);
        model.addAttribute("scourseList", courseAcademicsList);

        if("A".equals(user.getUserrole())) {
            model.addAttribute("layoutPath", "layouts/local-admin-layout");
        } else if ("U".equals(user.getUserrole())) {
            model.addAttribute("layoutPath", "layouts/coordinator-faculty-layout");
        } else {
            model.addAttribute("layoutPath", "layouts/admin-layout");
        }             

        return "pages/create-academic-subjects";
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role A (Local-Admin), U (Coordinator-Faculty), and S (Admin).
     * Endpoint tied with 'Manage Subjects' process (processcode = 28)
     */
    @PostMapping("/saveNewSubject")
    @ResponseBody
    public String savemapdepartmentsubject(
            @RequestParam("subjectname") String subjectname,
            @RequestParam("departmentcode") String departmentcode,
            @RequestParam(value = "semestercode", required = false) String semestercode,
            @RequestParam("subjectcode") String subjectcode,
            @RequestParam(value = "sphase", required = false) String sphase,
            @RequestParam("isshortterm") String isshortterm,
            @RequestParam("isoptional") String isoptional,
            @RequestParam("coursecode") String coursecode,
            Model model,
            HttpServletRequest request) {

        String response = "1";
        try {
            MT_Userlogin user = mtUserloginService.getUserloginFromAuthentication();
            String userRole = user.getRole().getRoleCode().toUpperCase();

            if (!(List.of("A", "S", "U").contains(userRole) &&
                    mProcessesService.isProcessGranted(user.getUsercode(), 28))) {
                throw new MyAuthorizationDeniedException(
                    ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
            }

            boolean isDuplicate;
            if (subjectcode == null || subjectcode.isEmpty()) {
                // Fore NEW subject.
                isDuplicate = mSubjectService.doesSubjectExists(departmentcode, coursecode, subjectname);
            } else {
                // For UPDATE Subject
                isDuplicate = mSubjectService.doesSubjectExistWithDifferentCode(departmentcode, coursecode, subjectname, subjectcode);
            }

            if (isDuplicate) {
                return "3";
            }
            
            M_Subjects subject = new M_Subjects();
            subject.setSubjectcode(subjectcode);
            subject.setSubjectname(subjectname);
            subject.setIsoptional(isoptional);
            subject.setIsshortterm(isshortterm);

            M_Departments department = new M_Departments();
            department.setDepartmentcode(departmentcode);
            subject.setDepartmentcode(department);

            M_Course_Academics course = new M_Course_Academics();
            course.setCoursecode(coursecode);
            subject.setCoursecode(course);

            if ("1".equals(isshortterm)) {
                M_ShortTerm_Phases phase = new M_ShortTerm_Phases();
                phase.setSphaseid(sphase);
                subject.setSphaseid(phase);
                subject.setSemestercode(null);
            } else {
                M_Semesters semester = new M_Semesters();
                semester.setSemestercode(semestercode);
                subject.setSemestercode(semester);
                subject.setSphaseid(null);
            }

            HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
            try {
                if (((response = mSubjectService.saveNewSubject(subject)).equals("2"))) {
                    persistenceLogger.info("M_Subjects saved successfully by userid {}", user.getUserid());
                    audittrailService.logAuditTrail(auditMap, user.getUserid(), "m_subjects saved successfully");
                    
                    return response;
                } else 
                    throw new PersistenceException();
            } catch (Exception ex) {
                persistenceLogger.error("M_Subjects save failed.\nMessage: {}\nUserid: {}", ex.getMessage(), user.getUserid(), ex);
                audittrailService.logAuditTrail(auditMap, user.getUserid(), "m_subjects save failed");
                
                return response;
            }
        } catch (AuthenticationCredentialsNotFoundException ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        }
    }
    
    /*
     * Public endpoint
     */
    @PostMapping("/getListOfSubjects")
    @ResponseBody
    public List<Object[]> getListOfSubjects(
            @RequestParam("departmentcode") String departmentcode,
            @RequestParam(value = "semestercode", required = false) String semestercode,
            @RequestParam(value = "sphase", required = false) String sphase,
            @RequestParam("coursecode") String coursecode,
            Model model) {
        List<Object[]> list = null;
        if (sphase.length() > 0) {
            //phase
            list = mSubjectService.getSubjectListByPhaseid(departmentcode, sphase, coursecode);
        } else if (semestercode.length() > 0) {
            //sem
            list = mSubjectService.getSubjectListBySemestercode(departmentcode, semestercode, coursecode);
        }
        return list;
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role T (Student)     
     * 'Feedback' process
     */
    @GetMapping("/viewsubjects")
    @SuppressWarnings("unchecked")
    public String renderSubjectListPage(Model model, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "page");
        }

        T_Students student = tStudentsService.findByUsercode(user.getUsercode());
        List<Object[]> subjectList  = new ArrayList<>();
        JSONArray sublist = new JSONArray();

        if (student.getIsshortterm().equals("0"))
            subjectList = mSubjectService.getGeneralStudentFacultySubjectListLongterm(student.getSemestercode().getSemestercode(),
                    student.getCoursecode().getCoursecode(), student.getStudentid());
        else if (student.getIsshortterm().equals("1"))
            subjectList = mSubjectService.getGeneralStudentFacultySubjectListShortterm(student.getSphaseid().getSphaseid(),
                    student.getCoursecode().getCoursecode(), student.getStudentid());

        List<Object[]> opt = mSubjectService.getoptionalstudentfacultysubjectlist(student.getUsercode().getUsercode(),
                student.getStudentid());

        for (Object[] c : subjectList) {
            JSONObject sub = new JSONObject();
            sub.put("sname", c[0]);
            sub.put("fname", c[1]);
            sub.put("fcode", c[2]);
            sub.put("subcode", c[3]);
            sub.put("stype", "Compulsory Subject");
            sublist.add(sub);
        }

        for (Object[] c : opt) {
            JSONObject sub = new JSONObject();
            sub.put("sname", c[0]);
            sub.put("fname", c[1]);
            sub.put("fcode", c[2]);
            sub.put("subcode", c[3]);
            sub.put("stype", "Optional Subject");
            sublist.add(sub);
        }

        model.addAttribute("sublist", sublist);
        model.addAttribute("studentid", student.getStudentid());

        return "pages/t_students/subject-list";
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role T (Student)     
     * Download Study Materials
     */
    @GetMapping("/viewstudymaterials")
    public String renderStudyMaterialsPage(Model model, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "page");
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

        // Get the optional subjects (using your original method)
        List<Object[]> optionalSubjects = mSubjectService.getStudentSubjectsList(user.getUsercode());

        // Combine both lists
        allSubjects.addAll(compulsorySubjects);
        allSubjects.addAll(optionalSubjects);

        // Add the combined list to the model
        model.addAttribute("sublist", allSubjects);

        return "pages/t_students/study-materials";
    }

    
    @PostMapping("/student-subjects")
    @SuppressWarnings("unchecked")
    public ResponseEntity<List<JSONArray>> getSubjectListForStudents(
        @RequestParam("departmentcode") String departmentcode,
        @RequestParam("isshortterm") String isshortterm,
        @RequestParam("spcode") String spcode,
        @RequestParam("coursecode") String coursecode
    ) {
        if (
            departmentcode == null || departmentcode.isBlank() ||
            isshortterm == null || isshortterm.isBlank() ||
            spcode == null || spcode.isBlank() ||
            coursecode == null || coursecode.isBlank()
        )
            return ResponseEntity.badRequest().build();

        List<M_Subjects> subjects = null;
        JSONArray result = new JSONArray();

        if (isshortterm.trim().equals("0"))
            subjects = mSubjectService.getSubjectsDepartmentSemester(departmentcode, spcode, coursecode);
        else if (isshortterm.equals("1"))
            subjects = mSubjectService.getSubjectsDepartmentPhase(departmentcode, spcode, coursecode);
        else
            return ResponseEntity.badRequest().build();

        if (subjects != null) {
            for (M_Subjects subject : subjects) {
                JSONObject sub = new JSONObject();
                sub.put("subjectcode", subject.getSubjectcode());
                sub.put("subjectname", subject.getSubjectname());
                sub.put("semcode", subject.getSemestercode());
                sub.put("phcode", subject.getSphaseid());
                sub.put("isopt", subject.getIsoptional());
                result.add(sub);
            }
            return ResponseEntity.ok(List.of(result));
        } else
            return ResponseEntity.ok(List.of(new JSONArray()));
    }

    @GetMapping("/subjectsByCourseAndFaculty")
    @ResponseBody
    public List<M_Subjects> getsubjectsByCourse(@RequestParam(name="coursecode") String coursecode, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "page");
        }
        return mSubjectService.findSubjectsByCourseCodeFacultyWise(coursecode, user.getUsercode());
    }
    /*
     * REST endpoint for Flutter app
     * GET /subjects/api/viewsubjects
     * Returns subject list as JSON for the logged-in student
     */
    @GetMapping("/api/viewsubjects")
    @ResponseBody
    @SuppressWarnings("unchecked")
    public ResponseEntity<JSONArray> getSubjectListForStudent(HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            return ResponseEntity.status(401).build();
        }

        T_Students student = tStudentsService.findByUsercode(user.getUsercode());
        List<Object[]> subjectList = new ArrayList<>();
        JSONArray sublist = new JSONArray();

        if (student.getIsshortterm().equals("0"))
            subjectList = mSubjectService.getGeneralStudentFacultySubjectListLongterm(
                    student.getSemestercode().getSemestercode(),
                    student.getCoursecode().getCoursecode(),
                    student.getStudentid());
        else if (student.getIsshortterm().equals("1"))
            subjectList = mSubjectService.getGeneralStudentFacultySubjectListShortterm(
                    student.getSphaseid().getSphaseid(),
                    student.getCoursecode().getCoursecode(),
                    student.getStudentid());

        List<Object[]> opt = mSubjectService.getoptionalstudentfacultysubjectlist(
                student.getUsercode().getUsercode(),
                student.getStudentid());

        for (Object[] c : subjectList) {
            JSONObject sub = new JSONObject();
            sub.put("subcode", c[3]);
            sub.put("sname", c[0]);
            sub.put("fname", c[1]);
            sub.put("fcode", c[2]);
            sub.put("stype", "Compulsory Subject");
            sublist.add(sub);
        }

        for (Object[] c : opt) {
            JSONObject sub = new JSONObject();
            sub.put("subcode", c[3]);
            sub.put("sname", c[0]);
            sub.put("fname", c[1]);
            sub.put("fcode", c[2]);
            sub.put("stype", "Optional Subject");
            sublist.add(sub);
        }

        return ResponseEntity.ok(sublist);
    }
     @PostMapping("/feedbacks/submit")
     @ResponseBody
     public ResponseEntity<String> submitFeedback(
         @RequestParam("subjectcode") String subjectcode,
         @RequestParam("facultycode") String facultycode,
         @RequestParam("feedback") String feedback,
         HttpServletRequest request) {

         MT_Userlogin user = mtUserloginService.getUserloginFromAuthentication();
         T_Students student = tStudentsService.findByUsercode(user.getUsercode());

         // save feedback to your feedback table here
         // feedbackService.save(student, subjectcode, facultycode, feedback);

         return ResponseEntity.ok("0"); // "0" = success
     }
    
}
