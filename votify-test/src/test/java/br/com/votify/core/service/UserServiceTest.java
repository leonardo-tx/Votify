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
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTest {
    private static final List<User> users = new ArrayList<>();
    private static Long entityId = 1L;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private PasswordEncoderService passwordEncoderService;
    
    @Mock
    private ContextService contextService;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private RefreshToken testRefreshToken;

    @BeforeEach
    void setUp() {
        // Configurar para modo lenient para permitir stubs não utilizados
        lenient().when(passwordEncoderService.encryptPassword(any())).thenReturn("encrypted_password");
        lenient().when(passwordEncoderService.checkPassword(any(), any())).thenReturn(true);
        
        testUser = new CommonUser(1L, "test-user", "Test User", "test@example.com", "password123");
        testRefreshToken = new RefreshToken("token123", null, testUser);
        
        // Limpar mocks antes de cada teste
        reset(userRepository, refreshTokenRepository, tokenService, contextService);
        
        // Configuração para o userRepository
        lenient().when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            if (user.getId() == null) {
                user.setId(entityId++);
            }
            return user;
        });
    }

    @Test
    @Order(0)
    public void createValidCommonUser() {
        // Arrange
        User user = new CommonUser(
            null,
            "valid-username",
            "Marcos Castro",
            "marcos@proton.me",
            "12345678abacaxi#"
        );
        
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUserName(anyString())).thenReturn(false);
        
        // Act
        User userFromService = assertDoesNotThrow(() -> userService.createUser(user));
        
        // Assert
        assertNotNull(userFromService);
        assertNotNull(userFromService.getId());
        assertEquals("valid-username", userFromService.getUserName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @Order(1)
    public void createValidModeratorUser() {
        // Arrange
        User user = new ModeratorUser(
            null,
            "silverhand",
            "Jhonny Silverhand",
            "jhonny@nightcity.2077",
            "6Samurai6"
        );
        
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUserName(anyString())).thenReturn(false);
        
        // Act
        User userFromService = assertDoesNotThrow(() -> userService.createUser(user));
        
        // Assert
        assertNotNull(userFromService);
        assertNotNull(userFromService.getId());
        assertEquals("silverhand", userFromService.getUserName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @Order(2)
    public void createValidAdminUser() {
        // Arrange
        User user = new AdminUser(
            null,
            "arthurzinho-gameplays",
            "Arthur Cervero",
            "arthurgatinho@gmail.com",
            "angelofthenight"
        );
        
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUserName(anyString())).thenReturn(false);
        
        // Act
        User userFromService = assertDoesNotThrow(() -> userService.createUser(user));
        
        // Assert
        assertNotNull(userFromService);
        assertNotNull(userFromService.getId());
        assertEquals("arthurzinho-gameplays", userFromService.getUserName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @Order(3)
    public void createUserWithEmailAlreadyExists() {
        // Arrange
        User user = new CommonUser(
            null,
            "arthurzinho",
            "Arthur Garcia",
            "arthurgatinho@gmail.com",
            "aeaeaeaeaeaeaea"
        );
        
        when(userRepository.existsByEmail("arthurgatinho@gmail.com")).thenReturn(true);
        
        // Act & Assert
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> userService.createUser(user)
        );
        assertEquals(VotifyErrorCode.EMAIL_ALREADY_EXISTS, exception.getErrorCode());
    }

    @Test
    @Order(3)
    public void createUserWithUserNameAlreadyExists() {
        // Arrange
        User user = new CommonUser(
            null,
            "silverhand",
            "Fã de Cyberpunk \uD83D\uDE0E",
            "silverhand@outlook.com",
            "yeeeeeeeey765"
        );
        
        when(userRepository.existsByUserName("silverhand")).thenReturn(true);
        
        // Act & Assert
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> userService.createUser(user)
        );
        assertEquals(VotifyErrorCode.USER_NAME_ALREADY_EXISTS, exception.getErrorCode());
    }

    @Test
    @Order(4)
    public void getAll() {
        // Arrange
        List<User> mockUsers = Arrays.asList(
            new CommonUser(1L, "user1", "User One", "user1@example.com", "password1"),
            new CommonUser(2L, "user2", "User Two", "user2@example.com", "password2"),
            new CommonUser(3L, "user3", "User Three", "user3@example.com", "password3")
        );
        when(userRepository.findAll()).thenReturn(mockUsers);
        
        // Act
        List<User> result = userService.getAll();
        
        // Assert
        assertEquals(3, result.size());
        verify(userRepository).findAll();
    }

    @Test
    @Order(4)
    public void getUserById() {
        // Arrange
        User mockUser = new ModeratorUser(2L, "silverhand", "Jhonny Silverhand", "jhonny@nightcity.2077", "6Samurai6");
        when(userRepository.findById(2L)).thenReturn(Optional.of(mockUser));
        
        // Act
        User user = assertDoesNotThrow(() -> userService.getUserById(2));
        
        // Assert
        assertNotNull(user);
        assertEquals("jhonny@nightcity.2077", user.getEmail());
        verify(userRepository).findById(2L);
    }

    @Test
    @Order(4)
    public void getNonExistentUser() {
        // Arrange
        when(userRepository.findById(10L)).thenReturn(Optional.empty());
        
        // Act & Assert
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> userService.getUserById(10)
        );
        assertEquals(VotifyErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @Order(4)
    public void login() throws VotifyException {
        // Arrange
        User mockUser = new ModeratorUser(2L, "silverhand", "Jhonny Silverhand", "jhonny@nightcity.2077", "6Samurai6");
        RefreshToken mockRefreshToken = new RefreshToken("refresh-token", null, mockUser);
        AuthTokens mockAuthTokens = new AuthTokens("access-token", mockRefreshToken);
        
        when(contextService.isAuthenticated()).thenReturn(false);
        when(userRepository.findByEmail("jhonny@nightcity.2077")).thenReturn(Optional.of(mockUser));
        when(passwordEncoderService.checkPassword(mockUser, "6Samurai6")).thenReturn(true);
        when(tokenService.createRefreshToken(mockUser)).thenReturn(mockRefreshToken);
        when(tokenService.createAccessToken(mockRefreshToken)).thenReturn("access-token");
        
        // Act
        AuthTokens authTokens = assertDoesNotThrow(
            () -> userService.login("jhonny@nightcity.2077", "6Samurai6")
        );
        
        // Assert
        assertNotNull(authTokens);
        assertEquals("access-token", authTokens.getAccessToken());
        assertEquals(mockRefreshToken, authTokens.getRefreshToken());
    }

    @Test
    @Order(4)
    public void incorretPasswordLogin() throws VotifyException {
        // Arrange
        User mockUser = new ModeratorUser(2L, "silverhand", "Jhonny Silverhand", "jhonny@nightcity.2077", "6Samurai6");
        
        when(contextService.isAuthenticated()).thenReturn(false);
        when(userRepository.findByEmail("jhonny@nightcity.2077")).thenReturn(Optional.of(mockUser));
        when(passwordEncoderService.checkPassword(mockUser, "6Samurai7")).thenReturn(false);
        
        // Act & Assert
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> userService.login("jhonny@nightcity.2077", "6Samurai7")
        );
        assertEquals(VotifyErrorCode.LOGIN_UNAUTHORIZED, exception.getErrorCode());
    }

    @Test
    @Order(4)
    public void incorretEmailLogin() throws VotifyException {
        // Arrange
        when(contextService.isAuthenticated()).thenReturn(false);
        when(userRepository.findByEmail("jhonny@nightcity.2076")).thenReturn(Optional.empty());
        
        // Act & Assert
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> userService.login("jhonny@nightcity.2076", "6Samurai6")
        );
        assertEquals(VotifyErrorCode.LOGIN_UNAUTHORIZED, exception.getErrorCode());
    }

    @Test
    @Order(4)
    public void allIncorrectLogin() throws VotifyException {
        // Arrange
        when(contextService.isAuthenticated()).thenReturn(false);
        when(userRepository.findByEmail("jhonny@nightcity.2076")).thenReturn(Optional.empty());
        
        // Act & Assert
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> userService.login("jhonny@nightcity.2076", "6Samurai7")
        );
        assertEquals(VotifyErrorCode.LOGIN_UNAUTHORIZED, exception.getErrorCode());
    }

    @Test
    @Order(5)
    public void loginAlreadyLogged() throws VotifyException {
        // Arrange
        when(contextService.isAuthenticated()).thenReturn(true);
        
        // Act & Assert
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