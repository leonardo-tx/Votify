package br.com.votify.api.controller.users;

import br.com.votify.api.configuration.SecurityConfig;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.dto.ApiResponse;
import br.com.votify.dto.users.*;
import br.com.votify.test.MockMvcHelper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.CoreMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SecurityConfig securityConfig;

    @InjectMocks
    private AuthController authController;

    private static PasswordResetResponseDTO passwordResetResponseDTO;

    private static User user;

    private static UserRegisterDTO userRegisterDTO;

    private static UserDetailedViewDTO userDetailedViewDTO;

    @BeforeAll
    public static void prepare() {
        userRegisterDTO = new UserRegisterDTO(
                "byces",
                "Byces",
                "123@gmail.com",
                "12345678"
        );

        user = userRegisterDTO.convertToEntity();
    }

    @Test
    @Order(0)
    public void register() throws Exception {

        ResultActions resultActions = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegisterDTO)));
        MockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.CREATED)
                .andExpect(jsonPath("data.id", is(4)))
                .andExpect(jsonPath("data.userName", is("byces")))
                .andExpect(jsonPath("data.name", is("Byces")))
                .andExpect(jsonPath("data.email", is("123@gmail.com")))
                .andExpect(jsonPath("data.role", is("CommonUser")));

        ApiResponse<UserDetailedViewDTO> apiResponse = objectMapper.readValue(
                resultActions.andReturn().getResponse().getContentAsByteArray(),
                new TypeReference<>() {}
        );
        userDetailedViewDTO = apiResponse.getData();

    }

    @Test
    @Order(1)
    public void login_WhenEmailNotConfirmed_ShouldReturnError() throws Exception {
        userRegisterDTO = new UserRegisterDTO(
                "byces2",
                "Byces2",
                "1234@gmail.com",
                "12345678"
        );

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegisterDTO)));

        UserLoginDTO userLoginDTO = new UserLoginDTO(userRegisterDTO.getEmail(), userRegisterDTO.getPassword());

        ResultActions resultActions = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userLoginDTO)));

        resultActions
                .andExpect(jsonPath("$.errorCode", is("email.pending.confirmation")));
    }

    @Test
    @Order(1)
    public void login_WhenEmailConfirmed_ShouldReturnTokens() throws Exception {
        UserLoginDTO userLoginDTO = new UserLoginDTO("123@gmail.com", "12345678");

        EmailConfirmationDto emailConfirmationDto = new EmailConfirmationDto(userDetailedViewDTO.getEmail(), userDetailedViewDTO.getConfirmationCode());

        mockMvc.perform(post("/users/confirm-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailConfirmationDto)));

        ResultActions resultActions = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userLoginDTO)));

        MockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data", is(nullValue())))
                .andExpect(MockMvcResultMatchers.cookie().exists("refresh_token"))
                .andExpect(MockMvcResultMatchers.cookie().exists("access_token"));
    }

    @Test
    @Order(1)
    public void logout() throws Exception {
        EmailConfirmationDto emailConfirmationDto = new EmailConfirmationDto(userDetailedViewDTO.getEmail(), userDetailedViewDTO.getConfirmationCode());

        mockMvc.perform(post("/users/confirm-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailConfirmationDto)));

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
    @Order(1)
    public void refreshTokens() throws Exception {
        EmailConfirmationDto emailConfirmationDto = new EmailConfirmationDto(userDetailedViewDTO.getEmail(), userDetailedViewDTO.getConfirmationCode());

        mockMvc.perform(post("/users/confirm-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailConfirmationDto)));

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
    @Order(1)
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
    @Order(2)
    public void forgotPasswordDuplicated() throws Exception {
        PasswordResetRequestDTO passwordResetRequestDTO = new PasswordResetRequestDTO("123@gmail.com");
        ResultActions resultActions = mockMvc.perform(post("/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordResetRequestDTO)));

        MockMvcHelper.testUnsuccessfulResponse(resultActions, VotifyErrorCode.PASSWORD_RESET_REQUEST_EXISTS);
    }

    @Test
    @Order(3)
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
    @Order(4)
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
}
