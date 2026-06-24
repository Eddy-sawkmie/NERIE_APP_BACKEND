package com.nic.nerie.t_participants.model;

import com.nic.nerie.m_categories.model.M_Categories;
import com.nic.nerie.m_designations.model.M_Designations;
import com.nic.nerie.m_districts.model.M_Districts;
import com.nic.nerie.m_minorities.model.M_Minorities;
import com.nic.nerie.m_participantofficetypes.model.M_ParticipantOfficeTypes;
import com.nic.nerie.m_qualifications.model.M_Qualifications;
import com.nic.nerie.m_qualificationsubjects.model.M_QualificationSubjects;
import com.nic.nerie.m_states.model.M_States;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "t_participants")
public class T_Participants implements Serializable {

    @Id
    private String usercode;

    @Column(name = "participantexperienceyears", columnDefinition = "int2")
    private Integer participantexperienceyears;

    @Column(name = "participantexperiencemonths", columnDefinition = "int2")
    private Integer participantexperiencemonths;

    private String gender;
    private String participantofficeaddress;
    private String officephoneno;
    private String addressline1;
    private String addressline2;
    private String pincode;
    private String landlineno;
    private String locality;
    private String isdifferentlyabled;
    private String isminority;
    private String otherparticipantofficetype;
    //    other minorityname
    private String others;
    private String participantteachingsubjects;
    //    m_qualifications
    @ManyToOne
    @JoinColumn(name = "participantqualificationcode")
    public M_Qualifications mqualifications;
    //            mt_qualificationssubjectsmap
    @ManyToOne
    @JoinColumn(name = "qualificationsubjectcode")
    public M_QualificationSubjects mqualificationsubjects;
    //    m_designations isparticipantdesignation=Y
    @ManyToOne
    @JoinColumn(name = "participantdesignationcode")
    public M_Designations mdesignations;
    //    m_states
    @ManyToOne
    @JoinColumn(name = "participantofficestatecode")
    public M_States mstatesparticipant;
    //    m_participantofficetypes
    @ManyToOne
    @JoinColumn(name = "participantofficetypecode")
    public M_ParticipantOfficeTypes mparticipantofficetypes;
    //    m_states
    @ManyToOne
    @JoinColumn(name = "statecode")
    public M_States mstates;
    //    m_districts
    @ManyToOne
    @JoinColumn(name = "districtcode")
    public M_Districts mdistricts;
    //    m_minorities
    @ManyToOne
    @JoinColumn(name = "minoritycode")
    public M_Minorities mminorities;
    //    m_categories
    @ManyToOne
    @JoinColumn(name = "categorycode")
    public M_Categories mcategories;

    //    mt_userlogin
    @ManyToOne
    @JoinColumn(name = "usercodewhoregistered")
    public MT_Userlogin mtuserlogin;

    private String differentlyableddetails;

    public T_Participants() {
    }

    public T_Participants(String usercode, Integer participantexperienceyears, Integer participantexperiencemonths, String gender, String participantofficeaddress, String officephoneno, String addressline1, String addressline2, String pincode, String landlineno, String locality, String isdifferentlyabled, String isminority, String others, String participantteachingsubjects, M_Qualifications mqualifications, M_QualificationSubjects mqualificationsubjects, M_Designations mdesignations, M_States mstatesparticipant, M_ParticipantOfficeTypes mparticipantofficetypes, M_States mstates, M_Districts mdistricts, M_Minorities mminorities, M_Categories mcategories, MT_Userlogin mtuserlogin, String otherparticipantofficetype) {
        this.usercode = usercode;
        this.participantexperienceyears = participantexperienceyears;
        this.participantexperiencemonths = participantexperiencemonths;
        this.gender = gender;
        this.participantofficeaddress = participantofficeaddress;
        this.officephoneno = officephoneno;
        this.addressline1 = addressline1;
        this.addressline2 = addressline2;
        this.pincode = pincode;
        this.landlineno = landlineno;
        this.locality = locality;
        this.isdifferentlyabled = isdifferentlyabled;
        this.isminority = isminority;
        this.others = others;
        this.participantteachingsubjects = participantteachingsubjects;
        this.mqualifications = mqualifications;
        this.mqualificationsubjects = mqualificationsubjects;
        this.mdesignations = mdesignations;
        this.mstatesparticipant = mstatesparticipant;
        this.mparticipantofficetypes = mparticipantofficetypes;
        this.mstates = mstates;
        this.mdistricts = mdistricts;
        this.mminorities = mminorities;
        this.mcategories = mcategories;
        this.mtuserlogin = mtuserlogin;
        this.otherparticipantofficetype = otherparticipantofficetype;
    }

