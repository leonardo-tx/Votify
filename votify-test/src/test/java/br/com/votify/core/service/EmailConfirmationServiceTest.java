package br.com.votify.core.service;

import br.com.votify.core.domain.entities.tokens.EmailConfirmation;
import br.com.votify.core.domain.entities.users.CommonUser;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.repository.EmailConfirmationRepository;
import br.com.votify.core.utils.EmailCodeGeneratorUtils;
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

    @InjectMocks
    private EmailConfirmationService emailConfirmationService;

    @Mock
    private EmailConfirmationRepository emailConfirmationRepository;

    private EmailConfirmation emailConfirmation;

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

        this.emailConfirmation = new EmailConfirmation(
                1L,
                user,
                code,
                LocalDateTime.now().plusMinutes(30)
                );
    }

    @Test
    public void deleteEmailConfirmationById() {
        doNothing().when(emailConfirmationRepository).deleteById(anyLong());
        assertDoesNotThrow(() -> emailConfirmationService.deleteById(emailConfirmation.getId()));
    }

    @Test
    public void confirmEmailInvalidCodeShouldThrowException() {
        when(emailConfirmationRepository.findByUserEmail(anyString())).thenReturn(Optional.of(emailConfirmation));

        assertThrows(VotifyException.class,() -> emailConfirmationService.confirmEmail("wrong-code",emailConfirmation.getUser().getEmail()));
    }

    @Test
    public void findExpiratedAccountsShouldReturnOne() {
        when(emailConfirmationRepository.findAllExpirated(any(LocalDateTime.class)))
                .thenReturn(List.of(emailConfirmation));

        List<EmailConfirmation> result = emailConfirmationService.findExpiredAccounts();

        assertEquals(result.get(0).getEmailConfirmationCode(), emailConfirmation.getEmailConfirmationCode());
    }
}
