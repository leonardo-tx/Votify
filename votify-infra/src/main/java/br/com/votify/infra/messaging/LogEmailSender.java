package br.com.votify.infra.messaging;

import br.com.votify.core.model.user.User;
import br.com.votify.core.service.messaging.EmailSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("!prod")
@Slf4j
public class LogEmailSender implements EmailSender {
    @Override
    public void sendEmail(User user, String subject, String message) {
        log.info("[DEV] Email sent to: {}", user.getEmail().getValue());
        log.info("[DEV] Subject: {}", subject);
        log.info("[DEV] Body: {}", message);
    }
}
