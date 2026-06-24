package com.nic.nerie.m_qualificationsubjects.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Table(name = "m_qualificationsubjects")
public class M_QualificationSubjects implements Serializable {

    @Id
    private String qualificationsubjectcode;
    private String qualificationsubjectname;

    public M_QualificationSubjects() {
    }

    public M_QualificationSubjects(String qualificationsubjectcode, String qualificationsubjectname) {
        this.qualificationsubjectcode = qualificationsubjectcode;
        this.qualificationsubjectname = qualificationsubjectname;
    }

    public String getQualificationsubjectcode() {
        return qualificationsubjectcode;
    }

    public void setQualificationsubjectcode(String qualificationsubjectcode) {
        this.qualificationsubjectcode = qualificationsubjectcode;
    }

    public String getQualificationsubjectname() {
        return qualificationsubjectname;
    }

    public void setQualificationsubjectname(String qualificationsubjectname) {
        this.qualificationsubjectname = qualificationsubjectname;
    }

}