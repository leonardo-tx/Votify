package br.com.votify.core.domain.entities.poll;

import br.com.votify.core.domain.entities.vote.VoteOption;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public final class PollValidator {

    public static void validateFields(Poll poll) throws VotifyException {
        validateTitle(poll.getTitle());
        validateDescription(poll.getDescription());
        validateChoiceLimitPerUser(poll.getChoiceLimitPerUser(), poll.getVoteOptions());
        valdiateStartEndDate(poll.getStartDate(),poll.getEndDate());
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

    public static void valdiateStartEndDate(LocalDateTime startDate, LocalDateTime endDate) throws VotifyException {
        if(Objects.isNull(startDate) || Objects.isNull(endDate)) {
            throw new VotifyException(VotifyErrorCode.POLL_DATE_EMPTY);
        }

        if(endDate.isBefore(startDate)) {
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

    private static void validateChoiceLimitPerUser(Integer choiceLimitPerUser, List<VoteOption> voteOptions) throws VotifyException {
        if (Objects.nonNull(voteOptions) && choiceLimitPerUser > voteOptions.size()) {
            throw new VotifyException(
                    VotifyErrorCode.POLL_INVALID_CHOICE_LIMIT_PER_USER
            );
        }
    }


}
