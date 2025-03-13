package br.com.votify.core.utils.validators;

import br.com.votify.core.domain.entities.poll.Poll;
import br.com.votify.core.domain.entities.vote.VoteOption;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PollValidatorTest {

    private final LocalDateTime now = LocalDateTime.now();

    @Test
    void shouldThrowExceptionWhenTitleIsNull() {
        Poll poll = createValidPoll();
        poll.setTitle(null);

        VotifyException exception = assertThrows(VotifyException.class, () -> PollValidator.validateFields(poll, now));
        assertEquals(VotifyErrorCode.POLL_TITLE_EMPTY, exception.getErrorCode());
    }

    @Test
    void shouldThrowExceptionWhenTitleIsInvalidLength() {
        Poll poll = createValidPoll();
        poll.setTitle("a");

        VotifyException exception = assertThrows(VotifyException.class, () -> PollValidator.validateFields(poll, now));
        assertEquals(VotifyErrorCode.POLL_TITLE_INVALID_LENGTH, exception.getErrorCode());
    }

    @Test
    void shouldThrowExceptionWhenDescriptionIsNull() {
        Poll poll = createValidPoll();
        poll.setDescription(null);

        VotifyException exception = assertThrows(VotifyException.class, () -> PollValidator.validateFields(poll, now));
        assertEquals(VotifyErrorCode.POLL_DESCRIPTION_EMPTY, exception.getErrorCode());
    }

    @Test
    void shouldThrowExceptionWhenStartDateIsAfterEndDate() {
        Poll poll = createValidPoll();
        poll.setStartDate(now.plusDays(2));
        poll.setEndDate(now.plusDays(1));
        poll.setVoteOptions(List.of(new VoteOption(1L, "teste 1", poll)));
        poll.setChoiceLimitPerUser(1);

        VotifyException exception = assertThrows(VotifyException.class, () -> PollValidator.validateFields(poll, now));
        assertEquals(VotifyErrorCode.POLL_DATE_INVALID, exception.getErrorCode());
    }

    @Test
    void shouldThrowExceptionWhenChoiceLimitExceedsVoteOptions() {
        Poll poll = createValidPoll();
        poll.setVoteOptions(List.of(new VoteOption(1L, "teste", poll)));
        poll.setChoiceLimitPerUser(10);

        VotifyException exception = assertThrows(VotifyException.class, () -> PollValidator.validateFields(poll, now));
        assertEquals(VotifyErrorCode.POLL_INVALID_CHOICE_LIMIT_PER_USER, exception.getErrorCode());
    }

    private Poll createValidPoll() {
        return new Poll("Valid Title", "Valid Description", now.plusDays(1), now.plusDays(5), true, List.of(
                new VoteOption(),
                new VoteOption()
        ), 2);
    }

    @Test
    void validateVoteOptions_shouldThrowException_whenVoteOptionsIsNull() {
        VotifyException exception = assertThrows(VotifyException.class,
                () -> PollValidator.validateVoteOptions(null));

        assertEquals(VotifyErrorCode.POLL_VOTE_OPTIONS_EMPTY, exception.getErrorCode());
    }

    @Test
    void validateVoteOptions_shouldThrowException_whenVoteOptionsSizeIsInvalid() {
        Poll poll = new Poll();
        List<VoteOption> voteOptions = List.of();

        VotifyException exception = assertThrows(VotifyException.class,
                () -> PollValidator.validateVoteOptions(voteOptions));

        assertEquals(VotifyErrorCode.POLL_VOTE_OPTIONS_INVALID_LENGTH, exception.getErrorCode());
    }

    @Test
    void validateVoteOptions_shouldNotThrowException_whenVoteOptionsAreValid() {
        Poll poll = new Poll();
        List<VoteOption> voteOptions = Arrays.asList(new VoteOption(1L, "vote option 1", poll), new VoteOption(2L, "vote option 2", poll));

        assertDoesNotThrow(() -> PollValidator.validateVoteOptions(voteOptions));
    }
}
