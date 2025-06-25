package br.com.votify.core.service.user;

import br.com.votify.core.model.user.PasswordReset;
import br.com.votify.core.model.user.User;
import br.com.votify.core.model.user.field.*;
import br.com.votify.core.properties.user.UserProperties;
import br.com.votify.core.service.messaging.EmailSender;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.core.repository.user.PasswordResetRepository;
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
    private PasswordResetRepository passwordResetRepository;

    @Mock
    private UserProperties userProperties;

    @Mock
    private EmailSender emailSender;

    @InjectMocks
    private PasswordResetService passwordResetService;

    PasswordResetServiceTest() throws VotifyException {
    }

    @Test
    void createPasswordResetRequest_Success() throws VotifyException {
        User testUser = mock(User.class);
        when(testUser.getId()).thenReturn(3L);
        when(testUser.getName()).thenReturn(new Name("Namee"));

        when(userProperties.getResetPasswordConfirmationExpirationMinutes()).thenReturn(15);
        when(passwordResetRepository.findByUser(testUser)).thenReturn(Optional.empty());
        when(passwordResetRepository.save(any(PasswordReset.class))).thenAnswer(i -> i.getArgument(0));

        PasswordReset passwordReset = passwordResetService.createPasswordResetRequest(testUser);

        assertNotNull(passwordReset);
        assertNotNull(passwordReset.getCode());
        assertNotNull(passwordReset.getCode().getValue());
        assertEquals(3L, passwordReset.getUserId());

        String subject = messages.getString("message.password.reset.subject");
        String body = String.format(
                messages.getString("message.password.reset.body"),
                testUser.getName().getValue(),
                passwordReset.getCode().encodeToUrlCode(),
                userProperties.getResetPasswordConfirmationExpirationMinutes()
        );
        verify(emailSender).sendEmail(testUser, subject, body);
    }

    @Test
    void createPasswordResetRequest_AlreadyExistsAndIsActive() {
        User testUser = mock(User.class);
        PasswordReset activePasswordReset = PasswordReset.parseUnsafe(
                ConfirmationCode.parseUnsafe("testcode"),
                testUser.getId(),
                Instant.now().plusSeconds(180)
        );
        when(passwordResetRepository.findByUser(testUser)).thenReturn(Optional.of(activePasswordReset));

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> passwordResetService.createPasswordResetRequest(testUser)
        );
        assertEquals(VotifyErrorCode.PASSWORD_RESET_REQUEST_EXISTS, exception.getErrorCode());
    }

    @Test
    void createPasswordResetRequest_AlreadyExistsAndIsExpired() throws VotifyException {
        User testUser = mock(User.class);
        when(testUser.getId()).thenReturn(3L);
        when(testUser.getName()).thenReturn(new Name("Nome"));

        PasswordReset activePasswordReset = PasswordReset.parseUnsafe(
                ConfirmationCode.parseUnsafe("testcode"),
                testUser.getId(),
                Instant.now().plusSeconds(-1)
        );
        when(passwordResetRepository.findByUser(testUser)).thenReturn(Optional.of(activePasswordReset));
        when(passwordResetRepository.save(any(PasswordReset.class))).thenAnswer(i -> i.getArgument(0));

        PasswordReset passwordReset = passwordResetService.createPasswordResetRequest(testUser);

        assertNotNull(passwordReset);
        assertNotNull(passwordReset.getCode());
        assertNotNull(passwordReset.getCode().getValue());
        assertEquals(3L, passwordReset.getUserId());

        String subject = messages.getString("message.password.reset.subject");
        String body = String.format(
                messages.getString("message.password.reset.body"),
                testUser.getName().getValue(),
                passwordReset.getCode().encodeToUrlCode(),
                userProperties.getResetPasswordConfirmationExpirationMinutes()
        );
        verify(emailSender).sendEmail(testUser, subject, body);
    }

    @Test
    void resetPassword_Success() throws VotifyException {
        String testCode = "TESTCODE";
        PasswordReset passwordReset = mock(PasswordReset.class);

        when(passwordReset.isExpired()).thenReturn(false);
        when(passwordResetRepository.findByCode(testCode)).thenReturn(Optional.of(passwordReset));

        PasswordReset passwordResetFromDelete = passwordResetService.resetPassword(testCode);

        assertEquals(passwordReset, passwordResetFromDelete);
        verify(passwordResetRepository).delete(passwordReset);
    }

    @Test
    void resetPassword_InvalidCode() {
        when(passwordResetRepository.findByCode("INVALID")).thenReturn(Optional.empty());

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> passwordResetService.resetPassword("INVALID")
        );
        assertEquals(VotifyErrorCode.PASSWORD_RESET_CODE_INVALID, exception.getErrorCode());
    }

    @Test
    void resetPassword_ExpiredToken() {
        String testCode = "EXPIREDCODE";
        PasswordReset passwordReset = mock(PasswordReset.class);

        when(passwordReset.isExpired()).thenReturn(true);
        when(passwordResetRepository.findByCode(testCode)).thenReturn(Optional.of(passwordReset));

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> passwordResetService.resetPassword(testCode)
        );

        assertEquals(VotifyErrorCode.PASSWORD_RESET_CODE_INVALID, exception.getErrorCode());
    }
}