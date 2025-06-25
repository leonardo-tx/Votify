package br.com.votify.core.model.user;

import br.com.votify.core.model.user.field.Email;
import br.com.votify.core.model.user.field.Name;
import br.com.votify.core.model.user.field.Password;
import br.com.votify.core.model.user.field.UserName;
import br.com.votify.core.service.user.PasswordEncoderService;
import br.com.votify.core.utils.exceptions.VotifyException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserTest {
    @Test
    void testValidUserConstruct() throws VotifyException {
        UserRegister userRegister = new UserRegister(
                "littledoge",
                "Leonardo Teixeira",
                "123@gmail.com",
                "19283784you"
        );

        PasswordEncoderService passwordEncoderService = mock(PasswordEncoderService.class);
        when(passwordEncoderService.encryptPassword(userRegister.getPassword())).thenReturn("encrypted_password");

        User user = assertDoesNotThrow(() -> new User(passwordEncoderService, userRegister));
        assertEquals(PermissionFlags.NONE, user.getPermissions());
        assertEquals(userRegister.getEmail(), user.getEmail());
        assertEquals(userRegister.getUserName(), user.getUserName());
        assertEquals(userRegister.getName(), user.getName());
        assertEquals("encrypted_password", user.getEncryptedPassword());
        assertEquals(UserRole.COMMON, user.getRole());
        assertFalse(user.isActive());
        assertNull(user.getId());
    }

    @Test
    void testUserConstructWithNullUserRegister() {
        PasswordEncoderService passwordEncoderService = mock(PasswordEncoderService.class);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new User(passwordEncoderService, null)
        );
        assertEquals("The user register must not be null.", exception.getMessage());
    }

    @Test
    void testUserConstructWithNullPasswordEncoder() {
        UserRegister userRegister = mock(UserRegister.class);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new User(null, userRegister)
        );
        assertEquals("The password encoder must not be null.", exception.getMessage());
    }

    @Test
    void testUserValidSetName() throws VotifyException {
        UserRegister userRegister = new UserRegister(
                "littledoge",
                "Leonardo Teixeira",
                "123@gmail.com",
                "19283784you"
        );
        PasswordEncoderService passwordEncoderService = mock(PasswordEncoderService.class);
        when(passwordEncoderService.encryptPassword(userRegister.getPassword())).thenReturn("encrypted_password");

        User user = assertDoesNotThrow(() -> new User(passwordEncoderService, userRegister));

        Name newName = new Name("Leonardo");
        user.setName(newName);

        assertEquals(newName, user.getName());
    }

    @Test
    void testUserSetNullName() throws VotifyException {
        UserRegister userRegister = new UserRegister(
                "littledoge",
                "Leonardo Teixeira",
                "123@gmail.com",
                "19283784you"
        );
        PasswordEncoderService passwordEncoderService = mock(PasswordEncoderService.class);
        when(passwordEncoderService.encryptPassword(userRegister.getPassword())).thenReturn("encrypted_password");

        User user = assertDoesNotThrow(() -> new User(passwordEncoderService, userRegister));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> user.setName(null)
        );
        assertEquals("Cannot set null to the name field.", exception.getMessage());
    }

    @Test
    void testUserValidSetEmail() throws VotifyException {
        UserRegister userRegister = new UserRegister(
                "littledoge",
                "Leonardo Teixeira",
                "123@gmail.com",
                "19283784you"
        );
        PasswordEncoderService passwordEncoderService = mock(PasswordEncoderService.class);
        when(passwordEncoderService.encryptPassword(userRegister.getPassword())).thenReturn("encrypted_password");

        User user = assertDoesNotThrow(() -> new User(passwordEncoderService, userRegister));

        Email newEmail = new Email("321@gmail.com");
        user.setEmail(newEmail);

        assertEquals(newEmail, user.getEmail());
    }

    @Test
    void testUserSetNullEmail() throws VotifyException {
        UserRegister userRegister = new UserRegister(
                "littledoge",
                "Leonardo Teixeira",
                "123@gmail.com",
                "19283784you"
        );
        PasswordEncoderService passwordEncoderService = mock(PasswordEncoderService.class);
        when(passwordEncoderService.encryptPassword(userRegister.getPassword())).thenReturn("encrypted_password");

        User user = assertDoesNotThrow(() -> new User(passwordEncoderService, userRegister));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> user.setEmail(null)
        );
        assertEquals("Cannot set null to the email field.", exception.getMessage());
    }

    @Test
    void testUserValidSetUserName() throws VotifyException {
        UserRegister userRegister = new UserRegister(
                "littledoge",
                "Leonardo Teixeira",
                "123@gmail.com",
                "19283784you"
        );
        PasswordEncoderService passwordEncoderService = mock(PasswordEncoderService.class);
        when(passwordEncoderService.encryptPassword(userRegister.getPassword())).thenReturn("encrypted_password");

        User user = assertDoesNotThrow(() -> new User(passwordEncoderService, userRegister));

        UserName newUserName = new UserName("littlecat");
        user.setUserName(newUserName);

        assertEquals(newUserName, user.getUserName());
    }

    @Test
    void testUserSetNullUserName() throws VotifyException {
        UserRegister userRegister = new UserRegister(
                "littledoge",
                "Leonardo Teixeira",
                "123@gmail.com",
                "19283784you"
        );
        PasswordEncoderService passwordEncoderService = mock(PasswordEncoderService.class);
        when(passwordEncoderService.encryptPassword(userRegister.getPassword())).thenReturn("encrypted_password");

        User user = assertDoesNotThrow(() -> new User(passwordEncoderService, userRegister));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> user.setUserName(null)
        );
        assertEquals("Cannot set null to the userName field.", exception.getMessage());
    }

    @Test
    void testUserValidSetPassword() throws VotifyException {
        UserRegister userRegister = new UserRegister(
                "littledoge",
                "Leonardo Teixeira",
                "123@gmail.com",
                "19283784you"
        );
        PasswordEncoderService passwordEncoderService = mock(PasswordEncoderService.class);
        when(passwordEncoderService.encryptPassword(userRegister.getPassword())).thenReturn("encrypted_password");

        User user = assertDoesNotThrow(() -> new User(passwordEncoderService, userRegister));

        Password newPassword = new Password("12345678");
        when(passwordEncoderService.encryptPassword(newPassword)).thenReturn("new_encrypted_password");

        user.setPassword(passwordEncoderService, newPassword);

        assertEquals("new_encrypted_password", user.getEncryptedPassword());
    }

    @Test
    void testUserSetNullPassword() throws VotifyException {
        UserRegister userRegister = new UserRegister(
                "littledoge",
                "Leonardo Teixeira",
                "123@gmail.com",
                "19283784you"
        );
        PasswordEncoderService passwordEncoderService = mock(PasswordEncoderService.class);
        when(passwordEncoderService.encryptPassword(userRegister.getPassword())).thenReturn("encrypted_password");

        User user = assertDoesNotThrow(() -> new User(passwordEncoderService, userRegister));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> user.setPassword(passwordEncoderService, null)
        );
        assertEquals("Cannot set null to the password field.", exception.getMessage());
    }

    @Test
    void testUserSetPasswordWithNullPasswordEncoder() throws VotifyException {
        UserRegister userRegister = new UserRegister(
                "littledoge",
                "Leonardo Teixeira",
                "123@gmail.com",
                "19283784you"
        );
        PasswordEncoderService passwordEncoderService = mock(PasswordEncoderService.class);
        when(passwordEncoderService.encryptPassword(userRegister.getPassword())).thenReturn("encrypted_password");

        User user = assertDoesNotThrow(() -> new User(passwordEncoderService, userRegister));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> user.setPassword(null, new Password("12345678"))
        );
        assertEquals("The password encoder must not be null.", exception.getMessage());
    }

    @Test
    void testParseUnsafe() throws VotifyException {
        User user = User.parseUnsafe(
                6L,
                new Email("mail@mail.com"),
                new UserName("username"),
                new Name("Name"),
                "encrypted_password",
                UserRole.ADMIN,
                true
        );
        assertEquals(6L, user.getId());
        assertEquals("mail@mail.com", user.getEmail().getValue());
        assertEquals("username", user.getUserName().getValue());
        assertEquals("Name", user.getName().getValue());
        assertEquals("encrypted_password", user.getEncryptedPassword());
        assertEquals(UserRole.ADMIN, user.getRole());
        assertTrue(user.isActive());
        assertEquals(PermissionFlags.ALL, user.getPermissions());
    }

    @Test
    void testHasPermissionCommonUser() throws VotifyException {
        User user = User.parseUnsafe(
                6L,
                new Email("mail@mail.com"),
                new UserName("username"),
                new Name("Name"),
                "encrypted_password",
                UserRole.COMMON,
                true
        );
        assertFalse(user.hasPermission(PermissionFlags.DETAILED_USER));
    }

    @Test
    void testHasPermissionModeratorUser() throws VotifyException {
        User user = User.parseUnsafe(
                6L,
                new Email("mail@mail.com"),
                new UserName("username"),
                new Name("Name"),
                "encrypted_password",
                UserRole.MODERATOR,
                true
        );
        assertTrue(user.hasPermission(PermissionFlags.DETAILED_USER));
    }

    @Test
    void testHasPermissionAdminUser() throws VotifyException {
        User user = User.parseUnsafe(
                6L,
                new Email("mail@mail.com"),
                new UserName("username"),
                new Name("Name"),
                "encrypted_password",
                UserRole.ADMIN,
                true
        );
        assertTrue(user.hasPermission(PermissionFlags.DETAILED_USER));
    }
}
