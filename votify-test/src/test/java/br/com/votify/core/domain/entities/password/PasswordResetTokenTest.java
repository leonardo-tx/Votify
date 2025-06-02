package br.com.votify.core.domain.entities.password;

import br.com.votify.core.domain.entities.users.CommonUser;
import org.junit.jupiter.api.Test;

import java.util.Date;

public class PasswordResetTokenTest {

    @Test
    public void testIsExpired_WhenExpired() {
        Date pastDate = new Date(System.currentTimeMillis() - 1000);
        User user = new CommonUser();
        PasswordResetToken token = new PasswordResetToken("CODE", user, pastDate);
        assertTrue(token.isExpired());
    }

    @Test
    public void testIsExpired_WhenNotExpired() {
        Date futureDate = new Date(System.currentTimeMillis() + 600000);
        User user = new CommonUser();
        PasswordResetToken token = new PasswordResetToken("CODE", user, futureDate);
        assertFalse(token.isExpired());
    }

    @Test
    public void testGettersAndSetters() {
        User user = new CommonUser();
        Date expiryDate = new Date();
        PasswordResetToken token = new PasswordResetToken("CODE1", user, expiryDate);

        assertEquals("CODE1", token.getCode());
        assertEquals(user, token.getUser());
        assertEquals(expiryDate, token.getExpiryDate());

        User newUser = new CommonUser();
        Date newExpiryDate = new Date(expiryDate.getTime() + 10000);

        token.setCode("CODE2");
        token.setUser(newUser);
        token.setExpiryDate(newExpiryDate);

        assertEquals("CODE2", token.getCode());
        assertEquals(newUser, token.getUser());
        assertEquals(newExpiryDate, token.getExpiryDate());
    }
}