package com.nic.nerie.t_notifications.controller;

import java.util.Date;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
import com.nic.nerie.m_processes.service.M_ProcessesService;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.mt_userlogin.service.MT_UserloginService;
import com.nic.nerie.t_notifications.model.T_Notifications;
import com.nic.nerie.t_notifications.service.MT_NotificationReceiverService;
import com.nic.nerie.t_notifications.service.T_NotificationsService;
import com.nic.nerie.utils.ExceptionUtil;
import com.nic.nerie.utils.UtilCommon;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/notifications")
public class T_NotificationsController {
    private final T_NotificationsService tNotificationsService;
    private final MT_UserloginService mtUserloginService;
    private final M_ProcessesService mProcessesService;
    private final MT_NotificationReceiverService notificationReceiverService;
    private final AudittrailService audittrailService;
    private static final Logger dataAccessLogger = LoggerFactory.getLogger("DATA_ACCESS_LOGGER");
    private static final Logger persistenceLogger = LoggerFactory.getLogger("DATA_PERSISTENCE_LOGGER");

    @Autowired
    public T_NotificationsController(
        T_NotificationsService tNotificationsService,
        AudittrailService audittrailService,
        MT_NotificationReceiverService notificationReceiverService,
        MT_UserloginService mtUserloginService,
        M_ProcessesService mProcessesService
    ) {
        this.tNotificationsService = tNotificationsService;
        this.notificationReceiverService = notificationReceiverService;
        this.audittrailService = audittrailService;
        this.mtUserloginService = mtUserloginService;
        this.mProcessesService = mProcessesService; 
    }

    @GetMapping("/mynotifications")
    public String ManageNotificationsPageUser(HttpServletRequest request, Model model) {
        MT_Userlogin mtUserlogin;
        try {
            mtUserlogin = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "Manage Notifications User , " + request.getMethod()), "page");
        }
        String userRole = mtUserlogin.getRole().getRoleCode().toUpperCase();
        switch (userRole) {
            // case "A":
            //     model.addAttribute("layoutPath", "layouts/local-admin-layout");
            //     break;
            case "U":
                model.addAttribute("layoutPath", "layouts/coordinator-faculty-layout");
                break;
            case "T":
                model.addAttribute("layoutPath", "layouts/student-layout");
                break;
            case "P":
                model.addAttribute("layoutPath", "layouts/participant-layout");
                break;
        }
        return "pages/notifications";
    }

    @PostMapping("/toggle")
    @ResponseBody
    public ResponseEntity<?> toggleNotification(@RequestParam String id,HttpServletRequest request) {
         MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
            boolean isRead = notificationReceiverService.toggleReadStatus(id, user);
            return ResponseEntity.ok(isRead);
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "Manage Notifications, " + request.getMethod()), "page");
        }
    }

    @PostMapping("/markAllAsRead")
    @ResponseBody
    public ResponseEntity<Void> markAllAsRead() {
        MT_Userlogin user = mtUserloginService.getUserloginFromAuthentication();
        if (user != null) {
            notificationReceiverService.markAllAsRead(user);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }


    /*
     * Secured endpoint
     * This endpoint is exclusive to role Z (Principal-Director)
     * Manage Notifications (processcode = 42)
     */
    @GetMapping("/manage")
    public String renderManageNotificationsPage(HttpServletRequest request, Model model) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "Manage Notifications, " + request.getMethod()), "page");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();

        // Only Principal-Director (Z) can Manage Notifications
        if (!(
            userRole.equalsIgnoreCase("Z") &&
            mProcessesService.isProcessGranted(user.getUsercode(), 42)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "Manage Notifications, " + request.getMethod(), user.getUserid()), "page");
        }

        model.addAttribute("allnotifications", tNotificationsService.findByUsercode(user.getUsercode()));

        return "pages/principal-director/manage-notifications";
    }

