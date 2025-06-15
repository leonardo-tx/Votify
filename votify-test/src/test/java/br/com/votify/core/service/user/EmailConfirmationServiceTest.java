package br.com.votify.core.service.user;

import br.com.votify.core.model.user.EmailConfirmation;
import br.com.votify.core.model.user.User;
import br.com.votify.core.model.user.field.ConfirmationCode;
import br.com.votify.core.model.user.field.Email;
import br.com.votify.core.properties.user.UserProperties;
import br.com.votify.core.repository.user.EmailConfirmationRepository;
import br.com.votify.core.repository.user.UserRepository;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.test.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailConfirmationServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ContextService contextService;

    @Mock
    private EmailConfirmationRepository emailConfirmationRepository;

    @Mock
    private UserProperties userProperties;

    @InjectMocks
    private EmailConfirmationService emailConfirmationService;

    private User newAccount;
    private User existingAccount;
    private EmailConfirmation emailConfirmationFromNewAccount;
    private EmailConfirmation emailConfirmationFromExistingAccount;

    @BeforeEach
    void setupBeforeEach() {
        newAccount = UserFactory.createUser(1, false);
        existingAccount = UserFactory.createUser(0, true);

        this.emailConfirmationFromNewAccount = EmailConfirmation.parseUnsafe(
                new ConfirmationCode(),
                null,
                Instant.now().plus(Duration.ofMinutes(30)),
                newAccount.getId()
        );
        this.emailConfirmationFromExistingAccount = EmailConfirmation.parseUnsafe(
                new ConfirmationCode(),
                Email.parseUnsafe("jhonny@new.nightcity.2077"),
                Instant.now().plus(Duration.ofMinutes(30)),
                existingAccount.getId()
        );
    }

    @Test
    void deleteEmailConfirmation() {
        doNothing().when(emailConfirmationRepository).delete(any(EmailConfirmation.class));
        assertDoesNotThrow(() -> emailConfirmationService.delete(emailConfirmationFromNewAccount));
    }

    @Test
    void confirmEmailInvalidCodeFromNewUserShouldThrowException() {
        when(emailConfirmationRepository.findByUserEmail(any(Email.class))).thenReturn(Optional.of(emailConfirmationFromNewAccount));
        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> emailConfirmationService.confirmEmail(
                        "wrong-code",
                        newAccount.getEmail()
                )
        );
        assertEquals(VotifyErrorCode.EMAIL_CONFIRMATION_CODE_INVALID, exception.getErrorCode());
        verifyNoInteractions(userRepository);
        verifyNoInteractions(contextService);
    }

    @Test
    void confirmEmailFromNewUser() {
        when(emailConfirmationRepository.findByUserEmail(any(Email.class))).thenReturn(Optional.of(emailConfirmationFromNewAccount));

        assertDoesNotThrow(() -> emailConfirmationService.confirmEmail(
                emailConfirmationFromNewAccount.getCode().getValue(),
                newAccount.getEmail()
        ));
        verify(emailConfirmationRepository).delete(emailConfirmationFromNewAccount);
        verifyNoInteractions(userRepository);
        verifyNoInteractions(contextService);
    }

    @Test
    void confirmEmailInvalidCodeFromExistingUserShouldThrowException() throws VotifyException {
        when(emailConfirmationRepository.findByUserEmail(any(Email.class))).thenReturn(Optional.of(emailConfirmationFromExistingAccount));
        when(contextService.getUserOrThrow()).thenReturn(existingAccount);
        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> emailConfirmationService.confirmEmail(
                        "wrong-code",
                        null
                )
        );
        assertEquals(VotifyErrorCode.EMAIL_CONFIRMATION_CODE_INVALID, exception.getErrorCode());
        verifyNoInteractions(userRepository);
    }

    @Test
    void confirmEmailFromExistingUser() throws VotifyException {
        when(emailConfirmationRepository.findByUserEmail(any(Email.class))).thenReturn(Optional.of(emailConfirmationFromExistingAccount));
        when(contextService.getUserOrThrow()).thenReturn(existingAccount);
        when(userRepository.save(existingAccount)).thenReturn(existingAccount);

        assertDoesNotThrow(() -> emailConfirmationService.confirmEmail(
                emailConfirmationFromExistingAccount.getCode().getValue(),
                null
        ));
        assertEquals("jhonny@new.nightcity.2077", existingAccount.getEmail().getValue());
        verify(emailConfirmationRepository).delete(emailConfirmationFromExistingAccount);
    }

    @Test
    void findExpiredAccountsShouldReturnOne() {
        when(emailConfirmationRepository.findAllExpired(any(Instant.class)))
                .thenReturn(List.of(emailConfirmationFromNewAccount));

        List<EmailConfirmation> result = emailConfirmationService.findExpiredAccounts();

        assertEquals(result.get(0).getCode(), emailConfirmationFromNewAccount.getCode());
    }

    @Test
    void existsByUserEmailTrue() {
        Email email = Email.parseUnsafe("123@gmail.com");
        when(emailConfirmationRepository.existsByUserEmail(email)).thenReturn(true);
        assertTrue(emailConfirmationService.existsByEmail(email));
    }

    @Test
    void existsByUserEmailFalse() {
        Email email = Email.parseUnsafe("321@gmail.com");
        when(emailConfirmationRepository.existsByUserEmail(email)).thenReturn(false);
        assertFalse(emailConfirmationService.existsByEmail(email));
    }

    @Test
    void addEmailConfirmationWhenNewAccount() throws VotifyException {
        when(userProperties.getEmailConfirmationExpirationMinutes()).thenReturn(1);
        when(emailConfirmationRepository.save(any(EmailConfirmation.class)))
                .thenAnswer(i -> i.getArguments()[0]);

        EmailConfirmation emailConfirmation = emailConfirmationService.addEmailConfirmation(
                newAccount,
                null
        );
        Instant now = Instant.now();
        assertEquals(emailConfirmationFromNewAccount.getUserId(), emailConfirmation.getUserId());
        assertTrue(emailConfirmation.getExpiration().isBefore(now.plusSeconds(60)));
        assertNull(emailConfirmation.getNewEmail());
    }

    @Test
    void addEmailConfirmationWhenExistingAccount() throws VotifyException {
        when(userProperties.getEmailConfirmationExpirationMinutes()).thenReturn(1);
        when(emailConfirmationRepository.save(any(EmailConfirmation.class)))
                .thenAnswer(i -> i.getArguments()[0]);

        EmailConfirmation emailConfirmation = emailConfirmationService.addEmailConfirmation(
                existingAccount,
                Email.parseUnsafe("jhonny@new.nightcity.2077")
        );
        Instant now = Instant.now();
        assertEquals(emailConfirmationFromNewAccount.getUserId(), emailConfirmation.getUserId());
        assertTrue(emailConfirmation.getExpiration().isBefore(now.plusSeconds(60)));
        assertEquals("jhonny@new.nightcity.2077", emailConfirmation.getNewEmail().getValue());
    }
}
