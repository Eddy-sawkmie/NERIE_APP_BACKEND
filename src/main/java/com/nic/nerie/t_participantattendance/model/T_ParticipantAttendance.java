package com.nic.nerie.t_participantattendance.model;

import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "t_participantattendance")
public class T_ParticipantAttendance implements Serializable {

    @EmbeddedId
    T_P_Attendance_Id participantid;
    @ManyToOne
    @JoinColumn(name = "entryusercode")
    public MT_Userlogin mtuserlogin;
    @ManyToOne
    @JoinColumn(name = "pusercode")
    public MT_Userlogin pusercode;
    private String attendance;
    @Temporal(TemporalType.TIMESTAMP)
    Date entrydate;

    public T_ParticipantAttendance() {
    }

    public T_ParticipantAttendance(T_P_Attendance_Id participantid, MT_Userlogin mtuserlogin, MT_Userlogin pusercode, String attendance, Date entrydate) {
        this.participantid = participantid;
        this.mtuserlogin = mtuserlogin;
        this.pusercode = pusercode;
        this.attendance = attendance;
        this.entrydate = entrydate;
    }

    public T_P_Attendance_Id getParticipantid() {
        return participantid;
    }

    public void setParticipantid(T_P_Attendance_Id participantid) {
        this.participantid = participantid;
    }

    public MT_Userlogin getMtuserlogin() {
        return mtuserlogin;
    }

    public void setMtuserlogin(MT_Userlogin mtuserlogin) {
        this.mtuserlogin = mtuserlogin;
    }

    public MT_Userlogin getPusercode() {
        return pusercode;
    }

    public void setPusercode(MT_Userlogin pusercode) {
        this.pusercode = pusercode;
    }

    public String getAttendance() {
        return attendance;
    }

    public void setAttendance(String attendance) {
        this.attendance = attendance;
    }

    public Date getEntrydate() {
        return entrydate;
    }

    public void setEntrydate(Date entrydate) {
        this.entrydate = entrydate;
    }

    @Override
    public String toString() {
        return "T_ParticipantAttendance{" + "participantid=" + participantid + ", mtuserlogin=" + mtuserlogin + ", pusercode=" + pusercode + ", attendance=" + attendance + ", entrydate=" + entrydate + '}';
    }
}