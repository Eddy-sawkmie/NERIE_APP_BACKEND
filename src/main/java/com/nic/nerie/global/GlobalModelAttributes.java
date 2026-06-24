package com.nic.nerie.global;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.mt_userlogin.service.MT_UserloginService;
import com.nic.nerie.t_notifications.model.NotificationReceiver;
import com.nic.nerie.t_notifications.service.MT_NotificationReceiverService;
import com.nic.nerie.t_notifications.service.T_NotificationsService;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalModelAttributes {

    private final T_NotificationsService tNotificationsService;
    private final MT_UserloginService mtUserloginService;
    private final MT_NotificationReceiverService MtNotificationReceiverService;

    public GlobalModelAttributes(T_NotificationsService tNotificationsService,
                                 MT_UserloginService mtUserloginService,
                                MT_NotificationReceiverService MtNotificationReceiverService) {
        this.tNotificationsService = tNotificationsService;
        this.mtUserloginService = mtUserloginService;
        this.MtNotificationReceiverService = MtNotificationReceiverService;
    }

    @ModelAttribute("sidebarnotifications")
    public List<NotificationReceiver> populateNotifications(HttpServletRequest request) {
        List<NotificationReceiver> notis = new ArrayList<>();

        try {
            MT_Userlogin mtUserlogin = mtUserloginService.getUserloginFromAuthentication();

            // If user is null (e.g., unauthenticated / on the login page), stop here.
            if (mtUserlogin == null) {
                return notis;
            }

            // Only fetch notifications if we have a valid, logged-in user
            notis = MtNotificationReceiverService.getNotificationsForUser(mtUserlogin);

        } catch (AuthenticationCredentialsNotFoundException ex) {
            // This is expected behavior on public pages like /login or /index.
            // We catch it silently so it doesn't pollute server logs.
        } catch (Exception ex) {
            // Only print a message for ACTUAL unexpected errors, rather than a full stack trace
            System.err.println("Error populating sidebar notifications: " + ex.getMessage());
            // Alternatively, use logger: logger.error("Error populating...", ex);
        }
        return notis;
    }

}

