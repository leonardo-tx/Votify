package br.com.votify.core.service;

import br.com.votify.core.domain.entities.tokens.RefreshToken;
import br.com.votify.core.domain.entities.tokens.TokenProperties;
import br.com.votify.core.domain.entities.users.CommonUser;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.repository.RefreshTokenRepository;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TokenServiceTest {
    private static RefreshToken refreshToken;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private TokenService tokenService;

    private User user;

    @BeforeEach
    public void setupBeforeEach() {
        MockitoAnnotations.openMocks(this);
        tokenService = new TokenService(
            refreshTokenRepository,
            new TokenProperties(100, 200, "aiwjeiuqhjiuyhed8qy872yye8qu$%$%$%$#%$yudgsauyg")
        );
        user = new CommonUser(100L, "userName", "name", "email", "password");
    }

    @Test
    @Order(0)
    public void createRefreshToken() {
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(
            (invocation) -> invocation.getArgument(0)
        );

        refreshToken = assertDoesNotThrow(() -> tokenService.createRefreshToken(user));
        assertNotNull(refreshToken);
    }

    @Test
    @Order(1)
    public void createAccessToken() {
        when(refreshTokenRepository.existsById(refreshToken.getId())).thenReturn(true);

        String accessToken = assertDoesNotThrow(() -> tokenService.createAccessToken(refreshToken));
        Long userId = assertDoesNotThrow(() -> tokenService.getUserIdFromAccessToken(accessToken));

        assertNotNull(accessToken);
        assertEquals(100L, userId);
    }

    @Test
    @Order(1)
    public void createAccessTokenWithValidRefreshTokenButNonExistentInDatabase() {
        when(refreshTokenRepository.existsById(refreshToken.getId())).thenReturn(false);

        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> tokenService.createAccessToken(refreshToken)
        );
        assertEquals(VotifyErrorCode.REFRESH_TOKEN_EXPIRED, exception.getErrorCode());
    }

    @Test
    @Order(1)
    public void increaseRefreshTokenExpiration() {
        String id = refreshToken.getId();

        when(refreshTokenRepository.findById(id)).thenReturn(Optional.of(refreshToken));
        when(refreshTokenRepository.save(refreshToken)).thenReturn(refreshToken);

        Date oldExpiration = refreshToken.getExpiration();

        RefreshToken refreshToken = assertDoesNotThrow(
            () -> tokenService.increaseRefreshTokenExpiration(id)
        );
        assertTrue(refreshToken.getExpiration().after(oldExpiration));
    }

    @Test
    @Order(1)
    public void increaseRefreshTokenExpirationButNonExistentInDatabase() {
        String id = refreshToken.getId();

        when(refreshTokenRepository.findById(id)).thenReturn(Optional.empty());

        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> tokenService.increaseRefreshTokenExpiration(id)
        );
        assertEquals(VotifyErrorCode.REFRESH_TOKEN_EXPIRED, exception.getErrorCode());
    }

    @Test
    @Order(1)
    public void deleteRefreshTokenById() {
        String id = refreshToken.getId();
        assertDoesNotThrow(() -> tokenService.deleteRefreshTokenById(id));

        verify(refreshTokenRepository).deleteById(id);
    }
}
