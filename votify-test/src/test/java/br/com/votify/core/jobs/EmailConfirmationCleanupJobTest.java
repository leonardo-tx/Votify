package br.com.votify.core.jobs;

import br.com.votify.core.domain.entities.tokens.EmailConfirmation;
import br.com.votify.core.domain.entities.users.CommonUser;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.repository.EmailConfirmationRepository;
import br.com.votify.core.service.EmailConfirmationService;
import br.com.votify.core.utils.EmailCodeGeneratorUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailConfirmationCleanupJobTest {

    @InjectMocks
    private EmailConfirmationCleanupJob emailConfirmationCleanupJob;
    @Mock
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
    void deleteExpiratedUsersAccountShouldDeleteOneAccount() {
        when(emailConfirmationService.findExpiredAccounts()).thenReturn(List.of(emailConfirmation));

        doNothing().when(emailConfirmationService).deleteById(anyLong());

        emailConfirmationCleanupJob.deleteExpiratedUsersAccount();

        verify(emailConfirmationService, times(1)).deleteById(emailConfirmation.getId());


    }
}