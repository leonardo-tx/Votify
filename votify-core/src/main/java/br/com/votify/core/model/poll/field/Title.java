package br.com.votify.core.model.poll.field;

import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Title {
    public static final int MIN_LENGTH = 5;
    public static final int MAX_LENGTH = 50;

    private String value;

    public Title(String title) throws VotifyException {
        if (title == null) {
            throw new VotifyException(VotifyErrorCode.POLL_TITLE_EMPTY);
        }
        if (title.length() < MIN_LENGTH || title.length() > MAX_LENGTH) {
            throw new VotifyException(VotifyErrorCode.POLL_TITLE_INVALID_LENGTH, MIN_LENGTH, MAX_LENGTH);
        }
        value = title;
    }

    public static Title parseUnsafe(String title) {
        Title titleObj = new Title();
        titleObj.value = title;

        return titleObj;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Title title = (Title) o;
        return Objects.equals(value, title.value);
    }
}
