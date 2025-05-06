package br.com.votify.core.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username:}")
    private String senderEmail;

    public void sendEmailConfirmation(String receiverEmail, String confirmationCode) {

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