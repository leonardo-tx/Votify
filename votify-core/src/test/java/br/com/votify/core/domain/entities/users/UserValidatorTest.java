package br.com.votify.core.domain.entities.users;

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
    public void testNullId() {
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> UserValidator.validateId(null)
        );
        assertEquals(VotifyErrorCode.ID_EMPTY, exception.getErrorCode());
    }

    @Property
    public void testValidIds(@ForAll("validIds") String id) {
        assertDoesNotThrow(() -> UserValidator.validateId(id));
    }

    @Property
    public void testIdsWithInvalidLength(@ForAll("idsWithInvalidLength") String id) {
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> UserValidator.validateId(id)
        );
        assertEquals(VotifyErrorCode.ID_INVALID_LENGTH, exception.getErrorCode());
    }

    @Property
    public void testInvalidIds(@ForAll("invalidIds") String id) {
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> UserValidator.validateId(id)
        );
        assertEquals(VotifyErrorCode.ID_INVALID, exception.getErrorCode());
    }

    @Property
    public void testIdsWithInvalidCharacter(@ForAll("idsWithInvalidCharacter") String id) {
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> UserValidator.validateId(id)
        );
        assertEquals(VotifyErrorCode.ID_INVALID_CHARACTER, exception.getErrorCode());
    }

    @Test
    public void testNullEmail() {
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> UserValidator.validateEmail(null)
        );
        assertEquals(VotifyErrorCode.EMAIL_EMPTY, exception.getErrorCode());
    }

    @Test
    public void testNullRole() {
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> UserValidator.validateRole(null)
        );
        assertEquals(VotifyErrorCode.ROLE_EMPTY, exception.getErrorCode());
    }

    @Test
    public void testRoles() {
        UserTypeEnum[] roles = UserTypeEnum.values();
        for (UserTypeEnum role : roles) {
            assertDoesNotThrow(() -> UserValidator.validateRole(role));
        }
    }

    @Test
    public void testNullFirstName() {
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> UserValidator.validateFirstName(null)
        );
        assertEquals(VotifyErrorCode.FIRST_NAME_EMPTY, exception.getErrorCode());
    }

    @Property
    public void testValidFirstNames(@ForAll("validFirstNames") String firstName) {
        assertDoesNotThrow(() -> UserValidator.validateFirstName(firstName));
    }

    @Property
    public void testFirstNamesWithInvalidLength(@ForAll("firstNamesWithInvalidLength") String firstName) {
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> UserValidator.validateFirstName(firstName)
        );
        assertEquals(VotifyErrorCode.FIRST_NAME_INVALID_LENGTH, exception.getErrorCode());
    }

    @Property
    public void testInvalidFirstNames(@ForAll("invalidFirstNames") String firstName) {
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> UserValidator.validateFirstName(firstName)
        );
        assertEquals(VotifyErrorCode.FIRST_NAME_INVALID, exception.getErrorCode());
    }

    @Test
    public void testNullLastName() {
        assertDoesNotThrow(() -> UserValidator.validateLastName(null));
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
    private Arbitrary<String> validIds() {
        return Arbitraries.strings()
            .withChars(validIdChars)
            .ofMinLength(User.ID_MIN_LENGTH)
            .ofMaxLength(User.ID_MAX_LENGTH)
            .filter(id -> !id.startsWith("-") && !id.endsWith("-"));
    }

    @Provide
    private Arbitrary<String> idsWithInvalidLength() {
        return Arbitraries.strings()
            .withChars(validIdChars)
            .filter(id ->
                !id.startsWith("-") && !id.endsWith("-") &&
                (id.length() < User.ID_MIN_LENGTH || id.length() > User.ID_MAX_LENGTH)
            );
    }

    @Provide
    private Arbitrary<String> invalidIds() {
        return Arbitraries.strings()
            .withChars(validIdChars)
            .ofMinLength(User.ID_MIN_LENGTH)
            .ofMaxLength(User.ID_MAX_LENGTH)
            .filter(id -> id.startsWith("-") || id.endsWith("-"));
    }

    @Provide
    private Arbitrary<String> idsWithInvalidCharacter() {
        return Arbitraries.strings()
            .ofMinLength(User.ID_MIN_LENGTH)
            .ofMaxLength(User.ID_MAX_LENGTH)
            .filter(id ->
                !id.startsWith("-") && !id.endsWith("-") &&
                id.chars().anyMatch(c -> Arrays.binarySearch(validIdChars, (char)c) < 0)
            );
    }

    @Provide
    private Arbitrary<String> validFirstNames() {
        return Arbitraries.strings()
            .excludeChars(isoChars)
            .ofMinLength(User.FIRST_NAME_MIN_LENGTH)
            .ofMaxLength(User.FIRST_NAME_MAX_LENGTH)
            .filter(firstName -> !nameHasMisplacedWhitespaces(firstName));
    }

    @Provide
    private Arbitrary<String> firstNamesWithInvalidLength() {
        return Arbitraries.strings()
            .excludeChars(isoChars)
            .ofMinLength(User.FIRST_NAME_MAX_LENGTH + 1)
            .filter(firstName ->
                (firstName.length() < User.FIRST_NAME_MIN_LENGTH || firstName.length() > User.FIRST_NAME_MAX_LENGTH) &&
                !nameHasMisplacedWhitespaces(firstName)
            );
    }

    @Provide
    private Arbitrary<String> invalidFirstNames() {
        return Arbitraries.strings()
            .ofMinLength(User.FIRST_NAME_MIN_LENGTH)
            .ofMaxLength(User.FIRST_NAME_MAX_LENGTH)
            .filter(firstName -> nameHasMisplacedWhitespaces(firstName));
    }

    @Provide
    private Arbitrary<String> validPasswords() {
        return Arbitraries.strings()
            .excludeChars(isoChars)
            .ofMinLength(User.PASSWORD_MIN_LENGTH)
            .filter(password -> password.getBytes().length <= User.PASSWORD_MAX_BYTES);
    }

    @Provide
    private Arbitrary<String> passwordsWithInvalidLength() {
        return Arbitraries.strings()
            .excludeChars(isoChars)
            .ofMaxLength(User.PASSWORD_MIN_LENGTH - 1)
            .filter(password -> password.getBytes().length <= User.PASSWORD_MAX_BYTES);
    }

    @Provide
    private Arbitrary<String> passwordsWithInvalidCharacter() {
        return Arbitraries.strings()
            .ofMinLength(User.PASSWORD_MIN_LENGTH)
            .filter(password ->
                password.getBytes().length <= User.PASSWORD_MAX_BYTES &&
                password.chars().anyMatch(c -> Arrays.binarySearch(isoChars, (char)c) >= 0)
            );
    }

    @Provide
    private Arbitrary<String> passwordsWithInvalidBytes() {
        return Arbitraries.strings()
            .excludeChars(isoChars)
            .filter(password -> password.getBytes().length > User.PASSWORD_MAX_BYTES);
    }

    private boolean nameHasMisplacedWhitespaces(String name) {
        if (Character.isSpaceChar(name.charAt(0)) ||
            Character.isSpaceChar(name.charAt(name.length() - 1))) {
            return true;
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
                return true;
            }
        }
        return false;
    }
}