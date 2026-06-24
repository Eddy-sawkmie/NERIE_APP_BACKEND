package com.nic.nerie.t_assignmenttest.model;

import com.nic.nerie.m_subjects.model.M_Subjects;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Transient;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Table(name = "t_assignmenttest")
public class T_Assignmenttest {
    @Id
    private String assignmenttestid;
    private byte[] reldoc;
    private String title;
    private String description;
    private String testtype;
    private String testno;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date uploaddate;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date submissiondate;

    @ManyToOne
    @JoinColumn(name = "subjectcode")
    public M_Subjects subjectcode;

    @ManyToOne
    @JoinColumn(name = "usercode")
    public MT_Userlogin usercode;

    private Integer passmark;
    private Integer fullmark;
    private String submissiontype;

    @Transient
    private String reldocAsString;

    public String getReldocAsString() {
        return reldocAsString;
    }

    public void setReldocAsString(String reldocAsString) {
        this.reldocAsString = reldocAsString;
    }

    public T_Assignmenttest() {
    }

    public T_Assignmenttest(String assignmenttestid, byte[] reldoc, String title, String description, String testtype, String testno, Date uploaddate, Date submissiondate, M_Subjects subjectcode, MT_Userlogin usercode, Integer passmark, Integer fullmark,String submisstiontype,String reldocAsString) {
        this.assignmenttestid = assignmenttestid;
        this.reldoc = reldoc;
        this.title = title;
        this.description = description;
        this.testtype = testtype;
        this.testno = testno;
        this.uploaddate = uploaddate;
        this.submissiondate = submissiondate;
        this.subjectcode = subjectcode;
        this.usercode = usercode;
        this.passmark = passmark;
        this.fullmark = fullmark;
        this.submissiontype = submissiontype;
    }

    public String getAssignmenttestid() {
        return assignmenttestid;
    }

    public void setAssignmenttestid(String assignmenttestid) {
        this.assignmenttestid = assignmenttestid;
    }

    public byte[] getReldoc() {
        return reldoc;
    }

    public void setReldoc(byte[] reldoc) {
        this.reldoc = reldoc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTesttype() {
        return testtype;
    }

    public void setTesttype(String testtype) {
        this.testtype = testtype;
    }

    public String getTestno() {
        return testno;
    }

    public void setTestno(String testno) {
        this.testno = testno;
    }

    public Date getUploaddate() {
        return uploaddate;
    }

    public void setUploaddate(Date uploaddate) {
        this.uploaddate = uploaddate;
    }

    public Date getSubmissiondate() {
        return submissiondate;
    }

    public void setSubmissiondate(Date submissiondate) {
        this.submissiondate = submissiondate;
    }

    public M_Subjects getSubjectcode() {
        return subjectcode;
    }

    public void setSubjectcode(M_Subjects subjectcode) {
        this.subjectcode = subjectcode;
    }

    public MT_Userlogin getUsercode() {
        return usercode;
    }

    public void setUsercode(MT_Userlogin usercode) {
        this.usercode = usercode;
    }

    public Integer getPassmark() {
        return passmark;
    }

    public void setPassmark(Integer passmark) {
        this.passmark = passmark;
    }

    public Integer getFullmark() {
        return fullmark;
    }

    public void setFullmark(Integer fullmark) {
        this.fullmark = fullmark;
    }

    public String getSubmissiontype() {
        return submissiontype;
    }

    public void setSubmissiontype(String submissiontype) {
        this.submissiontype = submissiontype;
    }

    @Override
    public String toString() {
        return "T_Assignmenttest{" + "assignmenttestid=" + assignmenttestid + ", reldoc=" + reldoc + ", title=" + title + ", description=" + description + ", testtype=" + testtype + ", testno=" + testno + ", uploaddate=" + uploaddate + ", submissiondate=" + submissiondate + ", subjectcode=" + subjectcode + ", usercode=" + usercode + ", passmark=" + passmark + ", fullmark=" + fullmark + ", submissiontype=" + submissiontype + '}';
    }
}
