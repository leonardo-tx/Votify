package br.com.votify.api.controller.users;

import br.com.votify.api.configuration.SecurityConfig;
import br.com.votify.core.domain.entities.users.CommonUser;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.service.ContextService;
import br.com.votify.core.service.UserService;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.dto.ApiResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private SecurityConfig securityConfig;

    @Mock
    private ContextService contextService;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private UserController userController;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new CommonUser(1L, "test-user", "Test User", "test@example.com", "password123");
    }

    @Test
    void deleteAccount_ShouldDeleteUserAndClearCookies() throws VotifyException {
        // Arrange
        when(contextService.getUserOrThrow()).thenReturn(testUser);
        doNothing().when(userService).deleteUser(testUser.getId());

        // Act
        ResponseEntity<ApiResponse<?>> responseEntity = userController.deleteAccount(response);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        verify(userService).deleteUser(testUser.getId());
        verify(response, times(2)).addCookie(any(Cookie.class));
        verify(securityConfig, times(2)).configureAccessTokenCookie(any(Cookie.class));
    }

    @Test
    void deleteAccount_WhenUserNotFound_ShouldPropagateException() throws VotifyException {
        // Arrange
        when(contextService.getUserOrThrow()).thenReturn(testUser);
        doThrow(new VotifyException(VotifyErrorCode.USER_NOT_FOUND))
            .when(userService).deleteUser(testUser.getId());

        // Act & Assert
        assertThrows(VotifyException.class, () -> userController.deleteAccount(response));
        verify(response, never()).addCookie(any(Cookie.class));
    }

    @Test
    void deleteAccount_WhenUnauthorized_ShouldPropagateException() throws VotifyException {
        // Arrange
        when(contextService.getUserOrThrow())
            .thenThrow(new VotifyException(VotifyErrorCode.COMMON_UNAUTHORIZED));

        // Act & Assert
        assertThrows(VotifyException.class, () -> userController.deleteAccount(response));
        verify(userService, never()).deleteUser(any());
        verify(response, never()).addCookie(any(Cookie.class));
    }
} 