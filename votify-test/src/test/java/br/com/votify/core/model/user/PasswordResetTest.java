package br.com.votify.core.model.user;

import br.com.votify.core.model.user.field.Email;
import br.com.votify.core.model.user.field.Name;
import br.com.votify.core.model.user.field.UserName;
import br.com.votify.core.properties.user.UserProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PasswordResetTest {
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
    public void testIsExpired_WhenExpired() {
        when(userProperties.getResetPasswordConfirmationExpirationMinutes()).thenReturn(-1);

        PasswordReset passwordReset = new PasswordReset(user, userProperties);
        assertTrue(passwordReset.isExpired());
    }

    @Test
    public void testIsExpired_WhenNotExpired() {
        when(userProperties.getResetPasswordConfirmationExpirationMinutes()).thenReturn(1);

        PasswordReset passwordReset = new PasswordReset(user, userProperties);
        assertFalse(passwordReset.isExpired());
    }

    @Test
    public void testWhenUserIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new PasswordReset(null, userProperties)
        );
        assertEquals("The user or it's id must not be null.", exception.getMessage());
    }

    @Test
    public void testWhenUserIdIsNull() {
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
                () -> new PasswordReset(userWithoutId, userProperties)
        );
        assertEquals("The user or it's id must not be null.", exception.getMessage());
    }

    @Test
    public void testWhenUserPropertiesIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new PasswordReset(user, null)
        );
        assertEquals("The user properties must not be null.", exception.getMessage());
    }
}