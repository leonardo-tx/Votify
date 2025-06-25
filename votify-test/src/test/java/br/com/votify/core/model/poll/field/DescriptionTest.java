package br.com.votify.core.model.poll.field;

import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import net.jqwik.api.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DescriptionTest {
    @Test
    void shouldThrowExceptionWhenDescriptionIsNull() {
        VotifyException exception = assertThrows(VotifyException.class, () -> new Description(null));
        assertEquals(VotifyErrorCode.POLL_DESCRIPTION_EMPTY, exception.getErrorCode());
    }

    @Test
    void testParseUnsafe() {
        Description description = Description.parseUnsafe("teste");
        assertEquals("teste", description.getValue());
    }

    @Property
    void shouldNotThrowWithValidDescriptions(@ForAll("validDescriptions") String description) {
        Description descriptionObject = assertDoesNotThrow(() -> new Description(description));
        assertEquals(description, descriptionObject.getValue());
    }

    @Property
    void shouldThrowExceptionWhenDescriptionIsLong(@ForAll("tooLongDescriptions") String description) {
        VotifyException exception = assertThrows(VotifyException.class, () -> new Description(description));
        assertEquals(VotifyErrorCode.POLL_DESCRIPTION_INVALID_LENGTH, exception.getErrorCode());
    }

    @Provide
    Arbitrary<String> validDescriptions() {
        return Arbitraries.strings()
                .ofMinLength(Description.MIN_LENGTH)
                .ofMaxLength(Description.MAX_LENGTH);
    }

    @Provide
    Arbitrary<String> tooLongDescriptions() {
        return Arbitraries.strings()
                .ofMinLength(Description.MAX_LENGTH + 1);
    }
}
