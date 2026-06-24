package com.nic.nerie.t_applications.model;

import com.nic.nerie.m_phases.model.M_Phases;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "t_applications")
public class T_Applications implements Serializable {

    @Id
    private String applicationcode;
    private String emailsent;
    @ManyToOne
    @JoinColumn(name = "usercode")
    public MT_Userlogin mtuserlogin;
    public String status;
    public String remarks;
    @ManyToOne
    @JoinColumn(name = "usercodewhoapplied")
    public MT_Userlogin mtuserloginapplied;
    @ManyToOne
    @JoinColumn(name = "phaseid")
    public M_Phases phaseid;

    private String name;
    private String designation;
    private String educationalqualification;
    private String experience;
    private String gender;
    private String addressoffice;
    private String addressresidence;
    private String contactno;
    private String emailid;
    private String localityregion;
    private String category;
    private String religiousminority;
    private String religiousminorityname;

    public T_Applications() {
    }

    public T_Applications(String applicationcode, String emailsent, MT_Userlogin mtuserlogin, String status, String remarks, MT_Userlogin mtuserloginapplied, M_Phases phaseid) {
        this.applicationcode = applicationcode;
        this.emailsent = emailsent;
        this.mtuserlogin = mtuserlogin;
        this.status = status;
        this.remarks = remarks;
        this.mtuserloginapplied = mtuserloginapplied;
        this.phaseid = phaseid;
    }

    public String getApplicationcode() {
        return applicationcode;
    }

    public void setApplicationcode(String applicationcode) {
        this.applicationcode = applicationcode;
    }

    public String getEmailsent() {
        return emailsent;
    }

    public void setEmailsent(String emailsent) {
        this.emailsent = emailsent;
    }

    public MT_Userlogin getMtuserlogin() {
        return mtuserlogin;
    }

    public void setMtuserlogin(MT_Userlogin mtuserlogin) {
        this.mtuserlogin = mtuserlogin;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public MT_Userlogin getMtuserloginapplied() {
        return mtuserloginapplied;
    }

    public void setMtuserloginapplied(MT_Userlogin mtuserloginapplied) {
        this.mtuserloginapplied = mtuserloginapplied;
    }

    public M_Phases getPhaseid() {
        return phaseid;
    }

    public void setPhaseid(M_Phases phaseid) {
        this.phaseid = phaseid;
    }

    //-----------------------------------
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getEducationalqualification() {
        return educationalqualification;
    }

    public void setEducationalqualification(String educationalqualification) {
        this.educationalqualification = educationalqualification;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddressoffice() {
        return addressoffice;
    }

    public void setAddressoffice(String addressoffice) {
        this.addressoffice = addressoffice;
    }

    public String getAddressresidence() {
        return addressresidence;
    }

    public void setAddressresidence(String addressresidence) {
        this.addressresidence = addressresidence;
    }

    public String getContactno() {
        return contactno;
    }

    public void setContactno(String contactno) {
        this.contactno = contactno;
    }

    public String getEmailid() {
        return emailid;
    }

    public void setEmailid(String emailid) {
        this.emailid = emailid;
    }

    public String getLocalityregion() {
        return localityregion;
    }

    public void setLocalityregion(String localityregion) {
        this.localityregion = localityregion;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getReligiousminority() {
        return religiousminority;
    }

    public void setReligiousminority(String religiousminority) {
        this.religiousminority = religiousminority;
    }

    public String getReligiousminorityname() {
        return religiousminorityname;
    }

    public void setReligiousminorityname(String religiousminorityname) {
        this.religiousminorityname = religiousminorityname;
    }

    @Override
    public String toString() {
        return "T_Applications{" + "applicationcode=" + applicationcode + ", emailsent=" + emailsent + ", mtuserlogin=" + mtuserlogin + ", status=" + status + ", remarks=" + remarks + ", mtuserloginapplied=" + mtuserloginapplied + ", phaseid=" + phaseid + '}';
    }
}
