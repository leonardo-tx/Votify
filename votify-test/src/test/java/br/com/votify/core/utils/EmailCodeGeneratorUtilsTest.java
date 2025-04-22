package br.com.votify.core.utils;

import net.jqwik.api.Property;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EmailCodeGeneratorUtilsTest {
    private static final HashSet<Character> chars = new HashSet<>();

    @BeforeAll
    static void setupBeforeAll() {
        for (int i = 0; i < EmailCodeGeneratorUtils.CHARACTERS.length(); i++) {
            char c = EmailCodeGeneratorUtils.CHARACTERS.charAt(i);
            chars.add(c);
        }
    }

    @Test
    public void charsAreUnique() {
        HashSet<Character> chars = new HashSet<>();
        for (int i = 0; i < EmailCodeGeneratorUtils.CHARACTERS.length(); i++) {
            char c = EmailCodeGeneratorUtils.CHARACTERS.charAt(i);
            assertTrue(chars.add(c));
        }
    }

    @Property
    public void generateEmailConfirmationCodes() {
        String code = EmailCodeGeneratorUtils.generateEmailConfirmationCode();
        assertEquals(100, code.length());

        for (int i = 0; i < code.length(); i++) {
            char c = code.charAt(i);
            assertTrue(chars.contains(c));
        }
    }
}
