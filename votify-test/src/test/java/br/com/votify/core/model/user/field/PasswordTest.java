package br.com.votify.core.model.user.field;

import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.test.CharCollections;
import net.jqwik.api.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PasswordTest {
    @Test
    void testEquals() {
        Password password1 = assertDoesNotThrow(() -> new Password("123456789"));
        Password password2 = assertDoesNotThrow(() -> new Password("123456789"));

        assertEquals(password1, password2);
        assertNotSame(password1, password2);
    }

    @Test
    void testNullPassword() {
        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> new Password(null)
        );
        assertEquals(VotifyErrorCode.PASSWORD_EMPTY, exception.getErrorCode());
    }

    @Test
    void testParseUnsafe() {
        Password password = Password.parseUnsafe("teste");
        assertEquals("teste", password.getValue());
    }

    @Property
    void testValidPasswords(@ForAll("validPasswords") String password) {
        assertDoesNotThrow(() -> new Password(password));
    }

    @Property
    void testPasswordsWithInvalidLength(@ForAll("passwordsWithInvalidLength") String password) {
        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> new Password(password)
        );
        assertEquals(VotifyErrorCode.PASSWORD_INVALID_LENGTH, exception.getErrorCode());
    }

    @Property
    void testPasswordsWithInvalidCharacter(@ForAll("passwordsWithInvalidCharacter") String password) {
        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> new Password(password)
        );
        assertEquals(VotifyErrorCode.PASSWORD_INVALID_CHARACTER, exception.getErrorCode());
    }

    @Property
    void testPasswordsWithInvalidBytes(@ForAll("passwordsWithInvalidBytes") String password) {
        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> new Password(password)
        );
        assertEquals(VotifyErrorCode.PASSWORD_INVALID_BYTES, exception.getErrorCode());
    }

    @Provide
    Arbitrary<String> validPasswords() {
        return Arbitraries.strings()
                .excludeChars(CharCollections.ISO_CHARS)
                .ofMinLength(Password.MIN_LENGTH)
                .filter(password -> password.getBytes().length <= Password.MAX_BYTES);
    }

    @Provide
    Arbitrary<String> passwordsWithInvalidLength() {
        return Arbitraries.strings()
                .excludeChars(CharCollections.ISO_CHARS)
                .ofMaxLength(Password.MIN_LENGTH - 1)
                .filter(password -> password.getBytes().length <= Password.MAX_BYTES);
    }

    @Provide
    Arbitrary<String> passwordsWithInvalidCharacter() {
        return Arbitraries.strings()
                .ofMinLength(Password.MIN_LENGTH)
                .filter(password ->
                        password.getBytes().length <= Password.MAX_BYTES &&
                                password.chars().anyMatch(c -> Arrays.binarySearch(CharCollections.ISO_CHARS, (char)c) >= 0)
                );
    }

    @Provide
    Arbitrary<String> passwordsWithInvalidBytes() {
        return Arbitraries.strings()
                .excludeChars(CharCollections.ISO_CHARS)
                .filter(password -> password.getBytes().length > Password.MAX_BYTES);
    }
}