//    /*
//     * Secured endpoint
//     * This endpoint is exclusive to role Z (Principal-Director)
//     */
//    @PostMapping("/upload")
//    public ResponseEntity<String> uploadNotification(
//        HttpServletRequest request,
//        @RequestParam(value = "notificationid", required = false) String notificationid,
//        @RequestParam("notification") String notification,
//        @RequestParam("usertype") String usertype
//    ) {
//        MT_Userlogin user;
//        try {
//            user = mtUserloginService.getUserloginFromAuthentication();
//        } catch (RuntimeException ex) {
//            throw new MyAuthenticationCredentialsNotFoundException(
//                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
//        }
//        String userRole = user.getRole().getRoleCode().toUpperCase();
//
//        // Only Principal-Director (Z) can Manage Notifications
//        if (!(
//            userRole.equalsIgnoreCase("Z") &&
//            mProcessesService.isProcessGranted(user.getUsercode(), 42)
//        )) {
//            throw new MyAuthorizationDeniedException(
//                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
//        }
//
//        // Param validations
//
//        // if updating
//        if (notificationid != null && !notificationid.isBlank()) {
//            if (!tNotificationsService.existsByNotificationid(notificationid.trim())) {
//                return ResponseEntity.badRequest().body("Notification does not exist");
//            }
//        }
//
//        if (notification == null || notification.isBlank() || notification.isBlank() || notification.length() > 1000) {
//            return ResponseEntity.badRequest().body("Notification cannot be empty");
//        }
//
//        if (usertype == null || usertype.isBlank() || usertype.isBlank() || usertype.length() > 20) {
//            return ResponseEntity.badRequest().body("usertype cannot be empty");
//        }
//        // T_Notifications newNotifications = new T_Notifications();
//
//        // if (notificationid != null && !notificationid.isBlank()) {
//        //     newNotifications.setNotificationid(notificationid.trim());
//        // }
//
//        // newNotifications.setNotification(notification.trim());
//        // newNotifications.setReceivertype(usertype);;
//        // newNotifications.setUsercode(user);
//        // newNotifications.setOfficecode(user.getMoffices());
//        // newNotifications.setEntrydate(new Date());
//
//        // HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
//
//        // try {
//        //     tNotificationsService.addNotifications(newNotifications);
//
//        //     persistenceLogger.info("T_Notifications saved successfully by userid {}", user.getUserid());
//        //     audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_notifications saved successfully");
//
//        //     return ResponseEntity.ok("1");
//        // } catch (Exception ex) {
//        //     persistenceLogger.error("T_Notifications save failed.\nMessage: {}\nUserid: {}", ex.getMessage(), user.getUserid(), ex);
//        //     audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_notifications save failed");
//
//        //     return ResponseEntity.ok("-1");
//        // }
//        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
//        try {
//            notificationReceiverService.createNotification(
//                    notificationid,
//                    notification,
//                    usertype,
//                    user
//            );
//            persistenceLogger.info("T_Notifications saved successfully by userid {}", user.getUserid());
//            audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_notifications saved successfully");
//            return ResponseEntity.ok("1");
//        } catch (Exception ex) {
//            persistenceLogger.error("T_Notifications save failed.\nMessage: {}\nUserid: {}",
//                    ex.getMessage(), user.getUserid(), ex);
//            audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_notifications save failed");
//            return ResponseEntity.ok("-1");
//        }
//    }


    /*
     * Secured endpoint
     * This endpoint is exclusive to role Z (Principal-Director)
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadNotification(
            HttpServletRequest request,
            @RequestParam(value = "notificationid", required = false) String notificationid,
            @RequestParam("notification") String notification,
            @RequestParam("usertype") String usertype
    ) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (RuntimeException ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();

        // Only Principal-Director (Z) can Manage Notifications
        if (!(
                userRole.equalsIgnoreCase("Z") &&
                        mProcessesService.isProcessGranted(user.getUsercode(), 42)
        )) {
            throw new MyAuthorizationDeniedException(
                    ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }

        // Param validations

        // if updating
        if (notificationid != null && !notificationid.isBlank()) {
            if (!tNotificationsService.existsByNotificationid(notificationid.trim())) {
                return ResponseEntity.badRequest().body("Notification does not exist");
            }
        }

        if (notification == null || notification.isBlank() || notification.length() > 1000) {
            return ResponseEntity.badRequest().body("Notification cannot be empty");
        }

        if (usertype == null || usertype.isBlank() || usertype.length() > 20) {
            return ResponseEntity.badRequest().body("usertype cannot be empty");
        }
        // T_Notifications newNotifications = new T_Notifications();

        // if (notificationid != null && !notificationid.isBlank()) {
        //     newNotifications.setNotificationid(notificationid.trim());
        // }

        // newNotifications.setNotification(notification.trim());
        // newNotifications.setReceivertype(usertype);;
        // newNotifications.setUsercode(user);
        // newNotifications.setOfficecode(user.getMoffices());
        // newNotifications.setEntrydate(new Date());

        // HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);

        // try {
        //     tNotificationsService.addNotifications(newNotifications);

        //     persistenceLogger.info("T_Notifications saved successfully by userid {}", user.getUserid());
        //     audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_notifications saved successfully");

        //     return ResponseEntity.ok("1");
        // } catch (Exception ex) {
        //     persistenceLogger.error("T_Notifications save failed.\nMessage: {}\nUserid: {}", ex.getMessage(), user.getUserid(), ex);
        //     audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_notifications save failed");

        //     return ResponseEntity.ok("-1");
        // }
        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
        try {
            notificationReceiverService.createNotification(
                    notificationid,
                    notification,
                    usertype,
                    user
            );
            persistenceLogger.info("T_Notifications saved successfully by userid {}", user.getUserid());
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_notifications saved successfully");
            return ResponseEntity.ok("1");
        } catch (Exception ex) {
            persistenceLogger.error("T_Notifications save failed.\nMessage: {}\nUserid: {}",
                    ex.getMessage(), user.getUserid(), ex);
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_notifications save failed");
            return ResponseEntity.ok("-1");
        }
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role Z (Principal-Director)
     */
    @GetMapping("/delete")
    public ResponseEntity<String> deleteNotification(
        HttpServletRequest request, 
        @RequestParam("notificationid") String notificationid
    ) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (RuntimeException ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();

        // Only Principal-Director (Z) can Manage Notifications
        if (!(
            userRole.equalsIgnoreCase("Z") &&
            mProcessesService.isProcessGranted(user.getUsercode(), 42)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }

        if (notificationid == null || notificationid.isBlank()) {
            return ResponseEntity.badRequest().body("Notification ID cannot be empty");
        }

        if (!tNotificationsService.existsByNotificationid(notificationid.trim())) {
            return ResponseEntity.badRequest().body("Notification does not exist");
        }

        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
        try {
            tNotificationsService.deleteNotification(notificationid.trim());

            persistenceLogger.info("T_Notifications with notificationid {} deleted successfully by userid {}", notificationid, user.getUserid());
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_notifications deleted successfully");

            return ResponseEntity.ok("1");
        } catch (Exception e) {
            persistenceLogger.error("Failed to delete t_notifications with notificationid: {}\nMessage: {}", notificationid, e.getMessage(), e);
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_notifications delete failed");

            return ResponseEntity.ok("-1");
        }
    }
    // ADD THIS METHOD to T_NotificationsController.java
// This returns notifications as JSON for the Flutter app
    @GetMapping("/api/mynotifications")
    @ResponseBody
    public ResponseEntity<?> getMyNotificationsApi(HttpServletRequest request) {
        MT_Userlogin mtUserlogin;
        try {
            mtUserlogin = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(),
                            "Get My Notifications API, " + request.getMethod()), "json");
        }

        // Returns notifications as JSON list
        return ResponseEntity.ok(
                notificationReceiverService.getNotificationsForUser(mtUserlogin)
        );
    }

}
