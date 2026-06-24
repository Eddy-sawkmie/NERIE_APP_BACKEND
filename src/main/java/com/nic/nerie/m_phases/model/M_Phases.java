package com.nic.nerie.m_phases.model;

import com.nic.nerie.m_programs.model.M_Programs;
import jakarta.persistence.*;

@Entity
@Table(name = "m_phases")
public class M_Phases {
    @Id
    private String phaseid;
    private String phasedescription;
    private String phaseno;

    @ManyToOne
    @JoinColumn(name = "programcode")
    public M_Programs programcode;

    public M_Phases() {
    }

    public M_Phases(String phaseid, String phasedescription, String phaseno, M_Programs programcode) {
        this.phaseid = phaseid;
        this.phasedescription = phasedescription;
        this.phaseno = phaseno;
        this.programcode = programcode;
    }

    public String getPhaseid() {
        return phaseid;
    }

    public void setPhaseid(String phaseid) {
        this.phaseid = phaseid;
    }

    public String getPhasedescription() {
        return phasedescription;
    }

    public void setPhasedescription(String phasedescription) {
        this.phasedescription = phasedescription;
    }

    public String getPhaseno() {
        return phaseno;
    }

    public void setPhaseno(String phaseno) {
        this.phaseno = phaseno;
    }

    public M_Programs getProgramcode() {
        return programcode;
    }

    public void setProgramcode(M_Programs programcode) {
        this.programcode = programcode;
    }

    @Override
    public String toString() {
        return "M_Phases{" + "phaseid=" + phaseid + ", phasedescription=" + phasedescription + ", phaseno=" + phaseno + ", programcode=" + programcode + '}';
    }
}
