package br.com.votify.core.model.user.field;

import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public final class Password {
    public static final int MIN_LENGTH = 8;
    public static final int MAX_BYTES = 72;

    private String value;

    public Password(String password) throws VotifyException {
        if (password == null) {
            throw new VotifyException(VotifyErrorCode.PASSWORD_EMPTY);
        }
        if (password.length() < MIN_LENGTH) {
            throw new VotifyException(VotifyErrorCode.PASSWORD_INVALID_LENGTH, MIN_LENGTH);
        }
        if (passwordHasInvalidChars(password)) {
            throw new VotifyException(VotifyErrorCode.PASSWORD_INVALID_CHARACTER);
        }
        byte[] bytes = password.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > MAX_BYTES) {
            throw new VotifyException(VotifyErrorCode.PASSWORD_INVALID_BYTES, MAX_BYTES);
        }
        value = password;
    }

    private static boolean passwordHasInvalidChars(String password) {
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (Character.isISOControl(c)) return true;
        }
        return false;
    }

    public static Password parseUnsafe(String password) {
        Password passwordObj = new Password();
        passwordObj.value = password;

        return passwordObj;
    }
}
