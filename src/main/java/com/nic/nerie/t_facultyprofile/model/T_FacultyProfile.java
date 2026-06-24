package com.nic.nerie.t_facultyprofile.model;

import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.t_researchpaper.model.T_ResearchPaper;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "t_facultyprofile")
public class T_FacultyProfile {
    @Id
    private String facultyprofileid;

    @ManyToOne
    @JoinColumn(name = "usercode")
    public MT_Userlogin usercode;

    private String gscholarlink;
    private String academicqualification;
    private String areaofspecialization;
    private String areaofinterest;
    private String briefprofile;
    private String researchprojects;
    private String orcid;

    @OneToMany(mappedBy = "facultyProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<T_ResearchPaper> researchpapers = new ArrayList<>();

    public T_FacultyProfile() {
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    public String getFacultyprofileid() {
        return facultyprofileid;
    }

    public void setFacultyprofileid(String facultyprofileid) {
        this.facultyprofileid = facultyprofileid;
    }

    public MT_Userlogin getUsercode() {
        return usercode;
    }

    public void setUsercode(MT_Userlogin usercode) {
        this.usercode = usercode;
    }

    public String getGscholarlink() {
        return gscholarlink;
    }

    public void setGscholarlink(String gscholarlink) {
        this.gscholarlink = gscholarlink;
    }

    public String getAcademicqualification() {
        return academicqualification;
    }

    public void setAcademicqualification(String academicqualification) {
        this.academicqualification = academicqualification;
    }

    public String getAreaofspecialization() {
        return areaofspecialization;
    }

    public void setAreaofspecialization(String areaofspecialization) {
        this.areaofspecialization = areaofspecialization;
    }

    public String getAreaofinterest() {
        return areaofinterest;
    }

    public void setAreaofinterest(String areaofinterest) {
        this.areaofinterest = areaofinterest;
    }

    public String getBriefprofile() {
        return briefprofile;
    }

    public void setBriefprofile(String briefprofile) {
        this.briefprofile = briefprofile;
    }

    public String getResearchprojects() {
        return researchprojects;
    }

    public void setResearchprojects(String researchprojects) {
        this.researchprojects = researchprojects;
    }

    public List<T_ResearchPaper> getResearchpapers() {
        return researchpapers;
    }

    public void setResearchpapers(List<T_ResearchPaper> researchpapers) {
        this.researchpapers = researchpapers;
    }

    @Override
    public String toString() {
        return "T_FacultyProfile{" + "facultyprofileid=" + facultyprofileid + ", usercode=" + usercode + ", gscholarlink=" + gscholarlink + ", academicqualification=" + academicqualification + ", areaofspecialization=" + areaofspecialization + ", areaofinterest=" + areaofinterest + ", briefprofile=" + briefprofile + ", researchprojects=" + researchprojects + ", orcid=" + orcid + '}';
    }
}
