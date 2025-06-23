package br.com.votify.infra.messaging;

import br.com.votify.core.model.user.User;
import br.com.votify.core.service.messaging.EmailSender;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("prod")
public class SmtpEmailSender implements EmailSender {
    @Override
    public void sendEmail(User user, String subject, String message) {
        // TODO: Send e-mail to the user.
    }
}
