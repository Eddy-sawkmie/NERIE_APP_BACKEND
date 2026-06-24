package com.nic.nerie.m_coursecategories.model;

import jakarta.persistence.*;

@Entity
@Table(name = "m_coursecategories")
public class M_CourseCategories {
    @Id
    private String coursecategorycode;
    private String coursecategoryname;
    private String coursetype;

    public M_CourseCategories() {
    }

    public M_CourseCategories(String coursecategorycode, String coursecategoryname, String coursetype) {
        this.coursecategorycode = coursecategorycode;
        this.coursecategoryname = coursecategoryname;
        this.coursetype = coursetype;
    }

    public String getCoursecategorycode() {
        return coursecategorycode;
    }

    public void setCoursecategorycode(String coursecategorycode) {
        this.coursecategorycode = coursecategorycode;
    }

    public String getCoursecategoryname() {
        return coursecategoryname;
    }

    public void setCoursecategoryname(String coursecategoryname) {
        this.coursecategoryname = coursecategoryname;
    }

    public String getCoursetype() {
        return coursetype;
    }

    public void setCoursetype(String coursetype) {
        this.coursetype = coursetype;
    }

}
