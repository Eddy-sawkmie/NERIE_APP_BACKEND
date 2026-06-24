package com.nic.nerie.t_notifications.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.t_notifications.model.NotificationReceiver;
import com.nic.nerie.t_notifications.model.NotificationReceiverId;

@Repository
public interface MT_NotificationReceiverRepository
        extends JpaRepository<NotificationReceiver, NotificationReceiverId> {

    // Fetch notifications
    @Query(value = """
        SELECT nr.*
        FROM nerie.mt_notification_receiver nr
        JOIN nerie.t_notifications n 
            ON nr.notificationid = n.notificationid
        WHERE nr.receiverusercode = :usercode
        ORDER BY nr.is_read ASC, n.entrydate DESC
        """, 
        nativeQuery = true)
    List<NotificationReceiver> findAllByUserOrderByReadAndDate(
            @Param("usercode") String usercode);




    // Toggle helper
    @Query("""
        SELECT nr
        FROM NotificationReceiver nr
        WHERE nr.notification.notificationid = :notificationId
        AND nr.user = :user
    """)
    Optional<NotificationReceiver> findByNotificationIdAndUser(
            @Param("notificationId") String notificationId,
            @Param("user") MT_Userlogin user);


    // Mark all as read (optimized)
    @Modifying
    @Query("""
        UPDATE NotificationReceiver nr
        SET nr.read = true,
            nr.readDate = CURRENT_TIMESTAMP
        WHERE nr.user = :user
        AND nr.read = false
    """)
    void markAllAsRead(@Param("user") MT_Userlogin user);


    // Unread count
    @Query("""
        SELECT COUNT(nr)
        FROM NotificationReceiver nr
        WHERE nr.user = :user
        AND nr.read = false
    """)
    long countUnreadByUser(@Param("user") MT_Userlogin user);
}
