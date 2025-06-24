package br.com.votify.core.model.poll;

import br.com.votify.core.model.poll.field.Description;
import br.com.votify.core.model.poll.field.Title;
import br.com.votify.core.model.user.User;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PollTest {
    @Test
    void testConstructPollWithNullPollRegister() {
        User user = mock(User.class);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Poll(null, user)
        );
        assertEquals("The poll register must not be null.", exception.getMessage());
    }

    @Test
    void testConstructPollWithNullUser() {
        PollRegister pollRegister = mock(PollRegister.class);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Poll(pollRegister, null)
        );
        assertEquals("The responsible or it's id must not be null.", exception.getMessage());
    }

    @Test
    void testConstructPollWithNullUserId() {
        User user = mock(User.class);
        PollRegister pollRegister = mock(PollRegister.class);

        when(user.getId()).thenReturn(null);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Poll(pollRegister, user)
        );
        assertEquals("The responsible or it's id must not be null.", exception.getMessage());
    }

    @Test
    void testConstructValidPoll() throws VotifyException {
        User user = mock(User.class);
        List<VoteOptionRegister> voteOptionRegisters = List.of(
                new VoteOptionRegister("Teste 1"),
                new VoteOptionRegister("Teste 2"),
                new VoteOptionRegister("Teste 3")
        );
        PollRegister pollRegister = new PollRegister(
                "Título",
                "",
                null,
                Instant.now().plus(Duration.ofDays(7)),
                false,
                voteOptionRegisters,
                1
        );

        when(user.getId()).thenReturn(1L);
        Poll poll = assertDoesNotThrow(() -> new Poll(pollRegister, user));
        assertEquals(pollRegister.getTitle(), poll.getTitle());
        assertEquals(pollRegister.getDescription(), poll.getDescription());
        assertEquals(pollRegister.getStartDate(), poll.getStartDate());
        assertEquals(pollRegister.getEndDate(), poll.getEndDate());
        assertEquals(pollRegister.isUserRegistration(), poll.isUserRegistration());
        assertEquals(pollRegister.getChoiceLimitPerUser(), poll.getChoiceLimitPerUser());
        assertEquals(pollRegister.getVoteOptionsSize(), poll.getVoteOptionsSize());

        for (int i = 0; i < poll.getVoteOptionsSize(); i++) {
            assertEquals(
                    pollRegister.getVoteOptions().get(i).getName(),
                    poll.getVoteOptions().get(i).getName()
            );
        }
    }

    @Test
    void testCancelActivePoll() {
        User user = mock(User.class);
        PollRegister pollRegister = mock(PollRegister.class);

        when(pollRegister.getStartDate()).thenReturn(Instant.now());
        when(pollRegister.getEndDate()).thenReturn(Instant.now().plus(Duration.ofDays(7)));
        when(user.getId()).thenReturn(1L);
        Poll poll = assertDoesNotThrow(() -> new Poll(pollRegister, user));

        assertFalse(poll.hasEnded());
        assertDoesNotThrow(poll::cancel);
        assertTrue(poll.hasEnded());
    }

    @Test
    void testCancelNotStartedPoll() {
        User user = mock(User.class);

        PollRegister pollRegister = mock(PollRegister.class);
        when(pollRegister.getStartDate()).thenReturn(Instant.now().plus(Duration.ofDays(1)));
        when(pollRegister.getEndDate()).thenReturn(Instant.now().plus(Duration.ofDays(7)));

        when(user.getId()).thenReturn(1L);
        Poll poll = assertDoesNotThrow(() -> new Poll(pollRegister, user));

        assertFalse(poll.hasEnded());
        assertDoesNotThrow(poll::cancel);
        assertTrue(poll.hasEnded());
        assertEquals(poll.getStartDate(), poll.getEndDate());
    }

    @Test
    void testCancelEndedPollShouldThrow() {
        User user = mock(User.class);

        PollRegister pollRegister = mock(PollRegister.class);
        when(pollRegister.getStartDate()).thenReturn(Instant.now().minus(Duration.ofDays(7)));
        when(pollRegister.getEndDate()).thenReturn(Instant.now().minus(Duration.ofDays(1)));

        when(user.getId()).thenReturn(1L);
        Poll poll = assertDoesNotThrow(() -> new Poll(pollRegister, user));

        VotifyException exception = assertThrows(VotifyException.class, poll::cancel);
        assertEquals(VotifyErrorCode.POLL_CANNOT_CANCEL_FINISHED, exception.getErrorCode());
    }

    @Test
    void testRemoveResponsible() {
        User user = mock(User.class);
        PollRegister pollRegister = mock(PollRegister.class);

        when(user.getId()).thenReturn(1L);
        Poll poll = assertDoesNotThrow(() -> new Poll(pollRegister, user));

        assertEquals(1L, poll.getResponsibleId());
        poll.removeResponsible();
        assertNull(poll.getResponsibleId());
    }

    @Test
    void testVoteWithNullVoteRegister() {
        User user = mock(User.class);
        PollRegister pollRegister = mock(PollRegister.class);

        when(user.getId()).thenReturn(1L);
        Poll poll = assertDoesNotThrow(() -> new Poll(pollRegister, user));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> poll.vote(null)
        );
        assertEquals("The vote register must not be null.", exception.getMessage());
    }

    @Test
    void testVoteBeforeStart() {
        User user = mock(User.class);
        PollRegister pollRegister = mock(PollRegister.class);
        VoteRegister voteRegister = mock(VoteRegister.class);

        when(user.getId()).thenReturn(1L);
        when(pollRegister.getStartDate()).thenReturn(Instant.now().plus(Duration.ofDays(1)));
        when(pollRegister.getEndDate()).thenReturn(Instant.now().plus(Duration.ofDays(7)));
        Poll poll = assertDoesNotThrow(() -> new Poll(pollRegister, user));

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> poll.vote(voteRegister)
        );
        assertEquals(VotifyErrorCode.POLL_VOTE_OUT_OF_DATE_INTERVAL, exception.getErrorCode());
    }

    @Test
    void testVoteAfterEnd() {
        User user = mock(User.class);
        PollRegister pollRegister = mock(PollRegister.class);
        VoteRegister voteRegister = mock(VoteRegister.class);

        when(user.getId()).thenReturn(1L);
        when(pollRegister.getStartDate()).thenReturn(Instant.now().minus(Duration.ofDays(7)));
        when(pollRegister.getEndDate()).thenReturn(Instant.now().minus(Duration.ofDays(1)));
        Poll poll = assertDoesNotThrow(() -> new Poll(pollRegister, user));

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> poll.vote(voteRegister)
        );
        assertEquals(VotifyErrorCode.POLL_VOTE_OUT_OF_DATE_INTERVAL, exception.getErrorCode());
    }

    @Test
    void testValidVote() throws VotifyException {
        List<VoteOptionRegister> voteOptionRegisters = List.of(
                new VoteOptionRegister("Teste 1"),
                new VoteOptionRegister("Teste 2"),
                new VoteOptionRegister("Teste 3")
        );
        User user = mock(User.class);
        PollRegister pollRegister = mock(PollRegister.class);
        VoteRegister voteRegister = mock(VoteRegister.class);

        when(user.getId()).thenReturn(1L);
        when(voteRegister.getUserId()).thenReturn(1L);
        when(voteRegister.getPollId()).thenReturn(2L);
        when(pollRegister.getStartDate()).thenReturn(Instant.now());
        when(pollRegister.getEndDate()).thenReturn(Instant.now().plus(Duration.ofDays(1)));
        when(pollRegister.getVoteOptions()).thenReturn(voteOptionRegisters);
        when(pollRegister.getVoteOptionsSize()).thenReturn(voteOptionRegisters.size());
        when(voteRegister.getOption()).thenReturn(5);
        Poll poll = assertDoesNotThrow(() -> new Poll(pollRegister, user));

        Vote vote = assertDoesNotThrow(() -> poll.vote(voteRegister));
        assertEquals(5, vote.getOption());
        assertEquals(1L, vote.getUserId());
        assertEquals(2L, vote.getPollId());
        assertEquals(1, poll.getVoteOptions().get(0).getCount());
        assertEquals(0, poll.getVoteOptions().get(1).getCount());
        assertEquals(1, poll.getVoteOptions().get(2).getCount());
    }

    @Test
    void testRemoveNullVoteShouldThrow() {
        User user = mock(User.class);
        PollRegister pollRegister = mock(PollRegister.class);

        when(user.getId()).thenReturn(1L);
        Poll poll = assertDoesNotThrow(() -> new Poll(pollRegister, user));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> poll.removeVote(null)
        );
        assertEquals("The vote must not be null.", exception.getMessage());
    }

    @Test
    void testRemoveVoteBeforeStart() {
        User user = mock(User.class);
        PollRegister pollRegister = mock(PollRegister.class);
        Vote vote = mock(Vote.class);

        when(user.getId()).thenReturn(1L);
        when(pollRegister.getStartDate()).thenReturn(Instant.now().plus(Duration.ofDays(1)));
        when(pollRegister.getEndDate()).thenReturn(Instant.now().plus(Duration.ofDays(7)));
        Poll poll = assertDoesNotThrow(() -> new Poll(pollRegister, user));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> poll.removeVote(vote)
        );
        assertEquals("It's not possible to remove a vote on a out of date poll.", exception.getMessage());
    }

    @Test
    void testRemoveVoteAfterEnd() {
        User user = mock(User.class);
        PollRegister pollRegister = mock(PollRegister.class);
        Vote vote = mock(Vote.class);

        when(user.getId()).thenReturn(1L);
        when(pollRegister.getStartDate()).thenReturn(Instant.now().minus(Duration.ofDays(7)));
        when(pollRegister.getEndDate()).thenReturn(Instant.now().minus(Duration.ofDays(1)));
        Poll poll = assertDoesNotThrow(() -> new Poll(pollRegister, user));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> poll.removeVote(vote)
        );
        assertEquals("It's not possible to remove a vote on a out of date poll.", exception.getMessage());
    }

    @Test
    void testRemoveVote() throws VotifyException {
        List<VoteOptionRegister> voteOptionRegisters = List.of(
                new VoteOptionRegister("Teste 1"),
                new VoteOptionRegister("Teste 2"),
                new VoteOptionRegister("Teste 3")
        );
        User user = mock(User.class);
        PollRegister pollRegister = mock(PollRegister.class);
        Vote vote = mock(Vote.class);

        when(user.getId()).thenReturn(1L);
        when(pollRegister.getStartDate()).thenReturn(Instant.now());
        when(pollRegister.getEndDate()).thenReturn(Instant.now().plus(Duration.ofDays(1)));
        when(pollRegister.getVoteOptions()).thenReturn(voteOptionRegisters);
        when(pollRegister.getVoteOptionsSize()).thenReturn(voteOptionRegisters.size());
        when(vote.getOption()).thenReturn(5);
        Poll poll = assertDoesNotThrow(() -> new Poll(pollRegister, user));

        assertDoesNotThrow(() -> poll.removeVote(vote));
        assertEquals(-1, poll.getVoteOptions().get(0).getCount());
        assertEquals(0, poll.getVoteOptions().get(1).getCount());
        assertEquals(-1, poll.getVoteOptions().get(2).getCount());
    }

    @Test
    void testParseUnsafe() {
        Title title = mock(Title.class);
        Description description = mock(Description.class);
        Instant startDate = mock(Instant.class);
        Instant endDate = mock(Instant.class);
        boolean userRegistration = true;
        List<VoteOption> voteOptions = List.of();
        int choiceLimitPerUser = 1;
        Long responsibleId = 1L;

        Poll poll = Poll.parseUnsafe(
                2L,
                title,
                description,
                startDate,
                endDate,
                userRegistration,
                voteOptions,
                choiceLimitPerUser,
                responsibleId
        );
        assertEquals(2L, poll.getId());
        assertEquals(title, poll.getTitle());
        assertEquals(poll.getDescription(), description);
        assertEquals(startDate, poll.getStartDate());
        assertEquals(endDate, poll.getEndDate());
        assertEquals(userRegistration, poll.isUserRegistration());
        assertEquals(voteOptions, poll.getVoteOptions());
        assertEquals(choiceLimitPerUser, poll.getChoiceLimitPerUser());
        assertEquals(responsibleId, poll.getResponsibleId());
    }
}
