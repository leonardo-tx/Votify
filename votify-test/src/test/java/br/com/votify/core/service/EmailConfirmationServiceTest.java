package br.com.votify.core.service;

import br.com.votify.core.domain.entities.tokens.EmailConfirmation;
import br.com.votify.core.domain.entities.tokens.EmailConfirmationExpirationProperties;
import br.com.votify.core.domain.entities.tokens.EmailProperties;
import br.com.votify.core.domain.entities.users.CommonUser;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.repository.EmailConfirmationRepository;
import br.com.votify.core.repository.UserRepository;
import br.com.votify.core.utils.EmailCodeGeneratorUtils;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailConfirmationServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ContextService contextService;

    @Mock
    private EmailConfirmationRepository emailConfirmationRepository;

    @Mock
    private EmailConfirmationExpirationProperties emailConfirmationExpirationProperties;

    @Mock
    private EmailService emailService;

    @Mock
    private EmailProperties emailProperties;

    @InjectMocks
    private EmailConfirmationService emailConfirmationService;

    private EmailConfirmation emailConfirmationFromNewAccount;
    private EmailConfirmation emailConfirmationFromExistingAccount;

    @BeforeEach
    public void setupBeforeEach() {
        User user = new CommonUser(
                1L,
                "silverhand",
                "Jhonny Silverhand",
                "jhonny@nightcity.2077",
                "6Samurai6"
        );

        String code = EmailCodeGeneratorUtils.generateEmailConfirmationCode();

        this.emailConfirmationFromNewAccount = new EmailConfirmation(
                1L,
                user,
                null,
                code,
                LocalDateTime.now().plusMinutes(30)
        );
        this.emailConfirmationFromExistingAccount = new EmailConfirmation(
                1L,
                user,
                "jhonny@new.nightcity.2077",
                code,
                LocalDateTime.now().plusMinutes(30)
        );
    }

    @Test
    public void deleteEmailConfirmationById() {
        doNothing().when(emailConfirmationRepository).deleteById(anyLong());
        assertDoesNotThrow(
                () -> emailConfirmationService.deleteById(emailConfirmationFromNewAccount.getId())
        );
    }

    @Test
    public void confirmEmailInvalidCodeFromNewUserShouldThrowException() {
        when(emailConfirmationRepository.findByUserEmail(anyString())).thenReturn(Optional.of(emailConfirmationFromNewAccount));
        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> emailConfirmationService.confirmEmail(
                        "wrong-code",
                        emailConfirmationFromNewAccount.getUser().getEmail()
                )
        );
        assertEquals(VotifyErrorCode.EMAIL_CONFIRMATION_CODE_INVALID, exception.getErrorCode());
        verifyNoInteractions(userRepository);
        verifyNoInteractions(contextService);
    }

    @Test
    public void confirmEmailFromNewUser() {
        when(emailConfirmationRepository.findByUserEmail(anyString())).thenReturn(Optional.of(emailConfirmationFromNewAccount));

        assertDoesNotThrow(() -> emailConfirmationService.confirmEmail(
                emailConfirmationFromNewAccount.getEmailConfirmationCode(),
                emailConfirmationFromNewAccount.getUser().getEmail()
        ));
        verify(emailConfirmationRepository).delete(emailConfirmationFromNewAccount);
        verifyNoInteractions(userRepository);
        verifyNoInteractions(contextService);
    }

    @Test
    public void confirmEmailInvalidCodeFromExistingUserShouldThrowException() throws VotifyException {
        when(emailConfirmationRepository.findByUserEmail(anyString())).thenReturn(Optional.of(emailConfirmationFromExistingAccount));
        when(contextService.getUserOrThrow()).thenReturn(emailConfirmationFromExistingAccount.getUser());
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
    public void confirmEmailFromExistingUser() throws VotifyException {
        when(emailConfirmationRepository.findByUserEmail(anyString())).thenReturn(Optional.of(emailConfirmationFromExistingAccount));
        when(contextService.getUserOrThrow()).thenReturn(emailConfirmationFromExistingAccount.getUser());
        when(userRepository.save(emailConfirmationFromExistingAccount.getUser())).thenReturn(emailConfirmationFromExistingAccount.getUser());

        assertDoesNotThrow(() -> emailConfirmationService.confirmEmail(
                emailConfirmationFromExistingAccount.getEmailConfirmationCode(),
                null
        ));
        verify(emailConfirmationRepository).delete(emailConfirmationFromExistingAccount);
    }

    @Test
    public void findExpiredAccountsShouldReturnOne() {
        when(emailConfirmationRepository.findAllExpirated(any(LocalDateTime.class)))
                .thenReturn(List.of(emailConfirmationFromNewAccount));

        List<EmailConfirmation> result = emailConfirmationService.findExpiredAccounts();

        assertEquals(result.get(0).getEmailConfirmationCode(), emailConfirmationFromNewAccount.getEmailConfirmationCode());
    }

    @Test
    public void existsByUserEmailTrue() {
        when(emailConfirmationRepository.existsByUserEmail("123@gmail.com")).thenReturn(true);
        assertTrue(emailConfirmationService.existsByEmail("123@gmail.com"));
    }

    @Test
    public void existsByUserEmailFalse() {
        when(emailConfirmationRepository.existsByUserEmail("321@gmail.com")).thenReturn(false);
        assertFalse(emailConfirmationService.existsByEmail("321@gmail.com"));
    }

    @Test
    public void addUserWhenNewAccount() throws VotifyException {
        when(emailConfirmationExpirationProperties.getExpirationMinutes()).thenReturn(1);
        when(emailProperties.isConfirmation()).thenReturn(true);
        when(emailConfirmationRepository.save(any(EmailConfirmation.class)))
                .thenAnswer(i -> i.getArguments()[0]);

        Optional<EmailConfirmation> emailConfirmationOptional = emailConfirmationService.addUser(
                emailConfirmationFromNewAccount.getUser(),
                null
        );
        assertTrue(emailConfirmationOptional.isPresent());
        EmailConfirmation emailConfirmation = emailConfirmationOptional.get();

        LocalDateTime now = LocalDateTime.now();
        assertNull(emailConfirmation.getId());
        assertEquals(emailConfirmationFromNewAccount.getUser(), emailConfirmation.getUser());
        assertTrue(emailConfirmation.getEmailConfirmationExpiration().isBefore(now.plusMinutes(1)));
        assertNull(emailConfirmation.getNewEmail());
        assertEquals(EmailCodeGeneratorUtils.CODE_LENGTH, emailConfirmation.getEmailConfirmationCode().length());
    }

    @Test
    public void addUserWhenExistingAccount() throws VotifyException {
        when(emailConfirmationExpirationProperties.getExpirationMinutes()).thenReturn(30);
        when(emailProperties.isConfirmation()).thenReturn(true);
        when(emailConfirmationRepository.save(any(EmailConfirmation.class)))
                .thenAnswer(i -> i.getArguments()[0]);

        Optional<EmailConfirmation> emailConfirmationOptional = emailConfirmationService.addUser(
                emailConfirmationFromExistingAccount.getUser(),
                "jhonny@new.nightcity.2077"
        );
        assertTrue(emailConfirmationOptional.isPresent());
        EmailConfirmation emailConfirmation = emailConfirmationOptional.get();

        LocalDateTime now = LocalDateTime.now();
        assertNull(emailConfirmation.getId());
        assertEquals(emailConfirmationFromNewAccount.getUser(), emailConfirmation.getUser());
        assertTrue(emailConfirmation.getEmailConfirmationExpiration().isBefore(now.plusMinutes(30)));
        assertEquals("jhonny@new.nightcity.2077", emailConfirmation.getNewEmail());
        assertEquals(EmailCodeGeneratorUtils.CODE_LENGTH, emailConfirmation.getEmailConfirmationCode().length());
    }
}
