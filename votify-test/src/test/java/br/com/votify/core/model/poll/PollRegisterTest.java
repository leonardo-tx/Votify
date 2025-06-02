package br.com.votify.core.model.poll;

import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PollRegisterTest {
    private final Instant futureDate = Instant.now().plusSeconds(3600);
    private final Instant pastDate = Instant.now().minusSeconds(3600);
    private final List<VoteOptionRegister> validVoteOptions = List.of(
            new VoteOptionRegister("Option 1"),
            new VoteOptionRegister("Option 2")
    );

    PollRegisterTest() throws VotifyException {
    }

    @Test
    void constructor_shouldSetDefaultStartDateToNowWhenNull() throws VotifyException {
        PollRegister poll = new PollRegister(
                "Title",
                "Description",
                null,
                futureDate,
                false,
                validVoteOptions,
                1
        );

        assertTrue(poll.getStartDate().isAfter(Instant.now().minusSeconds(1)));
        assertTrue(poll.getStartDate().isBefore(Instant.now().plusSeconds(1)));
    }

    @Test
    void constructor_shouldThrowExceptionWhenEndDateIsNull() {
        VotifyException exception = assertThrows(VotifyException.class, () ->
                new PollRegister(
                        "Title",
                        "Description",
                        Instant.now().plusSeconds(5),
                        null,
                        false,
                        validVoteOptions,
                        1
                )
        );
        assertEquals(VotifyErrorCode.POLL_DATE_EMPTY, exception.getErrorCode());
    }

    @Test
    void constructor_shouldThrowExceptionWhenEndDateIsBeforeStartDate() {
        VotifyException exception = assertThrows(VotifyException.class, () ->
                new PollRegister(
                        "Title",
                        "Description",
                        futureDate,
                        Instant.now().plusSeconds(5),
                        false,
                        validVoteOptions,
                        1
                )
        );
        assertEquals(VotifyErrorCode.POLL_DATE_INVALID, exception.getErrorCode());
    }

    @Test
    void constructor_shouldThrowExceptionWhenStartDateIsInPast() {
        VotifyException exception = assertThrows(VotifyException.class, () ->
                new PollRegister(
                        "Title",
                        "Description",
                        pastDate,
                        Instant.now().plusSeconds(5),
                        false,
                        validVoteOptions,
                        1
                )
        );
        assertEquals(VotifyErrorCode.POLL_DATE_INVALID, exception.getErrorCode());
    }

    @Test
    void constructor_shouldThrowExceptionWhenVoteOptionsSizeIsBelowMinimum() {
        List<VoteOptionRegister> emptyOptions = List.of();

        VotifyException exception = assertThrows(VotifyException.class, () ->
                new PollRegister(
                        "Title",
                        "Description",
                        Instant.now().plusSeconds(5),
                        futureDate,
                        false,
                        emptyOptions,
                        1
                )
        );
        assertEquals(VotifyErrorCode.POLL_VOTE_OPTIONS_INVALID_LENGTH, exception.getErrorCode());
    }

    @Test
    void constructor_shouldThrowExceptionWhenVoteOptionsSizeIsAboveMaximum() throws VotifyException {
        List<VoteOptionRegister> tooManyOptions = List.of(
                new VoteOptionRegister("Option 1"),
                new VoteOptionRegister("Option 2"),
                new VoteOptionRegister("Option 3"),
                new VoteOptionRegister("Option 4"),
                new VoteOptionRegister("Option 5"),
                new VoteOptionRegister("Option 6")
        );

        VotifyException exception = assertThrows(VotifyException.class, () ->
                new PollRegister(
                        "Title",
                        "Description",
                        Instant.now().plusSeconds(5),
                        futureDate,
                        false,
                        tooManyOptions,
                        1
                )
        );
        assertEquals(VotifyErrorCode.POLL_VOTE_OPTIONS_INVALID_LENGTH, exception.getErrorCode());
    }

    @Test
    void constructor_shouldThrowExceptionWhenChoiceLimitIsBelowMinimum() {
        VotifyException exception = assertThrows(VotifyException.class, () ->
                new PollRegister(
                        "Title",
                        "Description",
                        Instant.now().plusSeconds(5),
                        futureDate,
                        false,
                        validVoteOptions,
                        0
                )
        );
        assertEquals(VotifyErrorCode.POLL_INVALID_CHOICE_LIMIT_PER_USER, exception.getErrorCode());
    }

    @Test
    void constructor_shouldThrowExceptionWhenChoiceLimitIsAboveVoteOptionsSize() {
        VotifyException exception = assertThrows(VotifyException.class, () ->
                new PollRegister(
                        "Title",
                        "Description",
                        Instant.now().plusSeconds(5),
                        futureDate,
                        false,
                        validVoteOptions,
                        3
                )
        );
        assertEquals(VotifyErrorCode.POLL_INVALID_CHOICE_LIMIT_PER_USER, exception.getErrorCode());
    }

    @Test
    void constructor_shouldInitializeFieldsCorrectly() throws VotifyException {
        Instant startDate = Instant.now().plusSeconds(5);
        PollRegister poll = new PollRegister(
                "Test Title",
                "Test Description",
                startDate,
                futureDate,
                true,
                validVoteOptions,
                2
        );

        assertEquals("Test Title", poll.getTitle().getValue());
        assertEquals("Test Description", poll.getDescription().getValue());
        assertEquals(startDate, poll.getStartDate());
        assertEquals(futureDate, poll.getEndDate());
        assertTrue(poll.isUserRegistration());
        assertEquals(validVoteOptions, poll.getVoteOptions());
        assertEquals(2, poll.getChoiceLimitPerUser());
    }

    @Test
    void getVoteOptionsSize_shouldReturnCorrectSize() throws VotifyException {
        PollRegister poll = new PollRegister(
                "Title",
                "Description",
                Instant.now().plusSeconds(5),
                futureDate,
                false,
                validVoteOptions,
                1
        );

        assertEquals(validVoteOptions.size(), poll.getVoteOptionsSize());
    }
}
