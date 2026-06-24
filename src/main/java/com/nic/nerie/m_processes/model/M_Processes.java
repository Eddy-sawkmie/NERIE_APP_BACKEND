package com.nic.nerie.m_processes.model;


import com.nic.nerie.m_mainmenu.model.M_Mainmenu;
import jakarta.persistence.*;

@Entity
@Table(name = "m_processes")
public class M_Processes {
    @Id
    private int processcode;
    private String processname;
    private String pageurl;
    
    @Column(name = "newpageurl", length = 100)
    private String newpageurl;

    private String menuname;

    @ManyToOne
    @JoinColumn(name = "mainmenucode")
    public M_Mainmenu m_mainmenu;

    public M_Processes() {
    }

    public M_Processes(int processcode, String processname, String pageurl, String newpageurl, String menuname, M_Mainmenu m_mainmenu) {
        this.processcode = processcode;
        this.processname = processname;
        this.pageurl = pageurl;
        this.newpageurl = newpageurl;
        this.menuname = menuname;
        this.m_mainmenu = m_mainmenu;
    }

    public int getProcesscode() {
        return processcode;
    }

    public void setProcesscode(int processcode) {
        this.processcode = processcode;
    }

    public String getProcessname() {
        return processname;
    }

    public void setProcessname(String processname) {
        this.processname = processname;
    }

    public String getPageurl() {
        return pageurl;
    }

    public void setPageurl(String pageurl) {
        this.pageurl = pageurl;
    }

    public void setNewpageurl(String newpageurl) {
        this.newpageurl = newpageurl;
    }

    public String getNewpageurl() {
        return this.newpageurl;
    }
    
    public String getMenuname() {
        return menuname;
    }

    public void setMenuname(String menuname) {
        this.menuname = menuname;
    }

    public M_Mainmenu getM_mainmenu() {
        return m_mainmenu;
    }

    public void setM_mainmenu(M_Mainmenu m_mainmenu) {
        this.m_mainmenu = m_mainmenu;
    }
}
