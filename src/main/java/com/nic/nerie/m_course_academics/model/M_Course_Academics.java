package com.nic.nerie.m_course_academics.model;

import com.nic.nerie.m_departments.model.M_Departments;
import jakarta.persistence.*;

@Entity
@Table(name = "m_course_academics")
public class M_Course_Academics {
    @Id
    private String coursecode;
    private String coursename;
    private String isshortterm = "0";
    private String courseid;

    @ManyToOne
    @JoinColumn(name = "departmentcode")
    public M_Departments departmentcode;

    private String duration;

    public M_Course_Academics() {
    }

    public M_Course_Academics(String coursecode, String coursename, String courseid, M_Departments departmentcode, String duration) {
        this.coursecode = coursecode;
        this.coursename = coursename;
        this.courseid = courseid;
        this.departmentcode = departmentcode;
        this.duration = duration;
    }

    public String getCoursecode() {
        return coursecode;
    }

    public void setCoursecode(String coursecode) {
        this.coursecode = coursecode;
    }

    public String getCoursename() {
        return coursename;
    }

    public void setCoursename(String coursename) {
        this.coursename = coursename;
    }

    public String getIsshortterm() {
        return isshortterm;
    }

    public void setIsshortterm(String isshortterm) {
        this.isshortterm = isshortterm;
    }

    public String getCourseid() {
        return courseid;
    }

    public void setCourseid(String courseid) {
        this.courseid = courseid;
    }

    public M_Departments getDepartmentcode() {
        return departmentcode;
    }

    public void setDepartmentcode(M_Departments departmentcode) {
        this.departmentcode = departmentcode;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "M_Course_Academics{" + "coursecode=" + coursecode + ", coursename=" + coursename + ", isshortterm=" + isshortterm + ", courseid=" + courseid + ", departmentcode=" + departmentcode + ", duration=" + duration + '}';
    }
}
