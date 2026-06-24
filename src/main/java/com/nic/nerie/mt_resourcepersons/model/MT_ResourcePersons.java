package com.nic.nerie.mt_resourcepersons.model;


import com.nic.nerie.m_designations.model.M_Designations;
import com.nic.nerie.m_offices.model.M_Offices;
import com.nic.nerie.m_qualifications.model.M_Qualifications;
import com.nic.nerie.m_qualificationsubjects.model.M_QualificationSubjects;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import jakarta.persistence.*;

@Entity
@Table(name = "mt_resourcepersons")
public class MT_ResourcePersons {
    @Id
    private String rpslno;
    private String rpemailid;
    private String rpname;

    private String rpspecialization;

    private String rpinstitutename;
    private String rpofficeaddress;
    private String rpresidentialaddress;
    private String rpofficephone;
    private String rpresidencephone;
    private String rpmobileno;
    private String rpfax;

    @ManyToOne
    @JoinColumn(name = "entryuser")
    public MT_Userlogin mtuserlogin;

    @ManyToOne
    @JoinColumn(name = "officecode")
    public M_Offices moffices;

    @ManyToOne
    @JoinColumn(name="qualificationcode")
    public M_Qualifications qualificationcode;

    @ManyToOne
    @JoinColumn(name="designationcode")
    public M_Designations designationcode;

    @ManyToOne
    @JoinColumn(name = "qualificationsubjectcode")
    public M_QualificationSubjects mqualificationsubjects;

    public MT_ResourcePersons() {
    }

    public MT_ResourcePersons(String rpslno, String rpemailid, String rpname, String rpspecialization, String rpinstitutename, String rpofficeaddress, String rpresidentialaddress, String rpofficephone, String rpresidencephone, String rpmobileno, String rpfax, MT_Userlogin mtuserlogin, M_Offices moffices, M_Qualifications qualificationcode, M_Designations designationcode, M_QualificationSubjects mqualificationsubjects) {
        this.rpslno = rpslno;
        this.rpemailid = rpemailid;
        this.rpname = rpname;
        this.rpspecialization = rpspecialization;
        this.rpinstitutename = rpinstitutename;
        this.rpofficeaddress = rpofficeaddress;
        this.rpresidentialaddress = rpresidentialaddress;
        this.rpofficephone = rpofficephone;
        this.rpresidencephone = rpresidencephone;
        this.rpmobileno = rpmobileno;
        this.rpfax = rpfax;
        this.mtuserlogin = mtuserlogin;
        this.moffices = moffices;
        this.qualificationcode = qualificationcode;
        this.designationcode = designationcode;
        this.mqualificationsubjects = mqualificationsubjects;
    }

    public String getRpslno() {
        return rpslno;
    }

    public void setRpslno(String rpslno) {
        this.rpslno = rpslno;
    }

    public String getRpemailid() {
        return rpemailid;
    }

    public void setRpemailid(String rpemailid) {
        this.rpemailid = rpemailid;
    }

    public String getRpname() {
        return rpname;
    }

    public void setRpname(String rpname) {
        this.rpname = rpname;
    }

    public String getRpspecialization() {
        return rpspecialization;
    }

    public void setRpspecialization(String rpspecialization) {
        this.rpspecialization = rpspecialization;
    }

    public String getRpinstitutename() {
        return rpinstitutename;
    }

    public void setRpinstitutename(String rpinstitutename) {
        this.rpinstitutename = rpinstitutename;
    }

    public String getRpofficeaddress() {
        return rpofficeaddress;
    }

    public void setRpofficeaddress(String rpofficeaddress) {
        this.rpofficeaddress = rpofficeaddress;
    }

    public String getRpresidentialaddress() {
        return rpresidentialaddress;
    }

    public void setRpresidentialaddress(String rpresidentialaddress) {
        this.rpresidentialaddress = rpresidentialaddress;
    }

    public String getRpofficephone() {
        return rpofficephone;
    }

    public void setRpofficephone(String rpofficephone) {
        this.rpofficephone = rpofficephone;
    }

    public String getRpresidencephone() {
        return rpresidencephone;
    }

    public void setRpresidencephone(String rpresidencephone) {
        this.rpresidencephone = rpresidencephone;
    }

    public String getRpmobileno() {
        return rpmobileno;
    }

    public void setRpmobileno(String rpmobileno) {
        this.rpmobileno = rpmobileno;
    }

    public String getRpfax() {
        return rpfax;
    }

    public void setRpfax(String rpfax) {
        this.rpfax = rpfax;
    }

    public MT_Userlogin getMtuserlogin() {
        return mtuserlogin;
    }

    public void setMtuserlogin(MT_Userlogin mtuserlogin) {
        this.mtuserlogin = mtuserlogin;
    }

    public M_Offices getMoffices() {
        return moffices;
    }

    public void setMoffices(M_Offices moffices) {
        this.moffices = moffices;
    }

    public M_Qualifications getQualificationcode() {
        return qualificationcode;
    }

    public void setQualificationcode(M_Qualifications qualificationcode) {
        this.qualificationcode = qualificationcode;
    }

    public M_Designations getDesignationcode() {
        return designationcode;
    }

    public void setDesignationcode(M_Designations designationcode) {
        this.designationcode = designationcode;
    }

    public M_QualificationSubjects getMqualificationsubjects() {
        return mqualificationsubjects;
    }

    public void setMqualificationsubjects(M_QualificationSubjects mqualificationsubjects) {
        this.mqualificationsubjects = mqualificationsubjects;
    }

    @Override
    public String toString() {
        return "MT_ResourcePersons{" + "rpslno=" + rpslno + ", rpemailid=" + rpemailid + ", rpname=" + rpname + ", rpspecialization=" + rpspecialization + ", rpinstitutename=" + rpinstitutename + ", rpofficeaddress=" + rpofficeaddress + ", rpresidentialaddress=" + rpresidentialaddress + ", rpofficephone=" + rpofficephone + ", rpresidencephone=" + rpresidencephone + ", rpmobileno=" + rpmobileno + ", rpfax=" + rpfax + ", mtuserlogin=" + mtuserlogin + ", moffices=" + moffices + ", qualificationcode=" + qualificationcode + ", designationcode=" + designationcode + ", mqualificationsubjects=" + mqualificationsubjects + '}';
    }
}
