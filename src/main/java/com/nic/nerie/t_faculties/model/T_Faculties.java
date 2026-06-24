package com.nic.nerie.t_faculties.model;

import com.nic.nerie.m_departments.model.M_Departments;
import com.nic.nerie.m_designations.model.M_Designations;
import com.nic.nerie.m_offices.model.M_Offices;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import jakarta.persistence.*;

@Entity
@Table(name = "t_faculties")
public class T_Faculties {
    @Id
    private String facultyid;

    private String fname;
    private String mname;
    private String lname;

    @ManyToOne
    @JoinColumn(name = "departmentcode")
    public M_Departments departmentcode;

    @ManyToOne
    @JoinColumn(name = "designationcode")
    public M_Designations designationcode;


    @ManyToOne
    @JoinColumn(name = "usercode")
    public MT_Userlogin usercode;

    @ManyToOne
    @JoinColumn(name = "officecode")
    public M_Offices officecode;

    public T_Faculties() {
    }

    public T_Faculties(String facultyid) {
        this.facultyid = facultyid;
    }

    public T_Faculties(String facultyid, String fname, String mname, String lname, M_Departments departmentcode, M_Designations designationcode, MT_Userlogin usercode, M_Offices officecode) {
        this.facultyid = facultyid;
        this.fname = fname;
        this.mname = mname;
        this.lname = lname;
        this.departmentcode = departmentcode;
        this.designationcode = designationcode;
        this.usercode = usercode;
        this.officecode = officecode;
    }

    public String getFacultyid() {
        return facultyid;
    }

    public void setFacultyid(String facultyid) {
        this.facultyid = facultyid;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getMname() {
        return mname;
    }

    public void setMname(String mname) {
        this.mname = mname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public M_Departments getDepartmentcode() {
        return departmentcode;
    }

    public void setDepartmentcode(M_Departments departmentcode) {
        this.departmentcode = departmentcode;
    }

    public M_Designations getDesignationcode() {
        return designationcode;
    }

    public void setDesignationcode(M_Designations designationcode) {
        this.designationcode = designationcode;
    }

    public MT_Userlogin getUsercode() {
        return usercode;
    }

    public void setUsercode(MT_Userlogin usercode) {
        this.usercode = usercode;
    }

    public M_Offices getOfficecode() {
        return officecode;
    }

    public void setOfficecode(M_Offices officecode) {
        this.officecode = officecode;
    }

    @Override
    public String toString() {
        return "T_Faculties{" + "facultyid=" + facultyid + ", fname=" + fname + ", mname=" + mname + ", lname=" + lname + ", departmentcode=" + departmentcode + ", designationcode=" + designationcode + ", usercode=" + usercode + ", officecode=" + officecode + '}';
    }
}
