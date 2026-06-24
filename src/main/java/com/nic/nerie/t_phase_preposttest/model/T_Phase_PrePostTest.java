package com.nic.nerie.t_phase_preposttest.model;

import com.nic.nerie.m_phases.model.M_Phases;
import com.nic.nerie.t_preposttestquestions.model.T_PrePostTestQuestions;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "t_phase_preposttest")
public class T_Phase_PrePostTest {
    @Id
    private String testid;

    @ManyToOne
    @JoinColumn(name = "phaseid")
    public M_Phases phaseid;

    private String testurl;
    private String testtype;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "mt_phase_preposttest_questions_mapping", // Join table name
            joinColumns = @JoinColumn(name = "testid"), // Foreign key for T_Phase_PrePostTest
            inverseJoinColumns = @JoinColumn(name = "questionid") // Foreign key for T_PrePostTestQuestions
    )
    private Set<T_PrePostTestQuestions> questions;

    public String getTestid() {
        return testid;
    }

    public void setTestid(String testid) {
        this.testid = testid;
    }

    public M_Phases getPhaseid() {
        return phaseid;
    }

    public void setPhaseid(M_Phases phaseid) {
        this.phaseid = phaseid;
    }

    public void addQuestionToSet(T_PrePostTestQuestions q){
        questions.add(q);
    }

    public Set<T_PrePostTestQuestions> getQuestions() {
        return questions;
    }

    public void setQuestions(Set<T_PrePostTestQuestions> questions) {
        this.questions = questions;
    }

    public String getTesturl() {
        return testurl;
    }

    public void setTesturl(String testurl) {
        this.testurl = testurl;
    }

    public String getTesttype() {
        return testtype;
    }

    public void setTesttype(String testtype) {
        this.testtype = testtype;
    }



    @Override
    public String toString() {
        return "T_Phase_PrePostTest{" + "testid=" + testid + ", phaseid=" + phaseid + '}';
    }

    public T_Phase_PrePostTest() {
    }
}
