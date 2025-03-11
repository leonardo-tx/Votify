package br.com.votify.core.utils.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum VotifyErrorCode {
    USER_NAME_EMPTY("user.name.empty", HttpStatus.BAD_REQUEST),
    USER_NAME_INVALID_LENGTH("user.name.invalid.length", HttpStatus.BAD_REQUEST),
    USER_NAME_INVALID_CHARACTER("user.name.invalid.character", HttpStatus.BAD_REQUEST),
    USER_NAME_INVALID("user.name.invalid", HttpStatus.BAD_REQUEST),
    USER_NAME_ALREADY_EXISTS("user.name.already.exists", HttpStatus.BAD_REQUEST),
    EMAIL_EMPTY("email.empty", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID_LENGTH("email.invalid.length", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID("email.invalid", HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_EXISTS("email.already.exists", HttpStatus.BAD_REQUEST),
    PASSWORD_EMPTY("password.empty", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID_LENGTH("password.invalid.length", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID_CHARACTER("password.invalid.character", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID_BYTES("password.invalid.bytes", HttpStatus.BAD_REQUEST),
    NAME_EMPTY("name.empty", HttpStatus.BAD_REQUEST),
    NAME_INVALID_LENGTH("name.invalid.length", HttpStatus.BAD_REQUEST),
    NAME_INVALID("name.invalid", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND("user.not.found", HttpStatus.BAD_REQUEST),
    REFRESH_TOKEN_EXPIRED("refresh.token.expired", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_INVALID("refresh.token.invalid", HttpStatus.BAD_REQUEST),
    ACCESS_TOKEN_EXPIRED("access.token.expired", HttpStatus.UNAUTHORIZED),
    ACCESS_TOKEN_INVALID("access.token.invalid", HttpStatus.BAD_REQUEST),
    LOGIN_UNAUTHORIZED("login.unauthorized", HttpStatus.UNAUTHORIZED),
    LOGIN_ALREADY_LOGGED("login.already.logged", HttpStatus.BAD_REQUEST),
    COMMON_UNAUTHORIZED("common.unauthorized", HttpStatus.UNAUTHORIZED),
    INTERNAL("internal", HttpStatus.INTERNAL_SERVER_ERROR),
    BAD_REQUEST("bad.request", HttpStatus.BAD_REQUEST),
    ENDPOINT_NOT_FOUND("endpoint.not.found", HttpStatus.BAD_REQUEST),
    POLL_TITLE_ALREADY_EXISTS_FOR_THIS_USER("poll.title.duplicated", HttpStatus.BAD_REQUEST),
    POLL_TITLE_INVALID_LENGTH("poll.title.invalid.length", HttpStatus.BAD_REQUEST),
    POLL_TITLE_EMPTY("poll.title.empty", HttpStatus.BAD_REQUEST),
    POLL_DESCRIPTION_EMPTY("poll.description.empty", HttpStatus.BAD_REQUEST),
    POLL_DESCRIPTION_INVALID_LENGTH("poll.description.invalid.length", HttpStatus.BAD_REQUEST),
    POLL_VOTE_OPTIONS_EMPTY("poll.vote.options.empty", HttpStatus.BAD_REQUEST),
    POLL_INVALID_VOTE_OPTIONS_NUM("poll.vote.options.invalid.num", HttpStatus.BAD_REQUEST),
    POLL_INVALID_CHOICE_LIMIT_PER_USER("poll.choice.limit.invalid", HttpStatus.BAD_REQUEST),
    POLL_DATE_EMPTY("poll.date.empty", HttpStatus.BAD_REQUEST),
    POLL_DATE_INVALID("poll.date.invalid", HttpStatus.BAD_REQUEST);

    private final String code;
    private final HttpStatusCode httpStatusCode;

    VotifyErrorCode(String code, HttpStatusCode httpStatusCode) {
        this.code = code;
        this.httpStatusCode = httpStatusCode;
    }
}
