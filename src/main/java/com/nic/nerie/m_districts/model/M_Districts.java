/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nic.nerie.m_districts.model;

import com.nic.nerie.m_states.model.M_States;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "m_districts")
public class M_Districts implements Serializable {

    @Id
    private String districtcode;
    private String districtname;
    @ManyToOne
    @JoinColumn(name = "statecode")
    public M_States mstates;

    public M_Districts() {
    }

    public M_Districts(String districtcode, String districtname, M_States mstates) {
        this.districtcode = districtcode;
        this.districtname = districtname;
        this.mstates = mstates;
    }

    public String getDistrictcode() {
        return districtcode;
    }

    public void setDistrictcode(String districtcode) {
        this.districtcode = districtcode;
    }

    public String getDistrictname() {
        return districtname;
    }

    public void setDistrictname(String districtname) {
        this.districtname = districtname;
    }

    public M_States getMstates() {
        return mstates;
    }

    public void setMstates(M_States mstates) {
        this.mstates = mstates;
    }

}
