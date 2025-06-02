package br.com.votify.core.model.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class AuthTokensTest {
    @Test
    void testConstructValidAuthTokens() {
        AccessToken accessToken = mock(AccessToken.class);
        RefreshToken refreshToken = mock(RefreshToken.class);

        AuthTokens authTokens = assertDoesNotThrow(() -> new AuthTokens(accessToken, refreshToken));
        assertEquals(accessToken, authTokens.getAccessToken());
        assertEquals(refreshToken, authTokens.getRefreshToken());
    }

    @Test
    void testConstructAuthTokensWithNullAccessToken() {
        RefreshToken refreshToken = mock(RefreshToken.class);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new AuthTokens(null, refreshToken)
        );
        assertEquals("The access token must not be null.", exception.getMessage());
    }

    @Test
    void testConstructAuthTokensWithNullRefreshToken() {
        AccessToken accessToken = mock(AccessToken.class);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new AuthTokens(accessToken, null)
        );
        assertEquals("The refresh token must not be null.", exception.getMessage());
    }
}
