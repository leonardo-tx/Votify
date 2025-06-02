package br.com.votify.core.model.user.field;

import br.com.votify.core.utils.CharacterUtils;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import net.jqwik.api.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserNameTest {
    private final char[] validUserNameChars;

    public UserNameTest() {
        List<Character> validUserNameCharsList = new ArrayList<>();
        for (int i = 0; i < Character.MAX_VALUE; i++) {
            char c = (char)i;
            if (c == '-' || CharacterUtils.isOneByteDigit(c) || CharacterUtils.isOneByteLowercaseLetter(c)) {
                validUserNameCharsList.add(c);
            }
        }
        validUserNameChars = new char[validUserNameCharsList.size()];
        for (int i = 0; i < validUserNameCharsList.size(); i++) {
            validUserNameChars[i] = validUserNameCharsList.get(i);
        }
    }

    @Test
    void testEquals() {
        UserName userName1 = assertDoesNotThrow(() -> new UserName("nome"));
        UserName userName2 = assertDoesNotThrow(() -> new UserName("nome"));

        assertEquals(userName1, userName2);
        assertNotSame(userName1, userName2);
    }

    @Test
    void testNullUserName() {
        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> new UserName(null)
        );
        assertEquals(VotifyErrorCode.USER_NAME_EMPTY, exception.getErrorCode());
    }

    @Test
    void testParseUnsafe() {
        UserName userName = UserName.parseUnsafe("teste");
        assertEquals("teste", userName.getValue());
    }

    @Property
    void testValidUserNames(@ForAll("validUserNames") String userName) {
        assertDoesNotThrow(() -> new UserName(userName));
    }

    @Property
    void testUserNamesWithInvalidLength(@ForAll("userNamesWithInvalidLength") String userName) {
        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> new UserName(userName)
        );
        assertEquals(VotifyErrorCode.USER_NAME_INVALID_LENGTH, exception.getErrorCode());
    }

    @Property
    void testInvalidUserNames(@ForAll("invalidUserNames") String userName) {
        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> new UserName(userName)
        );
        assertEquals(VotifyErrorCode.USER_NAME_INVALID, exception.getErrorCode());
    }

    @Property
    void testUserNamesWithInvalidCharacter(@ForAll("userNamesWithInvalidCharacter") String userName) {
        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> new UserName(userName)
        );
        assertEquals(VotifyErrorCode.USER_NAME_INVALID_CHARACTER, exception.getErrorCode());
    }

    @Provide
    Arbitrary<String> validUserNames() {
        return Arbitraries.strings()
                .withChars(validUserNameChars)
                .ofMinLength(UserName.MIN_LENGTH)
                .ofMaxLength(UserName.MAX_LENGTH)
                .filter(userName -> !userName.startsWith("-") && !userName.endsWith("-"));
    }

    @Provide
    Arbitrary<String> userNamesWithInvalidLength() {
        return Arbitraries.strings()
                .withChars(validUserNameChars)
                .filter(userName ->
                        !userName.startsWith("-") && !userName.endsWith("-") &&
                                (userName.length() < UserName.MIN_LENGTH || userName.length() > UserName.MAX_LENGTH)
                );
    }

    @Provide
    Arbitrary<String> invalidUserNames() {
        return Arbitraries.strings()
                .withChars(validUserNameChars)
                .ofMinLength(UserName.MIN_LENGTH)
                .ofMaxLength(UserName.MAX_LENGTH)
                .filter(userName -> userName.startsWith("-") || userName.endsWith("-"));
    }

    @Provide
    Arbitrary<String> userNamesWithInvalidCharacter() {
        return Arbitraries.strings()
                .ofMinLength(UserName.MIN_LENGTH)
                .ofMaxLength(UserName.MAX_LENGTH)
                .filter(userName ->
                        !userName.startsWith("-") && !userName.endsWith("-") &&
                                userName.chars().anyMatch(c -> Arrays.binarySearch(validUserNameChars, (char)c) < 0)
                );
    }
}
