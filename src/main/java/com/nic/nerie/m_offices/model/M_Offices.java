/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nic.nerie.m_offices.model;

import com.nic.nerie.m_districts.model.M_Districts;
import com.nic.nerie.m_states.model.M_States;
import jakarta.persistence.*;

@Entity
@Table(name = "m_offices")
public class M_Offices {

    @Id
    private String officecode;
    private String officename;
    private String officeshortname;
    private String officeaddress;
    private String officepincode;
    private String contactpersonname;
    private String mobileno;
    private String landlineno;
    private String emailid;
    private String shorttermcoursecode;
    private String longtermcoursecode;

    @ManyToOne
    @JoinColumn(name = "statecode")
    public M_States mstates;

    @ManyToOne
    @JoinColumn(name = "officedistrictcode")
    public M_Districts mdistricts;

    public M_Offices() {
    }

    

    public M_Offices(String officecode, String officename, String officeshortname, String officeaddress,
            String officepincode, String contactpersonname, String mobileno, String landlineno, String emailid,
            String shorttermcoursecode, String longtermcoursecode, M_States mstates, M_Districts mdistricts) {
        this.officecode = officecode;
        this.officename = officename;
        this.officeshortname = officeshortname;
        this.officeaddress = officeaddress;
        this.officepincode = officepincode;
        this.contactpersonname = contactpersonname;
        this.mobileno = mobileno;
        this.landlineno = landlineno;
        this.emailid = emailid;
        this.shorttermcoursecode = shorttermcoursecode;
        this.longtermcoursecode = longtermcoursecode;
        this.mstates = mstates;
        this.mdistricts = mdistricts;
    }



    public String getOfficecode() {
        return officecode;
    }

    public void setOfficecode(String officecode) {
        this.officecode = officecode;
    }

    public String getOfficename() {
        return officename;
    }

    public void setOfficename(String officename) {
        this.officename = officename;
    }

    public String getOfficeshortname() {
        return officeshortname;
    }

    public void setOfficeshortname(String officeshortname) {
        this.officeshortname = officeshortname;
    }

    public String getOfficeaddress() {
        return officeaddress;
    }

    public void setOfficeaddress(String officeaddress) {
        this.officeaddress = officeaddress;
    }

    public String getOfficepincode() {
        return officepincode;
    }

    public void setOfficepincode(String officepincode) {
        this.officepincode = officepincode;
    }

    public String getContactpersonname() {
        return contactpersonname;
    }

    public void setContactpersonname(String contactpersonname) {
        this.contactpersonname = contactpersonname;
    }

    public String getMobileno() {
        return mobileno;
    }

    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }

    public String getLandlineno() {
        return landlineno;
    }

    public void setLandlineno(String landlineno) {
        this.landlineno = landlineno;
    }

    public String getEmailid() {
        return emailid;
    }

    public void setEmailid(String emailid) {
        this.emailid = emailid;
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

    public String getShorttermcoursecode() {
        return shorttermcoursecode;
    }

    public void setShorttermcoursecode(String shorttermcoursecode) {
        this.shorttermcoursecode = shorttermcoursecode;
    }

    public String getLongtermcoursecode() {
        return longtermcoursecode;
    }

    public void setLongtermcoursecode(String longtermcoursecode) {
        this.longtermcoursecode = longtermcoursecode;
    }



    @Override
    public String toString() {
        return "M_Offices [officecode=" + officecode + ", officename=" + officename + ", officeshortname="
                + officeshortname + ", officeaddress=" + officeaddress + ", officepincode=" + officepincode
                + ", contactpersonname=" + contactpersonname + ", mobileno=" + mobileno + ", landlineno=" + landlineno
                + ", emailid=" + emailid + ", shorttermcoursecode=" + shorttermcoursecode + ", longtermcoursecode="
                + longtermcoursecode + "]";
    }

    

}
