package com.nic.nerie.m_minorities.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;

@Entity
@Table(name = "m_minorities")
public class M_Minorities implements Serializable {

    @Id
    private String minoritycode;
    private String minorityname;

    public M_Minorities() {
    }

    public M_Minorities(String minoritycode, String minorityname) {
        this.minoritycode = minoritycode;
        this.minorityname = minorityname;
    }

    public String getMinoritycode() {
        return minoritycode;
    }

    public void setMinoritycode(String minoritycode) {
        this.minoritycode = minoritycode;
    }

    public String getMinorityname() {
        return minorityname;
    }

    public void setMinorityname(String minorityname) {
        this.minorityname = minorityname;
    }

}

