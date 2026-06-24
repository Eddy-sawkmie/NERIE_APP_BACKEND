package com.nic.nerie.t_facultyprofile.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nic.nerie.exceptions.MyAuthenticationCredentialsNotFoundException;
import com.nic.nerie.exceptions.MyAuthorizationDeniedException;
import com.nic.nerie.m_processes.service.M_ProcessesService;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.mt_userlogin.service.MT_UserloginService;
import com.nic.nerie.t_faculties.model.T_Faculties;
import com.nic.nerie.t_facultyprofile.model.T_FacultyProfile;
import com.nic.nerie.t_facultyprofile.service.T_FacultyProfileService;
import com.nic.nerie.utils.ExceptionUtil;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/faculty-research-profile")
public class T_FacultyProfileController {

    private final T_FacultyProfileService tFacultyProfileService;
    private final MT_UserloginService mtUserloginService;
    private final M_ProcessesService mProcessesService;

    public T_FacultyProfileController(T_FacultyProfileService tFacultyProfileService,
            MT_UserloginService mtUserloginService, M_ProcessesService mProcessesService) {
        this.tFacultyProfileService = tFacultyProfileService;
        this.mtUserloginService = mtUserloginService;
        this.mProcessesService = mProcessesService;
    }

    /*
     * Secured endpoint
     * Endpoint exclusive to role U (Coordinator-faculty)
     * 'Manage Faculty Profile' process (processcode = 47)
     */
    @GetMapping("/update")
    public String changeFacultyResearchProfile(@ModelAttribute("tfaculty") T_Faculties fac, Model model, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "Change Faculty Research Profile, " + request.getMethod()), "page");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!(
            List.of("U").contains(userRole) &&
            //mProcessesService.isProcessGranted(user.getUsercode(), 47)
            user.getIscurrentfaculty().equals("1")
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "Change Faculty Research Profile, " + request.getMethod(), user.getUserid()), "page");
        }
        T_FacultyProfile profile = tFacultyProfileService.getFacultyProfileByUsercode(user.getUsercode());
        model.addAttribute("profile",profile);
        model.addAttribute("layoutPath", "layouts/coordinator-faculty-layout");
        

        return "pages/change-faculty-research-profile";
    }

    @PostMapping("/post")
    @ResponseBody
    public ResponseEntity<String> postFacultyResearchProfile(
            @RequestParam(required = false) String gscholarlink,
            @RequestParam(required = false) String orcid,
            @RequestParam(required = false) String academicqualification,
            @RequestParam(required = false) String areaofspecialization,
            @RequestParam(required = false) String areaofinterest,
            @RequestParam(required = false) String briefprofile,
            @RequestParam(required = false) String researchprojects,
            HttpServletRequest request
    ) {

        MT_Userlogin user;

        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(
                            request.getRequestURI(),
                            "Post Faculty Research Profile, " + request.getMethod()),
                    "page");
        }

        T_FacultyProfile profile = tFacultyProfileService.getFacultyProfileByUsercode(user.getUsercode());

        if (profile == null) {
            profile = new T_FacultyProfile();
            profile.setUsercode(user);
        }

        profile.setAcademicqualification(academicqualification);
        profile.setAreaofinterest(areaofinterest);
        profile.setAreaofspecialization(areaofspecialization);
        profile.setBriefprofile(briefprofile);
        profile.setGscholarlink(gscholarlink);
        profile.setOrcid(orcid);
        profile.setResearchprojects(researchprojects);

        boolean result = tFacultyProfileService.saveFacultyResearchProfile(profile);

        return ResponseEntity.ok(result ? "1" : "2");
    }

}
