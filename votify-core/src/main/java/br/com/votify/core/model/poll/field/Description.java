package br.com.votify.core.model.poll.field;

import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public final class Description {
    public static final int MIN_LENGTH = 0;
    public static final int MAX_LENGTH = 512;

    private String value;

    public Description(String description) throws VotifyException {
        if (description == null) {
            throw new VotifyException(VotifyErrorCode.POLL_DESCRIPTION_EMPTY);
        }
        if (description.length() > MAX_LENGTH) {
            throw new VotifyException(VotifyErrorCode.POLL_DESCRIPTION_INVALID_LENGTH, MIN_LENGTH, MAX_LENGTH);
        }
        value = description;
    }

    public static Description parseUnsafe(String description) {
        Description descriptionObj = new Description();
        descriptionObj.value = description;

        return descriptionObj;
    }
}
