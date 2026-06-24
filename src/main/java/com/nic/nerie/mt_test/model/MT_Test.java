package com.nic.nerie.mt_test.model;

import com.nic.nerie.m_subjects.model.M_Subjects;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Table(name = "mt_test")
public class MT_Test {

    @Id
    private String testid;

    private String testno;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date testdate;

    private String testname;

    @ManyToOne
    @JoinColumn(name = "subjectcode")
    public M_Subjects subjectcode;


    @ManyToOne
    @JoinColumn(name = "usercode")
    public MT_Userlogin usercode;

    private String passmark;

    private String fullmark;

    @Temporal(TemporalType.TIMESTAMP)
    private Date entrydate;


    public MT_Test() {
    }

    public MT_Test(String testid, String testno, Date testdate, String testname, M_Subjects subjectcode, MT_Userlogin usercode, String passmark, String fullmark, Date entrydate) {
        this.testid = testid;
        this.testno = testno;
        this.testdate = testdate;
        this.testname = testname;
        this.subjectcode = subjectcode;
        this.usercode = usercode;
        this.passmark = passmark;
        this.fullmark = fullmark;
        this.entrydate = entrydate;
    }

    public String getTestid() {
        return testid;
    }

    public void setTestid(String testid) {
        this.testid = testid;
    }

    public String getTestno() {
        return testno;
    }

    public void setTestno(String testno) {
        this.testno = testno;
    }

    public Date getTestdate() {
        return testdate;
    }

    public void setTestdate(Date testdate) {
        this.testdate = testdate;
    }

    public String getTestname() {
        return testname;
    }

    public void setTestname(String testname) {
        this.testname = testname;
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

    public String getPassmark() {
        return passmark;
    }

    public void setPassmark(String passmark) {
        this.passmark = passmark;
    }

    public String getFullmark() {
        return fullmark;
    }

    public void setFullmark(String fullmark) {
        this.fullmark = fullmark;
    }

    public Date getEntrydate() {
        return entrydate;
    }

    public void setEntrydate(Date entrydate) {
        this.entrydate = entrydate;
    }

    @Override
    public String toString() {
        return "MT_Test{" + "testid=" + testid + ", testno=" + testno + ", testdate=" + testdate + ", testname=" + testname + ", subjectcode=" + subjectcode + ", usercode=" + usercode + ", passmark=" + passmark + ", fullmark=" + fullmark + ", entrydate=" + entrydate + '}';
    }

}
