package com.nic.nerie.t_studentassignment.model;

import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.t_assignmenttest.model.T_Assignmenttest;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "t_studentassignment")
public class T_StudentAssignment {
    @Id
    private String studentassignmentid;
    private byte[] reldoc;

    @Temporal(TemporalType.DATE)
    private Date uploaddate;

    @ManyToOne
    @JoinColumn(name = "assignmenttestid")
    public T_Assignmenttest assignmenttestid;

    @ManyToOne
    @JoinColumn(name = "usercode")
    public MT_Userlogin usercode;

    @Column(columnDefinition = "NUMERIC", length = 100, scale = 2)
    private Float assignmentmark;

    public T_StudentAssignment() {
    }

    public T_StudentAssignment(String studentassignmentid, byte[] reldoc, Date uploaddate, T_Assignmenttest assignmenttestid, MT_Userlogin usercode, Float assignmentmark) {
        this.studentassignmentid = studentassignmentid;
        this.reldoc = reldoc;
        this.uploaddate = uploaddate;
        this.assignmenttestid = assignmenttestid;
        this.usercode = usercode;
        this.assignmentmark = assignmentmark;
    }

    public String getStudentassignmentid() {
        return studentassignmentid;
    }

    public void setStudentassignmentid(String studentassignmentid) {
        this.studentassignmentid = studentassignmentid;
    }

    public byte[] getReldoc() {
        return reldoc;
    }

    public void setReldoc(byte[] reldoc) {
        this.reldoc = reldoc;
    }

    public Date getUploaddate() {
        return uploaddate;
    }

    public void setUploaddate(Date uploaddate) {
        this.uploaddate = uploaddate;
    }

    public T_Assignmenttest getAssignmenttestid() {
        return assignmenttestid;
    }

    public void setAssignmenttestid(T_Assignmenttest assignmenttestid) {
        this.assignmenttestid = assignmenttestid;
    }

    public MT_Userlogin getUsercode() {
        return usercode;
    }

    public void setUsercode(MT_Userlogin usercode) {
        this.usercode = usercode;
    }

    public Float getAssignmentmark() {
        return assignmentmark;
    }

    public void setAssignmentmark(Float assignmentmark) {
        this.assignmentmark = assignmentmark;
    }

    @Override
    public String toString() {
        return "T_StudentAssignment{" + "studentassignmentid=" + studentassignmentid + ", reldoc=" + reldoc + ", uploaddate=" + uploaddate + ", assignmenttestid=" + assignmenttestid + ", usercode=" + usercode + ", assignmentmark=" + assignmentmark + '}';
    }
}
