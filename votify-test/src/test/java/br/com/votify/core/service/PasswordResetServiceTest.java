package br.com.votify.core.service;

import br.com.votify.core.domain.entities.password.PasswordResetToken;
import br.com.votify.core.domain.entities.users.CommonUser;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.core.repository.PasswordResetTokenRepository;
import br.com.votify.core.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import br.com.votify.dto.ApiResponse;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PasswordResetServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private PasswordEncoderService passwordEncoderService;

    @InjectMocks
    private PasswordResetService passwordResetService;

    private User testUser;
    private String testEmail = "test@example.com";

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(passwordResetService, "expirationMinutes", 15);
        testUser = new CommonUser(1L, "testuser", "Test User", testEmail, "encodedPassword");
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
    }

    @Test
    public void createPasswordResetRequest_Success() throws VotifyException {
        when(passwordResetTokenRepository.findByUserAndExpiryDateAfter(eq(testUser), any(Date.class)))
                .thenReturn(Collections.emptyList());

        when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenAnswer(i -> i.getArgument(0));

        String code = passwordResetService.createPasswordResetRequest(testEmail);

        assertNotNull(code);
        verify(passwordResetTokenRepository).findByUserAndExpiryDateAfter(eq(testUser), any(Date.class));
        verify(passwordResetTokenRepository).save(any(PasswordResetToken.class));
    }

    @Test
    public void createPasswordResetRequest_UserNotFound() {
        VotifyException exception = assertThrows(VotifyException.class, () -> {
            passwordResetService.createPasswordResetRequest("nonexistent@example.com");
        });

        assertEquals("password.reset.email.not.found", exception.getErrorCode().getMessageKey());
        assertTrue(exception.getMessage().contains("Email not found."));
    }

    @Test
    public void createPasswordResetRequest_ActiveTokenExists() {
        List<PasswordResetToken> activeTokens = new ArrayList<>();
        activeTokens.add(new PasswordResetToken("CODE123", testUser, new Date(System.currentTimeMillis() + 600000)));

        when(passwordResetTokenRepository.findByUserAndExpiryDateAfter(eq(testUser), any(Date.class)))
                .thenReturn(activeTokens);

        VotifyException exception = assertThrows(VotifyException.class, () -> {
            passwordResetService.createPasswordResetRequest(testEmail);
        });
        assertEquals("password.reset.request.exists", exception.getErrorCode().getMessageKey());
        assertTrue(exception.getMessage().contains("There is already an active password reset request."));

    }

    @Test
    public void resetPassword_Success() throws VotifyException {
        String testCode = "TESTCODE";
        Date futureDate = new Date(System.currentTimeMillis() + 600000); // 10 minutos no futuro
        PasswordResetToken token = new PasswordResetToken(testCode, testUser, futureDate);

        when(passwordResetTokenRepository.findByCode(testCode)).thenReturn(Optional.of(token));
        when(passwordEncoderService.encryptPassword("newPassword")).thenReturn("encodedNewPassword");

        passwordResetService.resetPassword(testCode, "newPassword");

        verify(userRepository).save(testUser);
        assertEquals("encodedNewPassword", testUser.getPassword());
        verify(passwordResetTokenRepository).delete(token);
    }

    @Test
    public void resetPassword_InvalidCode() {
        when(passwordResetTokenRepository.findByCode("INVALID")).thenReturn(Optional.empty());

        Exception exception = assertThrows(VotifyException.class, () -> {
            passwordResetService.resetPassword("INVALID", "newPassword");
        });

        String expectedMessage = "The provided password reset code is invalid.";
        assertTrue(exception.getMessage().contains(expectedMessage));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void resetPassword_ExpiredToken() {
        String testCode = "EXPIREDCODE";
        Date pastDate = new Date(System.currentTimeMillis() - 600000);
        PasswordResetToken token = new PasswordResetToken(testCode, testUser, pastDate);

        when(passwordResetTokenRepository.findByCode(testCode)).thenReturn(Optional.of(token));

        Exception exception = assertThrows(VotifyException.class, () -> {
            passwordResetService.resetPassword(testCode, "newPassword");
        });
        String expectedMessage = "The provided password reset code is invalid.";
        assertTrue(exception.getMessage().contains(expectedMessage));

        verify(userRepository, never()).save(any(User.class));
    }
}