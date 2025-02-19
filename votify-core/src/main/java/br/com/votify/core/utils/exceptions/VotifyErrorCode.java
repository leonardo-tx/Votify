package br.com.votify.core.utils.exceptions;

import lombok.Getter;

@Getter
public enum VotifyErrorCode {
    ID_EMPTY("id.empty"),
    ID_INVALID_LENGTH("id.invalid.length"),
    ID_INVALID_CHARACTER("id.invalid.character"),
    ID_INVALID("id.invalid"),
    EMAIL_EMPTY("email.empty"),
    EMAIL_INVALID_LENGTH("email.invalid.length"),
    EMAIL_INVALID("email.invalid"),
    EMAIL_ALREADY_EXISTS("email.already.exists"),
    PASSWORD_EMPTY("password.empty"),
    PASSWORD_INVALID_LENGTH("password.invalid.length"),
    PASSWORD_INVALID_CHARACTER("password.invalid.character"),
    PASSWORD_INVALID_BYTES("password.invalid.bytes"),
    FIRST_NAME_EMPTY("first.name.empty"),
    FIRST_NAME_INVALID_LENGTH("first.name.invalid.length"),
    FIRST_NAME_INVALID("first.name.invalid"),
    LAST_NAME_EMPTY("last.name.empty"),
    LAST_NAME_INVALID_LENGTH("last.name.invalid.length"),
    LAST_NAME_INVALID("last.name.invalid"),
    ROLE_EMPTY("role.empty"),
    USER_NOT_FOUND("user.not.found");

    private final String code;

    VotifyErrorCode(String code) {
        this.code = code;
    }
}
