package com.nic.nerie.m_qualifications.model;

import com.nic.nerie.m_qualificationcategories.model.M_QualificationCategories;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "m_qualifications")
public class M_Qualifications implements Serializable {

    @Id
    private String qualificationcode;
    private String qualificationname;

    @ManyToOne
    @JoinColumn(name = "qualificationcategorycode")
    public M_QualificationCategories mqualificationcategories;

    public M_Qualifications() {
    }

    public M_Qualifications(String qualificationcode, String qualificationname, M_QualificationCategories mqualificationcategories) {
        this.qualificationcode = qualificationcode;
        this.qualificationname = qualificationname;
        this.mqualificationcategories = mqualificationcategories;
    }

    public String getQualificationcode() {
        return qualificationcode;
    }

    public void setQualificationcode(String qualificationcode) {
        this.qualificationcode = qualificationcode;
    }

    public String getQualificationname() {
        return qualificationname;
    }

    public void setQualificationname(String qualificationname) {
        this.qualificationname = qualificationname;
    }

    public M_QualificationCategories getMqualificationcategories() {
        return mqualificationcategories;
    }

    public void setMqualificationcategories(M_QualificationCategories mqualificationcategories) {
        this.mqualificationcategories = mqualificationcategories;
    }

}
