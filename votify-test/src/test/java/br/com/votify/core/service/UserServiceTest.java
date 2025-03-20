package br.com.votify.core.service;

import br.com.votify.core.domain.entities.tokens.AuthTokens;
import br.com.votify.core.domain.entities.tokens.RefreshToken;
import br.com.votify.core.domain.entities.users.*;
import br.com.votify.core.repository.UserRepository;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private ContextService contextService;

    @Mock
    private PasswordEncoderService passwordEncoderService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private UserService userService;

    private CommonUser user;

    @BeforeEach
    public void setupBeforeEach() {
        this.user = new CommonUser(
            1L,
            "silverhand",
            "Jhonny Silverhand",
            "jhonny@nightcity.2077",
            "6Samurai6"
        );
    }

    @Test
    public void createValidUser() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.existsByUserName(user.getUserName())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);
        when(passwordEncoderService.encryptPassword(user.getPassword())).thenReturn(user.getPassword());

        User userFromService = assertDoesNotThrow(() -> userService.register(user));
        assertNotNull(userFromService);
        assertNull(userFromService.getId());

        verify(passwordEncoderService).encryptPassword(user.getPassword());
    }

    @Test
    public void registerWithEmailAlreadyExists() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> userService.register(user)
        );
        assertEquals(VotifyErrorCode.EMAIL_ALREADY_EXISTS, exception.getErrorCode());
    }

    @Test
    public void registerUserNameAlreadyExists() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.existsByUserName(user.getUserName())).thenReturn(true);

        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> userService.register(user)
        );
        assertEquals(VotifyErrorCode.USER_NAME_ALREADY_EXISTS, exception.getErrorCode());
    }

    @Test
    public void getUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User user = assertDoesNotThrow(() -> userService.getUserById(1L));

        assertNotNull(user);
    }

    @Test
    public void getNonExistentUser() {
        when(userRepository.findById(10L)).thenReturn(Optional.empty());
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> userService.getUserById(10)
        );
        assertEquals(VotifyErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    public void login() throws VotifyException {
        RefreshToken refreshToken = new RefreshToken();

        when(contextService.isAuthenticated()).thenReturn(false);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoderService.checkPassword(user, user.getPassword())).thenReturn(true);
        when(tokenService.createRefreshToken(user)).thenReturn(refreshToken);
        when(tokenService.createAccessToken(refreshToken)).thenReturn("access_token");

        AuthTokens authTokens = assertDoesNotThrow(
            () -> userService.login(user.getEmail(), user.getPassword())
        );
        assertNotNull(authTokens);
    }

    @Test
    public void incorrectPasswordLogin() {
        String incorrectPassword = "6Samurai7";

        when(contextService.isAuthenticated()).thenReturn(false);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoderService.checkPassword(user, incorrectPassword)).thenReturn(false);

        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> userService.login(user.getEmail(), incorrectPassword)
        );
        assertEquals(VotifyErrorCode.LOGIN_UNAUTHORIZED, exception.getErrorCode());
    }

    @Test
    public void incorrectEmailLogin() {
        String incorrectEmail = "jhonny@nightcity.2076";

        when(contextService.isAuthenticated()).thenReturn(false);
        when(userRepository.findByEmail(incorrectEmail)).thenReturn(Optional.empty());

        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> userService.login(incorrectEmail, user.getPassword())
        );
        assertEquals(VotifyErrorCode.LOGIN_UNAUTHORIZED, exception.getErrorCode());
    }

    @Test
    public void loginAlreadyLogged() {
        when(contextService.isAuthenticated()).thenReturn(true);

        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> userService.login(user.getEmail(), user.getPassword())
        );
        assertEquals(VotifyErrorCode.LOGIN_ALREADY_LOGGED, exception.getErrorCode());
    }

    @Test
    public void deleteUser() {
        assertDoesNotThrow(() -> userService.deleteUser(user));
        verify(userRepository).delete(user);
    }

    @Test
    public void logoutAuthenticatedWithRefreshToken() {
        String refreshToken = "refresh_token";
        when(contextService.getCookieValueOrDefault(refreshToken, null)).thenReturn(refreshToken);
        when(contextService.isAuthenticated()).thenReturn(true);
        assertDoesNotThrow(() -> userService.logout());

        verify(tokenService).deleteRefreshTokenById(refreshToken);
    }

    @Test
    public void logoutAuthenticatedWithoutRefreshToken() {
        String refreshToken = "refresh_token";

        when(contextService.getCookieValueOrDefault(refreshToken, null)).thenReturn(null);
        when(contextService.isAuthenticated()).thenReturn(true);

        assertDoesNotThrow(() -> userService.logout());
        verifyNoInteractions(tokenService);
    }

    @Test
    public void logoutNotAuthenticatedWithRefreshToken() {
        String refreshToken = "refresh_token";

        when(contextService.getCookieValueOrDefault(refreshToken, null)).thenReturn(refreshToken);
        when(contextService.isAuthenticated()).thenReturn(false);

        assertDoesNotThrow(() -> userService.logout());
        verifyNoInteractions(tokenService);
    }
}