package br.com.votify.infra.messaging;

import br.com.votify.core.model.user.User;
import br.com.votify.core.model.user.field.Email;
import br.com.votify.core.utils.exceptions.VotifyException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogEmailSenderTest {
    @InjectMocks
    private LogEmailSender logEmailSender;

    @Test
    void sendEmail() throws VotifyException {
        User user = mock(User.class);
        when(user.getEmail()).thenReturn(new Email("user@mail.com.br"));

        logEmailSender.sendEmail(user, "Test Subject", "Test Message Body");
    }
}
