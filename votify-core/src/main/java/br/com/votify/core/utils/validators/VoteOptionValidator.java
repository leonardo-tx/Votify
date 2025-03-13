package br.com.votify.core.utils.validators;

import br.com.votify.core.domain.entities.vote.VoteOption;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;

public final class VoteOptionValidator {
    public static void validateFields(VoteOption voteOption) throws VotifyException {
        validateName(voteOption.getName());
    }

    public static void validateName(String name) throws VotifyException {
        if (name == null) {
            throw new VotifyException(VotifyErrorCode.VOTE_OPTION_NAME_EMPTY);
        }
        if (name.length() < VoteOption.NAME_MIN_LENGTH || name.length() > VoteOption.NAME_MAX_LENGTH) {
            throw new VotifyException(
                VotifyErrorCode.VOTE_OPTION_NAME_INVALID_LENGTH,
                VoteOption.NAME_MIN_LENGTH,
                VoteOption.NAME_MAX_LENGTH
            );
        }
    }
}
