package br.com.votify.core.utils.exceptions;

import jakarta.annotation.Nullable;
import lombok.Getter;
import java.util.ResourceBundle;

@Getter
public class VotifyException extends Exception {
    private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("messages");

    private final VotifyErrorCode errorCode;

    public VotifyException(VotifyErrorCode errorCode) {
        super(MESSAGES.getString(errorCode.getMessageKey()));
        this.errorCode = errorCode;
    }

    public VotifyException(VotifyErrorCode errorCode, @Nullable Object... args) {
        super(String.format(MESSAGES.getString(errorCode.getMessageKey()), args));
        this.errorCode = errorCode;
    }
}
