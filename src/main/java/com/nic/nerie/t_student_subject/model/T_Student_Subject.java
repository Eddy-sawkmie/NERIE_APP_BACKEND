package com.nic.nerie.t_student_subject.model;

import com.nic.nerie.m_subjects.model.M_Subjects;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import jakarta.persistence.*;

@Entity
@Table(name = "t_student_subject")
public class T_Student_Subject {
    @Id
    private String tssid;

    @ManyToOne
    @JoinColumn(name = "usercode")
    private MT_Userlogin usercode;

    @ManyToOne
    @JoinColumn(name = "subjectcode")
    private M_Subjects subjectcode;

    private String isactive;

    public T_Student_Subject() {
    }

    public T_Student_Subject(String tssid, MT_Userlogin usercode, M_Subjects subjectcode, String isactive) {
        this.tssid = tssid;
        this.usercode = usercode;
        this.subjectcode = subjectcode;
        this.isactive = isactive;
    }

    public String getTssid() {
        return tssid;
    }

    public void setTssid(String tssid) {
        this.tssid = tssid;
    }

    public MT_Userlogin getUsercode() {
        return usercode;
    }

    public void setUsercode(MT_Userlogin usercode) {
        this.usercode = usercode;
    }

    public M_Subjects getSubjectcode() {
        return subjectcode;
    }

    public void setSubjectcode(M_Subjects subjectcode) {
        this.subjectcode = subjectcode;
    }

    public String getIsactive() {
        return isactive;
    }

    public void setIsactive(String isactive) {
        this.isactive = isactive;
    }

    @Override
    public String toString() {
        return "T_Student_Subject{" + "tssid=" + tssid + ", usercode=" + usercode + ", subjectcode=" + subjectcode + ", isactive=" + isactive + '}';
    }
}
