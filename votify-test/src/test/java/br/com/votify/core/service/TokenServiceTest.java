package br.com.votify.core.service;

import br.com.votify.core.domain.entities.tokens.RefreshToken;
import br.com.votify.core.domain.entities.tokens.TokenProperties;
import br.com.votify.core.domain.entities.users.CommonUser;
import br.com.votify.core.repository.RefreshTokenRepository;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TokenServiceTest {
    private static final List<RefreshToken> refreshTokens = new ArrayList<>();
    private static TokenService tokenService;

    @BeforeAll
    public static void prepareBeforeAll() {
        RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);

        when(refreshTokenRepository.existsById(any(String.class))).thenAnswer((invocation) -> {
            String id = invocation.getArgument(0);
            for (RefreshToken refreshToken : refreshTokens) {
                if (Objects.equals(refreshToken.getId(), id)) return true;
            }
            return false;
        });
        when(refreshTokenRepository.findById(any(String.class))).thenAnswer((invocation) -> {
            String id = invocation.getArgument(0);
            for (RefreshToken refreshToken : refreshTokens) {
                if (Objects.equals(refreshToken.getId(), id)) return Optional.of(refreshToken);
            }
            return Optional.empty();
        });
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer((invocation) -> {
            RefreshToken createdRefreshToken = invocation.getArgument(0);
            for (int i = 0; i < refreshTokens.size(); i++) {
                RefreshToken refreshToken = refreshTokens.get(i);
                if (Objects.equals(refreshToken.getId(), createdRefreshToken.getId())) {
                    refreshTokens.set(i, createdRefreshToken);
                    return createdRefreshToken;
                }
            }
            refreshTokens.add(createdRefreshToken);
            return createdRefreshToken;
        });
        tokenService = new TokenService(
            refreshTokenRepository,
            new TokenProperties(100, 100, "secrehajghdja#$@#$@shdiufhsuyhfghuyhegst")
        );
    }

    @Test
    @Order(0)
    public void createRefreshToken() {
        RefreshToken refreshToken = assertDoesNotThrow(
            () -> tokenService.createRefreshToken(
                new CommonUser(100L, "userName", "name", "email", "password")
            )
        );
        assertNotNull(refreshToken);
    }

    @Test
    @Order(1)
    public void createAccessToken() {
        RefreshToken refreshToken = refreshTokens.get(0);
        String accessToken = assertDoesNotThrow(
            () -> tokenService.createAccessToken(refreshToken)
        );
        Long userId = assertDoesNotThrow(() -> tokenService.getUserIdFromAccessToken(accessToken));

        assertNotNull(accessToken);
        assertEquals(100L, userId);
    }

    @Test
    @Order(1)
    public void createAccessTokenWithValidRefreshTokenButNonExistentInDatabase() {
         tokenService.createRefreshToken(
            new CommonUser(2L, "userNameasdad", "name", "emailasdad", "password")
        );
        RefreshToken refreshToken = refreshTokens.remove(1);
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> tokenService.createAccessToken(refreshToken)
        );
        assertEquals(VotifyErrorCode.REFRESH_TOKEN_EXPIRED, exception.getErrorCode());
    }

    @Test
    @Order(1)
    public void increaseRefreshTokenExpiration() {
        String refreshTokenId = refreshTokens.get(0).getId();
        Date oldExpiration = refreshTokens.get(0).getExpiration();

        RefreshToken refreshToken = assertDoesNotThrow(
            () -> tokenService.increaseRefreshTokenExpiration(refreshTokenId)
        );
        assertTrue(refreshToken.getExpiration().after(oldExpiration));
    }

    @Test
    @Order(1)
    public void increaseRefreshTokenExpirationButNonExistentInDatabase() {
        tokenService.createRefreshToken(
                new CommonUser(2L, "userNameasdad", "name", "emailasdad", "password")
        );
        RefreshToken refreshToken = refreshTokens.remove(1);
        String refreshTokenId = refreshToken.getId();

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> tokenService.increaseRefreshTokenExpiration(refreshTokenId)
        );
        assertEquals(VotifyErrorCode.REFRESH_TOKEN_EXPIRED, exception.getErrorCode());
    }
}
