package com.nic.nerie.m_categories.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Table(name = "m_categories")
public class M_Categories implements Serializable {

    @Id
    private String categorycode;
    private String categoryname;

    public M_Categories(String categorycode, String categoryname) {
        this.categorycode = categorycode;
        this.categoryname = categoryname;
    }

    public M_Categories() {
    }

    public String getCategorycode() {
        return categorycode;
    }

    public void setCategorycode(String categorycode) {
        this.categorycode = categorycode;
    }

    public String getCategoryname() {
        return categoryname;
    }

    public void setCategoryname(String categoryname) {
        this.categoryname = categoryname;
    }

}

