package br.com.votify.core.service.user;

import br.com.votify.core.model.user.AccessToken;
import br.com.votify.core.model.user.RefreshToken;
import br.com.votify.core.model.user.User;
import br.com.votify.core.service.user.decorators.NeedsUserContext;
import br.com.votify.core.model.user.AuthTokens;
import br.com.votify.core.repository.user.UserRepository;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContextServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HandlerMethod handlerMethod;

    @Test
    void testConstructorWithNullCookies() throws NoSuchMethodException {
        when(request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE)).thenReturn(handlerMethod);
        when(handlerMethod.getMethod()).thenReturn(ContextServiceTest.class.getMethod("methodThatNeedsUserContext"));
        when(handlerMethod.getBeanType()).thenAnswer(invocation -> ClassThatNeedsUserContext.class);
        ContextService contextService = assertDoesNotThrow(
            () -> new ContextService(userRepository, tokenService, request)
        );

        assertFalse(contextService.isAuthenticated());

        verifyNoInteractions(tokenService);
        verifyNoInteractions(userRepository);
    }

    @Test
    void testConstructorWithNoDecorator() throws NoSuchMethodException {
        when(request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE)).thenReturn(handlerMethod);
        when(handlerMethod.getMethod()).thenReturn(ContextServiceTest.class.getMethod("methodThatDoesNotNeedUserContext"));
        when(handlerMethod.getBeanType()).thenAnswer(invocation -> ClassThatDoesNotNeedUserContext.class);

        ContextService contextService = assertDoesNotThrow(
                () -> new ContextService(userRepository, tokenService, request)
        );

        assertFalse(contextService.isAuthenticated());

        verifyNoMoreInteractions(request);
        verifyNoInteractions(tokenService);
        verifyNoInteractions(userRepository);
    }

    @Test
    void testConstructorWithAccessTokenNull() throws NoSuchMethodException {
        when(request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE)).thenReturn(handlerMethod);
        when(handlerMethod.getMethod()).thenReturn(ContextServiceTest.class.getMethod("methodThatNeedsUserContext"));
        when(handlerMethod.getBeanType()).thenAnswer(invocation -> ClassThatNeedsUserContext.class);
        when(request.getCookies()).thenReturn(new Cookie[] {
            new Cookie("refresh_token", "token2")
        });

        ContextService contextService = assertDoesNotThrow(
            () -> new ContextService(userRepository, tokenService, request)
        );
        assertEquals("token2", contextService.getCookieValue("refresh_token"));
        assertFalse(contextService.isAuthenticated());

        verifyNoInteractions(tokenService);
        verifyNoInteractions(userRepository);
    }

    @Test
    void testConstructorWithRefreshTokenNull() throws VotifyException, NoSuchMethodException {
        AccessToken accessToken = mock(AccessToken.class);
        when(accessToken.getUserId()).thenReturn(1L);

        when(request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE)).thenReturn(handlerMethod);
        when(handlerMethod.getMethod()).thenReturn(ContextServiceTest.class.getMethod("methodThatNeedsUserContext"));
        when(handlerMethod.getBeanType()).thenAnswer(invocation -> ClassThatNeedsUserContext.class);
        when(request.getCookies()).thenReturn(new Cookie[] {
            new Cookie("access_token", "token1")
        });
        when(tokenService.retrieveAccessToken(any(String.class))).thenReturn(accessToken);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mock(User.class)));

        ContextService contextService = assertDoesNotThrow(
            () -> new ContextService(userRepository, tokenService, request)
        );
        assertEquals("token1", contextService.getCookieValue("access_token"));
        assertTrue(contextService.isAuthenticated());
    }

    @Test
    void testConstructorWithAllCookies() throws VotifyException, NoSuchMethodException {
        AccessToken accessToken = mock(AccessToken.class);
        when(accessToken.getUserId()).thenReturn(1L);

        when(request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE)).thenReturn(handlerMethod);
        when(handlerMethod.getMethod()).thenReturn(ContextServiceTest.class.getMethod("methodThatNeedsUserContext"));
        when(handlerMethod.getBeanType()).thenAnswer(invocation -> ClassThatNeedsUserContext.class);
        when(request.getCookies()).thenReturn(new Cookie[] {
            new Cookie("access_token", "token1"),
            new Cookie("refresh_token", "token2")
        });
        when(tokenService.retrieveAccessToken(any(String.class))).thenReturn(accessToken);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mock(User.class)));

        ContextService contextService = assertDoesNotThrow(
            () -> new ContextService(userRepository, tokenService, request)
        );
        assertEquals("token1", contextService.getCookieValue("access_token"));
        assertEquals("token2", contextService.getCookieValue("refresh_token"));
        assertTrue(contextService.isAuthenticated());
    }

    @Test
    void throwIfNotAuthenticated() throws NoSuchMethodException {
        when(request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE)).thenReturn(handlerMethod);
        when(handlerMethod.getMethod()).thenReturn(ContextServiceTest.class.getMethod("methodThatNeedsUserContext"));
        when(handlerMethod.getBeanType()).thenAnswer(invocation -> ClassThatNeedsUserContext.class);
        ContextService contextService = new ContextService(userRepository, tokenService, request);
        VotifyException exception = assertThrows(
            VotifyException.class, contextService::throwIfNotAuthenticated
        );
        assertEquals(VotifyErrorCode.COMMON_UNAUTHORIZED, exception.getErrorCode());
    }

    @Test
    void doesNotThrowIfAuthenticated() throws VotifyException, NoSuchMethodException {
        AccessToken accessToken = mock(AccessToken.class);
        when(accessToken.getUserId()).thenReturn(1L);

        when(request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE)).thenReturn(handlerMethod);
        when(handlerMethod.getMethod()).thenReturn(ContextServiceTest.class.getMethod("methodThatNeedsUserContext"));
        when(handlerMethod.getBeanType()).thenAnswer(invocation -> ClassThatNeedsUserContext.class);
        when(request.getCookies()).thenReturn(new Cookie[] {
            new Cookie("access_token", "token1")
        });
        when(tokenService.retrieveAccessToken(any(String.class))).thenReturn(accessToken);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mock(User.class)));

        ContextService contextService = new ContextService(userRepository, tokenService, request);
        assertDoesNotThrow(contextService::throwIfNotAuthenticated);
    }

    @Test
    void throwIfUserIsNull() throws NoSuchMethodException {
        when(request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE)).thenReturn(handlerMethod);
        when(handlerMethod.getMethod()).thenReturn(ContextServiceTest.class.getMethod("methodThatNeedsUserContext"));
        when(handlerMethod.getBeanType()).thenAnswer(invocation -> ClassThatNeedsUserContext.class);
        ContextService contextService = new ContextService(userRepository, tokenService, request);
        VotifyException exception = assertThrows(
            VotifyException.class, contextService::getUserOrThrow
        );
        assertEquals(VotifyErrorCode.COMMON_UNAUTHORIZED, exception.getErrorCode());
    }

    @Test
    void doesNotThrowIfUserIsNotNull() throws VotifyException, NoSuchMethodException {
        AccessToken accessToken = mock(AccessToken.class);
        when(accessToken.getUserId()).thenReturn(1L);

        when(request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE)).thenReturn(handlerMethod);
        when(handlerMethod.getMethod()).thenReturn(ContextServiceTest.class.getMethod("methodThatNeedsUserContext"));
        when(handlerMethod.getBeanType()).thenAnswer(invocation -> ClassThatNeedsUserContext.class);
        when(request.getCookies()).thenReturn(new Cookie[] {
            new Cookie("access_token", "token1")
        });
        when(tokenService.retrieveAccessToken(any(String.class))).thenReturn(accessToken);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mock(User.class)));

        ContextService contextService = new ContextService(userRepository, tokenService, request);
        User user = assertDoesNotThrow(contextService::getUserOrThrow);

        assertNotNull(user);
    }

    @Test
    void refreshTokens() throws VotifyException, NoSuchMethodException {
        RefreshToken refreshToken = mock(RefreshToken.class);
        AccessToken accessToken = mock(AccessToken.class);

        when(request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE)).thenReturn(handlerMethod);
        when(handlerMethod.getMethod()).thenReturn(ContextServiceTest.class.getMethod("methodThatNeedsUserContext"));
        when(handlerMethod.getBeanType()).thenAnswer(invocation -> ClassThatNeedsUserContext.class);
        when(request.getCookies()).thenReturn(new Cookie[] {
            new Cookie("refresh_token", "token2")
        });
        when(tokenService.increaseRefreshTokenExpiration("token2")).thenReturn(refreshToken);
        when(tokenService.createAccessToken(refreshToken)).thenReturn(accessToken);

        ContextService contextService = new ContextService(userRepository, tokenService, request);
        AuthTokens authTokens = assertDoesNotThrow(contextService::refreshTokens);

        assertNotNull(authTokens);
    }

    @Test
    void refreshTokensWithRefreshTokenNull() throws VotifyException, NoSuchMethodException {
        AccessToken accessToken = mock(AccessToken.class);
        when(accessToken.getUserId()).thenReturn(1L);

        when(request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE)).thenReturn(handlerMethod);
        when(handlerMethod.getMethod()).thenReturn(ContextServiceTest.class.getMethod("methodThatNeedsUserContext"));
        when(handlerMethod.getBeanType()).thenAnswer(invocation -> ClassThatNeedsUserContext.class);
        when(request.getCookies()).thenReturn(new Cookie[] {
            new Cookie("access_token", "token1")
        });
        when(tokenService.retrieveAccessToken(any(String.class))).thenReturn(accessToken);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mock(User.class)));

        ContextService contextService = new ContextService(userRepository, tokenService, request);
        VotifyException exception = assertThrows(VotifyException.class, contextService::refreshTokens);
        assertEquals(VotifyErrorCode.REFRESH_TOKEN_EXPIRED, exception.getErrorCode());
    }

    @NeedsUserContext
    public void methodThatNeedsUserContext() {
        throw new UnsupportedOperationException();
    }

    public void methodThatDoesNotNeedUserContext() {
        throw new UnsupportedOperationException();
    }

    @NeedsUserContext
    public static class ClassThatNeedsUserContext {

    }

    public static class ClassThatDoesNotNeedUserContext {

    }
}
