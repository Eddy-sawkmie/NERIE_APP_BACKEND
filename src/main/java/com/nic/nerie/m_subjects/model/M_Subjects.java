package com.nic.nerie.m_subjects.model;

import com.nic.nerie.m_course_academics.model.M_Course_Academics;
import com.nic.nerie.m_departments.model.M_Departments;
import com.nic.nerie.m_semesters.model.M_Semesters;
import com.nic.nerie.m_shortterm_phases.model.M_ShortTerm_Phases;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "m_subjects")
public class M_Subjects {
    @Id
    private String subjectcode;

    private String subjectname;

    private String isshortterm = "0";

    @ManyToOne
    @JoinColumn(name = "departmentcode")
    public M_Departments departmentcode;

    @ManyToOne
    @JoinColumn(name = "semestercode")
    public M_Semesters semestercode;

    @ManyToOne
    @JoinColumn(name="sphaseid")
    public M_ShortTerm_Phases sphaseid;

    private String isoptional = "0";

    @ManyToOne
    @JoinColumn(name="coursecode")
    public M_Course_Academics coursecode;

    public M_Subjects() {
    }

    public M_Subjects(String subjectcode) {
        this.subjectcode = subjectcode;
    }

    public M_Subjects(String subjectcode, String subjectname, M_Departments departmentcode, M_Semesters semestercode, M_ShortTerm_Phases sphaseid, M_Course_Academics coursecode) {
        this.subjectcode = subjectcode;
        this.subjectname = subjectname;
        this.departmentcode = departmentcode;
        this.semestercode = semestercode;
        this.sphaseid = sphaseid;
        this.coursecode = coursecode;
    }

    public String getSubjectcode() {
        return subjectcode;
    }

    public void setSubjectcode(String subjectcode) {
        this.subjectcode = subjectcode;
    }

    public String getSubjectname() {
        return subjectname;
    }

    public void setSubjectname(String subjectname) {
        this.subjectname = subjectname;
    }

    public String getIsshortterm() {
        return isshortterm;
    }

    public void setIsshortterm(String isshortterm) {
        this.isshortterm = isshortterm;
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

    public M_ShortTerm_Phases getSphaseid() {
        return sphaseid;
    }

    public void setSphaseid(M_ShortTerm_Phases sphaseid) {
        this.sphaseid = sphaseid;
    }

    public String getIsoptional() {
        return isoptional;
    }

    public void setIsoptional(String isoptional) {
        this.isoptional = isoptional;
    }

    public M_Course_Academics getCoursecode() {
        return coursecode;
    }

    public void setCoursecode(M_Course_Academics coursecode) {
        this.coursecode = coursecode;
    }

    @Override
    public String toString() {
        return "M_Subjects{" + "subjectcode=" + subjectcode + ", subjectname=" + subjectname + ", isshortterm=" + isshortterm + ", departmentcode=" + departmentcode + ", semestercode=" + semestercode + ", sphaseid=" + sphaseid + ", isoptional=" + isoptional + ", coursecode=" + coursecode + '}';
    }
}
