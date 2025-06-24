package br.com.votify.core.job.user;

import br.com.votify.core.model.user.EmailConfirmation;
import br.com.votify.core.model.user.User;
import br.com.votify.core.model.user.field.Email;
import br.com.votify.core.service.user.EmailConfirmationService;
import br.com.votify.core.service.user.UserService;
import br.com.votify.core.utils.exceptions.VotifyException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailConfirmationCleanupJobTest {
    @Mock
    private EmailConfirmationService emailConfirmationService;

    @Mock
    private UserService userService;

    @InjectMocks
    private EmailConfirmationCleanupJob emailConfirmationCleanupJob;

    @Test
    void deleteExpiredUsersAccountShouldDeleteOneAccount() throws VotifyException {
        User nonActiveUser = mock(User.class);
        EmailConfirmation newAccountEmailConfirmation = mock(EmailConfirmation.class);
        EmailConfirmation editEmailConfirmation = mock(EmailConfirmation.class);

        when(newAccountEmailConfirmation.getNewEmail()).thenReturn(null);
        when(newAccountEmailConfirmation.getUserId()).thenReturn(2L);
        when(editEmailConfirmation.getNewEmail()).thenReturn(mock(Email.class));

        when(emailConfirmationService.findExpiredAccounts()).thenReturn(
                List.of(newAccountEmailConfirmation, editEmailConfirmation)
        );
        when(userService.getUserById(2L)).thenReturn(nonActiveUser);

        emailConfirmationCleanupJob.manageExpiredEmailConfirmations();

        verify(emailConfirmationService, times(1)).delete(editEmailConfirmation);
        verify(userService, times(1)).delete(nonActiveUser);
    }
}