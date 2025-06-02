package br.com.votify.core.domain.entities.password;

import br.com.votify.core.model.user.PasswordReset;
import br.com.votify.infra.persistence.user.UserEntity;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordResetTest {
    @Test
    public void testIsExpired_WhenExpired() {
        Date pastDate = new Date(System.currentTimeMillis() - 1000);
        UserEntity user = new CommonUser();
        PasswordReset token = new PasswordReset("CODE", user, pastDate);
        assertTrue(token.isExpired());
    }

    @Test
    public void testIsExpired_WhenNotExpired() {
        Date futureDate = new Date(System.currentTimeMillis() + 600000);
        UserEntity user = new CommonUser();
        PasswordReset token = new PasswordReset("CODE", user, futureDate);
        assertFalse(token.isExpired());
    }

    @Test
    public void testGettersAndSetters() {
        UserEntity user = new CommonUser();
        Date expiryDate = new Date();
        PasswordReset token = new PasswordReset("CODE1", user, expiryDate);

        assertEquals("CODE1", token.getCode());
        assertEquals(user, token.getUser());
        assertEquals(expiryDate, token.getExpiryDate());

        UserEntity newUser = new CommonUser();
        Date newExpiryDate = new Date(expiryDate.getTime() + 10000);

        token.setCode("CODE2");
        token.setUser(newUser);
        token.setExpiryDate(newExpiryDate);

        assertEquals("CODE2", token.getCode());
        assertEquals(newUser, token.getUser());
        assertEquals(newExpiryDate, token.getExpiryDate());
    }
}