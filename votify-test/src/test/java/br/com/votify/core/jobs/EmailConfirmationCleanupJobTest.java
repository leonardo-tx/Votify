package br.com.votify.core.jobs;

import br.com.votify.core.domain.entities.tokens.EmailConfirmation;
import br.com.votify.core.domain.entities.users.CommonUser;
import br.com.votify.core.service.EmailConfirmationService;
import br.com.votify.core.utils.EmailCodeGeneratorUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailConfirmationCleanupJobTest {
    @Mock
    private EmailConfirmationService emailConfirmationService;

    @Mock
    private UserService userService;

    @InjectMocks
    private EmailConfirmationCleanupJob emailConfirmationCleanupJob;

    private final List<EmailConfirmation> emailConfirmations = new ArrayList<>();

    @BeforeEach
    public void setupBeforeEach() {
        User user = CommonUser.builder()
                .id(1L)
                .userName("silverhand")
                .name("Jhonny Silverhand")
                .email("jhonny@nightcity.2077")
                .password("6Samurai6")
                .build();
        User user2 = CommonUser.builder()
                .id(1L)
                .userName("astarion")
                .name("Astarion")
                .email("astarion@bg3.gate")
                .password("mydarling")
                .build();

        String code = EmailCodeGeneratorUtils.generateEmailConfirmationCode();
        emailConfirmations.add(new EmailConfirmation(
                1L,
                user,
                null,
                code,
                LocalDateTime.now().plusMinutes(30)
        ));
        emailConfirmations.add(new EmailConfirmation(
                2L,
                user2,
                "astarion@bg3.larian",
                code,
                LocalDateTime.now().plusMinutes(30)
        ));
    }

    @Test
    void deleteExpiredUsersAccountShouldDeleteOneAccount() {
        when(emailConfirmationService.findExpiredAccounts()).thenReturn(emailConfirmations);

        doNothing().when(emailConfirmationService).deleteById(anyLong());

        emailConfirmationCleanupJob.manageExpiredEmailConfirmations();

        verify(emailConfirmationService, times(1)).deleteById(anyLong());
        verify(userService, times(1)).deleteUser(emailConfirmations.get(0).getUser());
    }
}