package com.nic.nerie.m_shortterm_phases.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "m_shortterm_phases")
public class M_ShortTerm_Phases {
    @Id
    private String sphaseid;
    private String sphasename;

    public M_ShortTerm_Phases() {
    }

    public M_ShortTerm_Phases(String sphaseid, String sphasename) {
        this.sphaseid = sphaseid;
        this.sphasename = sphasename;
    }

    public String getSphaseid() {
        return sphaseid;
    }

    public void setSphaseid(String sphaseid) {
        this.sphaseid = sphaseid;
    }

    public String getSphasename() {
        return sphasename;
    }

    public void setSphasename(String sphasename) {
        this.sphasename = sphasename;
    }

    @Override
    public String toString() {
        return "M_shortterm_phases{" + "sphaseid=" + sphaseid + ", sphasename=" + sphasename + '}';
    }

}