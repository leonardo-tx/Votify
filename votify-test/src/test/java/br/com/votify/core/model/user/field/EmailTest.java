package br.com.votify.core.model.user.field;

import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import net.jqwik.api.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmailTest {
    @Test
    void testEquals() {
        Email email1 = assertDoesNotThrow(() -> new Email("123@gmail.com"));
        Email email2 = assertDoesNotThrow(() -> new Email("123@gmail.com"));

        assertEquals(email1, email2);
        assertNotSame(email1, email2);
    }

    @Test
    void testNullEmail() {
        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> new Email(null)
        );
        assertEquals(VotifyErrorCode.EMAIL_EMPTY, exception.getErrorCode());
    }

    @Test
    void testParseUnsafe() {
        Email email = Email.parseUnsafe("teste");
        assertEquals("teste", email.getValue());
    }

    @Property
    void testValidEmails(@ForAll("validEmails") String email) {
        assertDoesNotThrow(() -> new Email(email));
    }

    @Property
    void testInvalidEmail(@ForAll("invalidEmails") String email) {
        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> new Email(email)
        );
        assertEquals(VotifyErrorCode.EMAIL_INVALID, exception.getErrorCode());
    }

    @Property
    void testEmailsWithInvalidLength(@ForAll("emailsWithInvalidLength") String email) {
        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> new Email(email)
        );
        assertEquals(VotifyErrorCode.EMAIL_INVALID_LENGTH, exception.getErrorCode());
    }

    @Provide
    Arbitrary<String> validEmails() {
        Arbitrary<String> localPart = Arbitraries.strings()
                .withChars("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789._%+-")
                .ofMinLength(1).ofMaxLength(64);
        Arbitrary<String> domain = Arbitraries.strings()
                .withChars("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-")
                .ofMinLength(1).ofMaxLength(182);
        Arbitrary<String> topLevelDomain = Arbitraries.strings()
                .withChars("abcdefghijklmnopqrstuvwxyz")
                .ofMinLength(2).ofMaxLength(6);

        return Combinators.combine(localPart, domain, topLevelDomain)
                .as((x, y, z) -> x + "@" + y + "." + z);
    }

    @Provide
    Arbitrary<String> emailsWithInvalidLength() {
        Arbitrary<String> localPart = Arbitraries.strings()
                .withChars("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789._%+-")
                .ofMinLength(1);
        Arbitrary<String> domain = Arbitraries.strings()
                .withChars("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-")
                .ofMinLength(1);

        return Combinators.combine(localPart, domain)
                .as((x, y) -> x + "@" + y)
                .filter(email -> email.length() < Email.MIN_LENGTH || email.length() > Email.MAX_LENGTH);
    }

    @Provide
    Arbitrary<String> invalidEmails() {
        return Arbitraries.oneOf(
                Arbitraries.strings()
                        .withChars("abcdefghijklmnopqrstuvwxyz0123456789._%+-")
                        .ofMinLength(Email.MIN_LENGTH).ofMaxLength(Email.MAX_LENGTH),
                Arbitraries.strings()
                        .withChars("abcdefghijklmnopqrstuvwxyz0123456789._%+-")
                        .ofMinLength(Email.MIN_LENGTH).ofMaxLength(Email.MAX_LENGTH - 1)
                        .map(localPart -> localPart + "@"),
                Arbitraries.strings()
                        .withChars("!@#$%^&*(){}[]|\\:;'\"<>,?/`~")
                        .ofMinLength(Email.MIN_LENGTH).ofMaxLength(Email.MAX_LENGTH - 9)
                        .map(invalidChars -> "user@" + invalidChars + ".com")
        );
    }
}
