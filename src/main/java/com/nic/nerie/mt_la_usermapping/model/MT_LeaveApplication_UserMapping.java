package com.nic.nerie.mt_la_usermapping.model;

import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import jakarta.persistence.*;

@Entity
@Table(name = "mt_la_usermapping")
public class MT_LeaveApplication_UserMapping {
    @Id
    private int lausermapcode;

    private int larolecode;
    //Roles
    //1 - Male Warden
    //2 - Female Warden
    //3 - Dean
    //4 - Principal (actually no need cause in the page we are checking with ROLE 'Z')

    private String remarks;

    @ManyToOne
    @JoinColumn(name = "usercode")
    public MT_Userlogin usercode;

    public int getLausermapcode() {
        return lausermapcode;
    }

    public void setLausermapcode(int lausermapcode) {
        this.lausermapcode = lausermapcode;
    }

    public int getLarolecode() {
        return larolecode;
    }

    public void setLarolecode(int larolecode) {
        this.larolecode = larolecode;
    }

    public MT_Userlogin getUsercode() {
        return usercode;
    }

    public void setUsercode(MT_Userlogin usercode) {
        this.usercode = usercode;
    }

    public String getRemarks() { return remarks; }

    public void setRemarks(String remarks) { this.remarks = remarks; }

    @Override
    public String toString() {
        return "MT_LeaveApplication_UserMapping{" + "lausermapcode=" + lausermapcode + ", larolecode=" + larolecode + '}';
    }
}
