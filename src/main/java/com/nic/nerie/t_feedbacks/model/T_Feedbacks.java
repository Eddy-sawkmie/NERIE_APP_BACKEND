package com.nic.nerie.t_feedbacks.model;

import com.nic.nerie.m_phases.model.M_Phases;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "t_feedbacks")
public class T_Feedbacks implements Serializable {

    @Id
    private String feedbackslno;
    private String feedback;
    @Temporal(TemporalType.TIMESTAMP)
    Date entrydate;
    @ManyToOne
    @JoinColumn(name = "phaseid")
    public M_Phases phaseid;
    @ManyToOne
    @JoinColumn(name = "usercode")
    public MT_Userlogin mtuserlogin;

    public T_Feedbacks() {
    }

    public T_Feedbacks(String feedbackslno, String feedback, Date entrydate, M_Phases phaseid, MT_Userlogin mtuserlogin) {
        this.feedbackslno = feedbackslno;
        this.feedback = feedback;
        this.entrydate = entrydate;
        this.phaseid = phaseid;
        this.mtuserlogin = mtuserlogin;
    }


    public String getFeedbackslno() {
        return feedbackslno;
    }

    public void setFeedbackslno(String feedbackslno) {
        this.feedbackslno = feedbackslno;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
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

    @Override
    public String toString() {
        return "T_Feedbacks{" + "feedbackslno=" + feedbackslno + ", feedback=" + feedback + ", entrydate=" + entrydate + ", phaseid=" + phaseid + ", mtuserlogin=" + mtuserlogin + '}';
    }
}
