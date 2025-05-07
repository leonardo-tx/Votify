package br.com.votify.core.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

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
        message.setSubject("Votify - Confirmation Code");
        message.setText(String.format(
            "Welcome to Votify! Please confirm your email by using the following code: %s\n\n" +
            "This code will expire in 30 minutes.",
            confirmationCode
        ));
        mailSender.send(message);
    }
}