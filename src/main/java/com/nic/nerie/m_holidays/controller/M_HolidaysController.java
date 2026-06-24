package com.nic.nerie.m_holidays.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nic.nerie.audittrail.service.AudittrailService;
import com.nic.nerie.exceptions.MyAuthenticationCredentialsNotFoundException;
import com.nic.nerie.exceptions.MyAuthorizationDeniedException;
import com.nic.nerie.m_holidays.model.M_Holidays;
import com.nic.nerie.m_holidays.service.M_HolidaysService;
import com.nic.nerie.m_processes.service.M_ProcessesService;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.mt_userlogin.service.MT_UserloginService;
import com.nic.nerie.utils.ExceptionUtil;
import com.nic.nerie.utils.UtilCommon;

import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/holidays")
public class M_HolidaysController {
    private final M_HolidaysService mHolidaysService;
    private final MT_UserloginService mtUserloginService;
    private final M_ProcessesService mProcessesService;
    private final AudittrailService audittrailService;
    private static final Logger persistenceLogger = LoggerFactory.getLogger("DATA_PERSISTENCE_LOGGER");
    
    @Autowired
    public M_HolidaysController(
        M_HolidaysService mHolidaysService, 
        MT_UserloginService mtUserloginService,
        M_ProcessesService mProcessesService,
        AudittrailService audittrailService
    ) {
        this.mHolidaysService = mHolidaysService;
        this.mtUserloginService = mtUserloginService;
        this.mProcessesService = mProcessesService;
        this.audittrailService = audittrailService;
    }
    
    /*
     * Secureed endpoint
     * This endpoint is exclusive to roles A (Local-admin) & U (Co-ordinator faculty)
     * 'Initialize Holiday List' process (processcode = 22)
     */
    @GetMapping("/init")
    public String renderHolidayInitializePage(Model model, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "Initialize Holiday List, " + request.getMethod()), "page");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!(
            List.of("A", "U").contains(userRole) &&
            mProcessesService.isProcessGranted(user.getUsercode(), 22)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "Initialize Holiday List, " + request.getMethod(), user.getUserid()), "page");
        }

        switch (user.getRole().getRoleCode().toUpperCase()) {
            case "A":
                model.addAttribute("layoutPath", "layouts/local-admin-layout");
                break;
            case "U":
                model.addAttribute("layoutPath", "layouts/coordinator-faculty-layout");
                break;
        }

        model.addAttribute("fyList", mHolidaysService.getapproveCCFY(user.getUsercode()));

        return "pages/init-holiday";
    }

    /*
     * Public endpoint
     */
    @PostMapping("/list")
    public ResponseEntity<List<Object[]>> getHolidayList(@RequestParam("finyearstart") String finyearstart,
                                                         @RequestParam("finyearend") String finyearend) {
        if (finyearstart == null || finyearstart.isBlank() || finyearend == null || finyearend.isBlank())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.emptyList());

        List<Object[]> holidaysList = mHolidaysService.getFYHolidayList(finyearstart, finyearend);

        return ResponseEntity.ok(holidaysList);
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to roles A (Local-admin) & U (Co-ordinator faculty)
     * Endpoint tied with 'Initialize Holiday List' process (processcode = 22)
     */
    @PostMapping("/save")
    public ResponseEntity<String> saveHolidayDetails(@RequestParam("holidaydate") String holidaydate,
                                                     @RequestParam("holidayreason") String holidayreason,
                                                     @RequestParam(value = "financialyear", required = false) String financialyear,
                                                     @RequestParam(value = "oldholidaydate", required = false) String oldholidaydate,
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
                        mProcessesService.isProcessGranted(user.getUsercode(), 22)
        )) {
            throw new MyAuthorizationDeniedException(
                    ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }

        // validating required fields
        // NOTE: [Old] holidaydate & oldholidaydate params are initialized to same value before making POST...
        // NOTE: [Old] both financialyear & oldholidaydate are no longer used...
        if (holidaydate == null || holidaydate.isBlank() || holidayreason == null || holidayreason.isBlank())
            return ResponseEntity.badRequest().body("Required fields are missing");

        // validating holidaydate
        Date parsedHolidayDate;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            dateFormat.setLenient(false);
            parsedHolidayDate = dateFormat.parse(holidaydate);
        } catch (ParseException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Invalid date format");
        }

        // validating holidayreason
        if (holidayreason.trim().length() > 255)
            return ResponseEntity.badRequest().body("Holiday reason must not exceed 255 characters");

        // preparing M_Holidays instance for persisting
        M_Holidays newHoliday = new M_Holidays();
        newHoliday.setHolidaydate(parsedHolidayDate);
        newHoliday.setHolidayreason(holidayreason);
        newHoliday.setMtuserlogin(user);

        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
        try {
            if ((newHoliday = mHolidaysService.saveHolidayDetails(newHoliday)) != null) {
                persistenceLogger.info("M_Holidays with holidaydate {} saved successfully by userid {}", newHoliday.getHolidaydate(), user.getUserid());
                audittrailService.logAuditTrail(auditMap, user.getUserid(), "m_holidays with holidaydate " + newHoliday.getHolidaydate() + " saved successfully");

                return ResponseEntity.ok("2");
            } else
                throw new PersistenceException();
        } catch (RuntimeException ex) {
            persistenceLogger.error("M_Holidays save failed.\nMessage: {}\nUserid: {}", ex.getMessage(), user.getUserid(), ex);
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "m_holidays save failed");

            return ResponseEntity.ok("");
        }
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to roles A (Local-admin) & U (Co-ordinator faculty)
     * Endpoint tied with 'Initialize Holiday List' process (processcode = 22)
     */
    @PostMapping("/remove")
    public ResponseEntity<String> removeHolidayDetails(@RequestParam("holidaydate") String holidaydate, HttpServletRequest request) {
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
            mProcessesService.isProcessGranted(user.getUsercode(), 22)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }

        // validating required fields
        // validating holidaydate
        if (holidaydate == null || holidaydate.isBlank())
            return ResponseEntity.badRequest().body("Required parameters missing");

        Date parsedDate;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            parsedDate = dateFormat.parse(holidaydate);
        } catch (ParseException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Invalid date format");
        }

        // deleting existing M_Holidays entity from database
        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
        try {
            mHolidaysService.removeHolidayDetailsByHolidaydate(parsedDate);
            persistenceLogger.info("M_Holidays with holidaydate {} deleted successfully by userid {}", parsedDate, user.getUserid());
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "m_holidays with holidaydate " + parsedDate + " deleted successfully");

            return ResponseEntity.ok("1");
        } catch (Exception ex) {
            persistenceLogger.error("M_Holidays delete failed.\nMessage: {}\nUserid: {}", ex.getMessage(), user.getUserid(), ex);
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "m_holidays delete failed");

            return ResponseEntity.ok("");
        }
    }
}
