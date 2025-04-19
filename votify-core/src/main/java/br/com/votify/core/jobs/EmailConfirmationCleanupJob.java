package br.com.votify.core.jobs;

import br.com.votify.core.domain.entities.tokens.EmailConfirmation;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.service.EmailConfirmationService;
import br.com.votify.core.service.UserService;
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

    @Scheduled(cron = "${app.email-confirmation.manager-job}")
    @Transactional
    public void manageExpiredEmailConfirmations() {
        List<EmailConfirmation> usersExpired = emailConfirmationService.findExpiredAccounts();
        for (EmailConfirmation emailConfirmation : usersExpired) {
            if (emailConfirmation.getNewEmail() == null) {
                userService.deleteUser(emailConfirmation.getUser());
                continue;
            }
            emailConfirmationService.deleteById(emailConfirmation.getId());
        }
    }
}
