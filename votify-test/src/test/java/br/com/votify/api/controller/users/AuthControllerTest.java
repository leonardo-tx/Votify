package br.com.votify.api.controller.users;

import br.com.votify.api.configuration.SecurityConfig;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.dto.ApiResponse;
import br.com.votify.dto.users.*;
import br.com.votify.test.MockMvcHelper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.CoreMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class AuthControllerTest {
    private static PasswordResetResponseDTO passwordResetResponseDTO;
    private static String emailConfirmationCode;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SecurityConfig securityConfig;

    @Test
    @Order(0)
    public void register() throws Exception {
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO(
                "byces",
                "Byces",
                "123@gmail.com",
                "12345678"
        );
        ResultActions resultActions = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegisterDTO)));
        MockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.CREATED)
                .andExpect(jsonPath("data.id", is(4)))
                .andExpect(jsonPath("data.userName", is("byces")))
                .andExpect(jsonPath("data.name", is("Byces")))
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
        MockMvcHelper.testUnsuccessfulResponse(resultActions, VotifyErrorCode.PENDING_EMAIL_CONFIRMATION);
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
        MockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data", is(nullValue())));
    }

    @Test
    @Order(3)
    public void login_WhenEmailConfirmed_ShouldReturnTokens() throws Exception {
        UserLoginDTO userLoginDTO = new UserLoginDTO("123@gmail.com", "12345678");
        ResultActions resultActions = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userLoginDTO)));
        MockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data", is(nullValue())))
                .andExpect(MockMvcResultMatchers.cookie().exists("refresh_token"))
                .andExpect(MockMvcResultMatchers.cookie().exists("access_token"));
    }

    @Test
    @Order(4)
    public void logout() throws Exception {
        Cookie[] cookies = MockMvcHelper.login(
                mockMvc, objectMapper, "123@gmail.com", "12345678"
        );

        ResultActions resultActions = mockMvc.perform(post("/auth/logout")
                .cookie(cookies));
        MockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data", is(nullValue())))
                .andExpect(MockMvcResultMatchers.cookie().exists("refresh_token"))
                .andExpect(MockMvcResultMatchers.cookie().exists("access_token"))
                .andExpect(MockMvcResultMatchers.cookie().value("refresh_token", ""))
                .andExpect(MockMvcResultMatchers.cookie().value("access_token", ""));
    }

    @Test
    @Order(4)
    public void refreshTokens() throws Exception {
        Cookie[] cookies = MockMvcHelper.login(
                mockMvc, objectMapper, "123@gmail.com", "12345678"
        );

        ResultActions resultActions = mockMvc.perform(post("/auth/refresh-tokens")
                .cookie(cookies));
        MockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
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

        MockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
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

        MockMvcHelper.testUnsuccessfulResponse(resultActions, VotifyErrorCode.PASSWORD_RESET_REQUEST_EXISTS);
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
        MockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data", is(nullValue())));
    }

    @Test
    @Order(7)
    public void loginAfterPasswordReset() throws Exception {
        UserLoginDTO userLoginDTO = new UserLoginDTO("123@gmail.com", "87654321");
        ResultActions resultActions = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userLoginDTO)));
        MockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data", is(nullValue())))
                .andExpect(MockMvcResultMatchers.cookie().exists("refresh_token"))
                .andExpect(MockMvcResultMatchers.cookie().exists("access_token"));
    }

    @Test
    @Order(8)
    public void loginAfterChangingEmail() throws Exception {
        Cookie[] cookies = MockMvcHelper.login(
                mockMvc, objectMapper, "123@gmail.com", "87654321"
        );
        UserUpdateEmailRequestDTO userUpdateEmailRequestDTO = new UserUpdateEmailRequestDTO("321@gmail.com");

        ResultActions resultActions = mockMvc.perform(put("/users/me/email")
                .cookie(cookies)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userUpdateEmailRequestDTO)));
        MockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK);

        ApiResponse<String> apiResponse = objectMapper.readValue(
                resultActions.andReturn().getResponse().getContentAsByteArray(),
                new TypeReference<>() {}
        );
        emailConfirmationCode = apiResponse.getData();
        MockMvcHelper.login(mockMvc, objectMapper, "123@gmail.com", "87654321");
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
        MockMvcHelper.testUnsuccessfulResponse(resultActions, VotifyErrorCode.COMMON_UNAUTHORIZED);
    }

    @Test
    @Order(11)
    public void confirmChangedEmail() throws Exception {
        Cookie[] cookies = MockMvcHelper.login(
                mockMvc, objectMapper, "123@gmail.com", "87654321"
        );
        EmailConfirmationRequestDTO emailConfirmationRequestDto = new EmailConfirmationRequestDTO(
                null,
                emailConfirmationCode
        );
        ResultActions resultActions = mockMvc.perform(post("/auth/confirm-email")
                .cookie(cookies)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailConfirmationRequestDto)));
        MockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK);
        MockMvcHelper.login(mockMvc, objectMapper, "321@gmail.com", "87654321");
    }
}
