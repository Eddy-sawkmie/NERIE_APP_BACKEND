package com.nic.nerie.mt_userloginrole.controller;

import com.nic.nerie.audittrail.service.AudittrailService;
import com.nic.nerie.exceptions.MyAuthenticationCredentialsNotFoundException;
import com.nic.nerie.exceptions.MyAuthorizationDeniedException;
import com.nic.nerie.m_processes.service.M_ProcessesService;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.mt_userlogin.service.MT_UserloginService;
import com.nic.nerie.mt_userloginrole.service.MT_UserloginRoleService;
import com.nic.nerie.utils.ExceptionUtil;
import com.nic.nerie.utils.UtilCommon;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/role-processes")
public class MT_UserRoleProcessController {
    private final MT_UserloginService mtUserloginService;
    private final M_ProcessesService mProcessesService;
    private final MT_UserloginRoleService mtUserloginRoleService;
    private final AudittrailService audittrailService;

    private static final Logger dataPersistenceLogger = LoggerFactory.getLogger("DATA_PERSISTENCE_LOGGER");

    @Autowired
    public MT_UserRoleProcessController(MT_UserloginService mtUserloginService, M_ProcessesService mProcessesService, MT_UserloginRoleService mtUserloginRoleService, AudittrailService audittrailService) {
        this.mtUserloginService = mtUserloginService;
        this.mProcessesService = mProcessesService;
        this.mtUserloginRoleService = mtUserloginRoleService;
        this.audittrailService = audittrailService;
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to S (Admin)
     */
    @GetMapping("/map")
    public String renderMapRoleProcessesPage(Model model, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception e) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        }

        if (!user.getRole().getRoleCode().equalsIgnoreCase("S")) {
            throw new MyAuthorizationDeniedException(
                    ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "Map Role to Processes, " + request.getMethod(), user.getUserid()), "page");
        }

        model.addAttribute("processes", mProcessesService.getActualAllProcesses());
        model.addAttribute("roles", mtUserloginRoleService.getRolesForProcessMapping());

        return "pages/admin/map-role-process";
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to S (Admin)
     */
    @PostMapping("/save")
    @ResponseBody
    public String saveRoleProcesses(@RequestParam("roleid") Integer roleid,
                                    @RequestParam("processcodes") List<Integer> processcodes,
                                    HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception e) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        }

        if (!user.getRole().getRoleCode().equalsIgnoreCase("S")) {
            throw new MyAuthorizationDeniedException(
                    ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "Map Role to Processes, " + request.getMethod(), user.getUserid()), "page");
        }

        // Collect client details for audit trail
        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);

        try {
            mtUserloginRoleService.saveRoleProcessMapping(roleid, processcodes);

            // Logging success
            dataPersistenceLogger.info("Role-process mapping updated successfully. RoleId: {}, ProcessCodes: {}, UpdatedByUserId: {}", roleid, processcodes, user.getUserid());

            // Audit trail success entry
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "Updated process mappings for roleId " + roleid);

            return "success";

        } catch (Exception e) {

            // Logging failure
            dataPersistenceLogger.error("Failed to update role-process mappings. RoleId: {}, ProcessCodes: {}, UserId: {}, Message: {}", roleid, processcodes, user.getUserid(), e.getMessage(), e);

            // Audit trail failure entry
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "Failed to update process mappings for roleId " + roleid);

            return "error";
        }
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to S (Admin)
     */
    @GetMapping("/getProcessesByRole")
    @ResponseBody
    public List<Integer> getProcessesByRole(@RequestParam("rolecode") Integer roleid, HttpServletRequest request) {

        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception e) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        }

        if (!user.getRole().getRoleCode().equalsIgnoreCase("S")) {
            throw new MyAuthorizationDeniedException(
                    ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "Get Processes By Role, " + request.getMethod(), user.getUserid()), "page");
        }
        return mtUserloginRoleService.getProcessCodesByRole(roleid);
    }
    /*
     * API Endpoint for Flutter/Mobile Clients
     * Returns JSON metadata for role-process mapping.
     */
    @GetMapping("/map-api")
    @ResponseBody
    public Map<String, Object> getMappingMetadataApi(HttpServletRequest request) {
        // Reuse the same authorization logic
        MT_Userlogin user = mtUserloginService.getUserloginFromAuthentication();
        if (!user.getRole().getRoleCode().equalsIgnoreCase("S")) {
            // Or throw an unauthorized exception
            throw new MyAuthorizationDeniedException("Unauthorized", "json");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("roles", mtUserloginRoleService.getRolesForProcessMapping());
        response.put("processes", mProcessesService.getActualAllProcesses());

        return response;
    }

}
