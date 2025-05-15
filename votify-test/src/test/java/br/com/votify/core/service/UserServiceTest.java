package br.com.votify.core.service;

import br.com.votify.core.domain.entities.tokens.AuthTokens;
import br.com.votify.core.domain.entities.tokens.EmailConfirmation;
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

    @Mock
    private EmailConfirmationService emailConfirmationService;

    @InjectMocks
    private UserService userService;

    private CommonUser user;

    @BeforeEach
    public void setupBeforeEach() {
        this.user = CommonUser.builder()
                .id(1L)
                .userName("silverhand")
                .name("Jhonny Silverhand")
                .email("jhonny@nightcity.2077")
                .password("6Samurai6")
                .build();
    }

    @Test
    public void createValidUser() throws VotifyException {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.existsByUserName(user.getUserName())).thenReturn(false);
        when(passwordEncoderService.encryptPassword(user.getPassword())).thenReturn(user.getPassword());
        when(emailConfirmationService.addUser(user, null)).thenReturn(Optional.of(new EmailConfirmation()));
        when(userRepository.save(user)).thenReturn(user);

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
    public void emailNotConfirmedLogin() {
        when(contextService.isAuthenticated()).thenReturn(false);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoderService.checkPassword(user, user.getPassword())).thenReturn(true);

        user.setEmailConfirmation(new EmailConfirmation());
        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> userService.login(user.getEmail(), user.getPassword())
        );
        assertEquals(VotifyErrorCode.PENDING_EMAIL_CONFIRMATION, exception.getErrorCode());
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


    @Test
    void updateUserInfo_Success_AllFields() throws VotifyException {
        String newName = "Johnny Silverhand Updated";
        String newUserName = "silverhand-updated";

        when(contextService.getUserOrThrow()).thenReturn(user);
        when(userRepository.existsByUserName(newUserName)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        User updatedUser = userService.updateUserInfo(newName, newUserName);

        assertNotNull(updatedUser);
        assertEquals(newName, updatedUser.getName());
        assertEquals(newUserName, updatedUser.getUserName());

        verify(userRepository).save(user);
    }

    @Test
    void updateUserInfo_Success_PartialFields() throws VotifyException {
        String newName = "Johnny Only Name Updated";

        when(contextService.getUserOrThrow()).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User updatedUser = userService.updateUserInfo(newName, null);

        assertNotNull(updatedUser);
        assertEquals(newName, updatedUser.getName());
        assertEquals(user.getUserName(), updatedUser.getUserName());
        assertEquals(user.getEmail(), updatedUser.getEmail());

        verify(userRepository).save(user);
    }

    @Test
    void updateUserInfoSuccessWithBlankUserName() throws VotifyException {
        String newName = "Johnny Only Name Updated";

        when(contextService.getUserOrThrow()).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User updatedUser = userService.updateUserInfo(newName, "");

        assertNotNull(updatedUser);
        assertEquals(newName, updatedUser.getName());
        assertEquals(user.getUserName(), updatedUser.getUserName());
        assertEquals(user.getEmail(), updatedUser.getEmail());

        verify(userRepository).save(user);
    }

    @Test
    void updateUserInfoSuccessWithBlankName() throws VotifyException {
        String newUserName = "diamondhand";

        when(contextService.getUserOrThrow()).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User updatedUser = userService.updateUserInfo("  ", newUserName);

        assertNotNull(updatedUser);
        assertEquals(user.getName(), updatedUser.getName());
        assertEquals(newUserName, updatedUser.getUserName());
        assertEquals(user.getEmail(), updatedUser.getEmail());

        verify(userRepository).save(user);
    }

    @Test
    void updateUserInfo_Fail_UserNameExists() throws VotifyException {
        String newUserName = "existing-user";

        when(contextService.getUserOrThrow()).thenReturn(user);
        when(userRepository.existsByUserName(newUserName)).thenReturn(true);

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> userService.updateUserInfo(null, newUserName)
        );

        assertEquals(VotifyErrorCode.USER_NAME_ALREADY_EXISTS, exception.getErrorCode());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUserPassword_Success() throws VotifyException {
        String oldPassword = "6Samurai6";
        String newPassword = "newSecurePassword123";
        String encryptedNewPassword = "encryptedNewPassword";

        when(contextService.getUserOrThrow()).thenReturn(user);
        when(passwordEncoderService.checkPassword(user, oldPassword)).thenReturn(true);
        when(passwordEncoderService.encryptPassword(newPassword)).thenReturn(encryptedNewPassword);
        when(userRepository.save(any(User.class))).thenReturn(user);

        assertDoesNotThrow(() -> userService.updateUserPassword(oldPassword, newPassword));

        assertEquals(encryptedNewPassword, user.getPassword());
        verify(passwordEncoderService).encryptPassword(newPassword);
        verify(userRepository).save(user);
    }

    @Test
    void updateUserPassword_Fail_InvalidOldPassword() throws VotifyException {
        String wrongOldPassword = "wrongPassword";
        String newPassword = "newSecurePassword123";

        when(contextService.getUserOrThrow()).thenReturn(user);
        when(passwordEncoderService.checkPassword(user, wrongOldPassword)).thenReturn(false);

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> userService.updateUserPassword(wrongOldPassword, newPassword)
        );

        assertEquals(VotifyErrorCode.INVALID_OLD_PASSWORD, exception.getErrorCode());
        verify(passwordEncoderService, never()).encryptPassword(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUserEmail_Success() throws VotifyException {
        String newEmail = "jhonny.new@nightcity.2077";
        when(contextService.getUserOrThrow()).thenReturn(user);
        when(userRepository.existsByEmail(newEmail)).thenReturn(false);
        when(emailConfirmationService.addUser(user, newEmail)).thenReturn(Optional.of(new EmailConfirmation()));

        User updatedUser = userService.updateUserEmail(newEmail);

        assertNotNull(updatedUser);
        assertNotEquals(newEmail, updatedUser.getEmail());
    }

    @Test
    void updateUserEmail_Fail_EmailExists() throws VotifyException {
        String existingEmail = "rogue@afterlife.2077";
        when(contextService.getUserOrThrow()).thenReturn(user);
        when(userRepository.existsByEmail(existingEmail)).thenReturn(true);

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> userService.updateUserEmail(existingEmail)
        );

        assertEquals(VotifyErrorCode.EMAIL_ALREADY_EXISTS, exception.getErrorCode());
        assertNotEquals("rogue@afterlife.2077", user.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUserEmail_Fail_EmailNullOrBlank() throws VotifyException {
        when(contextService.getUserOrThrow()).thenReturn(user);

        VotifyException exceptionNull = assertThrows(
                VotifyException.class,
                () -> userService.updateUserEmail(null)
        );
        assertEquals(VotifyErrorCode.EMAIL_EMPTY, exceptionNull.getErrorCode());

        VotifyException exceptionBlank = assertThrows(
                VotifyException.class,
                () -> userService.updateUserEmail("   ")
        );
        assertEquals(VotifyErrorCode.EMAIL_INVALID_LENGTH, exceptionBlank.getErrorCode());

        assertNotEquals("   ", user.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUserEmail_SameEmail_NoChange() throws VotifyException {
        String sameEmail = user.getEmail();
        when(contextService.getUserOrThrow()).thenReturn(user);

        User resultUser = userService.updateUserEmail(sameEmail);

        assertNotNull(resultUser);
        assertEquals(sameEmail, resultUser.getEmail());
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}