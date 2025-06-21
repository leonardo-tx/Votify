package br.com.votify.core.model.user.field;

import net.jqwik.api.Property;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class ConfirmationCodeTest {
    private static final HashSet<Character> ALL_CONFIRMATION_CODE_CHARS = new HashSet<>();

    @BeforeAll
    static void setupBeforeAll() {
        for (int i = 0; i < ConfirmationCode.CHARACTERS.length(); i++) {
            char c = ConfirmationCode.CHARACTERS.charAt(i);
            ALL_CONFIRMATION_CODE_CHARS.add(c);
        }
    }

    @Test
    void testMatches() {
        ConfirmationCode code = new ConfirmationCode();
        assertTrue(code.matches(code.getValue()));
    }

    @Test
    void testNotMatches() {
        ConfirmationCode code = new ConfirmationCode();
        assertFalse(code.matches("teste"));
    }

    @Test
    void testParseUnsafe() {
        ConfirmationCode code = ConfirmationCode.parseUnsafe("teste");
        assertEquals("teste", code.getValue());
    }

    @Test
    void charsAreUnique() {
        HashSet<Character> chars = new HashSet<>();
        for (int i = 0; i < ConfirmationCode.CHARACTERS.length(); i++) {
            char c = ConfirmationCode.CHARACTERS.charAt(i);
            assertTrue(chars.add(c));
        }
    }

    @Property
    void generateEmailConfirmationCodes() {
        ConfirmationCode code = new ConfirmationCode();
        assertEquals(ConfirmationCode.CODE_LENGTH, code.getValue().length());

        for (int i = 0; i < code.getValue().length(); i++) {
            char c = code.getValue().charAt(i);
            assertTrue(ALL_CONFIRMATION_CODE_CHARS.contains(c));
        }
    }
}
