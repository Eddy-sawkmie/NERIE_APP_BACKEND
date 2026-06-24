/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nic.nerie.m_designations.model;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "m_designations")
public class M_Designations implements Serializable {

    @Id
    private String designationcode;
    private String designationname;
    private String isparticipantdesignation;

    public M_Designations() {
    }

    public M_Designations(String designationcode, String designationname, String isparticipantdesignation) {
        this.designationcode = designationcode;
        this.designationname = designationname;
        this.isparticipantdesignation = isparticipantdesignation;
    }

    public String getDesignationcode() {
        return designationcode;
    }

    public void setDesignationcode(String designationcode) {
        this.designationcode = designationcode;
    }

    public String getDesignationname() {
        return designationname;
    }

    public void setDesignationname(String designationname) {
        this.designationname = designationname;
    }

    public String getIsparticipantdesignation() {
        return isparticipantdesignation;
    }

    public void setIsparticipantdesignation(String isparticipantdesignation) {
        this.isparticipantdesignation = isparticipantdesignation;
    }

}
