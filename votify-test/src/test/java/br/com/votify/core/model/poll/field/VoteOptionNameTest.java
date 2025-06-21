package br.com.votify.core.model.poll.field;

import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import net.jqwik.api.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VoteOptionNameTest {
    @Test
    void shouldThrowExceptionWhenVoteOptionNameIsNull() {
        VotifyException exception = assertThrows(VotifyException.class, () -> new VoteOptionName(null));
        assertEquals(VotifyErrorCode.VOTE_OPTION_NAME_EMPTY, exception.getErrorCode());
    }

    @Test
    void testParseUnsafe() {
        VoteOptionName voteOptionName = VoteOptionName.parseUnsafe("teste");
        assertEquals("teste", voteOptionName.getValue());
    }

    @Property
    void shouldNotThrowWithValidVoteOptionNames(@ForAll("validVoteOptionNames") String voteOptionName) {
        assertDoesNotThrow(() -> new VoteOptionName(voteOptionName));
    }

    @Property
    void shouldThrowExceptionWhenVoteOptionNameIsShort(@ForAll("tooShortVoteOptionNames") String voteOptionName) {
        VotifyException exception = assertThrows(VotifyException.class, () -> new VoteOptionName(voteOptionName));
        assertEquals(VotifyErrorCode.VOTE_OPTION_NAME_INVALID_LENGTH, exception.getErrorCode());
    }

    @Property
    void shouldThrowExceptionWhenVoteOptionNameIsLong(@ForAll("tooLongVoteOptionNames") String voteOptionName) {
        VotifyException exception = assertThrows(VotifyException.class, () -> new VoteOptionName(voteOptionName));
        assertEquals(VotifyErrorCode.VOTE_OPTION_NAME_INVALID_LENGTH, exception.getErrorCode());
    }

    @Provide
    Arbitrary<String> validVoteOptionNames() {
        return Arbitraries.strings()
                .ofMinLength(VoteOptionName.MIN_LENGTH)
                .ofMaxLength(VoteOptionName.MAX_LENGTH);
    }

    @Provide
    Arbitrary<String> tooShortVoteOptionNames() {
        return Arbitraries.strings()
                .ofMinLength(0)
                .ofMaxLength(VoteOptionName.MIN_LENGTH - 1);
    }

    @Provide
    Arbitrary<String> tooLongVoteOptionNames() {
        return Arbitraries.strings()
                .ofMinLength(VoteOptionName.MAX_LENGTH + 1);
    }
}
