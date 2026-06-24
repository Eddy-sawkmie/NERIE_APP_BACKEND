package com.nic.nerie.authentication.service;

import com.nic.nerie.configs.security.service.JwtService;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Autowired
    public AuthenticationService(
            AuthenticationManager authenticationManager,
            JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public String verify(@NotNull MT_Userlogin user) {
        Authentication authentication = null;
        
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUserid(), user.getUserpassword()));;
            
            if (authentication.isAuthenticated()) 
                return jwtService.generateToken(user.getUserid());  // STATELESS baby
        } catch (AuthenticationException ex) {
            ex.printStackTrace(); 
        }

        return null;
    }
}
