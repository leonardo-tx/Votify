package br.com.votify.core.model.user.field;

import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.test.CharCollections;
import net.jqwik.api.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NameTest {
    @Test
    void testEquals() {
        Name name1 = assertDoesNotThrow(() -> new Name("Nome"));
        Name name2 = assertDoesNotThrow(() -> new Name("Nome"));

        assertEquals(name1, name2);
        assertNotSame(name1, name2);
    }

    @Test
    void testNullName() {
        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> new Name(null)
        );
        assertEquals(VotifyErrorCode.NAME_EMPTY, exception.getErrorCode());
    }

    @Test
    void testParseUnsafe() {
        Name name = Name.parseUnsafe("teste");
        assertEquals("teste", name.getValue());
    }

    @Property
    void testValidNames(@ForAll("validNames") String name) {
        assertDoesNotThrow(() -> new Name(name));
    }

    @Property
    void testNamesWithInvalidLength(@ForAll("namesWithInvalidLength") String name) {
        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> new Name(name)
        );
        assertEquals(VotifyErrorCode.NAME_INVALID_LENGTH, exception.getErrorCode());
    }

    @Property
    void testInvalidNames(@ForAll("invalidNames") String name) {
        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> new Name(name)
        );
        assertEquals(VotifyErrorCode.NAME_INVALID, exception.getErrorCode());
    }

    @Test
    void testEmptyName() {
        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> new Name("")
        );
        assertEquals(VotifyErrorCode.NAME_INVALID_LENGTH, exception.getErrorCode());
    }

    @Provide
    Arbitrary<String> validNames() {
        return Arbitraries.strings()
                .excludeChars(CharCollections.ISO_CHARS)
                .ofMinLength(Name.MIN_LENGTH)
                .ofMaxLength(Name.MAX_LENGTH)
                .filter(this::nameHasValidWhitespaces);
    }

    @Provide
    Arbitrary<String> namesWithInvalidLength() {
        return Arbitraries.strings()
                .excludeChars(CharCollections.ISO_CHARS)
                .ofMinLength(Name.MAX_LENGTH + 1)
                .filter(this::nameHasValidWhitespaces);
    }

    @Provide
    Arbitrary<String> invalidNames() {
        List<String> names = List.of(
                " Jorel", "Samuel ", " Byces ", "Henrique  Mendonça",
                "João\nPedro", "Te\0ste", "123a\taa", "LittleDoge\f", "\baaa", "Espaço Inválido"
        );
        return Arbitraries.of(names);
    }

    private boolean nameHasValidWhitespaces(String name) {
        if (name.charAt(0) == ' ' || name.charAt(name.length() - 1) == ' ') {
            return false;
        }
        int whitespaceSequence = 0;
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isISOControl(c)) {
                return false;
            }
            if (c != ' ') {
                if (Character.isWhitespace(c)) return false;

                whitespaceSequence = 0;
                continue;
            }
            whitespaceSequence += 1;
            if (whitespaceSequence > 1) {
                return false;
            }
        }
        return true;
    }
}
