package com.nic.nerie.mt_program_members.model;

import com.nic.nerie.m_phases.model.M_Phases;
import com.nic.nerie.m_programs.model.M_Programs;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import jakarta.persistence.*;

@Entity
@Table(name = "mt_program_members")

public class MT_ProgramMembers {

    @Id
    private Integer program_memberid;
    @ManyToOne
    @JoinColumn(name = "programcode")
    public M_Programs programcode;
    @ManyToOne
    @JoinColumn(name = "phaseid")
    public M_Phases phaseid;
    @ManyToOne
    @JoinColumn(name = "usercode")
    public MT_Userlogin mtuserlogin;

    private String isheadcoordinator;
    private String isdelegated;

    @ManyToOne
    @JoinColumn(name = "delegatedby")
    public MT_Userlogin delegatedby;

    public MT_ProgramMembers() {
    }

    public MT_ProgramMembers(Integer program_memberid, M_Programs programcode, M_Phases phaseid, MT_Userlogin mtuserlogin,String isheadcoordinator,String isdelegated,MT_Userlogin delegatedby) {
        this.program_memberid = program_memberid;
        this.programcode = programcode;
        this.phaseid = phaseid;
        this.mtuserlogin = mtuserlogin;
        this.isheadcoordinator = isheadcoordinator;
        this.isdelegated = isdelegated;
        this.delegatedby = delegatedby;
    }

    public String getIsheadcoordinator() {
        return isheadcoordinator;
    }

    public void setIsheadcoordinator(String isheadcoordinator) {
        this.isheadcoordinator = isheadcoordinator;
    }

    public String getIsdelegated() {
        return isdelegated;
    }

    public void setIsdelegated(String isdelegated) {
        this.isdelegated = isdelegated;
    }



    public MT_Userlogin getDelegatedby() {
        return delegatedby;
    }

    public void setDelegatedby(MT_Userlogin delegatedby) {
        this.delegatedby = delegatedby;
    }



    public Integer getProgram_memberid() {
        return program_memberid;
    }

    public void setProgram_memberid(Integer program_memberid) {
        this.program_memberid = program_memberid;
    }

    public M_Programs getProgramcode() {
        return programcode;
    }

    public void setProgramcode(M_Programs programcode) {
        this.programcode = programcode;
    }

    public M_Phases getPhaseid() {
        return phaseid;
    }

    public void setPhaseid(M_Phases phaseid) {
        this.phaseid = phaseid;
    }

    public MT_Userlogin getMtuserlogin() {
        return mtuserlogin;
    }

    public void setMtuserlogin(MT_Userlogin mtuserlogin) {
        this.mtuserlogin = mtuserlogin;
    }

    @Override
    public String toString() {
        return "MT_ProgramMembers{" + "program_memberid=" + program_memberid + ", programcode=" + programcode + ", phaseid=" + phaseid + ", mtuserlogin=" + mtuserlogin + ", isheadcoordinator=" + isheadcoordinator + ", isdelegated=" + isdelegated + ", delegatedby=" + delegatedby + '}';
    }

}
