package br.com.votify.core.utils;

public final class CharacterUtils {
    public static boolean isOneByteDigit(char c) {
        return c >= 48 && c <= 57;
    }

    public static boolean isOneByteUppercaseLetter(char c) {
        return c >= 'A' && c <= 'Z';
    }

    public static boolean isOneByteLowercaseLetter(char c) {
        return c >= 97 && c <= 122;
    }
}
