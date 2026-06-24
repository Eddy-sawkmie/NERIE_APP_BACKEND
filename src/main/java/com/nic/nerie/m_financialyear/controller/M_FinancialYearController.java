package com.nic.nerie.m_financialyear.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nic.nerie.exceptions.MyAuthenticationCredentialsNotFoundException;
import com.nic.nerie.exceptions.MyAuthorizationDeniedException;
import com.nic.nerie.m_districts.service.M_DistrictsService;
import com.nic.nerie.m_financialyear.model.M_FinancialYear;
import com.nic.nerie.m_financialyear.service.M_FinancialYearService;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.mt_userlogin.service.MT_UserloginService;
import com.nic.nerie.utils.ExceptionUtil;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@Controller
@RequestMapping("/financial-year")
public class M_FinancialYearController {
    private final MT_UserloginService mtUserloginService;
    private final M_FinancialYearService mFinancialYearService;

    public M_FinancialYearController(MT_UserloginService mtUserloginService,
            M_FinancialYearService mFinancialYearService) {
        this.mtUserloginService = mtUserloginService;
        this.mFinancialYearService = mFinancialYearService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // The format matching your HTML <input type="date">
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        
        // Tell Spring to use this formatter for all java.util.Date fields
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role S (Admin)
     * '' process (processcode = )
     */
    @GetMapping()
    public String getFinancialYearPage(Model model, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "Add/Edit Designation, " + request.getMethod()), "page");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();
        switch (userRole) {
            // case "A":
            //     model.addAttribute("layoutPath", "layouts/local-admin-layout");
            //     break;
            case "S":
                model.addAttribute("layoutPath", "layouts/admin-layout");
                break;
        }
        model.addAttribute("fyList", mFinancialYearService.getfy());
        model.addAttribute("newFY", new M_FinancialYear());
        return "pages/admin/financial-year";
    }

    @PostMapping("/save")
    public String saveFinancialYear(@ModelAttribute("newFY") M_FinancialYear newFY, RedirectAttributes redirectAttributes) {
        try {
            mFinancialYearService.saveFinancialYear(newFY);
            redirectAttributes.addFlashAttribute("successMessage", "Financial Year saved successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to save the Financial Year. Please try again.");
        }
        return "redirect:/financial-year"; // Change this to match your actual GET URL path
    }
    
    
}
