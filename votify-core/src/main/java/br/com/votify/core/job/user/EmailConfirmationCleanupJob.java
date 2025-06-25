package br.com.votify.core.job.user;

import br.com.votify.core.model.user.EmailConfirmation;
import br.com.votify.core.model.user.User;
import br.com.votify.core.service.user.EmailConfirmationService;
import br.com.votify.core.service.user.UserService;
import br.com.votify.core.utils.exceptions.VotifyException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EmailConfirmationCleanupJob {
    private final EmailConfirmationService emailConfirmationService;
    private final UserService userService;

    @Scheduled(cron = "${app.user.email-confirmation-manager-job}")
    @Transactional
    public void manageExpiredEmailConfirmations() throws VotifyException {
        List<EmailConfirmation> usersExpired = emailConfirmationService.findExpiredAccounts();
        for (EmailConfirmation emailConfirmation : usersExpired) {
            if (emailConfirmation.getNewEmail() == null) {
                User user = userService.getUserById(emailConfirmation.getUserId());
                userService.delete(user);
                continue;
            }
            emailConfirmationService.delete(emailConfirmation);
        }
    }
}
