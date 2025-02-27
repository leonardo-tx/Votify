package br.com.votify.core.utils.validators;

import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.core.utils.CharacterUtils;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class UserValidator {
    public static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
    public static void validateFields(User user) throws VotifyException {
        validatePassword(user.getPassword());
        validateUserName(user.getUserName());
        validateEmail(user.getEmail());
        validateName(user.getName());
    }

    public static void validateUserName(String userName) throws VotifyException {
        if (userName == null) {
            throw new VotifyException(VotifyErrorCode.USER_NAME_EMPTY);
        }
        if (userName.length() < User.USER_NAME_MIN_LENGTH ||
            userName.length() > User.USER_NAME_MAX_LENGTH) {
            throw new VotifyException(
                VotifyErrorCode.USER_NAME_INVALID_LENGTH,
                User.USER_NAME_MIN_LENGTH,
                User.USER_NAME_MAX_LENGTH
            );
        }
        if (userName.charAt(0) == '-' || userName.charAt(userName.length() - 1) == '-') {
            throw new VotifyException(VotifyErrorCode.USER_NAME_INVALID);
        }
        for (int i = 0; i < userName.length(); i++) {
            char c = userName.charAt(i);
            if (c == '-' || CharacterUtils.isOneByteDigit(c) || CharacterUtils.isOneByteLowercaseLetter(c)) {
                continue;
            }
            throw new VotifyException(VotifyErrorCode.USER_NAME_INVALID_CHARACTER);
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

    public static void validateName(String name) throws VotifyException {
        if (name == null) {
            throw new VotifyException(VotifyErrorCode.NAME_EMPTY);
        }
        if (name.length() < User.NAME_MIN_LENGTH ||
            name.length() > User.NAME_MAX_LENGTH) {
            throw new VotifyException(
                VotifyErrorCode.NAME_INVALID_LENGTH,
                User.NAME_MIN_LENGTH,
                User.NAME_MAX_LENGTH
            );
        }
        if (nameHasInvalidChars(name)) {
            throw new VotifyException(VotifyErrorCode.NAME_INVALID);
        }
    }

    private static boolean nameHasInvalidChars(String name) {
        if (name.charAt(0) == ' ' || name.charAt(name.length() - 1) == ' ') {
            return true;
        }
        int whitespaceSequence = 0;
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isISOControl(c)) {
                return true;
            }
            if (c != ' ') {
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
