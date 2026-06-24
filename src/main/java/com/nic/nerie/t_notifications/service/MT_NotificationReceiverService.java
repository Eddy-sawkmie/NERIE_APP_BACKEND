package com.nic.nerie.t_notifications.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.mt_userlogin.service.MT_UserloginService;
import com.nic.nerie.t_notifications.model.NotificationReceiver;
import com.nic.nerie.t_notifications.model.T_Notifications;
import com.nic.nerie.t_notifications.repository.MT_NotificationReceiverRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MT_NotificationReceiverService {

    private final MT_NotificationReceiverRepository notificationReceiverRepository;
    private final T_NotificationsService notificationsService;
    private final MT_UserloginService mtUserloginService;

    public MT_NotificationReceiverService(MT_NotificationReceiverRepository notificationReceiverRepository,
        T_NotificationsService notificationsService,MT_UserloginService mtUserloginService
    ) {
        this.notificationReceiverRepository = notificationReceiverRepository;
        this.notificationsService = notificationsService;
        this.mtUserloginService = mtUserloginService;
    }

    // =========================================================
    // 1️⃣ Get All Notifications (Unread First, Newest First)
    // =========================================================
    @Transactional(readOnly = true)
    public List<NotificationReceiver> getNotificationsForUser(MT_Userlogin user) {
        return notificationReceiverRepository
                .findAllByUserOrderByReadAndDate(user.getUsercode());
    }

    // =========================================================
    // 2️⃣ Toggle Read Status
    // =========================================================
    @Transactional
    public boolean toggleReadStatus(String notificationId, MT_Userlogin user) {

        NotificationReceiver receiver =
                notificationReceiverRepository
                        .findByNotificationIdAndUser(notificationId, user)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Notification not assigned to user"));

        receiver.setRead(!receiver.isRead());

        if (receiver.isRead()) {
            receiver.setReadDate(new Date());
        } else {
            receiver.setReadDate(null);
        }

        // No explicit save needed (JPA dirty checking)
        return receiver.isRead();
    }

    // =========================================================
    // 3️⃣ Mark All As Read
    // =========================================================
    @Transactional
    public void markAllAsRead(MT_Userlogin user) {
        notificationReceiverRepository.markAllAsRead(user);
    }

    // =========================================================
    // 4️⃣ Get Unread Count
    // =========================================================
    @Transactional(readOnly = true)
    public long getUnreadCount(MT_Userlogin user) {
        return notificationReceiverRepository.countUnreadByUser(user);
    }

    // =========================================================
    // 5️⃣ Assign Notification To Users (When Creating Notification)
    // =========================================================
    // public void assignNotificationToUsers(T_Notifications notification,
    //                                       Set<MT_Userlogin> users) {

    //     for (MT_Userlogin user : users) {
    //         NotificationReceiver receiver =
    //                 new NotificationReceiver(notification, user);

    //         notificationReceiverRepository.save(receiver);
    //     }
    // }

//    @Transactional
//    public void createNotification(String notificationId,
//                                   String message,
//                                   String userType,
//                                   MT_Userlogin creator) {
//        String userrole="";
//        switch (userType) {
//            case "Students":
//                userrole="T";
//                break;
//            case "Faculties":
//                userrole="U";
//                break;
//            case "Coordinators":
//                userrole="U";
//                break;
//            default:
//                break;
//        }
//        // 1️⃣ Create notification
//        T_Notifications notification = new T_Notifications();
//
//        if (notificationId != null && !notificationId.isBlank()) {
//            notification.setNotificationid(notificationId.trim());
//        }
//
//        notification.setNotification(message.trim());
//        notification.setReceivertype(userType);
//        notification.setUsercode(creator);
//        notification.setOfficecode(creator.getMoffices());
//        notification.setEntrydate(new Date());
//
//        T_Notifications savedNotification =
//                notificationsService.saveNotification(notification);
//
//        // 2️⃣ Fetch target users
//        List<MT_Userlogin> targetUsers =
//                mtUserloginService.getMT_UsersByRole(userrole);
//
//        // 3️⃣ Create receiver rows
//        List<NotificationReceiver> receivers = targetUsers.stream()
//                .map(user -> new NotificationReceiver(savedNotification, user))
//                .toList();
//
//        notificationReceiverRepository.saveAll(receivers);
//    }

    @Transactional
    public void createNotification(String notificationId,
                                   String message,
                                   String userType,
                                   MT_Userlogin creator) {

        T_Notifications notification = new T_Notifications();

        if (notificationId != null && !notificationId.isBlank()) {
            notification.setNotificationid(notificationId.trim());
        }

        notification.setNotification(message.trim());
        notification.setReceivertype(userType);
        notification.setUsercode(creator);
        notification.setOfficecode(creator.getMoffices());
        notification.setEntrydate(new Date());

        T_Notifications savedNotification = notificationsService.saveNotification(notification);

        // Fetch target users with precise flag filtering
        List<MT_Userlogin> targetUsers = new ArrayList<>();

        // Handle "All" selection safely without triggering @NotBlank constraints
        if (userType.equalsIgnoreCase("All") || userType.equalsIgnoreCase("All Users")) {
            // Add all Students
            targetUsers.addAll(mtUserloginService.getMT_UsersByRole("T"));

            // Add all Role 'U' users who are EITHER a Faculty OR a Coordinator
            targetUsers.addAll(mtUserloginService.getMT_UsersByRole("U").stream()
                    .filter(u -> "1".equals(u.getIsfaculty()) || "1".equals(u.getIscoordinator()))
                    .toList());

        } else {
            switch (userType) {
                case "Students":
                    // Students just need the Role 'T'
                    targetUsers = mtUserloginService.getMT_UsersByRole("T");
                    break;

                case "Faculties":
                    // Fetch Role 'U', but ONLY keep those with isfaculty = "1"
                    targetUsers = mtUserloginService.getMT_UsersByRole("U").stream()
                            .filter(u -> "1".equals(u.getIsfaculty()))
                            .toList();
                    break;

                case "Coordinators":
                    // Fetch Role 'U', but ONLY keep those with iscoordinator = "1"
                    targetUsers = mtUserloginService.getMT_UsersByRole("U").stream()
                            .filter(u -> "1".equals(u.getIscoordinator()))
                            .toList();
                    break;

                default:
                    // Fail fast to prevent empty string database searches
                    throw new IllegalArgumentException("Unrecognized userType: " + userType);
            }
        }

        // Create receiver rows
        if (targetUsers != null && !targetUsers.isEmpty()) {
            List<NotificationReceiver> receivers = targetUsers.stream()
                    .map(user -> new NotificationReceiver(savedNotification, user))
                    .toList();

            notificationReceiverRepository.saveAll(receivers);
        }
    }


}

