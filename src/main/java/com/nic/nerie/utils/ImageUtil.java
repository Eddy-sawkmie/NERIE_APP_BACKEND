package com.nic.nerie.utils;

import java.util.Base64;

public class ImageUtil {
    public static String convertToBase64(byte[] image) {
        if (image == null || image.length == 0)
            return null;

        return Base64.getEncoder().encodeToString(image);
    }
}
