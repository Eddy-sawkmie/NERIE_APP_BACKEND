package com.nic.nerie.m_phasemoredetails.model;

import com.nic.nerie.m_phases.model.M_Phases;
import com.nic.nerie.m_programs.model.M_Programs;
import jakarta.persistence.*;

@Entity
@Table(name = "m_phasemoredetails")
public class M_PhaseMoreDetails {

    @Id
    private String phasedetailsid;

    @ManyToOne
    @JoinColumn(name = "phaseid")
    public M_Phases phaseid;

    @ManyToOne
    @JoinColumn(name = "programcode")
    public M_Programs programcode;

    private String focusareas;
    private String budgetproposed;
    private String targetgroup;
    private String stage;
    private String objectives;
    private String methodology;
    private String tools;
    private String kpindicators;
    private String outcomes;

    public String getPhasedetailsid() {
        return phasedetailsid;
    }

    public void setPhasedetailsid(String phasedetailsid) {
        this.phasedetailsid = phasedetailsid;
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

    public String getFocusareas() {
        return focusareas;
    }

    public void setFocusareas(String focusareas) {
        this.focusareas = focusareas;
    }

    public String getBudgetproposed() {
        return budgetproposed;
    }

    public void setBudgetproposed(String budgetproposed) {
        this.budgetproposed = budgetproposed;
    }

    public String getTargetgroup() {
        return targetgroup;
    }

    public void setTargetgroup(String targetgroup) {
        this.targetgroup = targetgroup;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getObjectives() {
        return objectives;
    }

    public void setObjectives(String objectives) {
        this.objectives = objectives;
    }

    public String getMethodology() {
        return methodology;
    }

    public void setMethodology(String methodology) {
        this.methodology = methodology;
    }

    public String getTools() {
        return tools;
    }

    public void setTools(String tools) {
        this.tools = tools;
    }

    public String getKpindicators() {
        return kpindicators;
    }

    public void setKpindicators(String kpindicators) {
        this.kpindicators = kpindicators;
    }


    public String getOutcomes() {
        return outcomes;
    }

    public void setOutcomes(String outcomes) {
        this.outcomes = outcomes;
    }

    @Override
    public String toString() {
        return "M_PhaseMoreDetails{" + "phasedetailsid=" + phasedetailsid + ", phaseid=" + phaseid + ", programcode=" + programcode + ", focusareas=" + focusareas + ", budgetproposed=" + budgetproposed + ", targetgroup=" + targetgroup + ", stage=" + stage + ", objectives=" + objectives + ", methodology=" + methodology + ", tools=" + tools + ", kpindicators="+kpindicators+ ", outcomes=" + outcomes + '}';
    }
}