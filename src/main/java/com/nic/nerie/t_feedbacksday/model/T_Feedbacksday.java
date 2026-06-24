package com.nic.nerie.t_feedbacksday.model;

import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.t_programtimetable.model.T_ProgramTimeTable;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "t_feedbacksday")
public class T_Feedbacksday implements Serializable {
    @Id
    private String feedbackdayid;

    private String feedback;

    @Temporal(TemporalType.TIMESTAMP)
    Date entrydate;

    @ManyToOne
    @JoinColumn(name = "programtimetablecode")
    public T_ProgramTimeTable programtimetablecode;

    @ManyToOne
    @JoinColumn(name = "usercode")
    public MT_Userlogin usercode;

    public T_Feedbacksday() {
    }

    public T_Feedbacksday(String feedbackdayid, String feedback, Date entrydate, T_ProgramTimeTable programtimetablecode, MT_Userlogin usercode) {
        this.feedbackdayid = feedbackdayid;
        this.feedback = feedback;
        this.entrydate = entrydate;
        this.programtimetablecode = programtimetablecode;
        this.usercode = usercode;
    }

    public String getFeedbackdayid() {
        return feedbackdayid;
    }

    public void setFeedbackdayid(String feedbackdayid) {
        this.feedbackdayid = feedbackdayid;
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



    public MT_Userlogin getUsercode() {
        return usercode;
    }

    public void setUsercode(MT_Userlogin usercode) {
        this.usercode = usercode;
    }

    public T_ProgramTimeTable getProgramtimetablecode() {
        return programtimetablecode;
    }

    public void setProgramtimetablecode(T_ProgramTimeTable programtimetablecode) {
        this.programtimetablecode = programtimetablecode;
    }

    @Override
    public String toString() {
        return "T_Feedbacksday{" + "feedbackdayid=" + feedbackdayid + ", feedback=" + feedback + ", entrydate=" + entrydate + ", programtimetablecode=" + programtimetablecode + ", usercode=" + usercode + '}';
    }
}