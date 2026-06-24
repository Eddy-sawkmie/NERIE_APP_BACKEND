package com.nic.nerie.t_notifications.service;

import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.t_notifications.model.T_Notifications;
import com.nic.nerie.t_notifications.repository.T_NotificationsRepository;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import jakarta.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.*;

@Service
@Validated
public class T_NotificationsService {
    private final T_NotificationsRepository tNotificationsRepository;

    @Autowired
    public T_NotificationsService(T_NotificationsRepository tNotificationsRepository) {
        this.tNotificationsRepository = tNotificationsRepository;
    }

    @Transactional(readOnly = true)
    public T_Notifications findByNotificationid(@NotNull @NotBlank String notificationid) {
        try {
            return tNotificationsRepository.findByNotificationid(notificationid.trim());
        } catch (Exception e) {
            throw new DataAccessResourceFailureException("Failed checking notifications existence by notificationid " + notificationid, e);
        }
    }

    @Transactional(rollbackFor = Exception.class, readOnly = false)
    public void updateNotification(@NotNull T_Notifications noti){
        try {
            if (noti.getNotificationid() == null || noti.getNotificationid().isEmpty()) {
                throw new RuntimeException("Error saving Notification: " + noti);
            }
            tNotificationsRepository.save(noti);
        }
        catch (Exception ex) {
            throw new RuntimeException("Error saving Notification: " + noti, ex);
        }
    }

    @Transactional(readOnly = true)
    public Boolean existsByNotificationid(@NotNull @NotBlank String notificationid) {
        try {
            return tNotificationsRepository.existsById(notificationid.trim());
        } catch (Exception e) {
            throw new DataAccessResourceFailureException("Failed checking notifications existence by notificationid " + notificationid, e);
        }
    }

    @Transactional(readOnly = true)
    public Set<T_Notifications> findByReceivertype(@NotNull @NotBlank String receivertype) {
        try {
            return new HashSet<>(tNotificationsRepository.findByReceivertype(receivertype.trim()));
        } catch (Exception e) {
            throw new DataAccessResourceFailureException("Failed to retrieve notifications by receiver type", e);
        }
    }

    @Transactional(readOnly = true)
    public List<T_Notifications> findByUsercode(@NotNull @NotBlank String usercode) {
        try {
            return tNotificationsRepository.findByUsercode(usercode.trim());
        } catch (Exception e) {
            throw new DataAccessResourceFailureException("Failed to retrieve notifications by usercode", e);
        }
    }

    @Transactional(rollbackFor = Exception.class, readOnly = false)
    public String addNotifications(@NotNull T_Notifications noti) {
        try {
            if (noti.getNotificationid() == null || noti.getNotificationid().isEmpty()) {
                Integer maxId = tNotificationsRepository.findMaxNotificationId();
                if (maxId == null) {
                    maxId = 0;
                }
                noti.setNotificationid(String.valueOf(maxId + 1));
            }
            tNotificationsRepository.save(noti);

            return "1";
        } catch (Exception ex) {
            throw new RuntimeException("Error saving Notification: " + noti, ex);
        }
    }

    @Transactional(rollbackFor = Exception.class, readOnly = false)
    public T_Notifications saveNotification(@NotNull T_Notifications noti) {
        try {
            if (noti.getNotificationid() == null || noti.getNotificationid().isEmpty()) {
                Integer maxId = tNotificationsRepository.findMaxNotificationId();
                if (maxId == null) {
                    maxId = 0;
                }
                noti.setNotificationid(String.valueOf(maxId + 1));
            }
            tNotificationsRepository.save(noti);
            return noti;
        } catch (Exception ex) {
            throw new RuntimeException("Error saving Notification: " + noti, ex);
        }
    }

    @Transactional(rollbackFor = Exception.class, readOnly = false)
    public void deleteNotification(@NotNull @NotBlank String notificationid) throws Exception {
        try {
            tNotificationsRepository.deleteByNotificationid(notificationid.trim());
        } catch (Exception e) {
            throw new Exception("Failed to delete notification with id: " + notificationid, e);
        }
    }

