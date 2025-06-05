package br.com.votify.core.service.user;

import br.com.votify.core.model.user.PasswordReset;
import br.com.votify.core.model.user.User;
import br.com.votify.core.model.user.UserRole;
import br.com.votify.core.model.user.field.*;
import br.com.votify.core.properties.user.UserProperties;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.core.repository.user.PasswordResetRepository;
import br.com.votify.core.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PasswordResetServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetRepository passwordResetRepository;

    @Mock
    private PasswordEncoderService passwordEncoderService;

    @Mock
    private UserProperties userProperties;

    @InjectMocks
    private PasswordResetService passwordResetService;

    private User testUser;
    private final String testEmail = "test@example.com";

    @BeforeEach
    public void setup() {
        testUser = User.parseUnsafe(
                3L,
                Email.parseUnsafe(testEmail),
                UserName.parseUnsafe("testuser"),
                Name.parseUnsafe("Test User"),
                "encodedPassword",
                UserRole.COMMON,
                true
        );
    }

    @Test
    public void createPasswordResetRequest_Success() throws VotifyException {
        Email email = Email.parseUnsafe(testEmail);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(userProperties.getResetPasswordConfirmationExpirationMinutes()).thenReturn(15);
        when(passwordResetRepository.findByUser(eq(testUser))).thenReturn(Optional.empty());
        when(passwordResetRepository.save(any(PasswordReset.class))).thenAnswer(i -> i.getArgument(0));

        PasswordReset passwordReset = passwordResetService.createPasswordResetRequest(email);

        assertNotNull(passwordReset);
        assertNotNull(passwordReset.getCode());
        assertNotNull(passwordReset.getCode().getValue());
        assertEquals(3L, passwordReset.getUserId());
    }

    @Test
    public void createPasswordResetRequest_UserNotFound() throws VotifyException {
        Email email = Email.parseUnsafe("nonexistent@example.com");
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> passwordResetService.createPasswordResetRequest(email)
        );
        assertEquals(VotifyErrorCode.PASSWORD_RESET_EMAIL_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    public void createPasswordResetRequest_ActiveTokenExists() throws VotifyException {
        Email email = Email.parseUnsafe(testEmail);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        PasswordReset activePasswordReset = PasswordReset.parseUnsafe(
                ConfirmationCode.parseUnsafe("testcode"),
                testUser.getId(),
                Instant.now().plusSeconds(180)
        );
        when(passwordResetRepository.findByUser(testUser)).thenReturn(Optional.of(activePasswordReset));

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> passwordResetService.createPasswordResetRequest(email)
        );
        assertEquals(VotifyErrorCode.PASSWORD_RESET_REQUEST_EXISTS, exception.getErrorCode());
    }

    @Test
    public void resetPassword_Success() throws VotifyException {
        String testCode = "TESTCODE";
        Password newPassword = Password.parseUnsafe("newPassword");
        Instant futureDate = Instant.now().plus(Duration.ofMinutes(10));
        PasswordReset token = PasswordReset.parseUnsafe(
                ConfirmationCode.parseUnsafe(testCode),
                testUser.getId(),
                futureDate
        );

        when(passwordResetRepository.findByCode(testCode)).thenReturn(Optional.of(token));
        when(passwordEncoderService.encryptPassword(newPassword)).thenReturn("encodedNewPassword");

        passwordResetService.resetPassword(testCode, newPassword);

        verify(userRepository).save(testUser);
        assertEquals("encodedNewPassword", testUser.getEncryptedPassword());
        verify(passwordResetRepository).delete(token);
    }

    @Test
    public void resetPassword_InvalidCode() {
        when(passwordResetRepository.findByCode("INVALID")).thenReturn(Optional.empty());

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> passwordResetService.resetPassword("INVALID", Password.parseUnsafe("newPassword"))
        );

        assertEquals(VotifyErrorCode.PASSWORD_RESET_CODE_INVALID, exception.getErrorCode());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void resetPassword_ExpiredToken() {
        String testCode = "EXPIREDCODE";
        Instant pastDate = Instant.now().minusSeconds(600);
        PasswordReset token = PasswordReset.parseUnsafe(
                ConfirmationCode.parseUnsafe(testCode),
                testUser.getId(),
                pastDate
        );

        when(passwordResetRepository.findByCode(testCode)).thenReturn(Optional.of(token));

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> passwordResetService.resetPassword(testCode, Password.parseUnsafe("newPassword"))
        );

        assertEquals(VotifyErrorCode.PASSWORD_RESET_CODE_INVALID, exception.getErrorCode());
        verify(userRepository, never()).save(any(User.class));
    }
}