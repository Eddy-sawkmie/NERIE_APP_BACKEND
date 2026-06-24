package com.nic.nerie.t_preposttestquestions.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "t_preposttestquestions")
public class T_PrePostTestQuestions {
    @Id
    private String questionid;

    private String questionname;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String option5;
    private String option6;

//    private String correctoption; //store the actual answer here

    private String questiontype;  //GENERIC/COMMON, SCIENCE...

    private String qopcode;

    private String op1correct;
    private String op2correct;
    private String op3correct;
    private String op4correct;
    private String op5correct;
    private String op6correct;

    @Transient
    private Set<String> correctoptions;

    public Set<String> getCorrectoptions() {
        Set<String> correctops = new HashSet<String>();
        if(qopcode.equals("1")){
            if(op1correct.equals("1")){
                correctops.add("1");
            }
            else if(op2correct.equals("1")){
                correctops.add("2");
            }
            else if(op3correct.equals("1")){
                correctops.add("3");
            }
            else if(op4correct.equals("1")){
                correctops.add("4");
            }
            else if(op5correct.equals("1")){
                correctops.add("5");
            }
            else if(op6correct.equals("1")){
                correctops.add("6");
            }
        }
        else{
            if(op1correct.equals("1")){
                correctops.add("1");
            }
            if(op2correct.equals("1")){
                correctops.add("2");
            }
            if(op3correct.equals("1")){
                correctops.add("3");
            }
            if(op4correct.equals("1")){
                correctops.add("4");
            }
            if(op5correct.equals("1")){
                correctops.add("5");
            }
            if(op6correct.equals("1")){
                correctops.add("6");
            }
        }

        return correctops;

    }



//    @Transient
//    private int totalmarks;

    public T_PrePostTestQuestions() {
    }

    public String getQuestionid() {
        return questionid;
    }

    public void setQuestionid(String questionid) {
        this.questionid = questionid;
    }

    public String getQuestionname() {
        return questionname;
    }

    public void setQuestionname(String questionname) {
        this.questionname = questionname;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public String getOption3() {
        return option3;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

//    public String getCorrectoption() {
//        return correctoption;
//    }
//
//    public void setCorrectoption(String correctoption) {
//        this.correctoption = correctoption;
//    }

    public String getQuestiontype() {
        return questiontype;
    }

    public void setQuestiontype(String questiontype) {
        this.questiontype = questiontype;
    }



//    @Override
//    public String toString() {
//        return "T_PrePostTestQuestions{" + "questionid=" + questionid + ", questionname=" + questionname + ", option1=" + option1 + ", option2=" + option2 + ", option3=" + option3 + ", correctoption=" + correctoption + ", questiontype=" + questiontype + '}';
//    }

    public String getOption4() {
        return option4;
    }

    public void setOption4(String option4) {
        this.option4 = option4;
    }

    public String getOption5() {
        return option5;
    }

    public void setOption5(String option5) {
        this.option5 = option5;
    }

    public String getOption6() {
        return option6;
    }

    public void setOption6(String option6) {
        this.option6 = option6;
    }

    public String getQopcode() {
        return qopcode;
    }

    public void setQopcode(String qopcode) {
        this.qopcode = qopcode;
    }

    public String getOp1correct() {
        return op1correct;
    }

    public void setOp1correct(String op1correct) {
        this.op1correct = op1correct;
    }

    public String getOp2correct() {
        return op2correct;
    }

    public void setOp2correct(String op2correct) {
        this.op2correct = op2correct;
    }

    public String getOp3correct() {
        return op3correct;
    }

    public void setOp3correct(String op3correct) {
        this.op3correct = op3correct;
    }

    public String getOp4correct() {
        return op4correct;
    }

    public void setOp4correct(String op4correct) {
        this.op4correct = op4correct;
    }

    public String getOp5correct() {
        return op5correct;
    }

    public void setOp5correct(String op5correct) {
        this.op5correct = op5correct;
    }

    public String getOp6correct() {
        return op6correct;
    }

    public void setOp6correct(String op6correct) {
        this.op6correct = op6correct;
    }

    @Override
    public String toString() {
        return "T_PrePostTestQuestions{" + "questionid=" + questionid + ", questionname=" + questionname + ", option1=" + option1 + ", option2=" + option2 + ", option3=" + option3 + ", option4=" + option4 + ", option5=" + option5 + ", option6=" + option6 + ", questiontype=" + questiontype + ", qopcode=" + qopcode + ", op1correct=" + op1correct + ", op2correct=" + op2correct + ", op3correct=" + op3correct + ", op4correct=" + op4correct + ", op5correct=" + op5correct + ", op6correct=" + op6correct + '}';
    }
}
