
package com.nic.nerie.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class GenerateSalt {
    public static String getSalt() {
        String salt = null;

        try {
            SecureRandom rnd = SecureRandom.getInstance("SHA1PRNG");
            salt = (Long.toString(Math.abs(rnd.nextLong()), 36)).toUpperCase();
            salt = salt.substring(0, 5);
            rnd = null;
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }

        return salt;
    }
}
