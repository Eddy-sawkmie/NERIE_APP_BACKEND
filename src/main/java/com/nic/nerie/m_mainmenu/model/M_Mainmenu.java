package com.nic.nerie.m_mainmenu.model;

import jakarta.persistence.*;

@Entity
@Table(name = "m_mainmenu")
public class M_Mainmenu {
    @Id
    @Column(name = "mainmenucode", columnDefinition = "int2")
    private int mainmenucode;
    private String mainmenuname;

    public M_Mainmenu() {
    }

    public M_Mainmenu(int mainmenucode, String mainmenuname) {
        this.mainmenucode = mainmenucode;
        this.mainmenuname = mainmenuname;
    }

    public int getMainmenucode() {
        return mainmenucode;
    }

    public void setMainmenucode(int mainmenucode) {
        this.mainmenucode = mainmenucode;
    }

    public String getMainmenuname() {
        return mainmenuname;
    }

    public void setMainmenuname(String mainmenuname) {
        this.mainmenuname = mainmenuname;
    }
}
