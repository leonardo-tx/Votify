package br.com.votify.core.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import br.com.votify.core.utils.email.EmailMessageCode;

@Service
public class EmailService {
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username:}")
    private String senderEmail;

    @Autowired(required = false)
    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmailConfirmation(String receiverEmail, String confirmationCode) {
        if (senderEmail == null || senderEmail.trim().isEmpty() || mailSender == null) {
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(receiverEmail);
        message.setSubject(EmailMessageCode.EMAIL_CONFIRMATION.getSubject());
        message.setText(EmailMessageCode.EMAIL_CONFIRMATION.formatMessage(confirmationCode));
        mailSender.send(message);
    }

    public void sendPasswordResetEmail(String receiverEmail, String resetCode) {
        if (senderEmail == null || senderEmail.trim().isEmpty() || mailSender == null) {
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(receiverEmail);
        message.setSubject(EmailMessageCode.PASSWORD_RESET.getSubject());
        message.setText(EmailMessageCode.PASSWORD_RESET.formatMessage(resetCode));
        mailSender.send(message);
    }
}