package br.com.votify.core.utils.email;

import lombok.Getter;

@Getter
public enum EmailMessageCode {
    EMAIL_CONFIRMATION(
        "Votify - Confirmation Code",
            "Welcome to Votify! Please confirm your email by using the following code: %s\n\nThis code will expire in 30 minutes."
    ),
    PASSWORD_RESET(
        "Votify - Password Reset",
        "You requested a password reset. Use the following code to reset your password: %s\n\nIf you did not request this, please ignore this email. This code will expire soon."
    );

    private final String subject;
    private final String messageTemplate;

    EmailMessageCode(String subject, String messageTemplate) {
        this.subject = subject;
        this.messageTemplate = messageTemplate;
    }

    public String formatMessage(String... args) {
        return String.format(messageTemplate, args);
    }
} 