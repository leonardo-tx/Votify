package br.com.votify.core.model.poll.field;

import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VoteOptionName {
    public static final int MIN_LENGTH = 3;
    public static final int MAX_LENGTH = 30;

    private String value;

    public VoteOptionName(String name) throws VotifyException {
        if (name == null) {
            throw new VotifyException(VotifyErrorCode.VOTE_OPTION_NAME_EMPTY);
        }
        if (name.length() < VoteOption.NAME_MIN_LENGTH || name.length() > VoteOption.NAME_MAX_LENGTH) {
            throw new VotifyException(VotifyErrorCode.VOTE_OPTION_NAME_INVALID_LENGTH, MIN_LENGTH, MAX_LENGTH);
        }
        value = name;
    }

    public static VoteOptionName parseUnsafe(String name) {
        VoteOptionName nameObj = new VoteOptionName();
        nameObj.value = name;

        return nameObj;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VoteOptionName name = (VoteOptionName) o;
        return Objects.equals(value, name.value);
    }
}
