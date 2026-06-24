package com.nic.nerie.exceptions;

import org.springframework.security.authorization.AuthorizationDeniedException;

public class MyAuthorizationDeniedException extends AuthorizationDeniedException {
    private String resourceType;

    public MyAuthorizationDeniedException(String msg, String resourceType) {
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
