package br.com.votify.core.utils.validators;

import br.com.votify.core.domain.entities.users.AdminUser;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.utils.CharacterUtils;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import net.jqwik.api.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserValidatorTest {
    private final char[] isoChars;
    private final char[] validIdChars;

    public UserValidatorTest() {
        List<Character> charList = new ArrayList<>();
        List<Character> validIdCharsList = new ArrayList<>();
        for (int i = 0; i < Character.MAX_VALUE; i++) {
            char c = (char)i;
            if (Character.isISOControl(c)) {
                charList.add(c);
            }
            if (c == '-' || CharacterUtils.isOneByteDigit(c) || CharacterUtils.isOneByteLowercaseLetter(c)) {
                validIdCharsList.add(c);
            }
        }
        isoChars = new char[charList.size()];
        validIdChars = new char[validIdCharsList.size()];
        for (int i = 0; i < charList.size(); i++) {
            isoChars[i] = charList.get(i);
        }
        for (int i = 0; i < validIdCharsList.size(); i++) {
            validIdChars[i] = validIdCharsList.get(i);
        }
    }

    @Test
    public void testValidUser() {
        User user = new AdminUser(
            null,
            "littledoge",
            "Leonardo Teixeira",
            "123@gmail.com",
            "19283784you"
        );
        assertDoesNotThrow(() -> UserValidator.validateFields(user));
    }

    @Test
    public void testNullUserName() {
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> UserValidator.validateUserName(null)
        );
        assertEquals(VotifyErrorCode.USER_NAME_EMPTY, exception.getErrorCode());
    }

    @Property
    public void testValidUserNames(@ForAll("validUserNames") String userName) {
        assertDoesNotThrow(() -> UserValidator.validateUserName(userName));
    }

    @Property
    public void testUserNamesWithInvalidLength(@ForAll("userNamesWithInvalidLength") String userName) {
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> UserValidator.validateUserName(userName)
        );
        assertEquals(VotifyErrorCode.USER_NAME_INVALID_LENGTH, exception.getErrorCode());
    }

    @Property
    public void testInvalidUserNames(@ForAll("invalidUserNames") String userName) {
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> UserValidator.validateUserName(userName)
        );
        assertEquals(VotifyErrorCode.USER_NAME_INVALID, exception.getErrorCode());
    }

    @Property
    public void testUserNamesWithInvalidCharacter(@ForAll("userNamesWithInvalidCharacter") String userName) {
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> UserValidator.validateUserName(userName)
        );
        assertEquals(VotifyErrorCode.USER_NAME_INVALID_CHARACTER, exception.getErrorCode());
    }

    @Test
    public void testNullEmail() {
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> UserValidator.validateEmail(null)
        );
        assertEquals(VotifyErrorCode.EMAIL_EMPTY, exception.getErrorCode());
    }

    @Property
    public void testValidEmails(@ForAll("validEmails") String email) {
        assertDoesNotThrow(() -> UserValidator.validateEmail(email));
    }

    @Property
    public void testInvalidEmail(@ForAll("invalidEmails") String email) {
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> UserValidator.validateEmail(email)
        );
        assertEquals(VotifyErrorCode.EMAIL_INVALID, exception.getErrorCode());
    }

    @Property
    public void testEmailsWithInvalidLength(@ForAll("emailsWithInvalidLength") String email) {
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> UserValidator.validateEmail(email)
        );
        assertEquals(VotifyErrorCode.EMAIL_INVALID_LENGTH, exception.getErrorCode());
    }

    @Test
    public void testNullName() {
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> UserValidator.validateName(null)
        );
        assertEquals(VotifyErrorCode.NAME_EMPTY, exception.getErrorCode());
    }

    @Property
    public void testValidNames(@ForAll("validNames") String name) {
        assertDoesNotThrow(() -> UserValidator.validateName(name));
    }

    @Property
    public void testNamesWithInvalidLength(@ForAll("namesWithInvalidLength") String name) {
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> UserValidator.validateName(name)
        );
        assertEquals(VotifyErrorCode.NAME_INVALID_LENGTH, exception.getErrorCode());
    }

    @Property
    public void testInvalidNames(@ForAll("invalidNames") String name) {
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> UserValidator.validateName(name)
        );
        assertEquals(VotifyErrorCode.NAME_INVALID, exception.getErrorCode());
    }

    @Test
    public void testEmptyName() {
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> UserValidator.validateName("")
        );
        assertEquals(VotifyErrorCode.NAME_INVALID_LENGTH, exception.getErrorCode());
    }

    @Test
    public void testNullPassword() {
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> UserValidator.validatePassword(null)
        );
        assertEquals(VotifyErrorCode.PASSWORD_EMPTY, exception.getErrorCode());
    }

    @Property
    public void testValidPasswords(@ForAll("validPasswords") String password) {
        assertDoesNotThrow(() -> UserValidator.validatePassword(password));
    }

    @Property
    public void testPasswordsWithInvalidLength(@ForAll("passwordsWithInvalidLength") String password) {
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> UserValidator.validatePassword(password)
        );
        assertEquals(VotifyErrorCode.PASSWORD_INVALID_LENGTH, exception.getErrorCode());
    }

    @Property
    public void testPasswordsWithInvalidCharacter(@ForAll("passwordsWithInvalidCharacter") String password) {
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> UserValidator.validatePassword(password)
        );
        assertEquals(VotifyErrorCode.PASSWORD_INVALID_CHARACTER, exception.getErrorCode());
    }

    @Property
    public void testPasswordsWithInvalidBytes(@ForAll("passwordsWithInvalidBytes") String password) {
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> UserValidator.validatePassword(password)
        );
        assertEquals(VotifyErrorCode.PASSWORD_INVALID_BYTES, exception.getErrorCode());
    }

    @Provide
    @SuppressWarnings("unused")
    private Arbitrary<String> validUserNames() {
        return Arbitraries.strings()
            .withChars(validIdChars)
            .ofMinLength(User.USER_NAME_MIN_LENGTH)
            .ofMaxLength(User.USER_NAME_MAX_LENGTH)
            .filter(id -> !id.startsWith("-") && !id.endsWith("-"));
    }

    @Provide
    @SuppressWarnings("unused")
    private Arbitrary<String> userNamesWithInvalidLength() {
        return Arbitraries.strings()
            .withChars(validIdChars)
            .filter(id ->
                !id.startsWith("-") && !id.endsWith("-") &&
                (id.length() < User.USER_NAME_MIN_LENGTH || id.length() > User.USER_NAME_MAX_LENGTH)
            );
    }

    @Provide
    @SuppressWarnings("unused")
    private Arbitrary<String> invalidUserNames() {
        return Arbitraries.strings()
            .withChars(validIdChars)
            .ofMinLength(User.USER_NAME_MIN_LENGTH)
            .ofMaxLength(User.USER_NAME_MAX_LENGTH)
            .filter(id -> id.startsWith("-") || id.endsWith("-"));
    }

    @Provide
    @SuppressWarnings("unused")
    private Arbitrary<String> userNamesWithInvalidCharacter() {
        return Arbitraries.strings()
            .ofMinLength(User.USER_NAME_MIN_LENGTH)
            .ofMaxLength(User.USER_NAME_MAX_LENGTH)
            .filter(id ->
                !id.startsWith("-") && !id.endsWith("-") &&
                id.chars().anyMatch(c -> Arrays.binarySearch(validIdChars, (char)c) < 0)
            );
    }

    @Provide
    @SuppressWarnings("unused")
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
    @SuppressWarnings("unused")
    Arbitrary<String> emailsWithInvalidLength() {
        Arbitrary<String> localPart = Arbitraries.strings()
                .withChars("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789._%+-")
                .ofMinLength(1);
        Arbitrary<String> domain = Arbitraries.strings()
                .withChars("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-")
                .ofMinLength(1);

        return Combinators.combine(localPart, domain)
                .as((x, y) -> x + "@" + y)
                .filter((email) -> email.length() < User.EMAIL_MIN_LENGTH || email.length() > User.EMAIL_MAX_LENGTH);
    }

    @Provide
    @SuppressWarnings("unused")
    Arbitrary<String> invalidEmails() {
        return Arbitraries.oneOf(
            // Sem "@"
            Arbitraries.strings()
                .withChars("abcdefghijklmnopqrstuvwxyz0123456789._%+-")
                .ofMinLength(User.EMAIL_MIN_LENGTH).ofMaxLength(User.EMAIL_MAX_LENGTH),

            // Sem domínio
            Arbitraries.strings()
                .withChars("abcdefghijklmnopqrstuvwxyz0123456789._%+-")
                .ofMinLength(User.EMAIL_MIN_LENGTH).ofMaxLength(User.EMAIL_MAX_LENGTH - 1)
                .map(localPart -> localPart + "@"),

            // Domínio inválido (com caracteres proibidos)
            Arbitraries.strings()
                .withChars("!@#$%^&*(){}[]|\\:;'\"<>,?/`~")
                .ofMinLength(User.EMAIL_MIN_LENGTH).ofMaxLength(User.EMAIL_MAX_LENGTH - 9)
                .map(invalidChars -> "user@" + invalidChars + ".com")
        );
    }

    @Provide
    @SuppressWarnings("unused")
    private Arbitrary<String> validNames() {
        return Arbitraries.strings()
            .excludeChars(isoChars)
            .ofMinLength(User.NAME_MIN_LENGTH)
            .ofMaxLength(User.NAME_MAX_LENGTH)
            .filter(this::nameHasValidWhitespaces);
    }

    @Provide
    @SuppressWarnings("unused")
    private Arbitrary<String> namesWithInvalidLength() {
        return Arbitraries.strings()
            .excludeChars(isoChars)
            .ofMinLength(User.NAME_MAX_LENGTH + 1)
            .filter(this::nameHasValidWhitespaces);
    }

    @Provide
    @SuppressWarnings("unused")
    private Arbitrary<String> invalidNames() {
        List<String> names = List.of(
            " Jorel", "Samuel ", " Byces ", "Henrique  Mendonça",
            "João\nPedro", "Te\0ste", "123a\taa", "LittleDoge\f", "\baaa"
        );
        return Arbitraries.of(names);
    }

    @Provide
    @SuppressWarnings("unused")
    private Arbitrary<String> validPasswords() {
        return Arbitraries.strings()
            .excludeChars(isoChars)
            .ofMinLength(User.PASSWORD_MIN_LENGTH)
            .filter(password -> password.getBytes().length <= User.PASSWORD_MAX_BYTES);
    }

    @Provide
    @SuppressWarnings("unused")
    private Arbitrary<String> passwordsWithInvalidLength() {
        return Arbitraries.strings()
            .excludeChars(isoChars)
            .ofMaxLength(User.PASSWORD_MIN_LENGTH - 1)
            .filter(password -> password.getBytes().length <= User.PASSWORD_MAX_BYTES);
    }

    @Provide
    @SuppressWarnings("unused")
    private Arbitrary<String> passwordsWithInvalidCharacter() {
        return Arbitraries.strings()
            .ofMinLength(User.PASSWORD_MIN_LENGTH)
            .filter(password ->
                password.getBytes().length <= User.PASSWORD_MAX_BYTES &&
                password.chars().anyMatch(c -> Arrays.binarySearch(isoChars, (char)c) >= 0)
            );
    }

    @Provide
    @SuppressWarnings("unused")
    private Arbitrary<String> passwordsWithInvalidBytes() {
        return Arbitraries.strings()
            .excludeChars(isoChars)
            .filter(password -> password.getBytes().length > User.PASSWORD_MAX_BYTES);
    }

    private boolean nameHasValidWhitespaces(String name) {
        if (Character.isSpaceChar(name.charAt(0)) ||
            Character.isSpaceChar(name.charAt(name.length() - 1))) {
            return false;
        }
        int whitespaceSequence = 0;
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (!Character.isSpaceChar(c)) {
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