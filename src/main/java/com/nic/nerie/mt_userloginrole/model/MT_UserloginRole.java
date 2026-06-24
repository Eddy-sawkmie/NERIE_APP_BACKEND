package com.nic.nerie.mt_userloginrole.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nic.nerie.m_processes.model.M_Processes;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "mt_userloginrole")
public class MT_UserloginRole {
    @Id
    private Integer roleId;

    @NotBlank
    @Column(name = "role_name", length=25, nullable = false)
    private String roleName;

    @NotBlank
    @Column(name = "role_code", length=2, unique=true, nullable = false)
    private String roleCode;

    @Column(name = "remarks", length=255)
    private String remarks;

    @ManyToMany
    @JoinTable(
            name = "mt_role_process",
            joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "roleId"),
            inverseJoinColumns = @JoinColumn(name = "processcode", referencedColumnName = "processCode")
    )
    private Set<M_Processes> processes = new HashSet<>();

    @OneToMany(mappedBy = "role", cascade = CascadeType.PERSIST, orphanRemoval = false)
    @JsonIgnore
    private List<MT_Userlogin> users;

    public MT_UserloginRole() {}

    public MT_UserloginRole(Integer roleId, String roleName, String roleCode, String remarks) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.roleCode = roleCode;
        this.remarks = remarks;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Set<M_Processes> getProcesses() {
        return processes;
    }

    @Override
    public String toString() {
        return "MT_UserloginRole{" +
                "roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
                ", roleCode='" + roleCode + '\'' +
                ", remarks='" + remarks + '\'' +
                '}';
    }
}
