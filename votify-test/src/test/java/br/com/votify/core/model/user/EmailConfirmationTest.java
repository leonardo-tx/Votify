package br.com.votify.core.model.user;

import br.com.votify.core.model.user.field.ConfirmationCode;
import br.com.votify.core.model.user.field.Email;
import br.com.votify.core.model.user.field.Name;
import br.com.votify.core.model.user.field.UserName;
import br.com.votify.core.properties.user.UserProperties;
import br.com.votify.core.utils.exceptions.VotifyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailConfirmationTest {
    @Mock
    private UserProperties userProperties;

    private User user;

    @BeforeEach
    void setupBeforeEach() {
        user = User.parseUnsafe(
                1L,
                Email.parseUnsafe("123@gmail.com"),
                UserName.parseUnsafe("abcd"),
                Name.parseUnsafe("ABCD"),
                "1729837187923",
                UserRole.ADMIN,
                true
        );
    }

    @Test
    void testIsExpired_WhenExpired() throws VotifyException {
        when(userProperties.getEmailConfirmationExpirationMinutes()).thenReturn(-1);

        EmailConfirmation emailConfirmation = new EmailConfirmation(new Email("321@gmail.com"), user, userProperties);
        assertTrue(emailConfirmation.isExpired());
    }

    @Test
    void testIsExpired_WhenNotExpired() {
        when(userProperties.getEmailConfirmationExpirationMinutes()).thenReturn(1);

        EmailConfirmation emailConfirmation = new EmailConfirmation(null, user, userProperties);
        assertFalse(emailConfirmation.isExpired());
    }

    @Test
    void testWhenUserIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new EmailConfirmation(null, null, userProperties)
        );
        assertEquals("The user or it's id must not be null.", exception.getMessage());
    }

    @Test
    void testWhenUserIdIsNull() {
        User userWithoutId = User.parseUnsafe(
                null,
                Email.parseUnsafe("123@gmail.com"),
                UserName.parseUnsafe("abcd"),
                Name.parseUnsafe("ABCD"),
                "1729837187923",
                UserRole.ADMIN,
                true
        );
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new EmailConfirmation(null, userWithoutId, userProperties)
        );
        assertEquals("The user or it's id must not be null.", exception.getMessage());
    }

    @Test
    void testWhenUserPropertiesIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new EmailConfirmation(null, user, null)
        );
        assertEquals("The user properties must not be null.", exception.getMessage());
    }

    @Test
    void testParseUnsafe() throws VotifyException {
        ConfirmationCode confirmationCode = new ConfirmationCode();
        Instant expiration = Instant.now();
        Email newEmail = new Email("1234@gmail.com");
        EmailConfirmation emailConfirmation = EmailConfirmation.parseUnsafe(
                confirmationCode, 
                newEmail,
                expiration,
                2L
        );
        assertEquals(confirmationCode, emailConfirmation.getCode());
        assertEquals(2L, emailConfirmation.getUserId());
        assertEquals(expiration, emailConfirmation.getExpiration());
        assertEquals(newEmail, emailConfirmation.getNewEmail());
    }
}
