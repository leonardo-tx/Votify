package br.com.votify.core.service.user;

import br.com.votify.core.model.user.EmailConfirmation;
import br.com.votify.core.model.user.User;
import br.com.votify.core.model.user.field.ConfirmationCode;
import br.com.votify.core.model.user.field.Email;
import br.com.votify.core.model.user.field.Name;
import br.com.votify.core.properties.user.UserProperties;
import br.com.votify.core.repository.user.EmailConfirmationRepository;
import br.com.votify.core.service.messaging.EmailSender;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailConfirmationServiceTest {
    private static final ResourceBundle messages = ResourceBundle.getBundle("messages");

    @Mock
    private EmailConfirmationRepository emailConfirmationRepository;

    @Mock
    private UserProperties userProperties;

    @Mock
    private EmailSender emailSender;

    @InjectMocks
    private EmailConfirmationService emailConfirmationService;

    @Test
    void deleteEmailConfirmation() {
        EmailConfirmation emailConfirmation = mock(EmailConfirmation.class);

        doNothing().when(emailConfirmationRepository).delete(emailConfirmation);
        assertDoesNotThrow(() -> emailConfirmationService.delete(emailConfirmation));
    }

    @Test
    void confirmEmailWithNullEmailShouldThrowException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> emailConfirmationService.confirmEmail("code", null)
        );
        assertEquals("The current email must not be empty.", exception.getMessage());
    }

    @Test
    void confirmEmailInvalidCodeShouldThrowException() throws VotifyException {
        Email email = new Email("test@example.com");
        EmailConfirmation emailConfirmationFromNewAccount = mock(EmailConfirmation.class);
        ConfirmationCode confirmationCode = new ConfirmationCode();

        when(emailConfirmationFromNewAccount.getCode()).thenReturn(confirmationCode);
        when(emailConfirmationRepository.findByUserEmail(email)).thenReturn(Optional.of(emailConfirmationFromNewAccount));
        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> emailConfirmationService.confirmEmail(
                        "wrong-code",
                        email
                )
        );
        assertEquals(VotifyErrorCode.EMAIL_CONFIRMATION_CODE_INVALID, exception.getErrorCode());
        verifyNoMoreInteractions(emailConfirmationRepository);
    }

    @Test
    void confirmEmailExpiredCodeShouldThrowException() throws VotifyException {
        Email email = new Email("test@example.com");
        EmailConfirmation emailConfirmationFromNewAccount = mock(EmailConfirmation.class);
        ConfirmationCode confirmationCode = new ConfirmationCode();

        when(emailConfirmationFromNewAccount.isExpired()).thenReturn(true);
        when(emailConfirmationRepository.findByUserEmail(email)).thenReturn(Optional.of(emailConfirmationFromNewAccount));
        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> emailConfirmationService.confirmEmail(
                        confirmationCode.getValue(),
                        email
                )
        );
        assertEquals(VotifyErrorCode.EMAIL_CONFIRMATION_CODE_INVALID, exception.getErrorCode());
        verifyNoMoreInteractions(emailConfirmationRepository);
    }

    @Test
    void confirmEmailFromNewUser() throws VotifyException {
        Email email = new Email("test@example.com");
        EmailConfirmation emailConfirmationFromNewAccount = mock(EmailConfirmation.class);
        ConfirmationCode confirmationCode = new ConfirmationCode();

        when(emailConfirmationFromNewAccount.getCode()).thenReturn(confirmationCode);
        when(emailConfirmationRepository.findByUserEmail(email)).thenReturn(Optional.of(emailConfirmationFromNewAccount));

        assertDoesNotThrow(() -> emailConfirmationService.confirmEmail(
                confirmationCode.getValue(),
                email
        ));
        verify(emailConfirmationRepository).delete(emailConfirmationFromNewAccount);
    }

    @Test
    void confirmEmailFromExistingUser() throws VotifyException {
        Email email = new Email("test@example.com");
        EmailConfirmation emailConfirmationFromExistingAccount = mock(EmailConfirmation.class);
        ConfirmationCode confirmationCode = new ConfirmationCode();

        when(emailConfirmationFromExistingAccount.getCode()).thenReturn(confirmationCode);
        when(emailConfirmationRepository.findByUserEmail(any(Email.class))).thenReturn(Optional.of(emailConfirmationFromExistingAccount));

        assertDoesNotThrow(() -> emailConfirmationService.confirmEmail(
                confirmationCode.getValue(),
                email
        ));
        verify(emailConfirmationRepository).delete(emailConfirmationFromExistingAccount);
    }

    @Test
    void confirmEmailFromExistingUserWithEmailFindsUser() throws VotifyException {
        Email oldEmail = new Email("jhonny@nightcity.2077");
        EmailConfirmation emailConfirmationFromExistingAccount = mock(EmailConfirmation.class);
        ConfirmationCode confirmationCode = new ConfirmationCode();

        when(emailConfirmationFromExistingAccount.getCode()).thenReturn(confirmationCode);
        when(emailConfirmationRepository.findByUserEmail(any(Email.class))).thenReturn(Optional.of(emailConfirmationFromExistingAccount));

        assertDoesNotThrow(() -> emailConfirmationService.confirmEmail(
                confirmationCode.getValue(),
                oldEmail
        ));
        verify(emailConfirmationRepository).delete(emailConfirmationFromExistingAccount);
    }

    @Test
    void findExpiredAccountsShouldReturnOne() {
        when(emailConfirmationRepository.findAllExpired(any(Instant.class)))
                .thenReturn(List.of(mock(EmailConfirmation.class)));

        List<EmailConfirmation> result = emailConfirmationService.findExpiredAccounts();

        assertEquals(1, result.size());
    }

    @Test
    void existsByUserEmailTrue() throws VotifyException {
        Email email = new Email("123@gmail.com");
        when(emailConfirmationRepository.existsByUserEmail(email)).thenReturn(true);
        assertTrue(emailConfirmationService.existsByEmail(email));
    }

    @Test
    void existsByUserEmailFalse() throws VotifyException {
        Email email = new Email("321@gmail.com");
        when(emailConfirmationRepository.existsByUserEmail(email)).thenReturn(false);
        assertFalse(emailConfirmationService.existsByEmail(email));
    }

    @Test
    void addEmailConfirmationWhenNewAccount() throws VotifyException {
        User newAccount = mock(User.class);
        when(newAccount.getId()).thenReturn(3L);
        when(newAccount.getName()).thenReturn(new Name("Name"));
        when(newAccount.getEmail()).thenReturn(new Email("mail@gmail.com"));

        when(userProperties.getEmailConfirmationExpirationMinutes()).thenReturn(1);
        when(emailConfirmationRepository.save(any(EmailConfirmation.class)))
                .thenAnswer(i -> i.getArguments()[0]);

        EmailConfirmation emailConfirmation = emailConfirmationService.addEmailConfirmation(
                newAccount,
                null
        );
        Instant now = Instant.now();
        assertEquals(newAccount.getId(), emailConfirmation.getUserId());
        assertTrue(emailConfirmation.getExpiration().isBefore(now.plusSeconds(60)));
        assertNull(emailConfirmation.getNewEmail());

        String subject = messages.getString("message.email.confirmation.subject");
        String body = String.format(
                messages.getString("message.email.confirmation.new.account.body"),
                newAccount.getName().getValue(),
                newAccount.getEmail().getValue(),
                emailConfirmation.getCode().encodeToUrlCode(),
                userProperties.getEmailConfirmationExpirationMinutes()
        );
        verify(emailSender).sendEmail(newAccount, subject, body);
    }

    @Test
    void addEmailConfirmationWhenAlreadyExistsExpiredConfirmation() throws VotifyException {
        EmailConfirmation existingEmailConfirmation = mock(EmailConfirmation.class);
        Email email = new Email("123@gmail.com");
        User newAccount = mock(User.class);
        when(newAccount.getId()).thenReturn(3L);
        when(newAccount.getName()).thenReturn(new Name("Name"));
        when(newAccount.getEmail()).thenReturn(email);

        when(existingEmailConfirmation.isExpired()).thenReturn(true);
        when(userProperties.getEmailConfirmationExpirationMinutes()).thenReturn(1);
        when(emailConfirmationRepository.findByUserEmail(email)).thenReturn(Optional.of(existingEmailConfirmation));
        when(emailConfirmationRepository.save(any(EmailConfirmation.class)))
                .thenAnswer(i -> i.getArguments()[0]);

        EmailConfirmation emailConfirmation = emailConfirmationService.addEmailConfirmation(
                newAccount,
                null
        );
        Instant now = Instant.now();
        assertEquals(newAccount.getId(), emailConfirmation.getUserId());
        assertTrue(emailConfirmation.getExpiration().isBefore(now.plusSeconds(60)));
        assertNull(emailConfirmation.getNewEmail());

        verify(emailConfirmationRepository).delete(existingEmailConfirmation);
        String subject = messages.getString("message.email.confirmation.subject");
        String body = String.format(
                messages.getString("message.email.confirmation.new.account.body"),
                newAccount.getName().getValue(),
                newAccount.getEmail().getValue(),
                emailConfirmation.getCode().encodeToUrlCode(),
                userProperties.getEmailConfirmationExpirationMinutes()
        );
        verify(emailSender).sendEmail(newAccount, subject, body);
    }

    @Test
    void addEmailConfirmationWhenAlreadyExistsActiveConfirmation() throws VotifyException {
        EmailConfirmation existingEmailConfirmation = mock(EmailConfirmation.class);
        Email email = new Email("123@gmail.com");
        User newAccount = mock(User.class);
        when(newAccount.getEmail()).thenReturn(email);

        when(existingEmailConfirmation.isExpired()).thenReturn(false);
        when(emailConfirmationRepository.findByUserEmail(email)).thenReturn(Optional.of(existingEmailConfirmation));

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> emailConfirmationService.addEmailConfirmation(newAccount, null)
        );
        assertEquals(VotifyErrorCode.PENDING_EMAIL_CONFIRMATION, exception.getErrorCode());
    }

    @Test
    void addEmailConfirmationWhenExistingAccount() throws VotifyException {
        User existingAccount = mock(User.class);
        when(existingAccount.getId()).thenReturn(2L);
        when(existingAccount.getName()).thenReturn(new Name("Jhonny Silverhand"));
        when(existingAccount.getEmail()).thenReturn(new Email("jhonny@nightcity.2077"));

        when(userProperties.getEmailConfirmationExpirationMinutes()).thenReturn(1);
        when(emailConfirmationRepository.save(any(EmailConfirmation.class)))
                .thenAnswer(i -> i.getArguments()[0]);

        EmailConfirmation emailConfirmation = emailConfirmationService.addEmailConfirmation(
                existingAccount,
                Email.parseUnsafe("jhonny@new.nightcity.2077")
        );
        Instant now = Instant.now();
        assertEquals(existingAccount.getId(), emailConfirmation.getUserId());
        assertTrue(emailConfirmation.getExpiration().isBefore(now.plusSeconds(60)));
        assertEquals("jhonny@new.nightcity.2077", emailConfirmation.getNewEmail().getValue());

        String subject = messages.getString("message.email.confirmation.subject");
        String body = String.format(
                messages.getString("message.email.confirmation.existing.account.body"),
                existingAccount.getName().getValue(),
                existingAccount.getEmail().getValue(),
                emailConfirmation.getCode().encodeToUrlCode(),
                userProperties.getEmailConfirmationExpirationMinutes()
        );
        verify(emailSender).sendEmail(existingAccount, subject, body);
    }

    @Test
    void deleteFromUser() {
        User user = mock(User.class);

        emailConfirmationService.deleteFromUser(user);

        verify(emailConfirmationRepository).deleteFromUser(user);
    }
}
