package br.com.votify.core.model.user.field;

import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Email {
    public static final int MIN_LENGTH = 5;
    public static final int MAX_LENGTH = 254;
    public static final Pattern PATTERN = Pattern.compile("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");

    private String value;

    public Email(String email) throws VotifyException {
        if (email == null) {
            throw new VotifyException(VotifyErrorCode.EMAIL_EMPTY);
        }
        if (email.length() < MIN_LENGTH || email.length() > MAX_LENGTH) {
            throw new VotifyException(VotifyErrorCode.EMAIL_INVALID_LENGTH, MIN_LENGTH, MAX_LENGTH);
        }
        Matcher matcher = PATTERN.matcher(email);
        if (!matcher.matches()) {
            throw new VotifyException(VotifyErrorCode.EMAIL_INVALID);
        }
        value = email;
    }

    public static Email parseUnsafe(String email) {
        Email emailObj = new Email();
        emailObj.value = email;

        return emailObj;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Email email = (Email) o;
        return Objects.equals(value, email.value);
    }
}
