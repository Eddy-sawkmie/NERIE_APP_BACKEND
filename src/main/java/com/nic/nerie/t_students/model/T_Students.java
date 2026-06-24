package com.nic.nerie.t_students.model;

import com.nic.nerie.m_course_academics.model.M_Course_Academics;
import com.nic.nerie.m_departments.model.M_Departments;
import com.nic.nerie.m_offices.model.M_Offices;
import com.nic.nerie.m_semesters.model.M_Semesters;
import com.nic.nerie.m_shortterm_phases.model.M_ShortTerm_Phases;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "t_students")
public class T_Students {
    @Id
    private String studentid;

    private String fname;
    private String mname;
    private String lname;
    private String email;
    private String mobileno;
    private String gender;
    private String academicyear;
    private String isshortterm = "0";
    private String iscurrent = "0";
    private String rollno;

    @Temporal(TemporalType.DATE)
    private Date dateofbirth;

    @ManyToOne
    @JoinColumn(name = "departmentcode")
    public M_Departments departmentcode;

    @ManyToOne
    @JoinColumn(name = "semestercode")
    public M_Semesters semestercode;

    @ManyToOne
    @JoinColumn(name="coursecode")
    public M_Course_Academics coursecode;

    @ManyToOne
    @JoinColumn(name = "usercode")
    // @JsonIgnore
    public MT_Userlogin usercode;

    @ManyToOne
    @JoinColumn(name = "sphaseid")
    public M_ShortTerm_Phases sphaseid;

    @ManyToOne
    @JoinColumn(name = "officecode")
    public M_Offices officecode;

    public T_Students() {
    }

    public T_Students(String studentid) {
        this.studentid = studentid;
    }

    public T_Students(String studentid, String fname, String mname, String lname, String email, String mobileno, String gender, String academicyear, String rollno, Date dateofbirth, M_Departments departmentcode, M_Semesters semestercode, M_Course_Academics coursecode, MT_Userlogin usercode, M_ShortTerm_Phases sphaseid, M_Offices officecode) {
        this.studentid = studentid;
        this.fname = fname;
        this.mname = mname;
        this.lname = lname;
        this.email = email;
        this.mobileno = mobileno;
        this.gender = gender;
        this.academicyear = academicyear;
        this.rollno = rollno;
        this.dateofbirth = dateofbirth;
        this.departmentcode = departmentcode;
        this.semestercode = semestercode;
        this.coursecode = coursecode;
        this.usercode = usercode;
        this.sphaseid = sphaseid;
        this.officecode = officecode;
    }

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileno() {
        return mobileno;
    }

    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAcademicyear() {
        return academicyear;
    }

    public void setAcademicyear(String academicyear) {
        this.academicyear = academicyear;
    }

    public String getIsshortterm() {
        return isshortterm;
    }

    public void setIsshortterm(String isshortterm) {
        this.isshortterm = isshortterm;
    }

    public String getIscurrent() {
        return iscurrent;
    }

    public void setIscurrent(String iscurrent) {
        this.iscurrent = iscurrent;
    }

    public String getRollno() {
        return rollno;
    }

    public void setRollno(String rollno) {
        this.rollno = rollno;
    }

    public Date getDateofbirth() {
        return dateofbirth;
    }

    public void setDateofbirth(Date dateofbirth) {
        this.dateofbirth = dateofbirth;
    }

    public M_Departments getDepartmentcode() {
        return departmentcode;
    }

    public void setDepartmentcode(M_Departments departmentcode) {
        this.departmentcode = departmentcode;
    }

    public M_Semesters getSemestercode() {
        return semestercode;
    }

    public void setSemestercode(M_Semesters semestercode) {
        this.semestercode = semestercode;
    }

    public M_Course_Academics getCoursecode() {
        return coursecode;
    }

    public void setCoursecode(M_Course_Academics coursecode) {
        this.coursecode = coursecode;
    }

    public MT_Userlogin getUsercode() {
        return usercode;
    }

    public void setUsercode(MT_Userlogin usercode) {
        this.usercode = usercode;
    }

    public M_ShortTerm_Phases getSphaseid() {
        return sphaseid;
    }

    public void setSphaseid(M_ShortTerm_Phases sphaseid) {
        this.sphaseid = sphaseid;
    }

    public M_Offices getOfficecode() {
        return officecode;
    }

    public void setOfficecode(M_Offices officecode) {
        this.officecode = officecode;
    }

    @Override
    public String toString() {
        return "T_Students{" + "studentid=" + studentid + ", fname=" + fname + ", mname=" + mname + ", lname=" + lname + ", email=" + email + ", mobileno=" + mobileno + ", gender=" + gender + ", academicyear=" + academicyear + ", isshortterm=" + isshortterm + ", iscurrent=" + iscurrent + ", rollno=" + rollno + ", dateofbirth=" + dateofbirth + ", departmentcode=" + departmentcode + ", semestercode=" + semestercode + ", coursecode=" + coursecode + ", usercode=" + usercode + ", sphaseid=" + sphaseid + ", officecode=" + officecode + '}';
    }
}
