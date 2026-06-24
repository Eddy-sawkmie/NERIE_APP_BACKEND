package com.nic.nerie.t_faculties.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.nic.nerie.captcha.service.CaptchaService;
import com.nic.nerie.m_departments.model.M_Departments;
import com.nic.nerie.m_designations.model.M_Designations;
import com.nic.nerie.m_subjects.model.M_Subjects;
import com.nic.nerie.t_facultyprofile.model.T_FacultyProfile;
import com.nic.nerie.t_facultyprofile.service.T_FacultyProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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
import com.nic.nerie.m_departments.service.M_DepartmentsService;
import com.nic.nerie.m_designations.service.M_DesignationsService;
import com.nic.nerie.m_offices.model.M_Offices;
import com.nic.nerie.m_processes.service.M_ProcessesService;
import com.nic.nerie.m_subjects.service.M_SubjectService;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.mt_userlogin.service.MT_UserloginService;
import com.nic.nerie.t_faculties.model.T_Faculties;
import com.nic.nerie.t_faculties.service.T_FacultiesService;
import com.nic.nerie.utils.ExceptionUtil;
import com.nic.nerie.utils.UtilCommon;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/faculties")
public class T_FacultiesController {
    private final MT_UserloginService mtUserloginService;
    private final M_DepartmentsService mDepartmentsService;
    private final M_DesignationsService mDesignationsService;
    private final T_FacultiesService tFacultiesService;
    private final M_SubjectService mSubjectService;
    private final M_ProcessesService mProcessesService;
    private final AudittrailService audittrailService;
    private final CaptchaService captchaService;
    private final T_FacultyProfileService tFacultyProfileService;

    private final Logger persistenceLogger = LoggerFactory.getLogger("DATA_PERSISTENCE_LOGGER");

    @Autowired
    public T_FacultiesController(
        MT_UserloginService mtUserloginService, 
        M_DepartmentsService mDepartmentsService, 
        M_DesignationsService mDesignationsService, 
        T_FacultiesService tFacultiesService, 
        M_SubjectService mSubjectService, 
        M_ProcessesService mProcessesService,
        AudittrailService audittrailService,
        CaptchaService captchaService,
        T_FacultyProfileService tFacultyProfileService
    ) {
        this.mtUserloginService = mtUserloginService;
        this.mDepartmentsService = mDepartmentsService;
        this.mDesignationsService = mDesignationsService;
        this.tFacultiesService = tFacultiesService;
        this.mSubjectService = mSubjectService;
        this.mProcessesService = mProcessesService;
        this.audittrailService = audittrailService;
        this.captchaService = captchaService;
        this.tFacultyProfileService = tFacultyProfileService;
    }

    /*
     * Public endpoint
     */
    @GetMapping("/research-list")
    public String renderFacultyResearchList(Model model) {
        // This ensures the login modal has a valid captcha immediately on page load
        if (!model.containsAttribute("captchaPrincipal")) {
            model.addAttribute("captchaPrincipal", captchaService.generateNewCaptcha());
        }
        model.addAttribute("currentPage", "faculty");
        model.addAttribute("facultylist", tFacultiesService.getFacultyResearchList("2"));
        return "pages/landing/faculty-research-list";
    }

    /*
     * Public endpoint
     */
    @GetMapping("/research-details")
    public String renderFacultyResearchDetails(@RequestParam("aid") String aid, 
                                               @RequestParam(value = "fname", required = false) String fname,
                                        Model model) {
        // This ensures the login modal has a valid captcha immediately on page load
        if (!model.containsAttribute("captchaPrincipal")) {
            model.addAttribute("captchaPrincipal", captchaService.generateNewCaptcha());
        }

        model.addAttribute("currentPage", "faculty");
        
        // Add the name to the model. Fallback to "Faculty Profile" if it's missing from the URL.
        model.addAttribute("facultyName", fname != null ? fname : "Faculty Profile");

        T_FacultyProfile facultyProfile = tFacultyProfileService.getFacultyProfileByUsercode(aid);
        if(facultyProfile == null || facultyProfile.getFacultyprofileid() == null || facultyProfile.getFacultyprofileid().isEmpty()) {
            facultyProfile = new T_FacultyProfile();
            facultyProfile.setAcademicqualification("No Details Available Yet");
            facultyProfile.setAreaofinterest("No Details Available Yet");
            facultyProfile.setAreaofspecialization("No Details Available Yet");
            facultyProfile.setBriefprofile("No Details Available Yet");
            facultyProfile.setGscholarlink("No Details Available Yet");
            facultyProfile.setResearchprojects("No Details Available Yet");
            facultyProfile.setOrcid("No Details Available Yet");
        }
        // Handle research papers
        if (facultyProfile.getResearchpapers() == null || facultyProfile.getResearchpapers().isEmpty()) {
            model.addAttribute("rpMessage", "No research papers yet");
        } else {
            model.addAttribute("rpList", facultyProfile.getResearchpapers());
        }

        // Program Details
        List<Object[]> programDetails = tFacultyProfileService.findProgramDetailsForFacultyProfile(aid);

        if (programDetails == null || programDetails.isEmpty()) {
            model.addAttribute("programMessage", "No programs available");
        } else {
            model.addAttribute("programList", programDetails);
        }
        model.addAttribute("prof", facultyProfile);
        model.addAttribute("faculty", "active");
        return "pages/landing/faculty-research-details";
    }

