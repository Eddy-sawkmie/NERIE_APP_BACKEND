package com.nic.nerie.t_internalevaluationmarks.model;

import com.nic.nerie.m_subjects.model.M_Subjects;
import com.nic.nerie.mt_test.model.MT_Test;
import com.nic.nerie.t_faculties.model.T_Faculties;
import com.nic.nerie.t_students.model.T_Students;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "t_internalevaluationmarks")
public class T_InternalEvaluationMarks {
    @Id
    private String internalevaluationid;

    @ManyToOne
    @JoinColumn(name = "studentid")
    public T_Students studentid;

    @ManyToOne
    @JoinColumn(name = "facultyid")
    public T_Faculties facultyid;

    @ManyToOne
    @JoinColumn(name = "subjectcode")
    public M_Subjects subjectcode;

    private BigDecimal marks;

    @Temporal(TemporalType.DATE)
    private Date entrydate;

    @ManyToOne
    @JoinColumn(name = "testid")
    private MT_Test testid;

    public T_InternalEvaluationMarks() {
    }

    public T_InternalEvaluationMarks(String internalevaluationid, T_Students studentid, T_Faculties facultyid, M_Subjects subjectcode, BigDecimal marks, Date entrydate, MT_Test testid) {
        this.internalevaluationid = internalevaluationid;
        this.studentid = studentid;
        this.facultyid = facultyid;
        this.subjectcode = subjectcode;
        this.marks = marks;
        this.entrydate = entrydate;
        this.testid = testid;
    }

    public String getInternalevaluationid() {
        return internalevaluationid;
    }

    public void setInternalevaluationid(String internalevaluationid) {
        this.internalevaluationid = internalevaluationid;
    }

    public T_Students getStudentid() {
        return studentid;
    }

    public void setStudentid(T_Students studentid) {
        this.studentid = studentid;
    }

    public T_Faculties getFacultyid() {
        return facultyid;
    }

    public void setFacultyid(T_Faculties facultyid) {
        this.facultyid = facultyid;
    }

    public M_Subjects getSubjectcode() {
        return subjectcode;
    }

    public void setSubjectcode(M_Subjects subjectcode) {
        this.subjectcode = subjectcode;
    }

    public BigDecimal getMarks() {
        return marks;
    }

    public void setMarks(BigDecimal marks) {
        this.marks = marks;
    }

    public Date getEntrydate() {
        return entrydate;
    }

    public void setEntrydate(Date entrydate) {
        this.entrydate = entrydate;
    }

    public MT_Test getTestid() {
        return testid;
    }

    public void setTestid(MT_Test testid) {
        this.testid = testid;
    }

    @Override
    public String toString() {
        return "T_InternalEvaluationMarks{" + "internalevaluationid=" + internalevaluationid + ", studentid=" + studentid + ", facultyid=" + facultyid + ", subjectcode=" + subjectcode + ", marks=" + marks + ", entrydate=" + entrydate + ", testid=" + testid + '}';
    }
}
