/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nic.nerie.utils;

import java.security.SecureRandom;

/**
 *
 * @author nic
 */
public class RandomPasswordGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();
    public static final int PASSWORD_LENGTH = 8;

    public RandomPasswordGenerator() {
    }

    public static String generateRandomPassword() {
        String pw = "";
//        try {
//            SecureRandom rnd = SecureRandom.getInstance("SH1PRNG");
            String letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890+@";
            for (int i = 0; i < PASSWORD_LENGTH; i++) {
                int index = (int) (RANDOM.nextDouble() * letters.length());
                pw += letters.substring(index, index + 1);
            }
//        } catch (NoSuchAlgorithmException ex) {
//        }

        return pw;
    }
}
