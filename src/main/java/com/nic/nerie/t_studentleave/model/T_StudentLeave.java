package com.nic.nerie.t_studentleave.model;

import com.nic.nerie.m_course_academics.model.M_Course_Academics;
import com.nic.nerie.m_semesters.model.M_Semesters;
import com.nic.nerie.m_shortterm_phases.model.M_ShortTerm_Phases;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.t_students.model.T_Students;
import jakarta.persistence.*;

import java.util.Date;

import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

@Entity
@Table(name = "t_studentleave")
public class T_StudentLeave {
    @Id
    @Generated(event = EventType.INSERT)
    @Column(name = "studentleaveid", insertable = false)
    private String studentleaveid;

    @Temporal(TemporalType.DATE)
    private Date requestedfrom;
    @Temporal(TemporalType.DATE)
    private Date requestedto;

    private byte[] gapprovalletter; //guardian
    //private byte[] wapprovalletter; //warden
    private String reasonforleave;
    private String leavestation;
    private String nameofguardian;
    private String guardianrelationship;
    private String phnoguardian;
    @Temporal(TemporalType.DATE)
    private Date applicationdate;

    @ManyToOne
    @JoinColumn(name = "studentid")
    public T_Students studentid;

    @ManyToOne
    @JoinColumn(name = "coursecode")
    public M_Course_Academics coursecode;

    @ManyToOne
    @JoinColumn(name = "semestercode")
    public M_Semesters semestercode;

    @ManyToOne
    @JoinColumn(name = "sphaseid")
    public M_ShortTerm_Phases sphaseid;

    private String iswardenapproved;
    private String isdeanapproved;
    private String isapproved;

    private String buildingno;
    private String roomno;
    private String remarks;

    @ManyToOne
    @JoinColumn(name = "actiontakenby")
    public MT_Userlogin usercode;

    @Temporal(TemporalType.DATE)
    private Date actiontakendate;

    private String rejectionreason;

    public String getStudentleaveid() {
        return studentleaveid;
    }

    public void setStudentleaveid(String studentleaveid) {
        this.studentleaveid = studentleaveid;
    }

    public Date getRequestedfrom() {
        return requestedfrom;
    }

    public void setRequestedfrom(Date requestedfrom) {
        this.requestedfrom = requestedfrom;
    }

    public Date getRequestedto() {
        return requestedto;
    }

    public void setRequestedto(Date requestedto) {
        this.requestedto = requestedto;
    }

    public byte[] getGapprovalletter() {
        return gapprovalletter;
    }

    public void setGapprovalletter(byte[] gapprovalletter) {
        this.gapprovalletter = gapprovalletter;
    }

//    public byte[] getWapprovalletter() {
//        return wapprovalletter;
//    }
//
//    public void setWapprovalletter(byte[] wapprovalletter) {
//        this.wapprovalletter = wapprovalletter;
//    }

    public String getReasonforleave() {
        return reasonforleave;
    }

    public void setReasonforleave(String reasonforleave) {
        this.reasonforleave = reasonforleave;
    }

    public String getLeavestation() {
        return leavestation;
    }

    public void setLeavestation(String leavestation) {
        this.leavestation = leavestation;
    }

    public String getNameofguardian() {
        return nameofguardian;
    }

    public void setNameofguardian(String nameofguardian) {
        this.nameofguardian = nameofguardian;
    }

    public String getGuardianrelationship() {
        return guardianrelationship;
    }

    public void setGuardianrelationship(String guardianrelationship) {
        this.guardianrelationship = guardianrelationship;
    }

    public String getPhnoguardian() {
        return phnoguardian;
    }

    public void setPhnoguardian(String phnoguardian) {
        this.phnoguardian = phnoguardian;
    }

    public Date getApplicationdate() {
        return applicationdate;
    }

    public void setApplicationdate(Date applicationdate) {
        this.applicationdate = applicationdate;
    }

    public T_Students getStudentid() {
        return studentid;
    }

    public void setStudentid(T_Students studentid) {
        this.studentid = studentid;
    }

    public String getIsapproved() {
        return isapproved;
    }

    public void setIsapproved(String isapproved) {
        this.isapproved = isapproved;
    }

    public String getIswardenapproved() {
        return iswardenapproved;
    }

    public void setIswardenapproved(String iswardenapproved) {
        this.iswardenapproved = iswardenapproved;
    }

    public String getIsdeanapproved() {
        return isdeanapproved;
    }

    public void setIsdeanapproved(String isdeanapproved) {
        this.isdeanapproved = isdeanapproved;
    }

    public MT_Userlogin getUsercode() {
        return usercode;
    }

    public void setUsercode(MT_Userlogin usercode) {
        this.usercode = usercode;
    }

    public Date getActiontakendate() {
        return actiontakendate;
    }

    public void setActiontakendate(Date actiontakendate) {
        this.actiontakendate = actiontakendate;
    }

    public String getRejectionreason() {
        return rejectionreason;
    }

    public void setRejectionreason(String rejectionreason) {
        this.rejectionreason = rejectionreason;
    }

    public String getBuildingno() {
        return buildingno;
    }

    public void setBuildingno(String buildingno) {
        this.buildingno = buildingno;
    }

    public String getRoomno() {
        return roomno;
    }

    public void setRoomno(String roomno) {
        this.roomno = roomno;
    }

    public M_Course_Academics getCoursecode() { return coursecode; }

    public void setCoursecode(M_Course_Academics coursecode) { this.coursecode = coursecode; }

    public M_Semesters getSemestercode() { return semestercode; }

    public void setSemestercode(M_Semesters semestercode) { this.semestercode = semestercode; }

    public M_ShortTerm_Phases getSphaseid() { return sphaseid; }

    public String getRemarks() { return remarks; }

    public void setRemarks(String remarks) { this.remarks = remarks; }

    //removed the 2 byte arrays
    @Override
    public String toString() {
        return "T_StudentLeave{" + "studentleaveid=" + studentleaveid + ", requestedfrom=" + requestedfrom + ", requestedto=" + requestedto + ", Building Number=" + buildingno + ", Room Number=" + roomno + ", reasonforleave=" + reasonforleave + ", leavestation=" + leavestation + ", nameofguardian=" + nameofguardian + ", guardianrelationship=" + guardianrelationship + ", phnoguardian=" + phnoguardian + ", applicationdate=" + applicationdate + ", studentid=" + studentid + ", iswardenapproved=" + iswardenapproved + ", isdeanapproved=" + isdeanapproved +", isapproved=" + isapproved + ", usercode=" + usercode + ", actiontakendate=" + actiontakendate + ", rejectionreason=" + rejectionreason + '}';
    }
}
