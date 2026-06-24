package com.nic.nerie.t_notifications.model;

import com.nic.nerie.m_offices.model.M_Offices;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import jakarta.persistence.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "t_notifications")
public class T_Notifications {
    @Id
    private String notificationid;

    private String notification;
    private String receivertype;

    @ManyToOne
    @JoinColumn(name = "usercode")
    public MT_Userlogin usercode;

    @Temporal(TemporalType.DATE)
    private Date entrydate;

    @ManyToOne
    @JoinColumn(name = "officecode")
    public M_Offices officecode;

//    @ManyToOne
//    @JoinColumn(name = "receiverusercode")
//    public MT_Userlogin receiverusercode;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "mt_notification_receiver", // Join table name
            joinColumns = @JoinColumn(name = "notificationid"), // pkey for T_Phase_PrePostTest
            inverseJoinColumns = @JoinColumn(name = "receiverusercode") // pkey for T_PrePostTestQuestions
    )
    private Set<MT_Userlogin> receivers = new HashSet<>(); //can be empty cause we have receivertype which is more general

    private String link;

    public T_Notifications() {
    }

    public T_Notifications(String notificationid, String notification, String receivertype, MT_Userlogin usercode, Date entrydate, M_Offices officecode) { //, MT_Userlogin receiverusercode
        this.notificationid = notificationid;
        this.notification = notification;
        this.receivertype = receivertype;
        this.usercode = usercode;
        this.entrydate = entrydate;
        this.officecode = officecode;
//        this.receiverusercode = receiverusercode;
    }

    public String getNotificationid() {
        return notificationid;
    }

    public void setNotificationid(String notificationid) {
        this.notificationid = notificationid;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getReceivertype() {
        return receivertype;
    }

    public void setReceivertype(String receivertype) {
        this.receivertype = receivertype;
    }

    public MT_Userlogin getUsercode() {
        return usercode;
    }

    public void setUsercode(MT_Userlogin usercode) {
        this.usercode = usercode;
    }

    public Date getEntrydate() {
        return entrydate;
    }

    public void setEntrydate(Date entrydate) {
        this.entrydate = entrydate;
    }

    public M_Offices getOfficecode() {
        return officecode;
    }

    public void setOfficecode(M_Offices officecode) {
        this.officecode = officecode;
    }

//    public MT_Userlogin getReceiverusercode() {
//        return receiverusercode;
//    }
//
//    public void setReceiverusercode(MT_Userlogin recieverusercode) {
//        this.receiverusercode = recieverusercode;
//    }

    public Set<MT_Userlogin> getReceivers() {
        return receivers;
    }

    public void setReceivers(Set<MT_Userlogin> receivers) {
        this.receivers = receivers;
    }

    public String getLink() { return link; }

    public void setLink(String link) { this.link = link; }

    @Override
    public String toString() {
        return "T_Notifications{" + "notificationid=" + notificationid + ", notification=" + notification + ", receivertype=" + receivertype + ", usercode=" + usercode + ", entrydate=" + entrydate + ", officecode=" + officecode + '}'; //", receiverusercode=" + receiverusercode +
    }

}
