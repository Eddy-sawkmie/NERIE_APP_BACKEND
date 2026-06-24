package com.nic.nerie.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator {
    public static Boolean isEmailValid(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }

        email = email.trim();

        if (email.length() > 254) {
            return false;
        }

        String emailRegex = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }
}