package com.nic.nerie.m_preposttestqcategory.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "m_preposttestqcategory")
public class M_PrePostTestQCategory {
    @Id
    private String pptqcategorycode;
    private String pptqcategoryname;

    public M_PrePostTestQCategory() {
    }

    public String getPptqcategorycode() {
        return pptqcategorycode;
    }

    public void setPptqcategorycode(String pptqcategorycode) {
        this.pptqcategorycode = pptqcategorycode;
    }

    public String getPptqcategoryname() {
        return pptqcategoryname;
    }

    public void setPptqcategoryname(String pptqcategoryname) {
        this.pptqcategoryname = pptqcategoryname;
    }

    @Override
    public String toString() {
        return "M_PrePostTestQCategory{" + "pptqcategorycode=" + pptqcategorycode + ", pptqcategoryname=" + pptqcategoryname + '}';
    }
}
