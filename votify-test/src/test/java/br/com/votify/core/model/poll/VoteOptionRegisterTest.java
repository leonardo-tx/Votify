package br.com.votify.core.model.poll;

import br.com.votify.core.utils.exceptions.VotifyException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VoteOptionRegisterTest {
    @Test
    void testValidVoteOption() {
        VoteOptionRegister voteOptionRegister = assertDoesNotThrow(
                () -> new VoteOptionRegister("Title")
        );
        assertEquals("Title", voteOptionRegister.getName().getValue());
    }

    @Test
    void testInvalidVoteOption() {
        assertThrows(VotifyException.class, () -> new VoteOptionRegister("t"));
    }
}
