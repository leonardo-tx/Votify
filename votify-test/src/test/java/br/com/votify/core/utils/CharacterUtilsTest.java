package br.com.votify.core.utils;

import net.jqwik.api.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CharacterUtilsTest {
    @Property
    public void testValidOneByteDigits(@ForAll("validOneByteDigits") char c) {
        assertTrue(CharacterUtils.isOneByteDigit(c));
    }

    @Property
    public void testInvalidOneByteDigits(@ForAll("invalidOneByteDigits") char c) {
        assertFalse(CharacterUtils.isOneByteDigit(c));
    }

    @Property
    public void testValidOneByteLowercaseLetters(@ForAll("validOneByteLowercaseLetters") char c) {
        assertTrue(CharacterUtils.isOneByteLowercaseLetter(c));
    }

    @Property
    public void testInvalidOneByteLowercaseLetters(@ForAll("invalidOneByteLowercaseLetters") char c) {
        assertFalse(CharacterUtils.isOneByteLowercaseLetter(c));
    }

    @Provide
    @SuppressWarnings("unused")
    private Arbitrary<Character> validOneByteDigits() {
        return Arbitraries.chars().with("1234567890");
    }

    @Provide
    @SuppressWarnings("unused")
    private Arbitrary<Character> invalidOneByteDigits() {
        return Arbitraries.chars().filter(c -> c < 48 || c > 57);
    }

    @Provide
    @SuppressWarnings("unused")
    private Arbitrary<Character> validOneByteLowercaseLetters() {
        return Arbitraries.chars().with("abcdefghijklmnopqrstuvwxyz");
    }

    @Provide
    @SuppressWarnings("unused")
    private Arbitrary<Character> invalidOneByteLowercaseLetters() {
        return Arbitraries.chars().filter(c -> c < 97 || c > 122);
    }
}
