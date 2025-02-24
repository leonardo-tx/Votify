package br.com.votify.core.utils.exceptions;

import lombok.Getter;

@Getter
public enum VotifyErrorCode {
    USER_NAME_EMPTY("user.name.empty"),
    USER_NAME_INVALID_LENGTH("user.name.invalid.length"),
    USER_NAME_INVALID_CHARACTER("user.name.invalid.character"),
    USER_NAME_INVALID("user.name.invalid"),
    USER_NAME_ALREADY_EXISTS("user.name.already.exists"),
    EMAIL_EMPTY("email.empty"),
    EMAIL_INVALID_LENGTH("email.invalid.length"),
    EMAIL_INVALID("email.invalid"),
    EMAIL_ALREADY_EXISTS("email.already.exists"),
    PASSWORD_EMPTY("password.empty"),
    PASSWORD_INVALID_LENGTH("password.invalid.length"),
    PASSWORD_INVALID_CHARACTER("password.invalid.character"),
    PASSWORD_INVALID_BYTES("password.invalid.bytes"),
    NAME_EMPTY("name.empty"),
    NAME_INVALID_LENGTH("name.invalid.length"),
    NAME_INVALID("name.invalid"),
    USER_NOT_FOUND("user.not.found");

    private final String code;

    VotifyErrorCode(String code) {
        this.code = code;
    }
}
