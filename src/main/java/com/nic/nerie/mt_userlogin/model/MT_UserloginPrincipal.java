package com.nic.nerie.mt_userlogin.model;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;

public class MT_UserloginPrincipal extends User {
    private final Boolean useBcrypt;

    public MT_UserloginPrincipal(MT_Userlogin user) {
        super(user.getUserid(), user.getUserpassword(), Collections.singleton(new SimpleGrantedAuthority(user.getRole().getRoleCode())));
        this.useBcrypt = user.getUseBcrypt();
    }

    public Boolean isUseBcrypt() {
        return useBcrypt;
    }
}
