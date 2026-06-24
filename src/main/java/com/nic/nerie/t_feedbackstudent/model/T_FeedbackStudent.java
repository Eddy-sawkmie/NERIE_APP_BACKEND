package com.nic.nerie.t_feedbackstudent.model;

import com.nic.nerie.m_subjects.model.M_Subjects;
import com.nic.nerie.t_faculties.model.T_Faculties;
import com.nic.nerie.t_students.model.T_Students;
import jakarta.persistence.*;

import java.util.Date;

import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

@Entity
@Table(name = "t_feedback_student")
public class T_FeedbackStudent {
    @Id
    @Generated(event = EventType.INSERT)
    private String feedbackid;

    private String feedback;

    @ManyToOne
    @JoinColumn(name = "subjectcode")
    public M_Subjects subjectcode;

    @ManyToOne
    @JoinColumn(name = "studentid")
    public T_Students studentid;

    @ManyToOne
    @JoinColumn(name = "facultyid")
    public T_Faculties facultyid;

    @Temporal(TemporalType.TIMESTAMP)
    private Date entrydate;

    public T_FeedbackStudent() {
    }

    public T_FeedbackStudent(String feedbackid, String feedback, M_Subjects subjectcode, T_Students studentid, T_Faculties facultyid, Date entrydate) {
        this.feedbackid = feedbackid;
        this.feedback = feedback;
        this.subjectcode = subjectcode;
        this.studentid = studentid;
        this.facultyid = facultyid;
        this.entrydate = entrydate;
    }

    public String getFeedbackid() {
        return feedbackid;
    }

    public void setFeedbackid(String feedbackid) {
        this.feedbackid = feedbackid;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public M_Subjects getSubjectcode() {
        return subjectcode;
    }

    public void setSubjectcode(M_Subjects subjectcode) {
        this.subjectcode = subjectcode;
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

    public Date getEntrydate() {
        return entrydate;
    }

    public void setEntrydate(Date entrydate) {
        this.entrydate = entrydate;
    }

    @Override
    public String toString() {
        return "T_FeedbackStudent{" + "feedbackid=" + feedbackid + ", feedback=" + feedback + ", subjectcode=" + subjectcode + ", studentid=" + studentid + ", facultyid=" + facultyid + ", entrydate=" + entrydate + '}';
    }
}
