package br.com.votify.core.service.messaging;

import br.com.votify.core.model.user.User;

public interface EmailSender {
    void sendEmail(User user, String subject, String message);
}
