package br.com.votify.core.service.user;

import br.com.votify.core.model.user.PasswordReset;
import br.com.votify.core.model.user.User;
import br.com.votify.core.model.user.field.*;
import br.com.votify.core.properties.user.UserProperties;
import br.com.votify.core.service.messaging.EmailSender;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.core.repository.user.PasswordResetRepository;
import br.com.votify.core.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {
    private static final ResourceBundle messages = ResourceBundle.getBundle("messages");

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetRepository passwordResetRepository;

    @Mock
    private PasswordEncoderService passwordEncoderService;

    @Mock
    private UserProperties userProperties;

    @Mock
    private EmailSender emailSender;

    @InjectMocks
    private PasswordResetService passwordResetService;

    private final Email email = new Email("test@example.com");

    PasswordResetServiceTest() throws VotifyException {
    }

    @Test
    void createPasswordResetRequest_Success() throws VotifyException {
        User testUser = mock(User.class);
        when(testUser.getId()).thenReturn(3L);
        when(testUser.getName()).thenReturn(new Name("Namee"));

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(userProperties.getResetPasswordConfirmationExpirationMinutes()).thenReturn(15);
        when(passwordResetRepository.findByUser(testUser)).thenReturn(Optional.empty());
        when(passwordResetRepository.save(any(PasswordReset.class))).thenAnswer(i -> i.getArgument(0));

        PasswordReset passwordReset = passwordResetService.createPasswordResetRequest(email);

        assertNotNull(passwordReset);
        assertNotNull(passwordReset.getCode());
        assertNotNull(passwordReset.getCode().getValue());
        assertEquals(3L, passwordReset.getUserId());

        String subject = messages.getString("message.password.reset.subject");
        String body = String.format(
                messages.getString("message.password.reset.body"),
                testUser.getName().getValue(),
                passwordReset.getCode().getValue(),
                userProperties.getResetPasswordConfirmationExpirationMinutes()
        );
        verify(emailSender).sendEmail(testUser, subject, body);
    }

    @Test
    void createPasswordResetRequest_UserNotFound() throws VotifyException {
        Email nonExistentEmail = new Email("nonexistent@example.com");
        when(userRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> passwordResetService.createPasswordResetRequest(nonExistentEmail)
        );
        assertEquals(VotifyErrorCode.PASSWORD_RESET_EMAIL_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void createPasswordResetRequest_AlreadyExistsAndIsActive() {
        User testUser = mock(User.class);
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
    void createPasswordResetRequest_AlreadyExistsAndIsExpired() throws VotifyException {
        User testUser = mock(User.class);
        when(testUser.getId()).thenReturn(3L);
        when(testUser.getName()).thenReturn(new Name("Nome"));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        PasswordReset activePasswordReset = PasswordReset.parseUnsafe(
                ConfirmationCode.parseUnsafe("testcode"),
                testUser.getId(),
                Instant.now().plusSeconds(-1)
        );
        when(passwordResetRepository.findByUser(testUser)).thenReturn(Optional.of(activePasswordReset));
        when(passwordResetRepository.save(any(PasswordReset.class))).thenAnswer(i -> i.getArgument(0));

        PasswordReset passwordReset = passwordResetService.createPasswordResetRequest(email);

        assertNotNull(passwordReset);
        assertNotNull(passwordReset.getCode());
        assertNotNull(passwordReset.getCode().getValue());
        assertEquals(3L, passwordReset.getUserId());

        String subject = messages.getString("message.password.reset.subject");
        String body = String.format(
                messages.getString("message.password.reset.body"),
                testUser.getName().getValue(),
                passwordReset.getCode().getValue(),
                userProperties.getResetPasswordConfirmationExpirationMinutes()
        );
        verify(emailSender).sendEmail(testUser, subject, body);
    }

    @Test
    void resetPassword_Success() throws VotifyException {
        User testUser = mock(User.class);
        String testCode = "TESTCODE";
        Password newPassword = Password.parseUnsafe("newPassword");
        PasswordReset passwordReset = mock(PasswordReset.class);

        when(passwordReset.isExpired()).thenReturn(false);
        when(passwordReset.getUserId()).thenReturn(3L);

        when(passwordResetRepository.findByCode(testCode)).thenReturn(Optional.of(passwordReset));
        when(userRepository.findById(3L)).thenReturn(Optional.of(testUser));

        passwordResetService.resetPassword(testCode, newPassword);

        verify(userRepository).save(testUser);
        verify(testUser).setPassword(passwordEncoderService, newPassword);
        verify(passwordResetRepository).delete(passwordReset);
    }

    @Test
    void resetPassword_UserNotFound() {
        String testCode = "TESTCODE";
        Password newPassword = Password.parseUnsafe("newPassword");
        PasswordReset passwordReset = mock(PasswordReset.class);

        when(passwordReset.isExpired()).thenReturn(false);
        when(passwordReset.getUserId()).thenReturn(3L);

        when(passwordResetRepository.findByCode(testCode)).thenReturn(Optional.of(passwordReset));
        when(userRepository.findById(3L)).thenReturn(Optional.empty());

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> passwordResetService.resetPassword(testCode, newPassword)
        );
        assertEquals(VotifyErrorCode.USER_NOT_FOUND, exception.getErrorCode());

        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(passwordResetRepository);
    }

    @Test
    void resetPassword_InvalidCode() {
        when(passwordResetRepository.findByCode("INVALID")).thenReturn(Optional.empty());

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> passwordResetService.resetPassword("INVALID", Password.parseUnsafe("newPassword"))
        );

        assertEquals(VotifyErrorCode.PASSWORD_RESET_CODE_INVALID, exception.getErrorCode());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void resetPassword_ExpiredToken() {
        User testUser = mock(User.class);
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