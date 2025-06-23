package br.com.votify.core.model.poll;

import br.com.votify.core.model.poll.field.VoteOptionName;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import net.jqwik.api.*;
import net.jqwik.api.constraints.IntRange;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VoteOptionTest {
    @Test
    void testConstructVoteOptionWithNullPoll() throws VotifyException {
        VoteOptionRegister voteOptionRegister = new VoteOptionRegister("Option");
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new VoteOption(voteOptionRegister, null, 0)
        );
        assertEquals("The poll must not be null.", exception.getMessage());
    }

    @Test
    void testConstructVoteOptionWithNullVoteOptionRegister() {
        Poll poll = mock(Poll.class);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new VoteOption(null, poll, 0)
        );
        assertEquals("The vote option register must not be null.", exception.getMessage());
    }

    @Test
    void testHasBeenVoted() throws VotifyException {
        Poll poll = mock(Poll.class);
        when(poll.getId()).thenReturn(3L);

        VoteOptionRegister voteOptionRegister = new VoteOptionRegister("Option");
        VoteOption voteOption = assertDoesNotThrow(() -> new VoteOption(voteOptionRegister, poll, 0));

        Vote voteAtTheOption = mock(Vote.class);
        when(voteAtTheOption.getOption()).thenReturn(1);

        Vote voteOtherOption = mock(Vote.class);
        when(voteOtherOption.getOption()).thenReturn(2);

        assertTrue(voteOption.hasBeenVoted(voteAtTheOption));
        assertFalse(voteOption.hasBeenVoted(voteOtherOption));
    }

    @Test
    void testParseUnsafe() throws VotifyException {
        VoteOptionName voteOptionName = new VoteOptionName("option");
        VoteOption voteOption = VoteOption.parseUnsafe(voteOptionName, 5, 1, 3L);
        assertEquals(voteOptionName, voteOption.getName());
        assertEquals(5, voteOption.getCount());
        assertEquals(1, voteOption.getSequence());
        assertEquals(3L, voteOption.getPollId());
    }

    @Test
    void increaseCount() throws VotifyException {
        Poll poll = mock(Poll.class);
        when(poll.getId()).thenReturn(3L);

        VoteOptionRegister voteOptionRegister = new VoteOptionRegister("Option");
        VoteOption voteOption = assertDoesNotThrow(() -> new VoteOption(voteOptionRegister, poll, 0));

        voteOption.incrementCount();
        assertEquals(1, voteOption.getCount());
    }

    @Test
    void decreaseCount() throws VotifyException {
        Poll poll = mock(Poll.class);
        when(poll.getId()).thenReturn(3L);

        VoteOptionRegister voteOptionRegister = new VoteOptionRegister("Option");
        VoteOption voteOption = assertDoesNotThrow(() -> new VoteOption(voteOptionRegister, poll, 0));

        voteOption.decreaseCount();
        assertEquals(-1, voteOption.getCount());
    }

    @Property
    void testValidSequencesConstructVoteOption(
            @ForAll @IntRange(min = VoteOption.MIN_SEQUENCE, max = VoteOption.MAX_SEQUENCE) int sequence
    ) throws VotifyException {
        Poll poll = mock(Poll.class);
        when(poll.getId()).thenReturn(3L);

        VoteOptionRegister voteOptionRegister = new VoteOptionRegister("Option");
        VoteOption voteOption = assertDoesNotThrow(() -> new VoteOption(voteOptionRegister, poll, sequence));

        assertEquals(poll.getId(), voteOption.getPollId());
        assertEquals(voteOptionRegister.getName(), voteOption.getName());
        assertEquals(0, voteOption.getCount());
    }

    @Property
    void testInvalidSequencesConstructVoteOption(@ForAll("invalidSequences") int sequence) throws VotifyException {
        Poll poll = mock(Poll.class);
        when(poll.getId()).thenReturn(3L);

        VoteOptionRegister voteOptionRegister = new VoteOptionRegister("Option");
        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> new VoteOption(voteOptionRegister, poll, sequence)
        );
        assertEquals(VotifyErrorCode.POLL_VOTE_OPTIONS_INVALID_LENGTH, exception.getErrorCode());
    }

    @Provide
    Arbitrary<Integer> invalidSequences() {
        return Arbitraries.integers().filter(n -> n < VoteOption.MIN_SEQUENCE || n > VoteOption.MAX_SEQUENCE);
    }
}
