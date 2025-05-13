package br.com.votify.core.utils.validators;

import br.com.votify.core.domain.entities.polls.Poll;
import br.com.votify.core.domain.entities.polls.VoteOption;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public final class PollValidator {
    public static void validateFields(Poll poll, Instant now) throws VotifyException {
        validateTitle(poll.getTitle());
        validateDescription(poll.getDescription());
        validateVoteOptions(poll.getVoteOptions());
        validateChoiceLimitPerUser(poll.getChoiceLimitPerUser(), poll.getVoteOptions());
        validateStartEndDate(poll.getStartDate(), poll.getEndDate(), now);
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

    public static void validateStartEndDate(Instant startDate, Instant endDate, Instant now) throws VotifyException {
        if (Objects.isNull(startDate) || Objects.isNull(endDate)) {
            throw new VotifyException(VotifyErrorCode.POLL_DATE_EMPTY);
        }
        if (endDate.isBefore(startDate) || startDate.isBefore(now)) {
            throw new VotifyException(VotifyErrorCode.POLL_DATE_INVALID);
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
        if (voteOptions.size() < Poll.VOTE_OPTIONS_MIN ||
            voteOptions.size() > Poll.VOTE_OPTIONS_MAX) {
            throw new VotifyException(
                VotifyErrorCode.POLL_VOTE_OPTIONS_INVALID_LENGTH,
                Poll.VOTE_OPTIONS_MIN,
                Poll.VOTE_OPTIONS_MAX
            );
        }
        for (VoteOption voteOption : voteOptions) {
            VoteOptionValidator.validateFields(voteOption);
        }
    }

    public static void validateChoiceLimitPerUser(Integer choiceLimitPerUser, List<VoteOption> voteOptions) throws VotifyException {
        if (choiceLimitPerUser < Poll.VOTE_OPTIONS_MIN || choiceLimitPerUser > voteOptions.size()) {
            throw new VotifyException(VotifyErrorCode.POLL_INVALID_CHOICE_LIMIT_PER_USER);
        }
    }
}
