package br.com.votify.core.model.poll;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VoteTest {
    @Test
    void testValidVoteConstruct() {
        VoteRegister voteRegister = mock(VoteRegister.class);
        when(voteRegister.getOption()).thenReturn(0);
        when(voteRegister.getPollId()).thenReturn(1L);
        when(voteRegister.getUserId()).thenReturn(2L);

        Vote vote = assertDoesNotThrow(() -> new Vote(voteRegister));
        assertEquals(voteRegister.getOption(), vote.getOption());
        assertEquals(voteRegister.getPollId(), vote.getPollId());
        assertEquals(voteRegister.getUserId(), vote.getUserId());
    }

    @Test
    void testVoteConstructWithNullVoteRegister() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Vote(null)
        );
        assertEquals("The vote register must not be null.", exception.getMessage());
    }

    @Test
    void testParseUnsafe() {
        Vote vote = Vote.parseUnsafe(3, 4L, 2L);
        assertEquals(3, vote.getOption());
        assertEquals(4L, vote.getPollId());
        assertEquals(2L, vote.getUserId());
    }
}
