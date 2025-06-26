package br.com.votify.infra.messaging;

import br.com.votify.core.model.user.User;
import br.com.votify.core.model.user.field.Email;
import br.com.votify.core.properties.email.EmailProperties;
import br.com.votify.core.utils.exceptions.VotifyException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SmtpEmailSenderTest {
    @Mock
    private JavaMailSender mailSender;

    @Mock
    private EmailProperties emailProperties;

    @InjectMocks
    private SmtpEmailSender emailService;

    @Test
    void sendEmail() throws VotifyException {
        User user = mock(User.class);
        when(user.getEmail()).thenReturn(new Email("user@mail.com.br"));

        when(emailProperties.getUsername()).thenReturn("mail@votify.com.br");

        emailService.sendEmail(user, "Test Subject", "Test Message Body");

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}
