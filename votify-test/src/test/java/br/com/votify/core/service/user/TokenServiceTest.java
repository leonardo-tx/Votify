package br.com.votify.core.service.user;

import br.com.votify.core.model.user.AccessToken;
import br.com.votify.core.model.user.RefreshToken;
import br.com.votify.core.model.user.User;
import br.com.votify.core.model.user.field.Email;
import br.com.votify.core.model.user.field.Name;
import br.com.votify.core.model.user.field.UserName;
import br.com.votify.core.properties.user.UserProperties;
import br.com.votify.core.repository.user.RefreshTokenRepository;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class TokenServiceTest {
    @Mock
    private UserProperties userProperties;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private TokenService tokenService;

    @BeforeEach
    void setupBeforeEach() {
        when(userProperties.getRefreshTokenSecret()).thenReturn("S#G5WUERGHWY3Hgu#$$#%yg7236t7uweh");

        tokenService = new TokenService(refreshTokenRepository, userProperties);
    }

    @Test
    void createRefreshToken() throws VotifyException {
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(user.getEmail()).thenReturn(new Email("mail@mail.com"));
        when(user.getUserName()).thenReturn(new UserName("user-name"));
        when(user.getName()).thenReturn(new Name("Name"));

        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(
            invocation -> invocation.getArgument(0)
        );
        when(userProperties.getRefreshTokenExpirationSeconds()).thenReturn(120);
        RefreshToken refreshToken = assertDoesNotThrow(() -> tokenService.createRefreshToken(user));

        assertNotNull(refreshToken);
    }

    @Test
    void createAccessToken() throws VotifyException {
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(user.getEmail()).thenReturn(new Email("mail@mail.com"));
        when(user.getUserName()).thenReturn(new UserName("user-name"));
        when(user.getName()).thenReturn(new Name("Name"));

        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(
                invocation -> invocation.getArgument(0)
        );
        when(userProperties.getRefreshTokenExpirationSeconds()).thenReturn(120);
        RefreshToken refreshToken = assertDoesNotThrow(() -> tokenService.createRefreshToken(user));

        when(userProperties.getAccessTokenExpirationSeconds()).thenReturn(60);
        AccessToken accessToken = assertDoesNotThrow(() -> tokenService.createAccessToken(refreshToken));

        assertNotNull(accessToken);
        assertEquals(user.getId(), accessToken.getUserId());
    }

    @Test
    void createAccessTokenWithExpiredRefreshToken() throws VotifyException {
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(user.getEmail()).thenReturn(new Email("mail@mail.com"));
        when(user.getUserName()).thenReturn(new UserName("user-name"));
        when(user.getName()).thenReturn(new Name("Name"));

        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(
                invocation -> invocation.getArgument(0)
        );
        when(userProperties.getRefreshTokenExpirationSeconds()).thenReturn(-10);
        RefreshToken refreshToken = assertDoesNotThrow(() -> tokenService.createRefreshToken(user));

        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> tokenService.createAccessToken(refreshToken)
        );
        assertEquals(VotifyErrorCode.REFRESH_TOKEN_EXPIRED, exception.getErrorCode());
    }

    @Test
    void increaseRefreshTokenExpiration() throws VotifyException {
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(user.getEmail()).thenReturn(new Email("mail@mail.com"));
        when(user.getUserName()).thenReturn(new UserName("user-name"));
        when(user.getName()).thenReturn(new Name("Name"));

        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(
                invocation -> invocation.getArgument(0)
        );
        when(userProperties.getRefreshTokenExpirationSeconds()).thenReturn(60);
        RefreshToken refreshToken = assertDoesNotThrow(() -> tokenService.createRefreshToken(user));
        String code = refreshToken.getCode();

        when(refreshTokenRepository.findByCode(code)).thenReturn(Optional.of(refreshToken));
        when(refreshTokenRepository.save(refreshToken)).thenReturn(refreshToken);

        Instant oldExpiration = refreshToken.getExpiration();

        assertDoesNotThrow(
            () -> tokenService.increaseRefreshTokenExpiration(code)
        );
        assertTrue(refreshToken.getExpiration().isAfter(oldExpiration));
    }

    @Test
    void increaseRefreshTokenExpirationButNonExistentInDatabase() {
        String code = "nqhewuiyqhe";
        when(refreshTokenRepository.findByCode(code)).thenReturn(Optional.empty());

        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> tokenService.increaseRefreshTokenExpiration(code)
        );
        assertEquals(VotifyErrorCode.REFRESH_TOKEN_EXPIRED, exception.getErrorCode());
    }

    @Test
    void deleteRefreshTokenById() throws VotifyException {
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(user.getEmail()).thenReturn(new Email("mail@mail.com"));
        when(user.getUserName()).thenReturn(new UserName("user-name"));
        when(user.getName()).thenReturn(new Name("Name"));

        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(
                invocation -> invocation.getArgument(0)
        );
        when(userProperties.getRefreshTokenExpirationSeconds()).thenReturn(60);
        RefreshToken refreshToken = assertDoesNotThrow(() -> tokenService.createRefreshToken(user));

        String code = refreshToken.getCode();
        assertDoesNotThrow(() -> tokenService.deleteRefreshToken(code));

        verify(refreshTokenRepository).deleteByCode(code);
    }

    @Test
    void revokeAllRefreshTokens() {
        User user = mock(User.class);
        tokenService.revokeAllRefreshTokens(user);

        verify(refreshTokenRepository).deleteAllByUser(user);
    }

    @Test
    void retrieveAccessToken() throws VotifyException {
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(user.getEmail()).thenReturn(new Email("mail@mail.com"));
        when(user.getUserName()).thenReturn(new UserName("user-name"));
        when(user.getName()).thenReturn(new Name("Name"));

        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(
                invocation -> invocation.getArgument(0)
        );
        when(userProperties.getRefreshTokenExpirationSeconds()).thenReturn(120);
        RefreshToken refreshToken = assertDoesNotThrow(() -> tokenService.createRefreshToken(user));

        when(userProperties.getAccessTokenExpirationSeconds()).thenReturn(60);
        AccessToken accessToken = assertDoesNotThrow(() -> tokenService.createAccessToken(refreshToken));

        AccessToken retrievedAccessToken = tokenService.retrieveAccessToken(accessToken.getCode());
        assertEquals(accessToken.getCode(), retrievedAccessToken.getCode());
    }
}
