package br.com.votify.api.controller.users;

import br.com.votify.api.configuration.SecurityConfig;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.dto.ApiResponse;
import br.com.votify.dto.users.*;
import br.com.votify.test.suites.ControllerTest;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.CoreMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class AuthControllerTest extends ControllerTest {
    private static PasswordResetResponseDTO passwordResetResponseDTO;
    private static String emailConfirmationCode;

    @Autowired
    private SecurityConfig securityConfig;

    @Test
    @Order(0)
    public void register() throws Exception {
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO(
                "test",
                "Teste",
                "123@gmail.com",
                "12345678"
        );
        ResultActions resultActions = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegisterDTO)));
        mockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.CREATED)
                .andExpect(jsonPath("data.id", is(48)))
                .andExpect(jsonPath("data.userName", is("test")))
                .andExpect(jsonPath("data.name", is("Teste")))
                .andExpect(jsonPath("data.email", is("123@gmail.com")))
                .andExpect(jsonPath("data.role", is("CommonUser")))
                .andExpect(jsonPath("data.confirmationCode", notNullValue()));

        ApiResponse<UserDetailedViewDTO> apiResponse = objectMapper.readValue(
                resultActions.andReturn().getResponse().getContentAsByteArray(),
                new TypeReference<>() {}
        );
        emailConfirmationCode = apiResponse.getData().getConfirmationCode();
    }

    @Test
    @Order(1)
    public void login_WhenEmailNotConfirmed_ShouldReturnError() throws Exception {
        UserLoginDTO userLoginDTO = new UserLoginDTO("123@gmail.com", "12345678");
        ResultActions resultActions = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userLoginDTO)));
        mockMvcHelper.testUnsuccessfulResponse(resultActions, VotifyErrorCode.PENDING_EMAIL_CONFIRMATION);
    }

    @Test
    @Order(2)
    public void confirmEmail() throws Exception {
        EmailConfirmationRequestDTO emailConfirmationRequestDto = new EmailConfirmationRequestDTO(
                "123@gmail.com",
                emailConfirmationCode
        );
        ResultActions resultActions = mockMvc.perform(post("/auth/confirm-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailConfirmationRequestDto)));
        mockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data", is(nullValue())));
    }

    @Test
    @Order(3)
    public void login_WhenEmailConfirmed_ShouldReturnTokens() throws Exception {
        UserLoginDTO userLoginDTO = new UserLoginDTO("123@gmail.com", "12345678");
        ResultActions resultActions = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userLoginDTO)));
        mockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data", is(nullValue())))
                .andExpect(MockMvcResultMatchers.cookie().exists("refresh_token"))
                .andExpect(MockMvcResultMatchers.cookie().exists("access_token"));
    }

    @Test
    @Order(4)
    public void logout() throws Exception {
        Cookie[] cookies = mockMvcHelper.login("123@gmail.com", "12345678");

        ResultActions resultActions = mockMvc.perform(post("/auth/logout")
                .cookie(cookies));
        mockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data", is(nullValue())))
                .andExpect(MockMvcResultMatchers.cookie().exists("refresh_token"))
                .andExpect(MockMvcResultMatchers.cookie().exists("access_token"))
                .andExpect(MockMvcResultMatchers.cookie().value("refresh_token", ""))
                .andExpect(MockMvcResultMatchers.cookie().value("access_token", ""));
    }

    @Test
    @Order(4)
    public void refreshTokens() throws Exception {
        Cookie[] cookies = mockMvcHelper.login("123@gmail.com", "12345678");

        ResultActions resultActions = mockMvc.perform(post("/auth/refresh-tokens")
                .cookie(cookies));
        mockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data", is(nullValue())))
                .andExpect(MockMvcResultMatchers.cookie().exists("refresh_token"))
                .andExpect(MockMvcResultMatchers.cookie().exists("access_token"));
    }

    @Test
    @Order(4)
    public void forgotPassword() throws Exception {
        PasswordResetRequestDTO passwordResetRequestDTO = new PasswordResetRequestDTO("123@gmail.com");
        ResultActions resultActions = mockMvc.perform(post("/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordResetRequestDTO)));

        mockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data.code", is(notNullValue())))
                .andExpect(jsonPath("data.expirationMinutes", is(securityConfig.getPasswordResetProperties().getExpirationMinutes())));

        ApiResponse<PasswordResetResponseDTO> apiResponse = objectMapper.readValue(
                resultActions.andReturn().getResponse().getContentAsByteArray(),
                new TypeReference<>() {}
        );
        passwordResetResponseDTO = apiResponse.getData();
    }

    @Test
    @Order(5)
    public void forgotPasswordDuplicated() throws Exception {
        PasswordResetRequestDTO passwordResetRequestDTO = new PasswordResetRequestDTO("123@gmail.com");
        ResultActions resultActions = mockMvc.perform(post("/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordResetRequestDTO)));

        mockMvcHelper.testUnsuccessfulResponse(resultActions, VotifyErrorCode.PASSWORD_RESET_REQUEST_EXISTS);
    }

    @Test
    @Order(6)
    public void resetPassword() throws Exception {
        PasswordResetConfirmDTO passwordResetConfirmDTO = new PasswordResetConfirmDTO(
                passwordResetResponseDTO.getCode(),
                "87654321"
        );

        ResultActions resultActions = mockMvc.perform(post("/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordResetConfirmDTO)));
        mockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data", is(nullValue())));
    }

    @Test
    @Order(7)
    public void loginAfterPasswordReset() throws Exception {
        UserLoginDTO userLoginDTO = new UserLoginDTO("123@gmail.com", "87654321");
        ResultActions resultActions = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userLoginDTO)));
        mockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data", is(nullValue())))
                .andExpect(MockMvcResultMatchers.cookie().exists("refresh_token"))
                .andExpect(MockMvcResultMatchers.cookie().exists("access_token"));
    }

    @Test
    @Order(8)
    public void loginAfterChangingEmail() throws Exception {
        Cookie[] cookies = mockMvcHelper.login("123@gmail.com", "87654321");
        UserUpdateEmailRequestDTO userUpdateEmailRequestDTO = new UserUpdateEmailRequestDTO("321@gmail.com");

        ResultActions resultActions = mockMvc.perform(put("/users/me/email")
                .cookie(cookies)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userUpdateEmailRequestDTO)));
        mockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK);

        ApiResponse<String> apiResponse = objectMapper.readValue(
                resultActions.andReturn().getResponse().getContentAsByteArray(),
                new TypeReference<>() {}
        );
        emailConfirmationCode = apiResponse.getData();
        mockMvcHelper.login("123@gmail.com", "87654321");
    }

    @Test
    @Order(10)
    public void confirmChangedEmailNotAuthenticated() throws Exception {
        EmailConfirmationRequestDTO emailConfirmationRequestDto = new EmailConfirmationRequestDTO(
                null,
                emailConfirmationCode
        );
        ResultActions resultActions = mockMvc.perform(post("/auth/confirm-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailConfirmationRequestDto)));
       mockMvcHelper.testUnsuccessfulResponse(resultActions, VotifyErrorCode.COMMON_UNAUTHORIZED);
    }

    @Test
    @Order(11)
    public void confirmChangedEmail() throws Exception {
        Cookie[] cookies = mockMvcHelper.login("123@gmail.com", "87654321");
        EmailConfirmationRequestDTO emailConfirmationRequestDto = new EmailConfirmationRequestDTO(
                null,
                emailConfirmationCode
        );
        ResultActions resultActions = mockMvc.perform(post("/auth/confirm-email")
                .cookie(cookies)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailConfirmationRequestDto)));
        mockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK);
        mockMvcHelper.login("321@gmail.com", "87654321");
    }
}
