package br.com.votify.core.service;

import br.com.votify.core.domain.entities.tokens.AuthTokens;
import br.com.votify.core.domain.entities.users.CommonUser;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.repository.UserRepository;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ContextServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Test
    public void testConstructorWithNullCookies() {
        assertDoesNotThrow(
            () -> new ContextService(userRepository, tokenService, httpServletRequest)
        );
    }

    @Test
    public void testConstructorWithAccessTokenNull() {
        when(httpServletRequest.getCookies()).thenReturn(new Cookie[] {
            new Cookie("refresh_token", "token2")
        });

        ContextService contextService = assertDoesNotThrow(
            () -> new ContextService(userRepository, tokenService, httpServletRequest)
        );
        assertEquals("token2", contextService.getCookieValue("refresh_token"));
        assertFalse(contextService.isAuthenticated());
    }

    @Test
    public void testConstructorWithRefreshTokenNull() throws VotifyException {
        when(httpServletRequest.getCookies()).thenReturn(new Cookie[] {
            new Cookie("access_token", "token1")
        });
        when(tokenService.getUserIdFromAccessToken(any(String.class))).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new CommonUser()));

        ContextService contextService = assertDoesNotThrow(
            () -> new ContextService(userRepository, tokenService, httpServletRequest)
        );
        assertEquals("token1", contextService.getCookieValue("access_token"));
        assertTrue(contextService.isAuthenticated());
    }

    @Test
    public void testConstructorWithAllCookies() throws VotifyException {
        when(httpServletRequest.getCookies()).thenReturn(new Cookie[] {
            new Cookie("access_token", "token1"),
            new Cookie("refresh_token", "token2")
        });
        when(tokenService.getUserIdFromAccessToken(any(String.class))).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new CommonUser()));

        ContextService contextService = assertDoesNotThrow(
            () -> new ContextService(userRepository, tokenService, httpServletRequest)
        );
        assertEquals("token1", contextService.getCookieValue("access_token"));
        assertEquals("token2", contextService.getCookieValue("refresh_token"));
        assertTrue(contextService.isAuthenticated());
    }

    @Test
    public void throwIfNotAuthenticated() throws VotifyException {
        ContextService contextService = new ContextService(userRepository, tokenService, httpServletRequest);
        VotifyException exception = assertThrows(
            VotifyException.class, contextService::throwIfNotAuthenticated
        );
        assertEquals(VotifyErrorCode.COMMON_UNAUTHORIZED, exception.getErrorCode());
    }

    @Test
    public void doesNotThrowIfAuthenticated() throws VotifyException {
        when(httpServletRequest.getCookies()).thenReturn(new Cookie[] {
            new Cookie("access_token", "token1")
        });
        when(tokenService.getUserIdFromAccessToken(any(String.class))).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new CommonUser()));

        ContextService contextService = new ContextService(userRepository, tokenService, httpServletRequest);
        assertDoesNotThrow(contextService::throwIfNotAuthenticated);
    }

    @Test
    public void throwIfUserIsNull() throws VotifyException {
        ContextService contextService = new ContextService(userRepository, tokenService, httpServletRequest);
        VotifyException exception = assertThrows(
            VotifyException.class, contextService::getUserOrThrow
        );
        assertEquals(VotifyErrorCode.COMMON_UNAUTHORIZED, exception.getErrorCode());
    }

    @Test
    public void doesNotThrowIfUserIsNotNull() throws VotifyException {
        when(httpServletRequest.getCookies()).thenReturn(new Cookie[] {
            new Cookie("access_token", "token1")
        });
        when(tokenService.getUserIdFromAccessToken(any(String.class))).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new CommonUser()));

        ContextService contextService = new ContextService(userRepository, tokenService, httpServletRequest);
        User user = assertDoesNotThrow(contextService::getUserOrThrow);

        assertNotNull(user);
    }

    @Test
    public void refreshTokens() throws VotifyException {
        when(httpServletRequest.getCookies()).thenReturn(new Cookie[] {
            new Cookie("refresh_token", "token2")
        });

        ContextService contextService = new ContextService(userRepository, tokenService, httpServletRequest);
        AuthTokens authTokens = assertDoesNotThrow(contextService::refreshTokens);
        assertNotNull(authTokens);

        verify(tokenService).increaseRefreshTokenExpiration("token2");
    }

    @Test
    public void refreshTokensWithRefreshTokenNull() throws VotifyException {
        when(httpServletRequest.getCookies()).thenReturn(new Cookie[] {
            new Cookie("access_token", "token1")
        });
        when(tokenService.getUserIdFromAccessToken(any(String.class))).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new CommonUser()));

        ContextService contextService = new ContextService(userRepository, tokenService, httpServletRequest);
        VotifyException exception = assertThrows(VotifyException.class, contextService::refreshTokens);
        assertEquals(VotifyErrorCode.REFRESH_TOKEN_EXPIRED, exception.getErrorCode());
    }
    
    @Test
    public void deleteUser_WhenAuthenticated_ShouldDeleteSuccessfully() throws VotifyException {
        User testUser = new CommonUser(1L, "test-user", "Test User", "test@example.com", "password123");
        
        when(httpServletRequest.getCookies()).thenReturn(new Cookie[] {
            new Cookie("access_token", "token1")
        });
        when(tokenService.getUserIdFromAccessToken(any(String.class))).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        
        ContextService contextService = new ContextService(userRepository, tokenService, httpServletRequest);

        assertDoesNotThrow(contextService::deleteUser);
        verify(userRepository).delete(any(User.class));
    }

    @Test
    public void userFromContextIsClone() throws VotifyException {
        User testUser = new CommonUser(1L, "test-user", "Test User", "test@example.com", "password123");

        when(httpServletRequest.getCookies()).thenReturn(new Cookie[] {
                new Cookie("access_token", "token1")
        });
        when(tokenService.getUserIdFromAccessToken(any(String.class))).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        ContextService contextService = new ContextService(userRepository, tokenService, httpServletRequest);
        assertNotSame(testUser, contextService.getUserOrThrow());
    }
}
