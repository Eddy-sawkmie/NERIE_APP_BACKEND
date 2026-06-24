package com.nic.nerie.t_participantanswerkey_preposttest.model;

import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.t_phase_preposttest.model.T_Phase_PrePostTest;
import com.nic.nerie.t_preposttestquestions.model.T_PrePostTestQuestions;
import jakarta.persistence.*;

@Entity
@Table(name = "t_participantanswerkey_preposttest")
public class T_ParticipantAnswerKey_PrePostTest {
    @Id
    private String paid;

    @ManyToOne
    @JoinColumn(name = "participantusercode")
    public MT_Userlogin participantusercode;

    private String testtype; //PRE or POST

    @ManyToOne
    @JoinColumn(name = "testid")
    public T_Phase_PrePostTest testid;

    @ManyToOne
    @JoinColumn(name = "questionid")
    public T_PrePostTestQuestions questionid;

    private String selectedoption;

    public String getSelectedoption() {
        return selectedoption;
    }

    public void setSelectedoption(String selectedoption) {
        this.selectedoption = selectedoption;
    }



    public String getPaid() {
        return paid;
    }

    public void setPaid(String paid) {
        this.paid = paid;
    }

    public MT_Userlogin getParticipantusercode() {
        return participantusercode;
    }

    public void setParticipantusercode(MT_Userlogin participantusercode) {
        this.participantusercode = participantusercode;
    }

    public String getTesttype() {
        return testtype;
    }

    public void setTesttype(String testtype) {
        this.testtype = testtype;
    }

    public T_Phase_PrePostTest getTestid() {
        return testid;
    }

    public void setTestid(T_Phase_PrePostTest testid) {
        this.testid = testid;
    }

    public T_PrePostTestQuestions getQuestionid() {
        return questionid;
    }

    public void setQuestionid(T_PrePostTestQuestions questionid) {
        this.questionid = questionid;
    }

    @Override
    public String toString() {
        return "T_ParticipantAnswerKey_PrePostTest{" + "paid=" + paid + ", participantusercode=" + participantusercode + ", testtype=" + testtype + ", testid=" + testid + ", questionid=" + questionid + ", selectedoption=" + selectedoption + '}';
    }


    public T_ParticipantAnswerKey_PrePostTest() {
    }
}
