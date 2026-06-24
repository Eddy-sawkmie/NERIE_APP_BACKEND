package com.nic.nerie.audittrail.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects; // Import Objects

@Embeddable
public class Audittrail_Id implements Serializable {

    private String userid;
    private String actiontaken;
    private String pageurl;
    private String browser;
    private String os;
    private String ipaddress;
    @Temporal(TemporalType.TIMESTAMP)
    private Date entrydate;

    public Audittrail_Id() {
    }

    public Audittrail_Id(String userid, String actiontaken, String pageurl, String browser, String os, String ipaddress, Date entrydate) {
        this.userid = userid;
        this.actiontaken = actiontaken;
        this.pageurl = pageurl;
        this.browser = browser;
        this.os = os;
        this.ipaddress = ipaddress;
        this.entrydate = entrydate;
    }

    public String getUserid() { return userid; }
    public void setUserid(String userid) { this.userid = userid; }
    public String getActiontaken() { return actiontaken; }
    public void setActiontaken(String actiontaken) { this.actiontaken = actiontaken; }
    public String getPageurl() { return pageurl; }
    public void setPageurl(String pageurl) { this.pageurl = pageurl; }
    public String getBrowser() { return browser; }
    public void setBrowser(String browser) { this.browser = browser; }
    public String getOs() { return os; }
    public void setOs(String os) { this.os = os; }
    public String getIpaddress() { return ipaddress; }
    public void setIpaddress(String ipaddress) { this.ipaddress = ipaddress; }
    public Date getEntrydate() { return entrydate; }
    public void setEntrydate(Date entrydate) { this.entrydate = entrydate; }


    // ADDED equals() and hashCode()

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Audittrail_Id that = (Audittrail_Id) o;
        // Compare ALL fields that are part of the composite key
        return Objects.equals(userid, that.userid) &&
                Objects.equals(actiontaken, that.actiontaken) &&
                Objects.equals(pageurl, that.pageurl) &&
                Objects.equals(browser, that.browser) &&
                Objects.equals(os, that.os) &&
                Objects.equals(ipaddress, that.ipaddress) &&
                Objects.equals(entrydate, that.entrydate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userid, actiontaken, pageurl, browser, os, ipaddress, entrydate);
    }
}