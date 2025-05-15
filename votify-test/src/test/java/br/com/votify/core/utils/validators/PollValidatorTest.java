package br.com.votify.core.utils.validators;

import br.com.votify.core.domain.entities.polls.Poll;
import br.com.votify.core.domain.entities.polls.VoteOption;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PollValidatorTest {

    private final Instant now = Instant.now();

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
        poll.setStartDate(now.plus(Duration.ofDays(2)));
        poll.setEndDate(now.plus(Duration.ofDays(1)));
        poll.setVoteOptions(List.of(new VoteOption(null, "teste 1", 0, poll)));
        poll.setChoiceLimitPerUser(1);

        VotifyException exception = assertThrows(VotifyException.class, () -> PollValidator.validateFields(poll, now));
        assertEquals(VotifyErrorCode.POLL_DATE_INVALID, exception.getErrorCode());
    }

    @Test
    void shouldThrowExceptionWhenChoiceLimitExceedsVoteOptions() {
        Poll poll = createValidPoll();
        poll.setVoteOptions(List.of(new VoteOption(null, "teste", 0, poll)));
        poll.setChoiceLimitPerUser(10);

        VotifyException exception = assertThrows(VotifyException.class, () -> PollValidator.validateFields(poll, now));
        assertEquals(VotifyErrorCode.POLL_INVALID_CHOICE_LIMIT_PER_USER, exception.getErrorCode());
    }

    private Poll createValidPoll() {
        return Poll.builder()
                .title("Valid Title")
                .description("Valid Description")
                .startDate(now.plus(Duration.ofDays(1)))
                .endDate(now.plus(Duration.ofDays(5)))
                .userRegistration(true)
                .voteOptions(List.of(new VoteOption(), new VoteOption()))
                .choiceLimitPerUser(2)
                .build();
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
        List<VoteOption> voteOptions = Arrays.asList(
                new VoteOption(null, "vote option 1", 0, poll),
                new VoteOption(null, "vote option 2", 0, poll)
        );

        assertDoesNotThrow(() -> PollValidator.validateVoteOptions(voteOptions));
    }
}
