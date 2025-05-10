package br.com.votify.core.utils.validators;

import br.com.votify.core.domain.entities.polls.Poll;
import br.com.votify.core.domain.entities.polls.VoteOption;
import br.com.votify.core.utils.CharacterUtils;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public final class PollValidator {
    private PollValidator() {}

    public static void validateFields(Poll poll, LocalDateTime now) throws VotifyException {
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
        int length = title.length();
        if (length < Poll.TITLE_MIN_LENGTH || length > Poll.TITLE_MAX_LENGTH) {
            throw new VotifyException(
                    VotifyErrorCode.POLL_TITLE_INVALID_LENGTH,
                    Poll.TITLE_MIN_LENGTH,
                    Poll.TITLE_MAX_LENGTH
            );
        }
        for (char c : title.toCharArray()) {
            if (CharacterUtils.isOneByteLowercaseLetter(c)
                    || CharacterUtils.isOneByteUppercaseLetter(c)
                    || CharacterUtils.isOneByteDigit(c)
                    || Character.isWhitespace(c)
                    || ".,!?'-".indexOf(c) >= 0) {
                continue;
            }
            throw new VotifyException(VotifyErrorCode.POLL_TITLE_INVALID_CHARACTER);
        }
    }

    public static void validateDescription(String description) throws VotifyException {
        if (description == null) {
            throw new VotifyException(VotifyErrorCode.POLL_DESCRIPTION_EMPTY);
        }
        int length = description.length();
        if (length < Poll.DESCRIPTION_MIN_LENGTH || length > Poll.DESCRIPTION_MAX_LENGTH) {
            throw new VotifyException(
                    VotifyErrorCode.POLL_DESCRIPTION_INVALID_LENGTH,
                    Poll.DESCRIPTION_MIN_LENGTH,
                    Poll.DESCRIPTION_MAX_LENGTH
            );
        }
        for (char c : description.toCharArray()) {
            if (Character.isISOControl(c)) {
                throw new VotifyException(VotifyErrorCode.POLL_DESCRIPTION_INVALID_CHARACTER);
            }
        }
    }

    public static void validateVoteOptions(List<VoteOption> voteOptions) throws VotifyException {
        if (voteOptions == null) {
            throw new VotifyException(VotifyErrorCode.POLL_VOTE_OPTIONS_EMPTY);
        }
        int size = voteOptions.size();
        if (size < Poll.VOTE_OPTIONS_MIN || size > Poll.VOTE_OPTIONS_MAX) {
            throw new VotifyException(
                    VotifyErrorCode.POLL_VOTE_OPTIONS_INVALID_LENGTH,
                    Poll.VOTE_OPTIONS_MIN,
                    Poll.VOTE_OPTIONS_MAX
            );
        }
        for (VoteOption vo : voteOptions) {
            VoteOptionValidator.validateFields(vo);
        }
    }

    public static void validateChoiceLimitPerUser(Integer choiceLimitPerUser, List<VoteOption> voteOptions) throws VotifyException {
        if (choiceLimitPerUser == null || choiceLimitPerUser < 1 || choiceLimitPerUser > voteOptions.size()) {
            throw new VotifyException(VotifyErrorCode.POLL_INVALID_CHOICE_LIMIT_PER_USER);
        }
    }

    public static void validateStartEndDate(LocalDateTime startDate, LocalDateTime endDate, LocalDateTime now) throws VotifyException {
        if (Objects.isNull(startDate) || Objects.isNull(endDate)) {
            throw new VotifyException(VotifyErrorCode.POLL_DATE_EMPTY);
        }
        if (endDate.isBefore(startDate) || startDate.isBefore(now)) {
            throw new VotifyException(VotifyErrorCode.POLL_DATE_INVALID);
        }
    }
}