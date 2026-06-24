package com.nic.nerie.mt_programdetails.model;

import com.nic.nerie.m_phases.model.M_Phases;
import com.nic.nerie.m_programs.model.M_Programs;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "mt_programdetails")
public class MT_ProgramDetails {
    @Id
    private String programdetailid;

    @Temporal(TemporalType.DATE)
    Date startdate;

    @Temporal(TemporalType.DATE)
    Date enddate;

    @Temporal(TemporalType.DATE)
    Date lastdate;

    @Temporal(TemporalType.DATE)
    Date courseclosedate;

    @Temporal(TemporalType.TIMESTAMP)
    Date approvaldate;

    @ManyToOne
    @JoinColumn(name = "phaseid")
    public M_Phases phaseid;

    @ManyToOne
    @JoinColumn(name = "programcode")
    public M_Programs programcode;

    private byte[] approvalletter;

    @ManyToOne
    @JoinColumn(name = "approvedusercode")
    public MT_Userlogin mtuserloginapproval;

    private String finalized;
    private String closingreport;
    private String closed;
    private String ttfinalized;
    private String rejectionremarks;
    private String programtype;

    @Temporal(TemporalType.TIMESTAMP)
    Date entrydate;

    @ManyToOne
    @JoinColumn(name = "rejectedusercode")
    public MT_Userlogin mtuserloginrejection;

    private byte[] rejectionletter;

    @Temporal(TemporalType.TIMESTAMP)
    Date rejectiondate;

    public MT_ProgramDetails() {
    }

    public MT_ProgramDetails(String programdetailid, Date startdate, Date enddate, Date lastdate, Date courseclosedate, Date approvaldate, M_Phases phaseid, M_Programs programcode, byte[] approvalletter, MT_Userlogin mtuserloginapproval, String finalized, String closingreport, String closed, String ttfinalized, String rejectionremarks, String programtype, Date entrydate, MT_Userlogin mtuserloginrejection, byte[] rejectionletter, Date rejectiondate) {
        this.programdetailid = programdetailid;
        this.startdate = startdate;
        this.enddate = enddate;
        this.lastdate = lastdate;
        this.courseclosedate = courseclosedate;
        this.approvaldate = approvaldate;
        this.phaseid = phaseid;
        this.programcode = programcode;
        this.approvalletter = approvalletter;
        this.mtuserloginapproval = mtuserloginapproval;
        this.finalized = finalized;
        this.closingreport = closingreport;
        this.closed = closed;
        this.ttfinalized = ttfinalized;
        this.rejectionremarks = rejectionremarks;
        this.programtype = programtype;
        this.entrydate = entrydate;
        this.mtuserloginrejection = mtuserloginrejection;
        this.rejectionletter = rejectionletter;
        this.rejectiondate = rejectiondate;
    }

    public String getProgramdetailid() {
        return programdetailid;
    }

    public void setProgramdetailid(String programdetailid) {
        this.programdetailid = programdetailid;
    }

    public Date getStartdate() {
        return startdate;
    }

    public void setStartdate(Date startdate) {
        this.startdate = startdate;
    }

    public Date getEnddate() {
        return enddate;
    }

    public void setEnddate(Date enddate) {
        this.enddate = enddate;
    }

    public Date getLastdate() {
        return lastdate;
    }

    public void setLastdate(Date lastdate) {
        this.lastdate = lastdate;
    }

    public Date getCourseclosedate() {
        return courseclosedate;
    }

    public void setCourseclosedate(Date courseclosedate) {
        this.courseclosedate = courseclosedate;
    }

    public Date getApprovaldate() {
        return approvaldate;
    }

    public void setApprovaldate(Date approvaldate) {
        this.approvaldate = approvaldate;
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

    public byte[] getApprovalletter() {
        return approvalletter;
    }

    public void setApprovalletter(byte[] approvalletter) {
        this.approvalletter = approvalletter;
    }

    public MT_Userlogin getMtuserloginapproval() {
        return mtuserloginapproval;
    }

    public void setMtuserloginapproval(MT_Userlogin mtuserloginapproval) {
        this.mtuserloginapproval = mtuserloginapproval;
    }

    public String getFinalized() {
        return finalized;
    }

    public void setFinalized(String finalized) {
        this.finalized = finalized;
    }

    public String getClosingreport() {
        return closingreport;
    }

    public void setClosingreport(String closingreport) {
        this.closingreport = closingreport;
    }

    public String getClosed() {
        return closed;
    }

    public void setClosed(String closed) {
        this.closed = closed;
    }

    public String getTtfinalized() {
        return ttfinalized;
    }

    public void setTtfinalized(String ttfinalized) {
        this.ttfinalized = ttfinalized;
    }

    public String getRejectionremarks() {
        return rejectionremarks;
    }

    public void setRejectionremarks(String rejectionremarks) {
        this.rejectionremarks = rejectionremarks;
    }

    public String getProgramtype() {
        return programtype;
    }

    public void setProgramtype(String programtype) {
        this.programtype = programtype;
    }

    public Date getEntrydate() {
        return entrydate;
    }

    public void setEntrydate(Date entrydate) {
        this.entrydate = entrydate;
    }

    public MT_Userlogin getMtuserloginrejection() {
        return mtuserloginrejection;
    }

    public void setMtuserloginrejection(MT_Userlogin mtuserloginrejection) {
        this.mtuserloginrejection = mtuserloginrejection;
    }

    public byte[] getRejectionletter() {
        return rejectionletter;
    }

    public void setRejectionletter(byte[] rejectionletter) {
        this.rejectionletter = rejectionletter;
    }

    public Date getRejectiondate() {
        return rejectiondate;
    }

    public void setRejectiondate(Date rejectiondate) {
        this.rejectiondate = rejectiondate;
    }

    @Override
    public String toString() {
        return "MT_ProgramDetails{" + "programdetailid=" + programdetailid + ", startdate=" + startdate + ", enddate=" + enddate + ", lastdate=" + lastdate + ", courseclosedate=" + courseclosedate + ", approvaldate=" + approvaldate + ", phaseid=" + phaseid + ", programcode=" + programcode + ", approvalletter=" + approvalletter + ", mtuserloginapproval=" + mtuserloginapproval + ", finalized=" + finalized + ", closingreport=" + closingreport + ", closed=" + closed + ", ttfinalized=" + ttfinalized + ", rejectionremarks=" + rejectionremarks + ", programtype=" + programtype + ", entrydate=" + entrydate + ", mtuserloginrejection=" + mtuserloginrejection + ", rejectionletter=" + rejectionletter + ", rejectiondate=" + rejectiondate + '}';
    }
}
