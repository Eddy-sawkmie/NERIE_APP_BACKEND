package com.nic.nerie.m_venues.model;

import com.nic.nerie.m_offices.model.M_Offices;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "m_venues")
public class M_Venues implements Serializable {
    @Id
    private String venuecode;
    private String venuename;
    @ManyToOne
    @JoinColumn(name = "officecode")
    public M_Offices moffices;

    public M_Venues() {
    }

    public M_Venues(String venuecode, String venuename, M_Offices moffices) {
        this.venuecode = venuecode;
        this.venuename = venuename;
        this.moffices = moffices;
    }

    public String getVenuecode() {
        return venuecode;
    }

    public void setVenuecode(String venuecode) {
        this.venuecode = venuecode;
    }

    public String getVenuename() {
        return venuename;
    }

    public void setVenuename(String venuename) {
        this.venuename = venuename;
    }

    public M_Offices getMoffices() {
        return moffices;
    }

    public void setMoffices(M_Offices moffices) {
        this.moffices = moffices;
    }

}

