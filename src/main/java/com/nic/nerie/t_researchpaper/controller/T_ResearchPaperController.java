package com.nic.nerie.t_researchpaper.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nic.nerie.exceptions.MyAuthenticationCredentialsNotFoundException;
import com.nic.nerie.exceptions.MyAuthorizationDeniedException;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.mt_userlogin.service.MT_UserloginService;
import com.nic.nerie.t_facultyprofile.model.T_FacultyProfile;
import com.nic.nerie.t_facultyprofile.service.T_FacultyProfileService;
import com.nic.nerie.t_researchpaper.model.T_ResearchPaper;
import com.nic.nerie.t_researchpaper.service.T_ResearchPaperService;
import com.nic.nerie.utils.ExceptionUtil;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/research-paper")
public class T_ResearchPaperController {
    
    private final T_FacultyProfileService tFacultyProfileService;
    private final MT_UserloginService mtUserloginService;
    private final T_ResearchPaperService tResearchPaperService;

    public T_ResearchPaperController(T_FacultyProfileService tFacultyProfileService,
            MT_UserloginService mtUserloginService, T_ResearchPaperService tResearchPaperService) {
        this.tFacultyProfileService = tFacultyProfileService;
        this.mtUserloginService = mtUserloginService;
        this.tResearchPaperService = tResearchPaperService;
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<String> addResearchPaper(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String journal,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String publisher,
            @RequestParam(required = false) String authors,
            @RequestParam(required = false) String link,
            HttpServletRequest request
    ) {

        MT_Userlogin user;

        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(
                            request.getRequestURI(),
                            "Add Research Paper, " + request.getMethod()),
                    "page");
        }

        if (!"U".equalsIgnoreCase(user.getRole().getRoleCode())) {
            throw new MyAuthorizationDeniedException(
                    ExceptionUtil.generateAuthorizationDeniedMessage(
                            request.getRequestURI(),
                            "Add Research Paper, " + request.getMethod(),
                            user.getUserid()),
                    "page");
        }

        T_FacultyProfile profile = tFacultyProfileService.getFacultyProfileByUsercode(user.getUsercode());

        T_ResearchPaper paper = new T_ResearchPaper();
        paper.setTitle(title);
        paper.setJournal(journal);
        paper.setYear(year);
        paper.setCategory(category);
        paper.setPublisher(publisher);
        paper.setAuthors(authors);
        paper.setLink(link);
        paper.setFacultyProfile(profile);

        boolean result = tResearchPaperService.saveResearchPaper(paper);

        return ResponseEntity.ok(result ? "1" : "-1");
    }

    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity<String> deleteResearchPaper(
            @RequestParam String researchpaperid
    ) {

        boolean result = tResearchPaperService.deleteResearchPaper(researchpaperid);

        return ResponseEntity.ok(result ? "1" : "0");
    }

    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<String> updateResearchPaper(
            @RequestParam String researchpaperid,
            @RequestParam String title,
            @RequestParam String journal,
            @RequestParam Integer year,
            @RequestParam String category,
            @RequestParam String publisher,
            @RequestParam String authors,
            @RequestParam String link
    ) {

        boolean result = tResearchPaperService.updateResearchPaper(
                researchpaperid,
                title,
                journal,
                year,
                category,
                publisher,
                authors,
                link
        );

        return ResponseEntity.ok(result ? "1" : "0");
    }
}
