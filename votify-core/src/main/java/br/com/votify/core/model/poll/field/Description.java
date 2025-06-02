package br.com.votify.core.model.poll.field;

import br.com.votify.core.model.poll.Poll;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Description {
    public static final int MIN_LENGTH = 0;
    public static final int MAX_LENGTH = 512;

    private String value;

    public Description(String description) throws VotifyException {
        if (description == null) {
            throw new VotifyException(VotifyErrorCode.POLL_DESCRIPTION_EMPTY);
        }
        if (description.length() < MIN_LENGTH || description.length() > MAX_LENGTH) {
            throw new VotifyException(VotifyErrorCode.POLL_DESCRIPTION_INVALID_LENGTH, MIN_LENGTH, MAX_LENGTH);
        }
    }

    public static Description parseUnsafe(String description) {
        Description descriptionObj = new Description();
        descriptionObj.value = description;

        return descriptionObj;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Description description = (Description) o;
        return Objects.equals(value, description.value);
    }
}
