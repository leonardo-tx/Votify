package br.com.votify.core.service;

import br.com.votify.core.domain.entities.tokens.AuthTokens;
import br.com.votify.core.domain.entities.tokens.RefreshToken;
import br.com.votify.core.domain.entities.users.*;
import br.com.votify.core.repository.RefreshTokenRepository;
import br.com.votify.core.repository.UserRepository;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTest {
    private static final List<User> users = new ArrayList<>();
    private static UserService userService;
    private static ContextService contextService;
    private static Long entityId = 1L;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private PasswordEncoderService passwordEncoderService;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private RefreshToken testRefreshToken;

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

    @BeforeEach
    void setUp() {
        testUser = new CommonUser(1L, "test-user", "Test User", "test@example.com", "password123");
        testRefreshToken = new RefreshToken("token123", null, testUser);
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
    void deleteUser_WhenUserExists_AndIsOwner_ShouldDeleteSuccessfully() throws VotifyException {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(contextService.getUserOrThrow()).thenReturn(testUser);
        when(refreshTokenRepository.findAllByUser(testUser))
            .thenReturn(Arrays.asList(testRefreshToken));

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(refreshTokenRepository).deleteAll(Arrays.asList(testRefreshToken));
        verify(userRepository).delete(testUser);
    }

    @Test
    void deleteUser_WhenUserDoesNotExist_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        VotifyException exception = assertThrows(VotifyException.class,
            () -> userService.deleteUser(1L));
        assertEquals(VotifyErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void deleteUser_WhenNotOwner_ShouldThrowException() throws VotifyException {
        // Arrange
        User otherUser = new CommonUser(2L, "other-user", "Other User", "other@example.com", "password123");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(contextService.getUserOrThrow()).thenReturn(otherUser);

        // Act & Assert
        VotifyException exception = assertThrows(VotifyException.class,
            () -> userService.deleteUser(1L));
        assertEquals(VotifyErrorCode.USER_DELETE_UNAUTHORIZED, exception.getErrorCode());
    }

    @Test
    void deleteUser_ShouldDeleteAllRefreshTokens() throws VotifyException {
        // Arrange
        RefreshToken token1 = new RefreshToken("token1", null, testUser);
        RefreshToken token2 = new RefreshToken("token2", null, testUser);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(contextService.getUserOrThrow()).thenReturn(testUser);
        when(refreshTokenRepository.findAllByUser(testUser))
            .thenReturn(Arrays.asList(token1, token2));

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(refreshTokenRepository).deleteAll(Arrays.asList(token1, token2));
        verify(userRepository).delete(testUser);
    }
}