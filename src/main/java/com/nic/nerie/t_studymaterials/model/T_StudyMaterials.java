package com.nic.nerie.t_studymaterials.model;

import com.nic.nerie.m_subjects.model.M_Subjects;
import com.nic.nerie.t_faculties.model.T_Faculties;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "t_studymaterials")
public class T_StudyMaterials {
    @Id
    private String studymaterialid;

    private String title;

    @Temporal(TemporalType.DATE)
    private Date uploaddate;

    @ManyToOne
    @JoinColumn(name = "subjectcode")
    public M_Subjects subjectcode;

    private byte[] reldoc;

    @ManyToOne
    @JoinColumn(name = "facultyid")
    public T_Faculties facultyid;

    public T_StudyMaterials() {
    }

    public T_StudyMaterials(String studymaterialid, String title, Date uploaddate, M_Subjects subjectcode, byte[] reldoc, T_Faculties facultyid) {
        this.studymaterialid = studymaterialid;
        this.title = title;
        this.uploaddate = uploaddate;
        this.subjectcode = subjectcode;
        this.reldoc = reldoc;
        this.facultyid = facultyid;
    }

    public String getStudymaterialid() {
        return studymaterialid;
    }

    public void setStudymaterialid(String studymaterialid) {
        this.studymaterialid = studymaterialid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getUploaddate() {
        return uploaddate;
    }

    public void setUploaddate(Date uploaddate) {
        this.uploaddate = uploaddate;
    }

    public M_Subjects getSubjectcode() {
        return subjectcode;
    }

    public void setSubjectcode(M_Subjects subjectcode) {
        this.subjectcode = subjectcode;
    }

    public byte[] getReldoc() {
        return reldoc;
    }

    public void setReldoc(byte[] reldoc) {
        this.reldoc = reldoc;
    }

    public T_Faculties getFacultyid() {
        return facultyid;
    }

    public void setFacultyid(T_Faculties facultyid) {
        this.facultyid = facultyid;
    }

    @Override
    public String toString() {
        return "T_StudyMaterials{" + "studymaterialid=" + studymaterialid + ", title=" + title + ", uploaddate=" + uploaddate + ", subjectcode=" + subjectcode + ", reldoc=" + reldoc + ", facultyid=" + facultyid + '}';
    }
}
