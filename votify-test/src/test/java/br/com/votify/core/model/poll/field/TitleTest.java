package br.com.votify.core.model.poll.field;

import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import net.jqwik.api.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TitleTest {
    @Test
    void shouldThrowExceptionWhenTitleIsNull() {
        VotifyException exception = assertThrows(VotifyException.class, () -> new Title(null));
        assertEquals(VotifyErrorCode.POLL_TITLE_EMPTY, exception.getErrorCode());
    }

    @Test
    void testParseUnsafe() {
        Title title = Title.parseUnsafe("teste");
        assertEquals("teste", title.getValue());
    }

    @Property
    void shouldNotThrowWithValidTitles(@ForAll("validTitles") String title) {
        assertDoesNotThrow(() -> new Title(title));
    }

    @Property
    void shouldThrowExceptionWhenTitleIsShort(@ForAll("tooShortTitles") String title) {
        VotifyException exception = assertThrows(VotifyException.class, () -> new Title(title));
        assertEquals(VotifyErrorCode.POLL_TITLE_INVALID_LENGTH, exception.getErrorCode());
    }

    @Property
    void shouldThrowExceptionWhenTitleIsLong(@ForAll("tooLongTitles") String title) {
        VotifyException exception = assertThrows(VotifyException.class, () -> new Title(title));
        assertEquals(VotifyErrorCode.POLL_TITLE_INVALID_LENGTH, exception.getErrorCode());
    }

    @Provide
    Arbitrary<String> validTitles() {
        return Arbitraries.strings()
                .ofMinLength(Title.MIN_LENGTH)
                .ofMaxLength(Title.MAX_LENGTH);
    }

    @Provide
    Arbitrary<String> tooShortTitles() {
        return Arbitraries.strings()
                .ofMinLength(0)
                .ofMaxLength(Title.MIN_LENGTH - 1);
    }

    @Provide
    Arbitrary<String> tooLongTitles() {
        return Arbitraries.strings()
                .ofMinLength(Title.MAX_LENGTH + 1);
    }
}
