package com.nic.nerie.m_qualificationcategories.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "m_qualificationcategories")
public class M_QualificationCategories {

    @Id
    private String qualificationcategorycode;
    private String qualificationcategoryname;

    public M_QualificationCategories() {
    }

    public M_QualificationCategories(String qualificationcategorycode, String qualificationcategoryname) {
        this.qualificationcategorycode = qualificationcategorycode;
        this.qualificationcategoryname = qualificationcategoryname;
    }

    public String getQualificationcategorycode() {
        return qualificationcategorycode;
    }

    public void setQualificationcategorycode(String qualificationcategorycode) {
        this.qualificationcategorycode = qualificationcategorycode;
    }

    public String getQualificationcategoryname() {
        return qualificationcategoryname;
    }

    public void setQualificationcategoryname(String qualificationcategoryname) {
        this.qualificationcategoryname = qualificationcategoryname;
    }

}

