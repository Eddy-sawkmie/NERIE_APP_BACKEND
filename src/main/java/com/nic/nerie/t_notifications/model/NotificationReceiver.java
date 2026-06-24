package com.nic.nerie.t_notifications.model;


import java.util.Date;

import com.nic.nerie.mt_userlogin.model.MT_Userlogin;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

//this is a join entity with a composite key,
@Entity
@Table(name = "mt_notification_receiver")
public class NotificationReceiver {

    @EmbeddedId
    private NotificationReceiverId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("notificationId")
    @JoinColumn(name = "notificationid")
    private T_Notifications notification;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("receiverUserCode")
    @JoinColumn(name = "receiverusercode")
    private MT_Userlogin user;

    @Column(name = "is_read", nullable = false)
    private boolean read = false;

    private Date readDate;

    public NotificationReceiver() {
        // REQUIRED by JPA
    }

    // Convenience constructor (VERY useful)
    public NotificationReceiver(T_Notifications notification, MT_Userlogin user) {
        this.notification = notification;
        this.user = user;
        this.id = new NotificationReceiverId(
                notification.getNotificationid(),
                user.getUsercode()
        );
        this.read = false;
    }

    public NotificationReceiverId getId() {
        return id;
    }

    public void setId(NotificationReceiverId id) {
        this.id = id;
    }

    public T_Notifications getNotification() {
        return notification;
    }

    public void setNotification(T_Notifications notification) {
        this.notification = notification;
    }

    public MT_Userlogin getUser() {
        return user;
    }

    public void setUser(MT_Userlogin user) {
        this.user = user;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Date getReadDate() {
        return readDate;
    }

    public void setReadDate(Date readDate) {
        this.readDate = readDate;
    }

    
    
}

// ✅ 1️⃣ Do We Create Getters & Setters?
// Yes. Always.
// JPA requires:
// Getter + Setter (or field access, but getters are standard)
// No-args constructor (mandatory)
// Proper equals() and hashCode() for the ID class

// ✅ 2️⃣ Constructor Requirements
// JPA requires:
// public NotificationReceiver() {}
// It MUST be:
// public or protected
// no-argument constructor
// Without it → Hibernate fails.

// 🎯 Why We Add That Convenience Constructor
// Instead of doing this every time:

// NotificationReceiver nr = new NotificationReceiver();
// nr.setNotification(notification);
// nr.setUser(user);
// nr.setId(new NotificationReceiverId(
//     notification.getNotificationid(),
//     user.getUsercode()
// ));
// nr.setRead(false);


// You can just do:
// NotificationReceiver nr = new NotificationReceiver(notification, user);
// Much cleaner.



