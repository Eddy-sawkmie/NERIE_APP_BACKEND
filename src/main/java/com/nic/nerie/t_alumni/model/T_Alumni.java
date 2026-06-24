package com.nic.nerie.t_alumni.model;

import com.nic.nerie.m_course_academics.model.M_Course_Academics;
import com.nic.nerie.m_departments.model.M_Departments;

import com.nic.nerie.m_offices.model.M_Offices;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "t_alumni")
public class T_Alumni {
    @Id
    private String alumniid;
    private String rollno;
    private String fname;
    private String mname;
    private String lname;
    private String batch;
    private String mobileno;
    private String email;
    private String currentoccupation;
    private String gender;
    

    @ManyToOne
    @JoinColumn(name = "departmentcode")
    public M_Departments departmentcode;

    
    @ManyToOne
    @JoinColumn(name = "coursecode")
    public M_Course_Academics coursecode;

    @ManyToOne
    @JoinColumn(name = "officecode")
    public M_Offices moffices;

    public T_Alumni() {
    }

    public T_Alumni(String alumniid, String rollno, String fname, String mname, String lname, String batch, String mobileno, String email, String currentoccupation, String gender, M_Departments departmentcode, M_Course_Academics coursecode, M_Offices moffices) {
        this.alumniid = alumniid;
        this.rollno = rollno;
        this.fname = fname;
        this.mname = mname;
        this.lname = lname;
        this.batch = batch;
        this.mobileno = mobileno;
        this.email = email;
        this.currentoccupation = currentoccupation;
        this.gender = gender;
        this.departmentcode = departmentcode;
        this.coursecode = coursecode;
        this.moffices = moffices;
    }

    public String getAlumniid() {
        return alumniid;
    }

    public void setAlumniid(String alumniid) {
        this.alumniid = alumniid;
    }

    public String getRollno() {
        return rollno;
    }

    public void setRollno(String rollno) {
        this.rollno = rollno;
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

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getMobileno() {
        return mobileno;
    }

    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCurrentoccupation() {
        return currentoccupation;
    }

    public void setCurrentoccupation(String currentoccupation) {
        this.currentoccupation = currentoccupation;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public M_Departments getDepartmentcode() {
        return departmentcode;
    }

    public void setDepartmentcode(M_Departments departmentcode) {
        this.departmentcode = departmentcode;
    }

    public M_Course_Academics getCoursecode() {
        return coursecode;
    }

    public void setCoursecode(M_Course_Academics coursecode) {
        this.coursecode = coursecode;
    }

    public M_Offices getMoffices() { return moffices; }

    public void setMoffices(M_Offices moffices) { this.moffices = moffices; }

    @Override
    public String toString() {
        return "T_Alumni{" + "alumniid=" + alumniid + ", rollno=" + rollno + ", fname=" + fname + ", mname=" + mname + ", lname=" + lname + ", batch=" + batch + ", mobileno=" + mobileno + ", email=" + email + ", currentoccupation=" + currentoccupation + ", gender=" + gender + ", departmentcode=" + departmentcode + ", coursecode=" + coursecode + ", office=" + (moffices != null ? moffices.getOfficecode() : "null") + '}';
    }
}
