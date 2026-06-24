package com.nic.nerie.t_programmaterials.model;

import com.nic.nerie.m_phases.model.M_Phases;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "t_programmaterials")
public class T_ProgramMaterials implements Serializable {

    @Id
    private String programmaterialid;
    private String materialdesc;
    private String reportormaterial;
    private byte[] materialfile;
    private String materialfiletype;
    @Temporal(TemporalType.TIMESTAMP)
    Date uploaddate;
    @ManyToOne
    @JoinColumn(name = "phaseid")
    public M_Phases phaseid;
    @ManyToOne
    @JoinColumn(name = "usercode")
    public MT_Userlogin mtuserlogin;

    public T_ProgramMaterials() {
    }

    public T_ProgramMaterials(String programmaterialid, String materialdesc, String reportormaterial, byte[] materialfile, String materialfiletype, Date uploaddate, M_Phases phaseid, MT_Userlogin mtuserlogin) {
        this.programmaterialid = programmaterialid;
        this.materialdesc = materialdesc;
        this.reportormaterial = reportormaterial;
        this.materialfile = materialfile;
        this.materialfiletype = materialfiletype;
        this.uploaddate = uploaddate;
        this.phaseid = phaseid;
        this.mtuserlogin = mtuserlogin;
    }

    public String getProgrammaterialid() {
        return programmaterialid;
    }

    public void setProgrammaterialid(String programmaterialid) {
        this.programmaterialid = programmaterialid;
    }

    public String getMaterialdesc() {
        return materialdesc;
    }

    public void setMaterialdesc(String materialdesc) {
        this.materialdesc = materialdesc;
    }

    public String getReportormaterial() {
        return reportormaterial;
    }

    public void setReportormaterial(String reportormaterial) {
        this.reportormaterial = reportormaterial;
    }

    public byte[] getMaterialfile() {
        return materialfile;
    }

    public void setMaterialfile(byte[] materialfile) {
        this.materialfile = materialfile;
    }

    public String getMaterialfiletype() {
        return materialfiletype;
    }

    public void setMaterialfiletype(String materialfiletype) {
        this.materialfiletype = materialfiletype;
    }

    public Date getUploaddate() {
        return uploaddate;
    }

    public void setUploaddate(Date uploaddate) {
        this.uploaddate = uploaddate;
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
        return "T_ProgramMaterials{" + "programmaterialid=" + programmaterialid + ", materialdesc=" + materialdesc + ", reportormaterial=" + reportormaterial + ", materialfile=" + materialfile + ", materialfiletype=" + materialfiletype + ", uploaddate=" + uploaddate + ", phaseid=" + phaseid + ", mtuserlogin=" + mtuserlogin + '}';
    }


}

