package br.com.votify.core.service.user;

import br.com.votify.core.model.user.User;
import br.com.votify.core.model.user.field.Password;
import br.com.votify.core.utils.exceptions.VotifyException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordEncoderServiceTest {
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordEncoderService passwordEncoderService;

    @Test
    void testEncryptPassword() throws VotifyException {
        Password password = new Password("password");
        when(passwordEncoder.encode(password.getValue())).thenReturn("encrypted");

        String encryptedPassword = passwordEncoderService.encryptPassword(password);
        assertEquals("encrypted", encryptedPassword);
    }

    @Test
    void testCheckPasswordSuccess() throws VotifyException {
        User user = mock(User.class);
        Password password = new Password("password");

        when(user.getEncryptedPassword()).thenReturn("encrypted");
        when(passwordEncoder.matches(password.getValue(), "encrypted")).thenReturn(true);

        boolean result = passwordEncoderService.checkPassword(user, password);
        assertTrue(result);
    }

    @Test
    void testCheckPasswordFail() throws VotifyException {
        User user = mock(User.class);
        Password password = new Password("password");

        when(user.getEncryptedPassword()).thenReturn("encrypted");
        when(passwordEncoder.matches(password.getValue(), "encrypted")).thenReturn(false);

        boolean result = passwordEncoderService.checkPassword(user, password);
        assertFalse(result);
    }
}
