package br.com.votify.core.service;

import br.com.votify.core.domain.entities.tokens.AuthTokens;
import br.com.votify.core.domain.entities.users.*;
import br.com.votify.core.repository.UserRepository;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTest {
    private static final List<User> users = new ArrayList<>();
    private static UserService userService;
    private static ContextService contextService;
    private static Long entityId = 1L;

    @BeforeAll
    public static void prepareBeforeAll() {
        contextService = mock(ContextService.class);
        PasswordEncoderService passwordEncoderService = new PasswordEncoderService();
        UserRepository userRepository = mock(UserRepository.class);
        TokenService tokenService = mock(TokenService.class);

        when(userRepository.existsByEmail(any(String.class))).thenAnswer((invocation) -> {
            String email = invocation.getArgument(0);
            for (User user : users) {
                if (Objects.equals(user.getEmail(), email)) return true;
            }
            return false;
        });
        when(userRepository.existsByUserName(any(String.class))).thenAnswer((invocation) -> {
            String userName = invocation.getArgument(0);
            for (User user : users) {
                if (Objects.equals(user.getUserName(), userName)) return true;
            }
            return false;
        });
        when(userRepository.findById(any(Long.class))).thenAnswer((invocation) -> {
            Long id = invocation.getArgument(0);
            for (User user : users) {
                if (Objects.equals(user.getId(), id)) return Optional.of(user);
            }
            return Optional.empty();
        });
        when(userRepository.findByEmail(any(String.class))).thenAnswer((invocation) -> {
            String email = invocation.getArgument(0);
            for (User user : users) {
                if (Objects.equals(user.getEmail(), email)) return Optional.of(user);
            }
            return Optional.empty();
        });
        doAnswer((invocation) -> {
            Long id = invocation.getArgument(0);
            for (int i = 0; i < users.size(); i++) {
                User user = users.get(i);
                if (Objects.equals(user.getId(), id)) {
                    users.remove(i);
                    return null;
                }
            }
            return null;
        }).when(userRepository).deleteById(any(Long.class));
        when(userRepository.save(any(User.class))).thenAnswer((invocation) -> {
            User createdUser = invocation.getArgument(0);
            for (int i = 0; i < users.size(); i++) {
                User user = users.get(i);
                if (Objects.equals(user.getId(), createdUser.getId())) {
                    users.set(i, createdUser);
                    return createdUser;
                }
            }
            createdUser.setId(entityId++);
            users.add(createdUser);
            return createdUser;
        });
        when(userRepository.findAll()).thenAnswer((invocation) -> users);
        userService = new UserService(contextService, passwordEncoderService, userRepository, tokenService);
    }

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
        User user = assertDoesNotThrow(() -> userService.getUserById(2));
        assertEquals("jhonny@nightcity.2077", user.getEmail());
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
    public void login() {
        AuthTokens authTokens = assertDoesNotThrow(
            () -> userService.login("jhonny@nightcity.2077", "6Samurai6")
        );
        assertNotNull(authTokens);
    }

    @Test
    @Order(4)
    public void incorretPasswordLogin() {
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> userService.login("jhonny@nightcity.2077", "6Samurai7")
        );
        assertEquals(VotifyErrorCode.LOGIN_UNAUTHORIZED, exception.getErrorCode());
    }

    @Test
    @Order(4)
    public void incorretEmailLogin() {
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> userService.login("jhonny@nightcity.2076", "6Samurai6")
        );
        assertEquals(VotifyErrorCode.LOGIN_UNAUTHORIZED, exception.getErrorCode());
    }

    @Test
    @Order(4)
    public void allIncorrectLogin() {
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> userService.login("jhonny@nightcity.2076", "6Samurai7")
        );
        assertEquals(VotifyErrorCode.LOGIN_UNAUTHORIZED, exception.getErrorCode());
    }

    @Test
    @Order(5)
    public void loginAlreadyLogged() {
        when(contextService.isAuthenticated()).thenReturn(true);
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> userService.login("arthurgatinho@gmail.com", "angelofthenight")
        );
        assertEquals(VotifyErrorCode.LOGIN_ALREADY_LOGGED, exception.getErrorCode());
    }

    @Test
    @Order(6)
    public void deleteUser() throws VotifyException {
        when(contextService.getUserOrThrow()).thenReturn(
            new CommonUser(1L, null, null, null, null)
        );
        assertDoesNotThrow(() -> userService.deleteUser(1L));
    }

    @Test
    @Order(6)
    public void deleteUserUnauthorized() throws VotifyException {
        when(contextService.getUserOrThrow()).thenReturn(
            new CommonUser(3L, null, null, null, null)
        );
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> userService.deleteUser(2L)
        );
        assertEquals(VotifyErrorCode.USER_DELETE_UNAUTHORIZED, exception.getErrorCode());
    }
}