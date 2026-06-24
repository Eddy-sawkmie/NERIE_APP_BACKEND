package com.nic.nerie.utils;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ExceptionUtil {
    public static String generateUnAuthenticatedMessage(@NotNull @NotBlank String endpointOrProcessname) {
        return String.format("%s was accessed by an unauthenticated user", endpointOrProcessname);
    }

    public static String generateUnAuthenticatedMessage(@NotNull @NotBlank String endpointOrProcessname, @NotNull @NotBlank String httpVerb) {
        return String.format("%s [%s] was accessed by an unauthenticated user", endpointOrProcessname, httpVerb);
    }

    public static String generateAuthorizationDeniedMessage(@NotNull @NotBlank String endpointOrProcessname, @NotNull @NotBlank String userid) {
        return String.format("%s was accessed by an unauthorized user with userid %s", endpointOrProcessname, userid);
    }

    public static String generateAuthorizationDeniedMessage(@NotNull @NotBlank String endpointOrProcessname, 
        @NotNull @NotBlank String httpVerb, @NotNull @NotBlank String userid) {
        return String.format("%s [%s] was accessed by an unauthorized user with userid %s", endpointOrProcessname, httpVerb, userid);
    }
}
