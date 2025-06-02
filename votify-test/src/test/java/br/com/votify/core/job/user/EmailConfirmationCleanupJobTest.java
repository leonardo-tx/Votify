package br.com.votify.core.job.user;

import br.com.votify.core.model.user.EmailConfirmation;
import br.com.votify.core.model.user.User;
import br.com.votify.core.model.user.UserRole;
import br.com.votify.core.model.user.field.ConfirmationCode;
import br.com.votify.core.model.user.field.Email;
import br.com.votify.core.model.user.field.Name;
import br.com.votify.core.model.user.field.UserName;
import br.com.votify.core.service.user.EmailConfirmationService;
import br.com.votify.core.service.user.UserService;
import br.com.votify.core.utils.exceptions.VotifyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailConfirmationCleanupJobTest {
    @Mock
    private EmailConfirmationService emailConfirmationService;

    @Mock
    private UserService userService;

    @InjectMocks
    private EmailConfirmationCleanupJob emailConfirmationCleanupJob;

    private final List<User> users = new ArrayList<>();
    private final List<EmailConfirmation> emailConfirmations = new ArrayList<>();

    @BeforeEach
    public void setupBeforeEach() {
        users.add(User.parseUnsafe(
                1L,
                Email.parseUnsafe("jhonny@nightcity.2077"),
                UserName.parseUnsafe("silverhand"),
                Name.parseUnsafe("Jhonny Silverhand"),
                "6Samurai6",
                UserRole.COMMON,
                false
        ));
        users.add(User.parseUnsafe(
                2L,
                Email.parseUnsafe("astarion@bg3.gate"),
                UserName.parseUnsafe("astarion"),
                Name.parseUnsafe("Astarion"),
                "mydarling",
                UserRole.COMMON,
                true
        ));

        emailConfirmations.add(EmailConfirmation.parseUnsafe(
                ConfirmationCode.parseUnsafe("new_user"),
                null,
                Instant.now().minusSeconds(1800),
                users.get(0).getId()
        ));
        emailConfirmations.add(EmailConfirmation.parseUnsafe(
                ConfirmationCode.parseUnsafe("updated_user"),
                Email.parseUnsafe("astarion@bg3.larian"),
                Instant.now().minusSeconds(1800),
                users.get(1).getId()
        ));
    }

    @Test
    void deleteExpiredUsersAccountShouldDeleteOneAccount() throws VotifyException {
        when(emailConfirmationService.findExpiredAccounts()).thenReturn(emailConfirmations);
        doNothing().when(emailConfirmationService).delete(any(EmailConfirmation.class));

        emailConfirmationCleanupJob.manageExpiredEmailConfirmations();

        verify(emailConfirmationService, times(1)).delete(emailConfirmations.get(1));
        verify(userService, times(1)).delete(users.get(0));
    }
}