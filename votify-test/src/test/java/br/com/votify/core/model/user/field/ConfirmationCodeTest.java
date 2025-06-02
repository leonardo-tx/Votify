package br.com.votify.core.model.user.field;

import net.jqwik.api.Property;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class ConfirmationCodeTest {
    private static final HashSet<Character> chars = new HashSet<>();

    @BeforeAll
    static void setupBeforeAll() {
        for (int i = 0; i < ConfirmationCode.CHARACTERS.length(); i++) {
            char c = ConfirmationCode.CHARACTERS.charAt(i);
            chars.add(c);
        }
    }

    @Test
    public void testEquals() {
        ConfirmationCode code1 = ConfirmationCode.parseUnsafe("teste");
        ConfirmationCode code2 = ConfirmationCode.parseUnsafe("teste");

        assertEquals(code1, code2);
        assertNotSame(code1, code2);
    }

    @Test
    public void charsAreUnique() {
        HashSet<Character> chars = new HashSet<>();
        for (int i = 0; i < ConfirmationCode.CHARACTERS.length(); i++) {
            char c = ConfirmationCode.CHARACTERS.charAt(i);
            assertTrue(chars.add(c));
        }
    }

    @Property
    public void generateEmailConfirmationCodes() {
        ConfirmationCode code = new ConfirmationCode();
        assertEquals(ConfirmationCode.CODE_LENGTH, code.getValue().length());

        for (int i = 0; i < code.getValue().length(); i++) {
            char c = code.getValue().charAt(i);
            assertTrue(chars.contains(c));
        }
    }
}