    /*
     * Secured endpoint
     * Endpoint exclusive to role A (Local-admin) & U (Coordinator-faculty)
     * 'Manage Faculties' process (processcode = 30)
     */
    @GetMapping("/register-faculties")
    public String renderFacultiesDetails(@ModelAttribute("tfaculty") T_Faculties fac, Model model, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "Manage Faculties, " + request.getMethod()), "page");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();
        String officeCode = user.getMoffices().getOfficecode();

        if (!(
            List.of("A", "U").contains(userRole) &&
            mProcessesService.isProcessGranted(user.getUsercode(), 30)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "Manage Faculties, " + request.getMethod(), user.getUserid()), "page");
        }
        
        if (user.getUserrole().equals("A")) {
            model.addAttribute("faculties", mtUserloginService.getFacultyCandidates(officeCode));
            model.addAttribute("departments", mDepartmentsService.getDepartments(officeCode));
            model.addAttribute("designation", mDesignationsService.getDesignations());
            model.addAttribute("allfaculties", tFacultiesService.getFacultySubjectsList(officeCode));
            //model.addAttribute("allsubjects", mSubjectService.getAllSubjectList());
            model.addAttribute("layoutPath", "layouts/local-admin-layout");
        } else {
            model.addAttribute("faculties", mtUserloginService.getFacultyCandidatesByUser(user.getUsercode(), officeCode)); //get only currently logged in user
            model.addAttribute("departments", mDepartmentsService.getDepartments(officeCode));
            model.addAttribute("designation", mDesignationsService.getDesignations());
            model.addAttribute("allfaculties", tFacultiesService.getFacultySubjectsListByUser(user.getUsercode(), officeCode)); //get only currently logged in user
            //model.addAttribute("allsubjects", mSubjectService.getAllSubjectList());
            model.addAttribute("layoutPath", "layouts/coordinator-faculty-layout");
        }

        return "pages/register-faculties";
    }

    @GetMapping("/by-courses")
    @ResponseBody
    public List<M_Subjects> getSubjectsForCourses(@RequestParam("courseCodes") List<String> courseCodes) {
        return mSubjectService.getSubjectsByCourseCodes(courseCodes);
    }

    /*
     * Public endpoint
     */
    @GetMapping("/facultyDetails")
    @ResponseBody
    public List<Object[]> facultydetails(@RequestParam("usercode") String usercode) {
        return tFacultiesService.getFacultyDetails(usercode);
    }

    /*
     * Public endpoint
     */
    @GetMapping("/facultySubjects")
    @ResponseBody
    public ResponseEntity<List<Object[]>> facultySubjects(@RequestParam String usercode) {
        List<Object[]> subjects = mSubjectService.getSubjectsListByFaculty(usercode);
        return ResponseEntity.ok(subjects);
    }

    /*
     * Secured endpoint
     * Endpoint exclusive to role A (Local-admin) & U (Coordinator-faculty)
     * 'Manage Faculties' process (processcode = 30)
     */
    @PostMapping("/createEditFaculty")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<String> createFaculty(@ModelAttribute("tfaculty") T_Faculties fac,
                                                @RequestParam("subject") String[] subjects,
                                                @RequestParam("course") String[] courses,
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
                        mProcessesService.isProcessGranted(user.getUsercode(), 30)
        )) {
            throw new MyAuthorizationDeniedException(
                    ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }

        String res = "-1";
        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
        try {
            M_Offices offices = new M_Offices();

            offices.setOfficecode(user.getMoffices().getOfficecode());
            fac.setOfficecode(offices);

            // Create/update faculty
            String facultyId = tFacultiesService.createFaculty(fac);

            if ("-1".equals(facultyId)) {
                throw new RuntimeException("Faculty creation/update failed");
            }

            persistenceLogger.info("T_Faculties with facultyid {} saved successfully by userid {}", facultyId, user.getUserid());
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_faculties with facultyid " + facultyId + " saved successfully");

            if (subjects != null) {
                res = tFacultiesService.saveFacultySubjects(fac.getUsercode().getUsercode(), subjects);

                if (!res.equals("-1")) {
                    persistenceLogger.info("t_faculty_subject saved successfully by userid {}", user.getUserid());
                    audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_faculty_subject saved successfully");
                } else {
                    throw new RuntimeException("t_faculty_subject save failed by userid " + user.getUserid());
                }
            }

            if (courses != null) {
                res = tFacultiesService.saveFacultyCourses(fac.getUsercode().getUsercode(), courses);

                if (!res.equals("-1")) {
                    persistenceLogger.info("t_faculty_courses saved successfully by userid {}", user.getUserid());
                    audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_faculty_courses saved successfully");
                } else {
                    throw new RuntimeException("t_faculty_courses save failed by userid " + user.getUserid());
                }
            }

            // Check if this is a new faculty (not an update)
            if (facultyId == null || "".equals(facultyId)) {
                T_Faculties faculty = tFacultiesService.getFacultyByFacultyID(facultyId);
                List<Integer> processids = mProcessesService.getMenuProcesses(8);
                boolean isSavedSuccessfully = true;

                for (Integer x : processids) {
                    String processRes = mProcessesService.createUserProcess(faculty.getUsercode().getUsercode(), x);

                    if (processRes.equals("-1")) {
                        isSavedSuccessfully = false;
                        break;
                    }
                }

                if (isSavedSuccessfully) {
                    persistenceLogger.info("mt_userprocesses saved successfully by userid {}", user.getUserid());
                    audittrailService.logAuditTrail(auditMap, user.getUserid(), "mt_userprocesses saved successfully");
                } else {
                    throw new RuntimeException("mt_userprocesses save failed by userid " + user.getUserid());
                }
            }

            return ResponseEntity.ok(res);
        } catch (Exception ex) {
            persistenceLogger.error("""
                T_Faculties save failed. 
                Message: {}
                Userid: {}
            """, ex.getMessage(), user.getUserid(), ex);
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "T_Faculties save failed");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("-1");
        }
    }



    /*
     * JSON endpoints for mobile app
     * These don't interfere with the web page functionality
     */

    @GetMapping("/faculties-json")
    @ResponseBody
    public List<Object[]> getFacultyListJson(HttpServletRequest request) {
        MT_Userlogin user = mtUserloginService.getUserloginFromAuthentication();
        String officeCode = user.getMoffices().getOfficecode();

        if (user.getUserrole().equals("A")) {
            return tFacultiesService.getFacultySubjectsList(officeCode);
        } else {
            return tFacultiesService.getFacultySubjectsListByUser(user.getUsercode(), officeCode);
        }
    }

    @GetMapping("/candidates-json")
    @ResponseBody
    public List<Object[]> getFacultyCandidatesJson(HttpServletRequest request) {
        MT_Userlogin user = mtUserloginService.getUserloginFromAuthentication();
        String officeCode = user.getMoffices().getOfficecode();

        if (user.getUserrole().equals("A")) {
            return mtUserloginService.getFacultyCandidates(officeCode);
        } else {
            return mtUserloginService.getFacultyCandidatesByUser(user.getUsercode(), officeCode);
        }
    }

    @GetMapping("/departments-json")
    @ResponseBody
    public List<M_Departments> getDepartmentsJson(HttpServletRequest request) {
        MT_Userlogin user = mtUserloginService.getUserloginFromAuthentication();
        String officeCode = user.getMoffices().getOfficecode();
        return mDepartmentsService.getDepartments(officeCode);
    }

    @GetMapping("/designations-json")
    @ResponseBody
    public List<M_Designations> getDesignationsJson() {
        return mDesignationsService.getDesignations();
    }



}
