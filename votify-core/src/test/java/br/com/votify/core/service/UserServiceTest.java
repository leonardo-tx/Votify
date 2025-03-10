package br.com.votify.core.service;

import br.com.votify.core.domain.entities.tokens.RefreshToken;
import br.com.votify.core.domain.entities.users.CommonUser;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.repository.RefreshTokenRepository;
import br.com.votify.core.repository.UserRepository;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private ContextService contextService;

    @Mock
    private TokenService tokenService;

    @Mock
    private PasswordEncoderService passwordEncoderService;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private RefreshToken testRefreshToken;

    @BeforeEach
    void setUp() {
        testUser = new CommonUser(1L, "test-user", "Test User", "test@example.com", "password123");
        testRefreshToken = new RefreshToken("token123", null, testUser);
    }

    @Test
    void deleteUser_WhenUserExists_AndIsOwner_ShouldDeleteSuccessfully() throws VotifyException {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(contextService.getUserOrThrow()).thenReturn(testUser);
        when(refreshTokenRepository.findAllByUser(testUser))
            .thenReturn(Arrays.asList(testRefreshToken));

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(refreshTokenRepository).deleteAll(Arrays.asList(testRefreshToken));
        verify(userRepository).delete(testUser);
    }

    @Test
    void deleteUser_WhenUserDoesNotExist_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        VotifyException exception = assertThrows(VotifyException.class,
            () -> userService.deleteUser(1L));
        assertEquals(VotifyErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void deleteUser_WhenNotOwner_ShouldThrowException() throws VotifyException {
        // Arrange
        User otherUser = new CommonUser(2L, "other-user", "Other User", "other@example.com", "password123");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(contextService.getUserOrThrow()).thenReturn(otherUser);

        // Act & Assert
        VotifyException exception = assertThrows(VotifyException.class,
            () -> userService.deleteUser(1L));
        assertEquals(VotifyErrorCode.USER_DELETE_UNAUTHORIZED, exception.getErrorCode());
    }

    @Test
    void deleteUser_ShouldDeleteAllRefreshTokens() throws VotifyException {
        // Arrange
        RefreshToken token1 = new RefreshToken("token1", null, testUser);
        RefreshToken token2 = new RefreshToken("token2", null, testUser);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(contextService.getUserOrThrow()).thenReturn(testUser);
        when(refreshTokenRepository.findAllByUser(testUser))
            .thenReturn(Arrays.asList(token1, token2));

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(refreshTokenRepository).deleteAll(Arrays.asList(token1, token2));
        verify(userRepository).delete(testUser);
    }
} 