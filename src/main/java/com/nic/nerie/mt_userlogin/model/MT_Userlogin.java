/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nic.nerie.mt_userlogin.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nic.nerie.m_designations.model.M_Designations;
import com.nic.nerie.m_offices.model.M_Offices;
import com.nic.nerie.mt_userloginrole.model.MT_UserloginRole;
import com.nic.nerie.t_notifications.model.T_Notifications;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Set;

@Entity
@Table(name = "mt_userlogin")
public class MT_Userlogin implements Serializable {
    @Id
    private String usercode;
    private String username;
    private String userdescription;
    private String userid;
    private String userpassword;

    @Column(name = "enabled", columnDefinition = "int2")
    private int enabled;

    private byte[] userphotograph;
    private String userrole;
    private String usermobile;
    private String ismodified;
    private String emailid;
    private String isfaculty;
    private String iscoordinator;
    private String iscurrentfaculty;

    @ManyToOne
    @JoinColumn(name = "role_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JsonIgnore
    private MT_UserloginRole role;

    @Column(name = "use_bcrypt")
    private Boolean useBcrypt;

    @ManyToOne
    @JoinColumn(name = "officecode")
    @JsonIgnore
    public M_Offices moffices;

    @ManyToOne
    @JoinColumn(name = "designationcode")
    @JsonIgnore
    private M_Designations mdesignations;

    @ManyToMany(mappedBy = "receivers", fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<T_Notifications> notifications;

    public MT_Userlogin() {
    }

    public MT_Userlogin(String usercode, String username, String userdescription, String userid, String userpassword, int enabled, byte[] userphotograph, String userrole, String usermobile, String ismodified, String emailid, String isfaculty, String iscoordinator, MT_UserloginRole role, Boolean useBcrypt, M_Offices moffices, M_Designations mdesignations) {
        this.usercode = usercode;
        this.username = username;
        this.userdescription = userdescription;
        this.userid = userid;
        this.userpassword = userpassword;
        this.enabled = enabled;
        this.userphotograph = userphotograph;
        this.userrole = userrole;
        this.usermobile = usermobile;
        this.ismodified = ismodified;
        this.emailid = emailid;
        this.isfaculty = isfaculty;
        this.iscoordinator = iscoordinator;
        this.role = role;
        this.useBcrypt = useBcrypt;
        this.moffices = moffices;
        this.mdesignations = mdesignations;
    }

    public String getUsercode() {
        return usercode;
    }

    public void setUsercode(String usercode) {
        this.usercode = usercode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserdescription() {
        return userdescription;
    }

    public void setUserdescription(String userdescription) {
        this.userdescription = userdescription;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUserpassword() {
        return userpassword;
    }

    public void setUserpassword(String userpassword) {
        this.userpassword = userpassword;
    }

    public int getEnabled() {
        return enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    public byte[] getUserphotograph() {
        return userphotograph;
    }

    public void setUserphotograph(byte[] userphotograph) {
        this.userphotograph = userphotograph;
    }

    public String getUserrole() {
        return userrole;
    }

    public void setUserrole(String userrole) {
        this.userrole = userrole;
    }

    public String getUsermobile() {
        return usermobile;
    }

    public void setUsermobile(String usermobile) {
        this.usermobile = usermobile;
    }

    public String getIsmodified() {
        return ismodified;
    }

    public void setIsmodified(String ismodified) {
        this.ismodified = ismodified;
    }

    public String getEmailid() {
        return emailid;
    }

    public void setEmailid(String emailid) {
        this.emailid = emailid;
    }

    public String getIsfaculty() {
        return isfaculty;
    }

    public String getIscurrentfaculty() { return iscurrentfaculty; }

    public void setIscurrentfaculty(String iscurrentfaculty) { this.iscurrentfaculty = iscurrentfaculty; }

    public void setIsfaculty(String isfaculty) {
        this.isfaculty = isfaculty;
    }

    public String getIscoordinator() { return iscoordinator; }

    public void setIscoordinator(String iscoordinator) { this.iscoordinator = iscoordinator; }

    public M_Offices getMoffices() {
        return moffices;
    }

    public void setMoffices(M_Offices moffices) {
        this.moffices = moffices;
    }

    public M_Designations getMdesignations() {
        return mdesignations;
    }

    public void setMdesignations(M_Designations mdesignations) {
        this.mdesignations = mdesignations;
    }

    public MT_UserloginRole getRole() {
        return role;
    }

    public void setRole(MT_UserloginRole role) {
        this.role = role;
    }

    public Boolean getUseBcrypt() {
        return useBcrypt;
    }

    public void setUseBcrypt(Boolean useBcrypt) {
        this.useBcrypt = useBcrypt;
    }

    public Set<T_Notifications> getNotifications() { return notifications; }

    public void setNotifications(Set<T_Notifications> notifications) { this.notifications = notifications; }

    @Override
    public String toString() {
        return "MT_Userlogin{" +
                "usercode='" + usercode + '\'' +
                ", username='" + username + '\'' +
                ", userdescription='" + userdescription + '\'' +
                ", userid='" + userid + '\'' +
                ", userpassword='" + userpassword + '\'' +
                ", enabled=" + enabled +
                ", userphotograph=" + Arrays.toString(userphotograph) +
                ", userrole='" + userrole + '\'' +
                ", usermobile='" + usermobile + '\'' +
                ", ismodified='" + ismodified + '\'' +
                ", emailid='" + emailid + '\'' +
                ", isfaculty='" + isfaculty + '\'' +
                ", iscoordinator='" + iscoordinator + '\'' +
                ", role=" + role +
                ", useBcrypt=" + useBcrypt +
                ", moffices=" + moffices +
                ", mdesignations=" + mdesignations +
                '}';
    }
}