    public List<T_Notifications> getNotificationsForUser(MT_Userlogin user, String officecode) {
        String userRoleCode = user.getRole().getRoleCode().toUpperCase();
        String receiverType;

        switch (userRoleCode) {
            case "T":
                receiverType = "Students";
                break;

            case "U":
                // Faculty is 1 and Coordinator is 'O'
                if ("1".equals(user.getIsfaculty())) {
                    receiverType = "Faculties";
                } else {
                    receiverType = "Coordinators";
                }
                break;

            default:
                // For other roles (like Principal 'Z'), they don't belong to a broadcast group.
                // We pass a non-existent type so the query only matches 'SPECIFIC' and 'All' for them.
                receiverType = "ROLE_DOES_NOT_EXIST";
                break;
        }

        return tNotificationsRepository.findNotificationsForUser(user, receiverType, officecode);
    }

    @Transactional(readOnly = true)
    public List<T_Notifications> getNotificationsByUser(@NotNull @NotBlank String usercode, @NotBlank @NotBlank String officecode) {
        try {
            return tNotificationsRepository.findNotificationsByUserNative(usercode.trim(), officecode);
        } catch (Exception e) {
            throw new DataAccessResourceFailureException("Failed to retrieve notifications for usercode: " + usercode, e);
        }
    }

    // @Transactional(readOnly = true)
    // public List<T_Notifications> getCombinedNotificationsForUser(MT_Userlogin user) {
    //     //  Specific User Notifications
    //     List<T_Notifications> specificList = getNotificationsByUser(user.getUsercode(), user.getMoffices().getOfficecode());

    //     // Fetch Role-Based / 'All' Notifications
    //     List<T_Notifications> groupList = getNotificationsForUser(user, user.getMoffices().getOfficecode());

    //     // Merge lists and remove duplicates using Map (Key = String ID)
    //     Map<String, T_Notifications> mergedMap = new HashMap<>();

    //     if (specificList != null) {
    //         for (T_Notifications n : specificList) {
    //             if (n.getNotificationid() != null) mergedMap.put(n.getNotificationid(), n);
    //         }
    //     }

    //     if (groupList != null) {
    //         for (T_Notifications n : groupList) {
    //             if (n.getNotificationid() != null) mergedMap.put(n.getNotificationid(), n);
    //         }
    //     }

    //     // Convert back to a list
    //     List<T_Notifications> finalResult = new ArrayList<>(mergedMap.values());

        // OLD SORT LOGIC
        // finalResult.sort((n1, n2) -> {

        //     // 1️⃣ UNREAD FIRST (false should come before true)
        //     int readCompare = Boolean.compare(n1.isRead(), n2.isRead());

        //     if (readCompare != 0) {
        //         return readCompare; 
        //         // false < true → unread first
        //     }

        //     // 2️⃣ DATE DESCENDING
        //     Date d1 = n1.getEntrydate();
        //     Date d2 = n2.getEntrydate();

        //     // Handle null dates safely
        //     if (d1 == null && d2 == null) return 0;
        //     if (d1 == null) return 1;
        //     if (d2 == null) return -1;

        //     int dateCompare = d2.compareTo(d1); // Descending order

        //     // If dates are different, return the date result immediately
        //     if (dateCompare != 0) {
        //         return dateCompare;
        //     }

        //     // 3️⃣ ID DESCENDING
        //     // Only runs if dates are exactly the same
        //     try {
        //         Long id1 = Long.parseLong(n1.getNotificationid());
        //         Long id2 = Long.parseLong(n2.getNotificationid());

        //         // Compare IDs: Higher ID comes first
        //         return id2.compareTo(id1);
        //     } catch (NumberFormatException e) {
        //         // Fallback: If ID contains letters (e.g., "A12"), sort alphabetically
        //         return n2.getNotificationid().compareTo(n1.getNotificationid());
        //     }
        // });

    //     finalResult.sort(
    //     Comparator
    //         .comparing(T_Notifications::isRead) // false first
    //         .thenComparing(T_Notifications::getEntrydate, Comparator.nullsLast(Comparator.reverseOrder()))
    //         .thenComparing(n -> {
    //             try {
    //                 return Long.parseLong(n.getNotificationid());
    //             } catch (NumberFormatException e) {
    //                 return 0L;
    //             }
    //         }, Comparator.reverseOrder())
    //     );
        
    //     return finalResult;
    // }

}
