package com.nic.nerie.t_notifications.repository;

import java.util.List;
import java.util.Set;

import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nic.nerie.t_notifications.model.T_Notifications;

public interface T_NotificationsRepository extends JpaRepository<T_Notifications, String> {
    @Query("from T_Notifications where receivertype = :receivertype ORDER BY entrydate DESC")
    List<T_Notifications> findByReceivertype(@Param("receivertype") String receivertype);

    @Query("from T_Notifications where usercode.usercode = :usercode ORDER BY entrydate DESC")
    List<T_Notifications> findByUsercode(@Param("usercode") String usercode);

    @Query("from T_Notifications where notificationid = :notificationid")
    T_Notifications findByNotificationid(@Param("notificationid") String notificationid);

    @Modifying
    @Query("DELETE FROM T_Notifications WHERE notificationid = :notificationid")
    void deleteByNotificationid(@Param("notificationid") String notificationid);

    @Query(value = "SELECT MAX(CAST(notificationid AS INTEGER)) FROM t_notifications", nativeQuery = true)
    Integer findMaxNotificationId();

    @Query("SELECT DISTINCT n FROM T_Notifications n LEFT JOIN n.receivers r " +
            "WHERE n.officecode.officecode = :officecode " + // Filter by Office
            "AND (r = :user " +
            "OR n.receivertype = :receiverType " +
            "OR n.receivertype = 'All') " +
            "ORDER BY n.entrydate DESC")
    List<T_Notifications> findNotificationsForUser(@Param("user") MT_Userlogin user,
                                                   @Param("receiverType") String receiverType,
                                                   @Param("officecode") String officecode);   

    @Query(value = """
            SELECT n.*
            FROM nerie.t_notifications n
            WHERE n.officecode = :officecode 
              AND n.notificationid IN (
                SELECT DISTINCT n_inner.notificationid
                FROM nerie.t_notifications n_inner
                LEFT JOIN nerie.mt_notification_receiver nr 
                    ON n_inner.notificationid = nr.notificationid
                WHERE n_inner.receiverusercode = :usercode
                   OR nr.receiverusercode = :usercode
                   OR n_inner.receivertype = 'All'
            )
            ORDER BY CAST(n.notificationid AS INTEGER) DESC
            """, nativeQuery = true)
    List<T_Notifications> findNotificationsByUserNative(@Param("usercode") String usercode,
                                                        @Param("officecode") String officecode);

}
