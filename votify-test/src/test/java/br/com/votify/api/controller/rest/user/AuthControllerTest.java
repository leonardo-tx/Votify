package br.com.votify.api.controller.rest.user;

import br.com.votify.core.model.user.*;
import br.com.votify.core.model.user.field.Email;
import br.com.votify.core.model.user.field.Name;
import br.com.votify.core.model.user.field.Password;
import br.com.votify.core.model.user.field.UserName;
import br.com.votify.core.service.user.ContextService;
import br.com.votify.core.service.user.PasswordResetService;
import br.com.votify.core.service.user.UserService;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.dto.user.*;
import br.com.votify.test.suites.ControllerTest;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentMatchers;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.CoreMatchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class AuthControllerTest extends ControllerTest {
    @MockitoBean
    private UserService userService;

    @MockitoBean
    private ContextService contextService;

    @MockitoBean
    private PasswordResetService passwordResetService;

    @BeforeEach
    void setupBeforeEach() {
        when(userService.getContext()).thenReturn(contextService);
    }

    @Test
    void register() throws Exception {
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO(
                "test",
                "Teste",
                "123@gmail.com",
                "12345678"
        );
        User createdUser = User.parseUnsafe(
                49L,
                new Email("123@gmail.com"),
                new UserName("test"),
                new Name("Teste"),
                "encrypted_password",
                UserRole.COMMON,
                false
        );
        when(userService.register(ArgumentMatchers.any(UserRegister.class))).thenReturn(createdUser);

        ResultActions resultActions = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegisterDTO)));
        mockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.CREATED)
                .andExpect(jsonPath("data.id", is(49)))
                .andExpect(jsonPath("data.userName", is("test")))
                .andExpect(jsonPath("data.name", is("Teste")))
                .andExpect(jsonPath("data.email", is("123@gmail.com")))
                .andExpect(jsonPath("data.role", is("COMMON")));
    }

    @Test
    void login_WhenEmailNotConfirmed_ShouldReturnError() throws Exception {
        UserLoginDTO userLoginDTO = new UserLoginDTO("123@gmail.com", "12345678");
        when(userService.login(eq(new Email("123@gmail.com")), eq(new Password("12345678"))))
                .thenThrow(new VotifyException(VotifyErrorCode.PENDING_EMAIL_CONFIRMATION));

        ResultActions resultActions = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userLoginDTO)));
        mockMvcHelper.testUnsuccessfulResponse(resultActions, VotifyErrorCode.PENDING_EMAIL_CONFIRMATION);
    }

    @Test
    void confirmEmail() throws Exception {
        EmailConfirmationRequestDTO emailConfirmationRequestDto = new EmailConfirmationRequestDTO(
                "123@gmail.com",
                "confirmation_code"
        );
        ResultActions resultActions = mockMvc.perform(post("/api/auth/confirm-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailConfirmationRequestDto)));
        mockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data", is(nullValue())));

        verify(userService).confirmEmail(eq("confirmation_code"), eq(new Email("123@gmail.com")));
    }

    @Test
    void login_WhenEmailConfirmed_ShouldReturnTokens() throws Exception {
        AccessToken accessToken = mock(AccessToken.class);
        RefreshToken refreshToken = mock(RefreshToken.class);
        AuthTokens authTokens = mock(AuthTokens.class);

        when(authTokens.getAccessToken()).thenReturn(accessToken);
        when(authTokens.getRefreshToken()).thenReturn(refreshToken);

        when(accessToken.getCode()).thenReturn("access_token_code");
        when(refreshToken.getCode()).thenReturn("refresh_token_code");

        UserLoginDTO userLoginDTO = new UserLoginDTO("123@gmail.com", "12345678");
        when(userService.login(eq(new Email("123@gmail.com")), eq(new Password("12345678"))))
                .thenReturn(authTokens);
        ResultActions resultActions = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userLoginDTO)));
        mockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data", is(nullValue())));
        mockMvcHelper.validateCookie(
                resultActions,
                userProperties.getAccessTokenCookieName(),
                "access_token_code",
                userProperties.getAccessTokenExpirationSeconds()
        );
        mockMvcHelper.validateCookie(
                resultActions,
                userProperties.getRefreshTokenCookieName(),
                "refresh_token_code",
                userProperties.getRefreshTokenExpirationSeconds()
        );
    }

    @Test
    void logout() throws Exception {
        ResultActions resultActions = mockMvc.perform(post("/api/auth/logout"));
        mockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data", is(nullValue())));
        mockMvcHelper.validateCookie(resultActions, userProperties.getAccessTokenCookieName(), "", 0);
        mockMvcHelper.validateCookie(resultActions, userProperties.getRefreshTokenCookieName(), "", 0);

        verify(userService).logout();
    }

    @Test
    void refreshTokens() throws Exception {
        AccessToken accessToken = mock(AccessToken.class);
        RefreshToken refreshToken = mock(RefreshToken.class);
        AuthTokens authTokens = mock(AuthTokens.class);

        when(authTokens.getAccessToken()).thenReturn(accessToken);
        when(authTokens.getRefreshToken()).thenReturn(refreshToken);

        when(accessToken.getCode()).thenReturn("access_token_code");
        when(refreshToken.getCode()).thenReturn("refresh_token_code");

        when(contextService.refreshTokens()).thenReturn(authTokens);

        ResultActions resultActions = mockMvc.perform(post("/api/auth/refresh-tokens"));
        mockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data", is(nullValue())));
        mockMvcHelper.validateCookie(
                resultActions,
                userProperties.getAccessTokenCookieName(),
                "access_token_code",
                userProperties.getAccessTokenExpirationSeconds()
        );
        mockMvcHelper.validateCookie(
                resultActions,
                userProperties.getRefreshTokenCookieName(),
                "refresh_token_code",
                userProperties.getRefreshTokenExpirationSeconds()
        );
    }

    @Test
    void forgotPassword() throws Exception {
        User user = mock(User.class);
        PasswordResetRequestDTO passwordResetRequestDTO = new PasswordResetRequestDTO("123@gmail.com");

        when(userService.getUserByEmail(eq(new Email("123@gmail.com")))).thenReturn(user);

        ResultActions resultActions = mockMvc.perform(post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordResetRequestDTO)));
        mockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data", is(nullValue())));

        verify(passwordResetService).createPasswordResetRequest(user);
    }

    @Test
    void forgotPasswordDuplicated() throws Exception {
        User user = mock(User.class);

        when(userService.getUserByEmail(eq(new Email("123@gmail.com")))).thenReturn(user);
        when(passwordResetService.createPasswordResetRequest(user))
                .thenThrow(new VotifyException(VotifyErrorCode.PASSWORD_RESET_REQUEST_EXISTS));

        PasswordResetRequestDTO passwordResetRequestDTO = new PasswordResetRequestDTO("123@gmail.com");
        ResultActions resultActions = mockMvc.perform(post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordResetRequestDTO)));
        mockMvcHelper.testUnsuccessfulResponse(resultActions, VotifyErrorCode.PASSWORD_RESET_REQUEST_EXISTS);
    }

    @Test
    void resetPassword() throws Exception {
        PasswordResetConfirmDTO passwordResetConfirmDTO = new PasswordResetConfirmDTO(
                "password_code",
                "87654321"
        );
        ResultActions resultActions = mockMvc.perform(post("/api/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordResetConfirmDTO)));
        mockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data", is(nullValue())));
        verify(userService).resetPassword(eq("password_code"), eq(new Password("87654321")));
    }

    @Test
    void confirmChangedEmailNotAuthenticated() throws Exception {
        doThrow(new VotifyException(VotifyErrorCode.COMMON_UNAUTHORIZED))
                .when(userService)
                .confirmEmail("email_confirmation_code", null);
        EmailConfirmationRequestDTO emailConfirmationRequestDto = new EmailConfirmationRequestDTO(
                null,
                "email_confirmation_code"
        );
        ResultActions resultActions = mockMvc.perform(post("/api/auth/confirm-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailConfirmationRequestDto)));
       mockMvcHelper.testUnsuccessfulResponse(resultActions, VotifyErrorCode.COMMON_UNAUTHORIZED);
    }

    @Test
    void confirmChangedEmail() throws Exception {
        EmailConfirmationRequestDTO emailConfirmationRequestDto = new EmailConfirmationRequestDTO(
                null,
                "email_confirmation_code"
        );
        ResultActions resultActions = mockMvc.perform(post("/api/auth/confirm-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailConfirmationRequestDto)));
        mockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK);
        verify(userService).confirmEmail("email_confirmation_code", null);
    }
}
