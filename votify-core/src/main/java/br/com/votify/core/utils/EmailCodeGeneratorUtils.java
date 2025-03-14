package br.com.votify.core.utils;

import java.security.SecureRandom;

public class EmailCodeGeneratorUtils {

    private static final int CODE_LENGTH = 6;

    public static int generateEmailConfirmationCode() {
        SecureRandom random = new SecureRandom();
        int min = (int) Math.pow(10, CODE_LENGTH - 1);
        int max = (int) Math.pow(10, CODE_LENGTH) - 1;
        return random.nextInt(max - min + 1) + min;
    }
}