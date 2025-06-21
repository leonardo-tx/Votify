package br.com.votify.core.model.user;

import br.com.votify.core.utils.exceptions.VotifyException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserRegisterTest {
    @Test
    void testValidUser() {
        UserRegister userRegister = assertDoesNotThrow(() -> new UserRegister(
                "littledoge",
                "Leonardo Teixeira",
                "123@gmail.com",
                "19283784you"
        ));
        assertEquals("littledoge", userRegister.getUserName().getValue());
        assertEquals("Leonardo Teixeira", userRegister.getName().getValue());
        assertEquals("123@gmail.com", userRegister.getEmail().getValue());
        assertEquals("19283784you", userRegister.getPassword().getValue());
    }

    @Test
    void testInvalidUser() {
        assertThrows(VotifyException.class, () -> new UserRegister(
                "@# littledoge",
                "     ",
                "12345",
                "123"
        ));
    }
}
