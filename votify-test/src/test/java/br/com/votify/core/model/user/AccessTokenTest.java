package br.com.votify.core.model.user;

import br.com.votify.core.properties.user.UserProperties;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.SecretKey;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccessTokenTest {
    private static final SecretKey ACCESS_SECRET_KEY = Jwts.SIG.HS256.key().build();
    private static final SecretKey REFRESH_SECRET_KEY = Jwts.SIG.HS256.key().build();

    @Test
    void testConstructValidAccessTokenWithRefreshToken() {
        UserProperties userProperties = mock(UserProperties.class);
        when(userProperties.getAccessTokenExpirationSeconds()).thenReturn(10);

        Claims claims = mock(Claims.class);
        when(claims.get("userName")).thenReturn("user-name");
        when(claims.get("name")).thenReturn("name");
        when(claims.get("email")).thenReturn("test@example.com");

        RefreshToken refreshToken = mock(RefreshToken.class);
        when(refreshToken.getClaims(REFRESH_SECRET_KEY)).thenReturn(claims);
        when(refreshToken.isExpired()).thenReturn(false);

        AccessToken accessToken = assertDoesNotThrow(
                () -> new AccessToken(refreshToken, userProperties, ACCESS_SECRET_KEY, REFRESH_SECRET_KEY)
        );
        assertNotNull(accessToken.getCode());
        assertEquals(refreshToken.getUserId(), accessToken.getUserId());
    }

    @Test
    void testConstructAccessTokenWithExpiredRefreshToken() {
        UserProperties userProperties = mock(UserProperties.class);

        RefreshToken refreshToken = mock(RefreshToken.class);
        when(refreshToken.isExpired()).thenReturn(true);

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> new AccessToken(refreshToken, userProperties, ACCESS_SECRET_KEY, REFRESH_SECRET_KEY)
        );
        assertEquals(VotifyErrorCode.REFRESH_TOKEN_EXPIRED, exception.getErrorCode());
    }

    @Test
    void testConstructAccessTokenWithNullUserProperties() {
        RefreshToken refreshToken = mock(RefreshToken.class);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new AccessToken(refreshToken, null, ACCESS_SECRET_KEY, REFRESH_SECRET_KEY)
        );
        assertEquals("The user properties must not be null.", exception.getMessage());
    }

    @Test
    void testConstructAccessTokenWithNullAccessSecretKey() {
        RefreshToken refreshToken = mock(RefreshToken.class);
        UserProperties userProperties = mock(UserProperties.class);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new AccessToken(refreshToken, userProperties, null, REFRESH_SECRET_KEY)
        );
        assertEquals("The access secret key must not be null.", exception.getMessage());
    }

    @Test
    void testConstructAccessTokenWithNullRefreshSecretKey() {
        RefreshToken refreshToken = mock(RefreshToken.class);
        UserProperties userProperties = mock(UserProperties.class);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new AccessToken(refreshToken, userProperties, ACCESS_SECRET_KEY, null)
        );
        assertEquals("The refresh secret key must not be null.", exception.getMessage());
    }

    @Test
    void testConstructAccessTokenWithNullRefreshToken() {
        UserProperties userProperties = mock(UserProperties.class);
        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> new AccessToken(null, userProperties, ACCESS_SECRET_KEY, REFRESH_SECRET_KEY)
        );
        assertEquals(VotifyErrorCode.REFRESH_TOKEN_INVALID, exception.getErrorCode());
    }

    @Test
    void reconstructValidAccessToken() {
        Instant issuedAt = Instant.now();
        Instant expiration = issuedAt.plusSeconds(120);
        String token = Jwts.builder()
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiration))
                .subject("4")
                .claim("userName", "username")
                .claim("name", "name")
                .claim("email", "mail@example.com")
                .signWith(ACCESS_SECRET_KEY)
                .compact();

        AccessToken reconstructedAccessToken = assertDoesNotThrow(() ->
                new AccessToken(token, ACCESS_SECRET_KEY)
        );
        assertEquals(token, reconstructedAccessToken.getCode());
        assertEquals(4L, reconstructedAccessToken.getUserId());
    }

    @Test
    void reconstructAccessTokenWithNullSecretKey() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new AccessToken("Token", null)
        );
        assertEquals("The secret key must not be null.", exception.getMessage());
    }

    @Test
    void reconstructAccessTokenWithNullToken() {
        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> new AccessToken(null, ACCESS_SECRET_KEY)
        );
        assertEquals(VotifyErrorCode.ACCESS_TOKEN_INVALID, exception.getErrorCode());
    }

    @Test
    void reconstructAccessTokenWithInvalidToken() {
        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> new AccessToken("naiushduyq", ACCESS_SECRET_KEY)
        );
        assertEquals(VotifyErrorCode.ACCESS_TOKEN_INVALID, exception.getErrorCode());
    }

    @Test
    void reconstructAccessTokenWithExpiredToken() {
        Instant issuedAt = Instant.now().minusSeconds(10);
        Instant expiration = issuedAt.minusSeconds(1);
        String token = Jwts.builder()
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiration))
                .subject("4")
                .claim("userName", "username")
                .claim("name", "name")
                .claim("email", "mail@example.com")
                .signWith(ACCESS_SECRET_KEY)
                .compact();

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> new AccessToken(token, ACCESS_SECRET_KEY)
        );
        assertEquals(VotifyErrorCode.ACCESS_TOKEN_EXPIRED, exception.getErrorCode());
    }

    @Test
    void reconstructAccessTokenWithInvalidSubject() {
        Instant issuedAt = Instant.now();
        Instant expiration = issuedAt.plusSeconds(120);
        String token = Jwts.builder()
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiration))
                .subject("SUBJECT_INVALID")
                .claim("userName", "username")
                .claim("name", "name")
                .claim("email", "mail@example.com")
                .signWith(ACCESS_SECRET_KEY)
                .compact();

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> new AccessToken(token, ACCESS_SECRET_KEY)
        );
        assertEquals(VotifyErrorCode.ACCESS_TOKEN_INVALID, exception.getErrorCode());
    }

    @Test
    void reconstructAccessTokenWithNullUserName() {
        Instant issuedAt = Instant.now();
        Instant expiration = issuedAt.plusSeconds(120);
        String token = Jwts.builder()
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiration))
                .subject("4")
                .claim("name", "name")
                .claim("email", "mail@example.com")
                .signWith(ACCESS_SECRET_KEY)
                .compact();

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> new AccessToken(token, ACCESS_SECRET_KEY)
        );
        assertEquals(VotifyErrorCode.ACCESS_TOKEN_INVALID, exception.getErrorCode());
    }

    @Test
    void reconstructAccessTokenWithNullName() {
        Instant issuedAt = Instant.now();
        Instant expiration = issuedAt.plusSeconds(120);
        String token = Jwts.builder()
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiration))
                .subject("4")
                .claim("userName", "username")
                .claim("email", "mail@example.com")
                .signWith(ACCESS_SECRET_KEY)
                .compact();

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> new AccessToken(token, ACCESS_SECRET_KEY)
        );
        assertEquals(VotifyErrorCode.ACCESS_TOKEN_INVALID, exception.getErrorCode());
    }

    @Test
    void reconstructAccessTokenWithNullEmail() {
        Instant issuedAt = Instant.now();
        Instant expiration = issuedAt.plusSeconds(120);
        String token = Jwts.builder()
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiration))
                .subject("4")
                .claim("userName", "username")
                .claim("name", "name")
                .signWith(ACCESS_SECRET_KEY)
                .compact();

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> new AccessToken(token, ACCESS_SECRET_KEY)
        );
        assertEquals(VotifyErrorCode.ACCESS_TOKEN_INVALID, exception.getErrorCode());
    }

    @Test
    void reconstructAccessTokenWithNullIssuedAt() {
        Instant expiration = Instant.now().plusSeconds(120);
        String token = Jwts.builder()
                .expiration(Date.from(expiration))
                .subject("4")
                .claim("userName", "username")
                .claim("name", "name")
                .claim("email", "mail@example.com")
                .signWith(ACCESS_SECRET_KEY)
                .compact();

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> new AccessToken(token, ACCESS_SECRET_KEY)
        );
        assertEquals(VotifyErrorCode.ACCESS_TOKEN_INVALID, exception.getErrorCode());
    }

    @Test
    void reconstructAccessTokenWithNullExpiration() {
        Instant issuedAt = Instant.now();
        String token = Jwts.builder()
                .issuedAt(Date.from(issuedAt))
                .subject("4")
                .claim("userName", "username")
                .claim("name", "name")
                .claim("email", "mail@example.com")
                .signWith(ACCESS_SECRET_KEY)
                .compact();

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> new AccessToken(token, ACCESS_SECRET_KEY)
        );
        assertEquals(VotifyErrorCode.ACCESS_TOKEN_INVALID, exception.getErrorCode());
    }

    @Test
    void testParseUnsafe() {
        AccessToken accessToken = AccessToken.parseUnsafe("code", 5L);

        assertEquals("code", accessToken.getCode());
        assertEquals(5L, accessToken.getUserId());
    }
}
