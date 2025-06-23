package br.com.votify.core.model.poll;

import br.com.votify.core.model.user.User;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VoteRegisterTest {
    @Test
    void testValidVote() {
        User user = mock(User.class);
        Poll poll = mock(Poll.class);

        when(poll.getChoiceLimitPerUser()).thenReturn(2);
        when(poll.getVoteOptionsSize()).thenReturn(5);

        when(user.getId()).thenReturn(1L);
        when(poll.getId()).thenReturn(3L);

        VoteRegister voteRegister = assertDoesNotThrow(() -> new VoteRegister(17, user, poll));
        assertEquals(1L, voteRegister.getUserId());
        assertEquals(3L, voteRegister.getPollId());
        assertEquals(17, voteRegister.getOption());
    }

    @Test
    void testEmptyVote() {
        User user = mock(User.class);
        Poll poll = mock(Poll.class);

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> new VoteRegister(0, user, poll)
        );
        assertEquals(VotifyErrorCode.POLL_VOTE_EMPTY, exception.getErrorCode());
    }

    @Test
    void testInvalidVotedOption() {
        User user = mock(User.class);
        Poll poll = mock(Poll.class);

        when(poll.getVoteOptionsSize()).thenReturn(5);

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> new VoteRegister(32, user, poll)
        );
        assertEquals(VotifyErrorCode.POLL_VOTE_INVALID, exception.getErrorCode());
    }

    @Test
    void testVotesOverflowLimit() {
        User user = mock(User.class);
        Poll poll = mock(Poll.class);

        when(poll.getChoiceLimitPerUser()).thenReturn(2);
        when(poll.getVoteOptionsSize()).thenReturn(5);

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> new VoteRegister(31, user, poll)
        );
        assertEquals(VotifyErrorCode.POLL_VOTE_INVALID, exception.getErrorCode());
    }

    @Test
    void testVoteWithNullUser() {
        Poll poll = mock(Poll.class);
        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> new VoteRegister(17, null, poll)
        );
        assertEquals(VotifyErrorCode.COMMON_UNAUTHORIZED, exception.getErrorCode());
    }

    @Test
    void testVoteWithNullPoll() {
        User user = mock(User.class);
        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> new VoteRegister(17, user, null)
        );
        assertEquals(VotifyErrorCode.POLL_NOT_FOUND, exception.getErrorCode());
    }
}
