package br.com.votify.core.service.user;

import br.com.votify.core.model.user.*;
import br.com.votify.core.model.user.field.*;
import br.com.votify.core.properties.user.UserProperties;
import br.com.votify.core.repository.user.UserRepository;
import br.com.votify.core.service.poll.PollService;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
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
    private PollService pollService;

    @Mock
    private PasswordResetService passwordResetService;

    @Mock
    private UserProperties userProperties;

    @InjectMocks
    private UserService userService;

    @Test
    void createValidUser() throws VotifyException {
        UserRegister userRegister = mock(UserRegister.class);

        when(userRepository.existsByEmail(userRegister.getEmail())).thenReturn(false);
        when(userRepository.existsByUserName(userRegister.getUserName())).thenReturn(false);
        when(passwordEncoderService.encryptPassword(userRegister.getPassword()))
                .thenReturn("encryptedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User userFromService = assertDoesNotThrow(() -> userService.register(userRegister));
        assertNotNull(userFromService);
        assertNull(userFromService.getId());

        verify(emailConfirmationService).addEmailConfirmation(any(User.class), isNull());
    }

    @Test
    void registerWithEmailAlreadyExists() {
        UserRegister userRegister = mock(UserRegister.class);
        when(userRepository.existsByEmail(userRegister.getEmail())).thenReturn(true);

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> userService.register(userRegister)
        );
        assertEquals(VotifyErrorCode.EMAIL_ALREADY_EXISTS, exception.getErrorCode());
    }

    @Test
    void registerUserNameAlreadyExists() {
        UserRegister userRegister = mock(UserRegister.class);
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
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User returnedUser = assertDoesNotThrow(() -> userService.getUserById(1L));

        assertNotNull(returnedUser);
        assertEquals(1L, returnedUser.getId());
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
        User activeUser = mock(User.class);
        Password password = mock(Password.class);
        when(activeUser.isActive()).thenReturn(true);

        AccessToken accessToken = AccessToken.parseUnsafe("access_token", activeUser.getId());
        RefreshToken refreshToken = RefreshToken.parseUnsafe(
                "refresh_token", Instant.now().plusSeconds(60), activeUser.getId()
        );

        when(contextService.isAuthenticated()).thenReturn(false);
        when(userRepository.findByEmail(activeUser.getEmail())).thenReturn(Optional.of(activeUser));
        when(passwordEncoderService.checkPassword(activeUser, password)).thenReturn(true);
        when(tokenService.createRefreshToken(activeUser)).thenReturn(refreshToken);
        when(tokenService.createAccessToken(refreshToken)).thenReturn(accessToken);

        AuthTokens authTokens = assertDoesNotThrow(() -> userService.login(activeUser.getEmail(), password));
        assertNotNull(authTokens);
    }

    @Test
    void incorrectPasswordLogin() {
        User activeUser = mock(User.class);
        Password incorrectPassword = mock(Password.class);

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
        Password password = mock(Password.class);
        Email incorrectEmail = Email.parseUnsafe("jhonny@nightcity.2076");

        when(contextService.isAuthenticated()).thenReturn(false);
        when(userRepository.findByEmail(incorrectEmail)).thenReturn(Optional.empty());

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> userService.login(incorrectEmail, password)
        );
        assertEquals(VotifyErrorCode.LOGIN_UNAUTHORIZED, exception.getErrorCode());
    }

    @Test
    void emailNotConfirmedLogin() {
        User inactiveUser = mock(User.class);
        Password password = mock(Password.class);

        when(inactiveUser.isActive()).thenReturn(false);
        when(contextService.isAuthenticated()).thenReturn(false);
        when(userRepository.findByEmail(inactiveUser.getEmail())).thenReturn(Optional.of(inactiveUser));
        when(passwordEncoderService.checkPassword(inactiveUser, password)).thenReturn(true);

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> userService.login(inactiveUser.getEmail(), password)
        );
        assertEquals(VotifyErrorCode.PENDING_EMAIL_CONFIRMATION, exception.getErrorCode());
    }

    @Test
    void loginAlreadyLogged() throws VotifyException {
        User activeUser = mock(User.class);
        Password password = new Password("Password123");

        when(contextService.isAuthenticated()).thenReturn(true);

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> userService.login(activeUser.getEmail(), password)
        );
        assertEquals(VotifyErrorCode.LOGIN_ALREADY_LOGGED, exception.getErrorCode());
    }

    @Test
    void deleteUser() {
        User activeUser = mock(User.class);

        userService.delete(activeUser);

        verify(tokenService).revokeAllRefreshTokens(activeUser);
        verify(pollService).deletePollInfoFromUser(activeUser);
        verify(passwordResetService).deleteFromUser(activeUser);
        verify(emailConfirmationService).deleteFromUser(activeUser);
        verify(userRepository).delete(activeUser);
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
        User activeUser = mock(User.class);
        Name newName = new Name("New Name");
        UserName newUserName = new UserName("new-username");

        when(activeUser.getUserName()).thenReturn(new UserName("old-username"));
        when(contextService.getUserOrThrow()).thenReturn(activeUser);
        when(userRepository.existsByUserName(newUserName)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        User updatedUser = userService.updateUserInfo(newName, newUserName);

        assertNotNull(updatedUser);
        verify(activeUser).setUserName(newUserName);
        verify(activeUser).setName(newName);
    }

    @Test
    void updateUserInfo_Success_SameUserName() throws VotifyException {
        User activeUser = mock(User.class);
        Name newName = new Name("New Name");
        UserName newUserName = new UserName("old-username");

        when(activeUser.getUserName()).thenReturn(new UserName("old-username"));
        when(contextService.getUserOrThrow()).thenReturn(activeUser);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        User updatedUser = userService.updateUserInfo(newName, newUserName);

        assertNotNull(updatedUser);
        verify(activeUser).setUserName(newUserName);
        verify(activeUser).setName(newName);
    }

    @Test
    void updateUserInfoSuccessWithNullUserName() throws VotifyException {
        User activeUser = mock(User.class);
        Name newName = new Name("New Name");

        when(contextService.getUserOrThrow()).thenReturn(activeUser);
        when(userRepository.save(any(User.class))).thenReturn(activeUser);

        User updatedUser = userService.updateUserInfo(newName, null);

        assertNotNull(updatedUser);
        verify(activeUser, never()).setUserName(any(UserName.class));
        verify(activeUser).setName(newName);
    }

    @Test
    void updateUserInfoSuccessWithNullName() throws VotifyException {
        User activeUser = mock(User.class);
        UserName newUserName = new UserName("new-username");

        when(activeUser.getUserName()).thenReturn(new UserName("old-username"));
        when(contextService.getUserOrThrow()).thenReturn(activeUser);
        when(userRepository.save(any(User.class))).thenReturn(activeUser);

        User updatedUser = userService.updateUserInfo(null, newUserName);

        assertNotNull(updatedUser);
        verify(activeUser).setUserName(newUserName);
        verify(activeUser, never()).setName(any(Name.class));
    }

    @Test
    void updateUserInfo_Fail_UserNameExists() throws VotifyException {
        User activeUser = mock(User.class);
        UserName newUserName = new UserName("new-username");

        when(activeUser.getUserName()).thenReturn(new UserName("old-username"));
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
        User activeUser = mock(User.class);

        Password oldPassword = new Password("oldPassword");
        Password newPassword = new Password("newPassword");

        when(contextService.getUserOrThrow()).thenReturn(activeUser);
        when(passwordEncoderService.checkPassword(activeUser, oldPassword)).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(activeUser);

        assertDoesNotThrow(() -> userService.updateUserPassword(oldPassword, newPassword));
        verify(activeUser).setPassword(passwordEncoderService, newPassword);
    }

    @Test
    void updateUserPassword_Fail_InvalidOldPassword() throws VotifyException {
        User activeUser = mock(User.class);

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
    void updateUserPassword_Fail_NullOldPassword() throws VotifyException {
        User activeUser = mock(User.class);

        Password newPassword = new Password("newSecurePassword123");

        when(contextService.getUserOrThrow()).thenReturn(activeUser);

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> userService.updateUserPassword(null, newPassword)
        );

        assertEquals(VotifyErrorCode.PASSWORD_EMPTY, exception.getErrorCode());
        verify(passwordEncoderService, never()).encryptPassword(any(Password.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUserPassword_Fail_NullNewPassword() throws VotifyException {
        User activeUser = mock(User.class);

        Password oldPassword = new Password("oldPassword");

        when(contextService.getUserOrThrow()).thenReturn(activeUser);

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> userService.updateUserPassword(oldPassword, null)
        );

        assertEquals(VotifyErrorCode.PASSWORD_EMPTY, exception.getErrorCode());
        verify(passwordEncoderService, never()).encryptPassword(any(Password.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUserEmail_Success() throws VotifyException {
        User activeUser = mock(User.class);
        Email oldEmail = new Email("old@example.com");
        Email newEmail = new Email("new@example.com");
        EmailConfirmation emailConfirmation = mock(EmailConfirmation.class);

        when(activeUser.getEmail()).thenReturn(oldEmail);
        when(contextService.getUserOrThrow()).thenReturn(activeUser);
        when(userRepository.existsByEmail(newEmail)).thenReturn(false);
        when(emailConfirmationService.addEmailConfirmation(activeUser, newEmail)).thenReturn(emailConfirmation);

        assertDoesNotThrow(() -> userService.updateUserEmail(newEmail));

        assertNotEquals(newEmail, activeUser.getEmail());
    }

    @Test
    void updateUserEmail_Fail_EmailExists() throws VotifyException {
        User activeUser = mock(User.class);
        Email existingEmail = new Email("new@example.com");

        when(activeUser.getEmail()).thenReturn(new Email("old@example.com"));
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
        User activeUser = mock(User.class);
        when(contextService.getUserOrThrow()).thenReturn(activeUser);

        VotifyException exceptionNull = assertThrows(
                VotifyException.class,
                () -> userService.updateUserEmail(null)
        );
        assertEquals(VotifyErrorCode.EMAIL_EMPTY, exceptionNull.getErrorCode());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUserEmail_SameEmail_NoChange() throws VotifyException {
        User activeUser = mock(User.class);
        when(activeUser.getEmail()).thenReturn(mock(Email.class));

        Email sameEmail = activeUser.getEmail();
        when(contextService.getUserOrThrow()).thenReturn(activeUser);

        userService.updateUserEmail(sameEmail);

        verify(userRepository, never()).existsByEmail(any(Email.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void resetPassword_UserNotFound() throws VotifyException {
        String testCode = "TESTCODE";
        PasswordReset passwordReset = mock(PasswordReset.class);

        when(passwordReset.getUserId()).thenReturn(3L);
        when(passwordResetService.resetPassword(testCode)).thenReturn(passwordReset);
        when(userRepository.findById(3L)).thenReturn(Optional.empty());

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> userService.resetPassword(testCode, new Password("12345678"))
        );
        assertEquals(VotifyErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void resetPassword_Valid() throws VotifyException {
        User user = mock(User.class);
        Password password = new Password("12345678");

        String testCode = "TESTCODE";
        PasswordReset passwordReset = mock(PasswordReset.class);

        when(passwordReset.getUserId()).thenReturn(3L);
        when(passwordResetService.resetPassword(testCode)).thenReturn(passwordReset);
        when(userRepository.findById(3L)).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> userService.resetPassword(testCode, password));
        verify(user).setPassword(passwordEncoderService, password);
    }

    @Test
    void getUserByEmail_Found() throws VotifyException {
        User user = mock(User.class);
        Email email = new Email("mail@mail.com");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        User returnedUser = assertDoesNotThrow(() -> userService.getUserByEmail(email));
        assertEquals(user, returnedUser);
    }

    @Test
    void getUserByEmail_NotFound() throws VotifyException {
        Email email = new Email("mail@mail.com");

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> userService.getUserByEmail(email)
        );
        assertEquals(VotifyErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void testConfirmEmail_WithCurrentEmailNull_ShouldGetUserFromContext() throws VotifyException {
        String code = "123456";
        User mockUser = mock(User.class);
        Email mockEmail = mock(Email.class);
        EmailConfirmation mockConfirmation = mock(EmailConfirmation.class);

        when(contextService.getUserOrThrow()).thenReturn(mockUser);
        when(mockUser.getEmail()).thenReturn(mockEmail);
        when(emailConfirmationService.confirmEmail(code, mockEmail)).thenReturn(mockConfirmation);
        when(mockConfirmation.getNewEmail()).thenReturn(null);

        userService.confirmEmail(code, null);

        verify(contextService).getUserOrThrow();
        verify(mockUser).getEmail();
        verify(emailConfirmationService).confirmEmail(code, mockEmail);
        verifyNoInteractions(userRepository, passwordResetService);
    }

    @Test
    void testConfirmEmail_WithNewEmailNull_ShouldDoNothing() throws VotifyException {
        String code = "123456";
        Email mockEmail = mock(Email.class);
        EmailConfirmation mockConfirmation = mock(EmailConfirmation.class);

        when(emailConfirmationService.confirmEmail(code, mockEmail)).thenReturn(mockConfirmation);
        when(mockConfirmation.getNewEmail()).thenReturn(null);

        userService.confirmEmail(code, mockEmail);

        verify(emailConfirmationService).confirmEmail(code, mockEmail);
        verifyNoInteractions(contextService, userRepository, passwordResetService);
    }

    @Test
    void testConfirmEmail_WithNewEmail_ShouldUpdateUserEmail() throws VotifyException {
        String code = "123456";
        Email mockEmail = mock(Email.class);
        Email newEmail = new Email("new@example.com");
        EmailConfirmation mockConfirmation = mock(EmailConfirmation.class);
        User mockUser = mock(User.class);

        when(emailConfirmationService.confirmEmail(code, mockEmail)).thenReturn(mockConfirmation);
        when(mockConfirmation.getNewEmail()).thenReturn(newEmail);
        when(mockConfirmation.getUserId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        userService.confirmEmail(code, mockEmail);

        verify(emailConfirmationService).confirmEmail(code, mockEmail);
        verify(userRepository).findById(1L);
        verify(passwordResetService).deleteFromUser(mockUser);
        verify(mockUser).setEmail(newEmail);
        verify(userRepository).save(mockUser);
    }

    @Test
    void testConfirmEmail_WithNewEmailAndUserNotFound_ShouldThrowException() throws VotifyException {
        String code = "123456";
        Email mockEmail = mock(Email.class);
        Email newEmail = new Email("new@example.com");
        EmailConfirmation mockConfirmation = mock(EmailConfirmation.class);

        when(emailConfirmationService.confirmEmail(code, mockEmail)).thenReturn(mockConfirmation);
        when(mockConfirmation.getNewEmail()).thenReturn(newEmail);
        when(mockConfirmation.getUserId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(VotifyException.class, () -> {
            userService.confirmEmail(code, mockEmail);
        });

        verify(emailConfirmationService).confirmEmail(code, mockEmail);
        verify(userRepository).findById(1L);
        verifyNoInteractions(passwordResetService);
    }

    @Test
    void testConfirmEmail_WithCurrentEmailAndUserWithNewEmail_ShouldUpdateEmailWithoutContextLookup() throws VotifyException {
        String code = "123456";
        Email mockEmail = mock(Email.class);
        Email newEmail = new Email("new@example.com");
        EmailConfirmation mockConfirmation = mock(EmailConfirmation.class);
        User mockUser = mock(User.class);

        when(mockConfirmation.getNewEmail()).thenReturn(newEmail);
        when(emailConfirmationService.confirmEmail(code, mockEmail)).thenReturn(mockConfirmation);
        when(mockConfirmation.getUserId()).thenReturn(6L);
        when(userRepository.findById(6L)).thenReturn(Optional.of(mockUser));
        when(mockConfirmation.getNewEmail()).thenReturn(newEmail);

        userService.confirmEmail(code, mockEmail);

        verify(emailConfirmationService).confirmEmail(code, mockEmail);
        verifyNoInteractions(contextService);
        verify(passwordResetService).deleteFromUser(mockUser);
        verify(mockUser).setEmail(newEmail);
        verify(userRepository).save(mockUser);
    }

    @Test
    void testConfirmEmail_WithCurrentEmailAndUserWithNewEmail_ShouldUpdateEmailWithContextLookup() throws VotifyException {
        String code = "123456";
        Email mockEmail = mock(Email.class);
        Email newEmail = new Email("new@example.com");
        EmailConfirmation mockConfirmation = mock(EmailConfirmation.class);
        User mockUser = mock(User.class);

        when(mockUser.getEmail()).thenReturn(mockEmail);
        when(contextService.getUserOrThrow()).thenReturn(mockUser);
        when(mockConfirmation.getNewEmail()).thenReturn(newEmail);
        when(emailConfirmationService.confirmEmail(code, mockEmail)).thenReturn(mockConfirmation);
        when(mockConfirmation.getNewEmail()).thenReturn(newEmail);

        userService.confirmEmail(code, null);

        verify(emailConfirmationService).confirmEmail(code, mockEmail);
        verify(passwordResetService).deleteFromUser(mockUser);
        verify(mockUser).setEmail(newEmail);
        verify(userRepository).save(mockUser);
    }
}
