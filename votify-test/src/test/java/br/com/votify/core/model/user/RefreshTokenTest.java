package br.com.votify.core.model.user;

import br.com.votify.core.model.user.field.Email;
import br.com.votify.core.model.user.field.Name;
import br.com.votify.core.model.user.field.UserName;
import br.com.votify.core.properties.user.UserProperties;
import br.com.votify.core.utils.exceptions.VotifyException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.SecretKey;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenTest {
    private static final SecretKey REFRESH_SECRET_KEY = Jwts.SIG.HS256.key().build();

    @Test
    void testConstructValidRefreshToken() throws VotifyException {
        User user = mock(User.class);
        when(user.getUserName()).thenReturn(new UserName("username"));
        when(user.getName()).thenReturn(new Name("Name"));
        when(user.getEmail()).thenReturn(new Email("mail@example.com"));
        when(user.getId()).thenReturn(2L);

        UserProperties userProperties = mock(UserProperties.class);
        when(userProperties.getRefreshTokenExpirationSeconds()).thenReturn(120);

        RefreshToken refreshToken = assertDoesNotThrow(
                () -> new RefreshToken(user, userProperties, REFRESH_SECRET_KEY)
        );
        assertEquals(user.getId(), refreshToken.getUserId());
        assertFalse(refreshToken.isExpired());
        assertNotNull(refreshToken.getCode());
        assertNotNull(refreshToken.getExpiration());

        Claims claims = refreshToken.getClaims(REFRESH_SECRET_KEY);
        assertEquals(user.getEmail().getValue(), claims.get("email"));
        assertEquals(user.getUserName().getValue(), claims.get("userName"));
        assertEquals(user.getName().getValue(), claims.get("name"));
        assertEquals("2", claims.getSubject());
    }

    @Test
    void testConstructWithNullUser() {
        UserProperties userProperties = mock(UserProperties.class);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new RefreshToken(null, userProperties, REFRESH_SECRET_KEY)
        );
        assertEquals("The user or it's id must not be null.", exception.getMessage());
    }

    @Test
    void testConstructWithNullUserId() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(null);

        UserProperties userProperties = mock(UserProperties.class);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new RefreshToken(user, userProperties, REFRESH_SECRET_KEY)
        );
        assertEquals("The user or it's id must not be null.", exception.getMessage());
    }

    @Test
    void testConstructWithNullUserProperties() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new RefreshToken(user, null, REFRESH_SECRET_KEY)
        );
        assertEquals("The user properties must not be null.", exception.getMessage());
    }

    @Test
    void testConstructWithNullSecretKey() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);

        UserProperties userProperties = mock(UserProperties.class);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new RefreshToken(user, userProperties, null)
        );
        assertEquals("The secret key must not be null.", exception.getMessage());
    }

    @Test
    void testIncreaseExpiration() throws VotifyException {
        User user = mock(User.class);
        when(user.getUserName()).thenReturn(new UserName("username"));
        when(user.getName()).thenReturn(new Name("Name"));
        when(user.getEmail()).thenReturn(new Email("mail@example.com"));
        when(user.getId()).thenReturn(2L);

        UserProperties userProperties = mock(UserProperties.class);
        when(userProperties.getRefreshTokenExpirationSeconds()).thenReturn(-10);

        RefreshToken refreshToken = assertDoesNotThrow(
                () -> new RefreshToken(user, userProperties, REFRESH_SECRET_KEY)
        );

        assertTrue(refreshToken.isExpired());
        when(userProperties.getRefreshTokenExpirationSeconds()).thenReturn(120);

        refreshToken.increaseExpiration(userProperties);

        assertFalse(refreshToken.isExpired());
    }

    @Test
    void testParseUnsafe() {
        Instant expiration = Instant.now();
        RefreshToken refreshToken = RefreshToken.parseUnsafe("code", expiration, 9L);

        assertEquals(expiration, refreshToken.getExpiration());
        assertEquals("code", refreshToken.getCode());
        assertEquals(9L, refreshToken.getUserId());
    }
}