    public Integer getParticipantexperienceyears() {
        return participantexperienceyears;
    }

    public void setParticipantexperienceyears(Integer participantexperienceyears) {
        this.participantexperienceyears = participantexperienceyears;
    }

    public Integer getParticipantexperiencemonths() {
        return participantexperiencemonths;
    }

    public void setParticipantexperiencemonths(Integer participantexperiencemonths) {
        this.participantexperiencemonths = participantexperiencemonths;
    }

    public String getUsercode() {
        return usercode;
    }

    public void setUsercode(String usercode) {
        this.usercode = usercode;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getParticipantofficeaddress() {
        return participantofficeaddress;
    }

    public void setParticipantofficeaddress(String participantofficeaddress) {
        this.participantofficeaddress = participantofficeaddress;
    }

    public String getOfficephoneno() {
        return officephoneno;
    }

    public void setOfficephoneno(String officephoneno) {
        this.officephoneno = officephoneno;
    }

    public String getAddressline1() {
        return addressline1;
    }

    public void setAddressline1(String addressline1) {
        this.addressline1 = addressline1;
    }

    public String getAddressline2() {
        return addressline2;
    }

    public void setAddressline2(String addressline2) {
        this.addressline2 = addressline2;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getLandlineno() {
        return landlineno;
    }

    public void setLandlineno(String landlineno) {
        this.landlineno = landlineno;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getIsdifferentlyabled() {
        return isdifferentlyabled;
    }

    public void setIsdifferentlyabled(String isdifferentlyabled) {
        this.isdifferentlyabled = isdifferentlyabled;
    }

    public String getIsminority() {
        return isminority;
    }

    public void setIsminority(String isminority) {
        this.isminority = isminority;
    }

    public String getOthers() {
        return others;
    }

    public void setOthers(String others) {
        this.others = others;
    }

    public String getParticipantteachingsubjects() {
        return participantteachingsubjects;
    }

    public void setParticipantteachingsubjects(String participantteachingsubjects) {
        this.participantteachingsubjects = participantteachingsubjects;
    }

    public M_Qualifications getMqualifications() {
        return mqualifications;
    }

    public void setMqualifications(M_Qualifications mqualifications) {
        this.mqualifications = mqualifications;
    }

    public M_QualificationSubjects getMqualificationsubjects() {
        return mqualificationsubjects;
    }

    public void setMqualificationsubjects(M_QualificationSubjects mqualificationsubjects) {
        this.mqualificationsubjects = mqualificationsubjects;
    }

    public M_Designations getMdesignations() {
        return mdesignations;
    }

    public void setMdesignations(M_Designations mdesignations) {
        this.mdesignations = mdesignations;
    }

    public M_States getMstatesparticipant() {
        return mstatesparticipant;
    }

    public void setMstatesparticipant(M_States mstatesparticipant) {
        this.mstatesparticipant = mstatesparticipant;
    }

    public M_ParticipantOfficeTypes getMparticipantofficetypes() {
        return mparticipantofficetypes;
    }

    public void setMparticipantofficetypes(M_ParticipantOfficeTypes mparticipantofficetypes) {
        this.mparticipantofficetypes = mparticipantofficetypes;
    }

    public M_States getMstates() {
        return mstates;
    }

    public void setMstates(M_States mstates) {
        this.mstates = mstates;
    }

    public M_Districts getMdistricts() {
        return mdistricts;
    }

    public void setMdistricts(M_Districts mdistricts) {
        this.mdistricts = mdistricts;
    }

    public M_Minorities getMminorities() {
        return mminorities;
    }

    public void setMminorities(M_Minorities mminorities) {
        this.mminorities = mminorities;
    }

    public M_Categories getMcategories() {
        return mcategories;
    }

    public void setMcategories(M_Categories mcategories) {
        this.mcategories = mcategories;
    }

    public MT_Userlogin getMtuserlogin() {
        return mtuserlogin;
    }

    public void setMtuserlogin(MT_Userlogin mtuserlogin) {
        this.mtuserlogin = mtuserlogin;
    }

    public String getOtherparticipantofficetype() {
        return otherparticipantofficetype;
    }

    public void setOtherparticipantofficetype(String otherparticipantofficetype) {
        this.otherparticipantofficetype = otherparticipantofficetype;
    }

    public String getDifferentlyableddetails() {
        return differentlyableddetails;
    }

    public void setDifferentlyableddetails(String differentlyableddetails) {
        this.differentlyableddetails = differentlyableddetails;
    }

}