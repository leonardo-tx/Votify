package br.com.votify.core.service.user;

import br.com.votify.core.model.user.*;
import br.com.votify.core.model.user.field.*;
import br.com.votify.core.properties.user.UserProperties;
import br.com.votify.core.repository.poll.PollRepository;
import br.com.votify.core.repository.poll.VoteRepository;
import br.com.votify.core.repository.user.UserRepository;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.test.UserFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
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

    @Mock
    private PollRepository pollRepository;

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private UserProperties userProperties;

    @InjectMocks
    private UserService userService;

    private UserRegister userRegister;

    private User activeUser;

    private User inactiveUser;

    @BeforeEach
    void setupBeforeEach() throws VotifyException {
        this.activeUser = UserFactory.createUser(0, true);
        this.inactiveUser = UserFactory.createUser(0, false);
        this.userRegister = new UserRegister(
                "silverhand",
                "Jhonny Silverhand",
                "jhonny@nightcity.2077",
                "6Samurai6"
        );
    }

    @Test
    void createValidUser() throws VotifyException {
        when(userRepository.existsByEmail(userRegister.getEmail())).thenReturn(false);
        when(userRepository.existsByUserName(userRegister.getUserName())).thenReturn(false);
        when(passwordEncoderService.encryptPassword(userRegister.getPassword()))
                .thenReturn(userRegister.getPassword().getValue());
        doNothing().when(emailConfirmationService).addEmailConfirmation(any(User.class), null);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User userFromService = assertDoesNotThrow(() -> userService.register(userRegister));
        assertNotNull(userFromService);
        assertNull(userFromService.getId());
    }

    @Test
    void registerWithEmailAlreadyExists() {
        when(userRepository.existsByEmail(userRegister.getEmail())).thenReturn(true);

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> userService.register(userRegister)
        );
        assertEquals(VotifyErrorCode.EMAIL_ALREADY_EXISTS, exception.getErrorCode());
    }

    @Test
    void registerUserNameAlreadyExists() {
        when(userRepository.existsByEmail(userRegister.getEmail())).thenReturn(false);
        when(userRepository.existsByUserName(userRegister.getUserName())).thenReturn(true);

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> userService.register(userRegister)
        );
        assertEquals(VotifyErrorCode.USER_NAME_ALREADY_EXISTS, exception.getErrorCode());
    }

    @Test
    void getUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(activeUser));
        User user = assertDoesNotThrow(() -> userService.getUserById(1L));

        assertNotNull(user);
        assertEquals(1L, user.getId());
    }

    @Test
    void getNonExistentUser() {
        when(userRepository.findById(10L)).thenReturn(Optional.empty());
        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> userService.getUserById(10)
        );
        assertEquals(VotifyErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void login() throws VotifyException {
        Password factoryPassword = UserFactory.getPasswordFrom(activeUser);
        AccessToken accessToken = AccessToken.parseUnsafe("access_token", activeUser.getId());
        RefreshToken refreshToken = RefreshToken.parseUnsafe(
                "refresh_token", Instant.now().plusSeconds(60), activeUser.getId()
        );

        when(contextService.isAuthenticated()).thenReturn(false);
        when(userRepository.findByEmail(activeUser.getEmail())).thenReturn(Optional.of(activeUser));
        when(passwordEncoderService.checkPassword(activeUser, factoryPassword)).thenReturn(true);
        when(tokenService.createRefreshToken(activeUser)).thenReturn(refreshToken);
        when(tokenService.createAccessToken(refreshToken)).thenReturn(accessToken);

        AuthTokens authTokens = assertDoesNotThrow(() -> userService.login(activeUser.getEmail(), factoryPassword));
        assertNotNull(authTokens);
    }

    @Test
    void incorrectPasswordLogin() {
        Password incorrectPassword = Password.parseUnsafe("6Samurai7");

        when(contextService.isAuthenticated()).thenReturn(false);
        when(userRepository.findByEmail(activeUser.getEmail())).thenReturn(Optional.of(activeUser));
        when(passwordEncoderService.checkPassword(activeUser, incorrectPassword)).thenReturn(false);

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> userService.login(activeUser.getEmail(), incorrectPassword)
        );
        assertEquals(VotifyErrorCode.LOGIN_UNAUTHORIZED, exception.getErrorCode());
    }

    @Test
    void incorrectEmailLogin() {
        Password factoryPassword = UserFactory.getPasswordFrom(activeUser);
        Email incorrectEmail = Email.parseUnsafe("jhonny@nightcity.2076");

        when(contextService.isAuthenticated()).thenReturn(false);
        when(userRepository.findByEmail(incorrectEmail)).thenReturn(Optional.empty());

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> userService.login(incorrectEmail, factoryPassword)
        );
        assertEquals(VotifyErrorCode.LOGIN_UNAUTHORIZED, exception.getErrorCode());
    }

    @Test
    void emailNotConfirmedLogin() {
        Password factoryPassword = UserFactory.getPasswordFrom(inactiveUser);

        when(contextService.isAuthenticated()).thenReturn(false);
        when(userRepository.findByEmail(inactiveUser.getEmail())).thenReturn(Optional.of(inactiveUser));
        when(passwordEncoderService.checkPassword(inactiveUser, factoryPassword)).thenReturn(true);

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> userService.login(inactiveUser.getEmail(), factoryPassword)
        );
        assertEquals(VotifyErrorCode.PENDING_EMAIL_CONFIRMATION, exception.getErrorCode());
    }

    @Test
    void loginAlreadyLogged() {
        Password factoryPassword = UserFactory.getPasswordFrom(activeUser);

        when(contextService.isAuthenticated()).thenReturn(true);

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> userService.login(activeUser.getEmail(), factoryPassword)
        );
        assertEquals(VotifyErrorCode.LOGIN_ALREADY_LOGGED, exception.getErrorCode());
    }

    @Test
    void deleteUserWithNoPollOrVote() {
        when(voteRepository.findAllFromUser(activeUser)).thenReturn(List.of());
        when(pollRepository.findAllByResponsible(activeUser)).thenReturn(List.of());

        assertDoesNotThrow(() -> userService.delete(activeUser));
        verify(userRepository).delete(activeUser);
        verify(tokenService).revokeAllRefreshTokens(activeUser);
        verifyNoMoreInteractions(voteRepository);
        verifyNoMoreInteractions(pollRepository);
    }

    @Test
    void logoutAuthenticatedWithRefreshToken() {
        String refreshToken = "refresh_token";
        when(userProperties.getRefreshTokenCookieName()).thenReturn(refreshToken);
        when(contextService.getCookieValueOrDefault(refreshToken, null)).thenReturn(refreshToken);
        when(contextService.isAuthenticated()).thenReturn(true);
        assertDoesNotThrow(() -> userService.logout());

        verify(tokenService).deleteRefreshToken(refreshToken);
    }

    @Test
    void logoutAuthenticatedWithoutRefreshToken() {
        String refreshToken = "refresh_token";

        when(userProperties.getRefreshTokenCookieName()).thenReturn(refreshToken);
        when(contextService.getCookieValueOrDefault(refreshToken, null)).thenReturn(null);
        when(contextService.isAuthenticated()).thenReturn(true);

        assertDoesNotThrow(() -> userService.logout());
        verifyNoInteractions(tokenService);
    }

    @Test
    void logoutNotAuthenticatedWithRefreshToken() {
        String refreshToken = "refresh_token";

        when(userProperties.getRefreshTokenCookieName()).thenReturn(refreshToken);
        when(contextService.getCookieValueOrDefault(refreshToken, null)).thenReturn(refreshToken);
        when(contextService.isAuthenticated()).thenReturn(false);

        assertDoesNotThrow(() -> userService.logout());
        verifyNoInteractions(tokenService);
    }


    @Test
    void updateUserInfo_Success_AllFields() throws VotifyException {
        Name newName = Name.parseUnsafe("Johnny Silverhand Updated");
        UserName newUserName = UserName.parseUnsafe("silverhand-updated");

        when(contextService.getUserOrThrow()).thenReturn(activeUser);
        when(userRepository.existsByUserName(newUserName)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        User updatedUser = userService.updateUserInfo(newName, newUserName);

        assertNotNull(updatedUser);
        assertEquals(newName, updatedUser.getName());
        assertEquals(newUserName, updatedUser.getUserName());
    }

    @Test
    void updateUserInfo_Success_PartialFields() throws VotifyException {
        Name newName = Name.parseUnsafe("Johnny Only Name Updated");

        when(contextService.getUserOrThrow()).thenReturn(activeUser);
        when(userRepository.save(any(User.class))).thenReturn(activeUser);

        User updatedUser = userService.updateUserInfo(newName, null);

        assertNotNull(updatedUser);
        assertEquals(newName, updatedUser.getName());
        assertEquals(activeUser.getUserName(), updatedUser.getUserName());
        assertEquals(activeUser.getEmail(), updatedUser.getEmail());
    }

    @Test
    void updateUserInfoSuccessWithNullUserName() throws VotifyException {
        Name newName = Name.parseUnsafe("Johnny Only Name Updated");

        when(contextService.getUserOrThrow()).thenReturn(activeUser);
        when(userRepository.save(any(User.class))).thenReturn(activeUser);

        User updatedUser = userService.updateUserInfo(newName, null);

        assertNotNull(updatedUser);
        assertEquals(newName, updatedUser.getName());
        assertEquals(activeUser.getUserName(), updatedUser.getUserName());
        assertEquals(activeUser.getEmail(), updatedUser.getEmail());
    }

    @Test
    void updateUserInfoSuccessWithNullName() throws VotifyException {
        UserName newUserName = UserName.parseUnsafe("diamondhand");

        when(contextService.getUserOrThrow()).thenReturn(activeUser);
        when(userRepository.save(any(User.class))).thenReturn(activeUser);

        User updatedUser = userService.updateUserInfo(null, newUserName);

        assertNotNull(updatedUser);
        assertEquals(activeUser.getName(), updatedUser.getName());
        assertEquals(newUserName, updatedUser.getUserName());
        assertEquals(activeUser.getEmail(), updatedUser.getEmail());
    }

    @Test
    void updateUserInfo_Fail_UserNameExists() throws VotifyException {
        UserName newUserName = UserName.parseUnsafe("existing-user");

        when(contextService.getUserOrThrow()).thenReturn(activeUser);
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
        Password oldPassword = UserFactory.getPasswordFrom(activeUser);
        Password newPassword = Password.parseUnsafe("newSecurePassword123");
        String encryptedNewPassword = "encryptedNewPassword";

        when(contextService.getUserOrThrow()).thenReturn(activeUser);
        when(passwordEncoderService.checkPassword(activeUser, oldPassword)).thenReturn(true);
        when(passwordEncoderService.encryptPassword(newPassword)).thenReturn(encryptedNewPassword);
        when(userRepository.save(any(User.class))).thenReturn(activeUser);

        assertDoesNotThrow(() -> userService.updateUserPassword(oldPassword, newPassword));
        assertEquals(encryptedNewPassword, activeUser.getEncryptedPassword());
    }

    @Test
    void updateUserPassword_Fail_InvalidOldPassword() throws VotifyException {
        Password wrongOldPassword = Password.parseUnsafe("wrongPassword");
        Password newPassword = Password.parseUnsafe("newSecurePassword123");

        when(contextService.getUserOrThrow()).thenReturn(activeUser);
        when(passwordEncoderService.checkPassword(activeUser, wrongOldPassword)).thenReturn(false);

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> userService.updateUserPassword(wrongOldPassword, newPassword)
        );

        assertEquals(VotifyErrorCode.INVALID_OLD_PASSWORD, exception.getErrorCode());
        verify(passwordEncoderService, never()).encryptPassword(any(Password.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUserEmail_Success() throws VotifyException {
        Email newEmail = Email.parseUnsafe("jhonny.new@nightcity.2077");
        EmailConfirmation emailConfirmation = EmailConfirmation.parseUnsafe(
                ConfirmationCode.parseUnsafe("code"),
                newEmail,
                Instant.now().plusSeconds(60),
                activeUser.getId()
        );

        when(contextService.getUserOrThrow()).thenReturn(activeUser);
        when(userRepository.existsByEmail(newEmail)).thenReturn(false);
        when(emailConfirmationService.addEmailConfirmation(activeUser, newEmail)).thenReturn(emailConfirmation);

        assertDoesNotThrow(() -> userService.updateUserEmail(newEmail));

        assertNotEquals(newEmail, activeUser.getEmail());
    }

    @Test
    void updateUserEmail_Fail_EmailExists() throws VotifyException {
        Email existingEmail = Email.parseUnsafe("rogue@afterlife.2077");

        when(contextService.getUserOrThrow()).thenReturn(activeUser);
        when(userRepository.existsByEmail(existingEmail)).thenReturn(true);

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> userService.updateUserEmail(existingEmail)
        );

        assertEquals(VotifyErrorCode.EMAIL_ALREADY_EXISTS, exception.getErrorCode());
        assertNotEquals(existingEmail, activeUser.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUserEmail_Fail_EmailNull() throws VotifyException {
        when(contextService.getUserOrThrow()).thenReturn(activeUser);

        VotifyException exceptionNull = assertThrows(
                VotifyException.class,
                () -> userService.updateUserEmail(null)
        );
        assertEquals(VotifyErrorCode.EMAIL_EMPTY, exceptionNull.getErrorCode());

        assertNotEquals(null, activeUser.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUserEmail_SameEmail_NoChange() throws VotifyException {
        Email sameEmail = activeUser.getEmail();
        when(contextService.getUserOrThrow()).thenReturn(activeUser);

        userService.updateUserEmail(sameEmail);

        verify(userRepository, never()).existsByEmail(any(Email.class));
        verify(userRepository, never()).save(any(User.class));
    }
}
