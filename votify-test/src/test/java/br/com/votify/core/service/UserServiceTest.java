package br.com.votify.core.service;

import br.com.votify.api.VotifyApiApplication;
import br.com.votify.api.configuration.SecurityConfig;
import br.com.votify.core.domain.entities.users.AdminUser;
import br.com.votify.core.domain.entities.users.CommonUser;
import br.com.votify.core.domain.entities.users.ModeratorUser;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = { VotifyApiApplication.class, SecurityConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @Test
    @Order(0)
    public void createValidCommonUser() {
        User user = new CommonUser(
            null,
            "valid-username",
            "Marcos Castro",
            "marcos@proton.me",
            "12345678abacaxi#"
        );
        User userFromService = assertDoesNotThrow(() -> userService.createUser(user));
        assertEquals(1, userFromService.getId());
    }

    @Test
    @Order(1)
    public void createValidModeratorUser() {
        User user = new ModeratorUser(
            2L,
            "silverhand",
            "Jhonny Silverhand",
            "jhonny@nightcity.2077",
            "6Samurai6"
        );
        User userFromService = assertDoesNotThrow(() -> userService.createUser(user));
        assertEquals(2, userFromService.getId());
    }

    @Test
    @Order(2)
    public void createValidAdminUser() {
        User user = new AdminUser(
            2342342L,
            "arthurzinho-gameplays",
            "Arthur Cervero",
            "arthurgatinho@gmail.com",
            "angelofthenight"
        );
        User userFromService = assertDoesNotThrow(() -> userService.createUser(user));
        assertEquals(3, userFromService.getId());
    }

    @Test
    @Order(3)
    public void createUserWithEmailAlreadyExists() {
        User user = new CommonUser(
            null,
            "arthurzinho",
            "Arthur Garcia",
            "arthurgatinho@gmail.com",
            "aeaeaeaeaeaeaea"
        );
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> userService.createUser(user)
        );
        assertEquals(VotifyErrorCode.EMAIL_ALREADY_EXISTS, exception.getErrorCode());
    }

    @Test
    @Order(3)
    public void createUserWithUserNameAlreadyExists() {
        User user = new CommonUser(
            null,
            "silverhand",
            "Fã de Cyberpunk \uD83D\uDE0E",
            "silverhand@outlook.com",
            "yeeeeeeeey765"
        );
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> userService.createUser(user)
        );
        assertEquals(VotifyErrorCode.USER_NAME_ALREADY_EXISTS, exception.getErrorCode());
    }

    @Test
    @Order(4)
    public void getAll() {
        assertEquals(3, userService.getAll().size());
    }

    @Test
    @Order(4)
    public void getUserById() {
        User user = assertDoesNotThrow(() -> userService.getUserById(1));
        assertEquals("marcos@proton.me", user.getEmail());
    }

    @Test
    @Order(4)
    public void getNonExistentUser() {
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> userService.getUserById(10)
        );
        assertEquals(VotifyErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @Order(4)
    public void checkPasswordMatchFromCreatedUser() throws VotifyException {
        User user = userService.getUserById(1);
        assertTrue(userService.checkPassword(user, "12345678abacaxi#"));
    }

    @Test
    @Order(4)
    public void checkInvalidPasswordFromCreatedUser() throws VotifyException {
        User user = userService.getUserById(2);
        assertFalse(userService.checkPassword(user, "12345678abacaxi#"));
    }
}
