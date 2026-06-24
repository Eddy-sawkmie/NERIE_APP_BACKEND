package com.nic.nerie.m_departments.controller;

import com.nic.nerie.m_departments.model.M_Departments;
import com.nic.nerie.m_departments.service.M_DepartmentsService;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.mt_userlogin.service.MT_UserloginService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/departments")
public class M_DepartmentsController {
    private final MT_UserloginService mtUserloginService;
    private final M_DepartmentsService mDepartmentsService;

    @Autowired
    public M_DepartmentsController(MT_UserloginService mtUserloginService, M_DepartmentsService mDepartmentsService) {
        this.mtUserloginService = mtUserloginService;
        this.mDepartmentsService = mDepartmentsService;
    }

    @PostMapping("/saveDepartments")
    @ResponseBody
    public ResponseEntity<String> saveDepartments(@ModelAttribute("macademicsubject") M_Departments mdept, Model model) {
        try {
            // Get authenticated user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            MT_Userlogin user = mtUserloginService.getUserloginFromAuthentication(auth);

            // Check if user is authenticated and is faculty
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("-1");
            }

            // Validate department name
            String deptName = mdept.getDepartmentname();
            if (deptName == null || deptName.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("3"); // Empty department name
            }

            if (deptName.trim().length() > 100) {
                return ResponseEntity.badRequest().body("4"); // Invalid length
            }

            // Check if department already exists
            if (mDepartmentsService.checkDepartmentExist(mdept)) {
                return ResponseEntity.ok("1"); // Already exists
            }

            mdept.setMoffices(user.getMoffices());

            // Save department
            boolean isSaved = mDepartmentsService.saveDepartmentDetails(mdept);
            if (isSaved) {
                return ResponseEntity.ok("2"); // Successfully saved
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("-1"); // Save failed
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("-1"); // General error
        }
    }

    //flutter API
    @GetMapping("/api/list")
    @ResponseBody
    public ResponseEntity<List<M_Departments>> getDepartmentList(HttpServletRequest request) {
        try {
            MT_Userlogin user = mtUserloginService.getUserloginFromAuthentication();
            String officeCode = user.getMoffices().getOfficecode();
            List<M_Departments> departments = mDepartmentsService.getDepartmentList(officeCode);
            return ResponseEntity.ok(departments);
        } catch (Exception ex) {
            return ResponseEntity.status(401).build();
        }
    }
    // In M_DepartmentsController (or similar)
    @GetMapping("/getAllJson")
    @ResponseBody
    public List<M_Departments> getAllDepartments(HttpServletRequest request) {
        MT_Userlogin user = mtUserloginService.getUserloginFromAuthentication();
        return mDepartmentsService.getDepartmentList(user.getMoffices().getOfficecode());
    }


}
