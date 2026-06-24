package com.nic.nerie.t_participantfeedbacks.model;

import com.nic.nerie.m_phases.model.M_Phases;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "t_participantfeedbacks")
public class T_ParticipantFeedbacks {
    @Id
    private String pfeedbackno;

    @Temporal(TemporalType.TIMESTAMP)
    Date entrydate;

    @ManyToOne
    @JoinColumn(name = "phaseid")
    public M_Phases phaseid;

    @ManyToOne
    @JoinColumn(name = "usercode")
    public MT_Userlogin mtuserlogin;

    private String q1;
    private String q2;
    private String q3;
    private String q4;
    private String q5;
    private String q6;
    private String q7;
    private String q8;
    private String q9;
    private String q10;
    private String q11;
    private String q12;

    public String getPfeedbackno() {
        return pfeedbackno;
    }

    public void setPfeedbackno(String pfeedbackno) {
        this.pfeedbackno = pfeedbackno;
    }

    public Date getEntrydate() {
        return entrydate;
    }

    public void setEntrydate(Date entrydate) {
        this.entrydate = entrydate;
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

    public String getQ1() {
        return q1;
    }

    public void setQ1(String q1) {
        this.q1 = q1;
    }

    public String getQ2() {
        return q2;
    }

    public void setQ2(String q2) {
        this.q2 = q2;
    }

    public String getQ3() {
        return q3;
    }

    public void setQ3(String q3) {
        this.q3 = q3;
    }

    public String getQ4() {
        return q4;
    }

    public void setQ4(String q4) {
        this.q4 = q4;
    }

    public String getQ5() {
        return q5;
    }

    public void setQ5(String q5) {
        this.q5 = q5;
    }

    public String getQ6() {
        return q6;
    }

    public void setQ6(String q6) {
        this.q6 = q6;
    }

    public String getQ7() {
        return q7;
    }

    public void setQ7(String q7) {
        this.q7 = q7;
    }

    public String getQ8() {
        return q8;
    }

    public void setQ8(String q8) {
        this.q8 = q8;
    }

    public String getQ9() {
        return q9;
    }

    public void setQ9(String q9) {
        this.q9 = q9;
    }

    public String getQ10() {
        return q10;
    }

    public void setQ10(String q10) {
        this.q10 = q10;
    }

    public String getQ11() {
        return q11;
    }

    public void setQ11(String q11) {
        this.q11 = q11;
    }

    public String getQ12() {
        return q12;
    }

    public void setQ12(String q12) {
        this.q12 = q12;
    }

    @Transient
    private String q1Text;

    @Transient
    private String q2Text;

    @Transient
    private String q3Text;

    @Transient
    private String q4Text;

    @Transient
    private String q5Text;

    @Transient
    private String q6Text;

    @Transient
    private String q7Text;

    // Mapping method for each question
    public String mapToText(String value) {
        switch (value) {
            case "1": return "Poor";
            case "2": return "Average";
            case "3": return "Good";
            case "4": return "Excellent";
            default: return "Unknown";
        }
    }

    // Getters for transient fields
    public String getQ1Text() {
        return mapToText(q1);
    }

    public String getQ2Text() {
        return mapToText(q2);
    }

    public String getQ3Text() {
        return mapToText(q3);
    }

    public String getQ4Text() {
        return mapToText(q4);
    }

    public String getQ5Text() {
        return mapToText(q5);
    }

    public String getQ6Text() {
        return mapToText(q6);
    }

    public String getQ7Text() {
        return mapToText(q7);
    }

    @Override
    public String toString() {
        return "T_ParticipantFeedbacks{" + "pfeedbackno=" + pfeedbackno + ", entrydate=" + entrydate + ", phaseid=" + phaseid + ", mtuserlogin=" + mtuserlogin + ", q1=" + q1 + ", q2=" + q2 + ", q3=" + q3 + ", q4=" + q4 + ", q5=" + q5 + ", q6=" + q6 + ", q7=" + q7 + ", q8=" + q8 + ", q9=" + q9 + ", q10=" + q10 + ", q11=" + q11 + ", q12=" + q12 + '}';
    }
}
