package br.com.votify.core.utils.exceptions;

import jakarta.annotation.Nullable;
import lombok.Getter;
import java.util.ResourceBundle;

@Getter
public class VotifyException extends Exception {
    private static final ResourceBundle messages = ResourceBundle.getBundle("messages");

    private final VotifyErrorCode errorCode;

    public VotifyException(VotifyErrorCode errorCode) {
        super(messages.getString(errorCode.getMessageKey()));
        this.errorCode = errorCode;
    }

    public VotifyException(VotifyErrorCode errorCode, @Nullable Object... args) {
        super(String.format(messages.getString(errorCode.getMessageKey()), args));
        this.errorCode = errorCode;
    }
}
