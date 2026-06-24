package com.nic.nerie.m_programs.model;

import com.nic.nerie.m_coursecategories.model.M_CourseCategories;
import com.nic.nerie.m_offices.model.M_Offices;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import jakarta.persistence.*;

@Entity
@Table(name = "m_programs")
public class M_Programs {
    @Id
    private String programcode;
    private String programname;
    private String programdescription;
    private String closed;
    private String closingreport;
    private String programid;

    @ManyToOne
    @JoinColumn(name = "coursecodecategory")
    public M_CourseCategories mcoursecategories;

    @ManyToOne
    @JoinColumn(name = "officecode")
    public M_Offices moffices;

    @ManyToOne
    @JoinColumn(name = "enteredby")
    public MT_Userlogin usercode;

    @Column(name = "programcattwo", length = 30)
    private String programcattwo;

    public M_Programs() {
    }

    public M_Programs(String programcode, String programname, String programdescription, String closed, String closingreport, String programid, M_CourseCategories mcoursecategories, M_Offices moffices, MT_Userlogin usercode, String programcattwo) {
        this.programcode = programcode;
        this.programname = programname;
        this.programdescription = programdescription;
        this.closed = closed;
        this.closingreport = closingreport;
        this.programid = programid;
        this.mcoursecategories = mcoursecategories;
        this.moffices = moffices;
        this.usercode = usercode;
        this.programcattwo = programcattwo;
    }

    public String getProgramcode() {
        return programcode;
    }

    public void setProgramcode(String programcode) {
        this.programcode = programcode;
    }

    public String getProgramname() {
        return programname;
    }

    public void setProgramname(String programname) {
        this.programname = programname;
    }

    public String getProgramdescription() {
        return programdescription;
    }

    public void setProgramdescription(String programdescription) {
        this.programdescription = programdescription;
    }

    public String getClosed() {
        return closed;
    }

    public void setClosed(String closed) {
        this.closed = closed;
    }

    public String getClosingreport() {
        return closingreport;
    }

    public void setClosingreport(String closingreport) {
        this.closingreport = closingreport;
    }

    public String getProgramid() {
        return programid;
    }

    public void setProgramid(String programid) {
        this.programid = programid;
    }

    public M_CourseCategories getMcoursecategories() {
        return mcoursecategories;
    }

    public void setMcoursecategories(M_CourseCategories mcoursecategories) {
        this.mcoursecategories = mcoursecategories;
    }

    public M_Offices getMoffices() {
        return moffices;
    }

    public void setMoffices(M_Offices moffices) {
        this.moffices = moffices;
    }

    public MT_Userlogin getUsercode() {
        return usercode;
    }

    public void setUsercode(MT_Userlogin usercode) {
        this.usercode = usercode;
    }

    public String getProgramcattwo() {
        return programcattwo;
    }

    public void setProgramcattwo(String programcattwo) {
        this.programcattwo = programcattwo;
    }

    @Override
    public String toString() {
        return "M_Programs{" + "programcode=" + programcode + ", programname=" + programname + ", programdescription=" + programdescription + ", closed=" + closed + ", closingreport=" + closingreport + ", programid=" + programid + ", mcoursecategories=" + mcoursecategories + ", moffices=" + moffices + ", usercode=" + usercode + ", programcattwo= " + programcattwo + '}';
    }
}
