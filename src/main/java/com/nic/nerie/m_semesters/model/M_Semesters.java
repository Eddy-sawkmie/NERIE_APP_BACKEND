package com.nic.nerie.m_semesters.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "m_semesters")
public class M_Semesters {
    @Id
    private String semestercode;
    private String semestername;

    public M_Semesters() {
    }

    public M_Semesters(String semestercode, String semestername) {
        this.semestercode = semestercode;
        this.semestername = semestername;
    }

    public String getSemestercode() {
        return semestercode;
    }

    public void setSemestercode(String semestercode) {
        this.semestercode = semestercode;
    }

    public String getSemestername() {
        return semestername;
    }

    public void setSemestername(String semestername) {
        this.semestername = semestername;
    }

    @Override
    public String toString() {
        return "M_Semesters{" + "semestercode=" + semestercode + ", semestername=" + semestername + '}';
    }
}
