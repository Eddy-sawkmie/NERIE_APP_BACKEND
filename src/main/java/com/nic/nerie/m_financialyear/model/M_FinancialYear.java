package com.nic.nerie.m_financialyear.model;

import jakarta.persistence.*;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "m_financialyear")
public class M_FinancialYear {
    @Id
    private String fyid;

    @Column(name = "fyname", columnDefinition = "character varying")
    private String fyname;

    @Temporal(TemporalType.DATE)
    @Column(name = "fystart", columnDefinition = "date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fystart;

    @Temporal(TemporalType.DATE)
    @Column(name = "fyend", columnDefinition = "date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fyend;

    @Column(name = "fyvalue", columnDefinition = "character varying", length = 16)
    private String fyvalue;

    public M_FinancialYear() {
    }

    public M_FinancialYear(String fyid, String fyname, Date fystart, Date fyend, String fyvalue) {
        this.fyid = fyid;
        this.fyname = fyname;
        this.fystart = fystart;
        this.fyend = fyend;
        this.fyvalue = fyvalue;
    }

    public String getFyid() {
        return fyid;
    }

    public void setFyid(String fyid) {
        this.fyid = fyid;
    }

    public String getFyname() {
        return fyname;
    }

    public void setFyname(String fyname) {
        this.fyname = fyname;
    }

    public Date getFystart() {
        return fystart;
    }

    public void setFystart(Date fystart) {
        this.fystart = fystart;
    }

    public Date getFyend() {
        return fyend;
    }

    public void setFyend(Date fyend) {
        this.fyend = fyend;
    }

    public String getFyvalue() {
        return fyvalue;
    }

    public void setFyvalue(String fyvalue) {
        this.fyvalue = fyvalue;
    }

    @Override
    public String toString() {
        return "M_FinancialYear{" +
                "fyid='" + fyid + '\'' +
                ", fyname='" + fyname + '\'' +
                ", fystart=" + fystart +
                ", fyend=" + fyend +
                ", fyvalue='" + fyvalue + '\'' +
                '}';
    }
}
