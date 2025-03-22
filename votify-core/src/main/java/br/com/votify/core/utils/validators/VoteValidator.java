package br.com.votify.core.utils.validators;

import br.com.votify.core.domain.entities.polls.Poll;
import br.com.votify.core.domain.entities.polls.Vote;
import br.com.votify.core.domain.entities.polls.VoteOption;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;

import java.util.List;

public final class VoteValidator {
    public static void validateFields(Vote vote) throws VotifyException {
        validateUser(vote.getUser());
        validatePoll(vote.getPoll());
        validateValue(vote.getOption(), vote.getPoll().getVoteOptions(), vote.getPoll().getChoiceLimitPerUser());
    }

    public static void validateUser(User user) throws VotifyException {
        if (user == null) {
            throw new VotifyException(VotifyErrorCode.COMMON_UNAUTHORIZED);
        }
    }

    public static void validatePoll(Poll poll) throws VotifyException {
        if (poll == null) {
            throw new VotifyException(VotifyErrorCode.POLL_NOT_FOUND);
        }
    }

    public static void validateValue(int value, List<VoteOption> voteOptions, int choiceLimit) throws VotifyException {
        if (value == 0) {
            throw new VotifyException(VotifyErrorCode.POLL_VOTE_EMPTY);
        }

        int max = (1 << voteOptions.size()) - 1;
        if ((value & max) != value || Integer.bitCount(value) > choiceLimit) {
            throw new VotifyException(VotifyErrorCode.POLL_VOTE_INVALID);
        }
    }
}
