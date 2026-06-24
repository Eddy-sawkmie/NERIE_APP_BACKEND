package com.nic.nerie.m_activities.model;

import com.nic.nerie.m_phases.model.M_Phases;
import com.nic.nerie.m_programs.model.M_Programs;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "m_activities")
public class M_Activities {

    @Id
    private String activityid;

    @ManyToOne
    @JoinColumn(name = "phaseid")
    public M_Phases phaseid;

    @ManyToOne
    @JoinColumn(name = "programcode")
    public M_Programs programcode;

    private String activityname;
    private String activitydescription;

    @Temporal(TemporalType.TIMESTAMP)
    Date activitystartdate;
    @Temporal(TemporalType.TIMESTAMP)
    Date activityenddate;

    private String expenditure;

    public String getActivityid() {
        return activityid;
    }

    public void setActivityid(String activityid) {
        this.activityid = activityid;
    }

    public M_Phases getPhaseid() {
        return phaseid;
    }

    public void setPhaseid(M_Phases phaseid) {
        this.phaseid = phaseid;
    }

    public M_Programs getProgramcode() {
        return programcode;
    }

    public void setProgramcode(M_Programs programcode) {
        this.programcode = programcode;
    }

    public String getActivityname() {
        return activityname;
    }

    public void setActivityname(String activityname) {
        this.activityname = activityname;
    }

    public String getActivitydescription() {
        return activitydescription;
    }

    public void setActivitydescription(String activitydescription) {
        this.activitydescription = activitydescription;
    }

    public Date getActivitystartdate() {
        return activitystartdate;
    }

    public void setActivitystartdate(Date activitystartdate) {
        this.activitystartdate = activitystartdate;
    }

    public Date getActivityenddate() {
        return activityenddate;
    }

    public void setActivityenddate(Date activityenddate) {
        this.activityenddate = activityenddate;
    }

    public String getExpenditure() {
        return expenditure;
    }

    public void setExpenditure(String expenditure) {
        this.expenditure = expenditure;
    }

    @Override
    public String toString() {
        return "M_Activities{" + "activityid=" + activityid + ", phaseid=" + phaseid + ", programcode=" + programcode + ", activityname=" + activityname + ", activitydescription=" + activitydescription + ", activitystartdate=" + activitystartdate + ", activityenddate=" + activityenddate + ", expenditure=" + expenditure + '}';
    }

}

