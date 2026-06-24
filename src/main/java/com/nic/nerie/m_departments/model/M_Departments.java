package com.nic.nerie.m_departments.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nic.nerie.m_offices.model.M_Offices;
import jakarta.persistence.*;

@Entity
@Table(name = "m_departments")
public class M_Departments {
    @Id
    private String departmentcode;
    private String departmentname;

    @ManyToOne
    @JoinColumn(name = "officecode")
    @JsonIgnore
    private M_Offices moffices;

    public M_Departments() {
    }

    public M_Departments(String departmentcode, String departmentname, M_Offices moffices) {
        this.departmentcode = departmentcode;
        this.departmentname = departmentname;
        this.moffices = moffices;
    }

    public String getDepartmentcode() {
        return departmentcode;
    }

    public void setDepartmentcode(String departmentcode) {
        this.departmentcode = departmentcode;
    }

    public String getDepartmentname() {
        return departmentname;
    }

    public void setDepartmentname(String departmentname) {
        this.departmentname = departmentname;
    }

    public M_Offices getMoffices() {
        return moffices;
    }

    public void setMoffices(M_Offices moffices) {
        this.moffices = moffices;
    }

    @Override
    public String toString() {
        return "M_Departments{" +
                "departmentcode='" + departmentcode + '\'' +
                ", departmentname='" + departmentname + '\'' +
                ", moffices=" + (moffices != null ? moffices.getOfficecode() : null) +
                '}';
    }
}
