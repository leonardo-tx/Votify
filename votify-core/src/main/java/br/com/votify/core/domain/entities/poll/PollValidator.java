package br.com.votify.core.domain.entities.poll;

import br.com.votify.core.domain.entities.vote.VoteOption;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;

import java.util.List;

public final class PollValidator {

    public static void validateFields(Poll poll) throws VotifyException {
        validateTitle(poll.getTitle());
        validateDescription(poll.getDescription());
        validateVoteOptions(poll.getVoteOptions());
        validateChoiceLimitPerUser(poll.getChoiceLimitPerUser(), poll.getVoteOptions());
    }

    public static void validateTitle(String title) throws VotifyException {
        if (title == null) {
            throw new VotifyException(VotifyErrorCode.POLL_TITLE_EMPTY);
        }
        if (title.length() < Poll.TITLE_MIN_LENGTH ||
                title.length() > Poll.TITLE_MAX_LENGTH) {
            throw new VotifyException(
                    VotifyErrorCode.POLL_TITLE_INVALID_LENGTH,
                    Poll.TITLE_MIN_LENGTH,
                    Poll.TITLE_MAX_LENGTH
            );
        }
    }

    public static void validateDescription(String description) throws VotifyException {
        if (description == null) {
            throw new VotifyException(VotifyErrorCode.POLL_DESCRIPTION_EMPTY);
        }
        if (description.length() < Poll.DESCRIPTION_MIN_LENGTH ||
                description.length() > Poll.DESCRIPTION_MAX_LENGTH) {
            throw new VotifyException(
                    VotifyErrorCode.POLL_DESCRIPTION_INVALID_LENGTH,
                    Poll.DESCRIPTION_MIN_LENGTH,
                    Poll.DESCRIPTION_MAX_LENGTH
            );
        }
    }

    public static void validateVoteOptions(List<VoteOption> voteOptions) throws VotifyException {
        if (voteOptions == null) {
            throw new VotifyException(VotifyErrorCode.POLL_VOTE_OPTIONS_EMPTY);
        }
        if (voteOptions.isEmpty() ||
                voteOptions.size() > Poll.VOTE_OPTIONS_MAX) {
            throw new VotifyException(
                    VotifyErrorCode.POLL_INVALID_VOTE_OPTIONS_NUM,
                    Poll.VOTE_OPTIONS_MIN,
                    Poll.VOTE_OPTIONS_MAX
            );
        }
    }

    private static void validateChoiceLimitPerUser(Integer choiceLimitPerUser, List<VoteOption> voteOptions) throws VotifyException {
        if (choiceLimitPerUser > voteOptions.size()) {
            throw new VotifyException(
                    VotifyErrorCode.POLL_INVALID_CHOICE_LIMIT_PER_USER
            );
        }
    }


}
