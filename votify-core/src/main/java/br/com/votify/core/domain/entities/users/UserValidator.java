package br.com.votify.core.domain.entities.users;

import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.core.utils.CharacterUtils;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class UserValidator {
    public static final Pattern EMAIL_PATTERN = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])");

    public static void validateFields(User user) throws VotifyException {
        validatePassword(user.getPassword());
        validateId(user.getId());
        validateRole(user.getRole());
        validateEmail(user.getEmail());
        validateFirstName(user.getFirstName());
        validateLastName(user.getLastName());
    }

    public static void validateNonNullFields(User user) throws VotifyException {
        if (user.getPassword() != null) {
            validatePassword(user.getPassword());
        }
        if (user.getId() != null) {
            validateId(user.getId());
        }
        if (user.getRole() != null) {
            validateRole(user.getRole());
        }
        if (user.getEmail() != null) {
            validateEmail(user.getEmail());
        }
        if (user.getFirstName() != null) {
            validateFirstName(user.getFirstName());
        }
        if (user.getLastName() != null) {
            validateLastName(user.getLastName());
        }
    }

    public static void validateId(String id) throws VotifyException {
        if (id == null) {
            throw new VotifyException(VotifyErrorCode.ID_EMPTY);
        }
        if (id.length() < User.ID_MIN_LENGTH ||
            id.length() > User.ID_MAX_LENGTH) {
            throw new VotifyException(
                VotifyErrorCode.ID_INVALID_LENGTH,
                User.ID_MIN_LENGTH,
                User.ID_MAX_LENGTH
            );
        }
        if (id.charAt(0) == '-' || id.charAt(id.length() - 1) == '-') {
            throw new VotifyException(VotifyErrorCode.ID_INVALID);
        }
        for (int i = 0; i < id.length(); i++) {
            char c = id.charAt(i);
            if (c == '-' || CharacterUtils.isOneByteDigit(c) || CharacterUtils.isOneByteLowercaseLetter(c)) {
                continue;
            }
            throw new VotifyException(VotifyErrorCode.ID_INVALID_CHARACTER);
        }
    }

    public static void validateRole(UserTypeEnum role) throws VotifyException {
        if (role == null) {
            throw new VotifyException(VotifyErrorCode.ROLE_EMPTY);
        }
    }

    public static void validateEmail(String email) throws VotifyException {
        if (email == null) {
            throw new VotifyException(VotifyErrorCode.EMAIL_EMPTY);
        }
        if (email.length() < User.EMAIL_MIN_LENGTH ||
            email.length() > User.EMAIL_MAX_LENGTH) {
            throw new VotifyException(
                VotifyErrorCode.EMAIL_INVALID_LENGTH,
                User.EMAIL_MIN_LENGTH,
                User.EMAIL_MAX_LENGTH
            );
        }
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        if (!matcher.matches()) {
            throw new VotifyException(VotifyErrorCode.EMAIL_INVALID);
        }
    }

    public static void validatePassword(String password) throws VotifyException {
        if (password == null) {
            throw new VotifyException(VotifyErrorCode.PASSWORD_EMPTY);
        }
        if (password.length() < User.PASSWORD_MIN_LENGTH) {
            throw new VotifyException(VotifyErrorCode.PASSWORD_INVALID_LENGTH, User.PASSWORD_MIN_LENGTH);
        }
        if (passwordHasInvalidChars(password)) {
            throw new VotifyException(VotifyErrorCode.PASSWORD_INVALID_CHARACTER);
        }
        byte[] bytes = password.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > User.PASSWORD_MAX_BYTES) {
            throw new VotifyException(VotifyErrorCode.PASSWORD_INVALID_BYTES);
        }
    }

    private static boolean passwordHasInvalidChars(String password) {
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (Character.isISOControl(c)) return true;
        }
        return false;
    }

    public static void validateFirstName(String firstName) throws VotifyException {
        if (firstName == null) {
            throw new VotifyException(VotifyErrorCode.FIRST_NAME_EMPTY);
        }
        if (firstName.length() < User.FIRST_NAME_MIN_LENGTH ||
            firstName.length() > User.FIRST_NAME_MAX_LENGTH) {
            throw new VotifyException(
                VotifyErrorCode.FIRST_NAME_INVALID_LENGTH,
                User.FIRST_NAME_MIN_LENGTH,
                User.FIRST_NAME_MAX_LENGTH
            );
        }
        if (nameHasInvalidChars(firstName)) {
            throw new VotifyException(VotifyErrorCode.FIRST_NAME_INVALID);
        }
    }

    public static void validateLastName(String lastName) throws VotifyException {
        if (lastName == null) {
            return;
        }
        if (lastName.length() < User.LAST_NAME_MIN_LENGTH ||
            lastName.length() > User.LAST_NAME_MAX_LENGTH) {
            throw new VotifyException(
                VotifyErrorCode.LAST_NAME_INVALID_LENGTH,
                User.LAST_NAME_MIN_LENGTH,
                User.LAST_NAME_MAX_LENGTH
            );
        }
        if (nameHasInvalidChars(lastName)) {
            throw new VotifyException(VotifyErrorCode.LAST_NAME_INVALID);
        }
    }

    private static boolean nameHasInvalidChars(String name) {
        if (Character.isSpaceChar(name.charAt(0)) ||
            Character.isSpaceChar(name.charAt(name.length() - 1))) {
            return true;
        }
        int whitespaceSequence = 0;
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isISOControl(c)) {
                return true;
            }
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
