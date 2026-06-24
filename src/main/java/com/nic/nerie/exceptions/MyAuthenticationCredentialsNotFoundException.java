package com.nic.nerie.exceptions;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

public class MyAuthenticationCredentialsNotFoundException extends AuthenticationCredentialsNotFoundException {
    private String resourceType;

    public MyAuthenticationCredentialsNotFoundException(String msg, String resourceType) {
        super(msg);
        this.resourceType = resourceType;
    }

    public String getResourceType() {
        return this.resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    } 
}
