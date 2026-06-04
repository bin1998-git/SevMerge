package com.example.SevMerge.core.util;

import java.util.Random;

public class MailUtil {

    public static String generateRandomCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}
