package br.com.votify.core.jobs;

import br.com.votify.core.domain.entities.tokens.EmailConfirmation;
import br.com.votify.core.service.EmailConfirmationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EmailConfirmationCleanupJob {

    private final EmailConfirmationService emailConfirmationService;

    @Scheduled(cron = "${app.email-confirmation.delete-expirated-accounts}")
    public void deleteExpiratedUsersAccount() {
        List<EmailConfirmation> usersExpirated = emailConfirmationService.findExpiredAccounts();
        usersExpirated.forEach(emailConfirmation -> emailConfirmationService.deleteById(emailConfirmation.getId()));
    }

}
