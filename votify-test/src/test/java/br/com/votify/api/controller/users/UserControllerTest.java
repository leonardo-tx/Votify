package br.com.votify.api.controller.users;

import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.dto.users.UserUpdateEmailRequestDTO;
import br.com.votify.dto.users.UserUpdateInfoRequestDTO;
import br.com.votify.dto.users.UserUpdatePasswordRequestDTO;
import br.com.votify.test.MockMvcHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Order(0)
    public void getUserByIdAsGuest() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/users/{id}", 2));
        MockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data.id", is(2)))
                .andExpect(jsonPath("data.userName", is("moderator")))
                .andExpect(jsonPath("data.name", is("Moderator")))
                .andExpect(jsonPath("data.email", is(nullValue())))
                .andExpect(jsonPath("data.role", is(nullValue())));
    }

    @Test
    @Order(0)
    public void getUserByIdAsCommonUser() throws Exception {
        Cookie[] cookies = MockMvcHelper.login(
                mockMvc, objectMapper, "common@votify.com.br", "password123"
        );
        ResultActions resultActions = mockMvc.perform(get("/users/{id}", 1)
                .cookie(cookies)
        );
        MockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data.id", is(1)))
                .andExpect(jsonPath("data.userName", is("admin")))
                .andExpect(jsonPath("data.name", is("Administrator")))
                .andExpect(jsonPath("data.email", is(nullValue())))
                .andExpect(jsonPath("data.role", is(nullValue())));
    }

    @Test
    @Order(0)
    public void getUserByIdAsModeratorUser() throws Exception {
        Cookie[] cookies = MockMvcHelper.login(
                mockMvc, objectMapper, "moderator@votify.com.br", "moderator321"
        );
        ResultActions resultActions = mockMvc.perform(get("/users/{id}", 1)
                .cookie(cookies)
        );
        MockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data.id", is(1)))
                .andExpect(jsonPath("data.userName", is("admin")))
                .andExpect(jsonPath("data.name", is("Administrator")))
                .andExpect(jsonPath("data.email", is("admin@votify.com.br")))
                .andExpect(jsonPath("data.role", is("AdminUser")));
    }

    @Test
    @Order(0)
    public void getUserByIdAsAdminUser() throws Exception {
        Cookie[] cookies = MockMvcHelper.login(
                mockMvc, objectMapper, "admin@votify.com.br", "admin123"
        );
        ResultActions resultActions = mockMvc.perform(get("/users/{id}", 3)
                .cookie(cookies)
        );
        MockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data.id", is(3)))
                .andExpect(jsonPath("data.userName", is("common")))
                .andExpect(jsonPath("data.name", is("Common")))
                .andExpect(jsonPath("data.email", is("common@votify.com.br")))
                .andExpect(jsonPath("data.role", is("CommonUser")));
    }

    @Test
    @Order(0)
    public void getSelf() throws Exception {
        Cookie[] cookies = MockMvcHelper.login(
                mockMvc, objectMapper, "common@votify.com.br", "password123"
        );
        ResultActions resultActions = mockMvc.perform(get("/users/me").cookie(cookies));
        MockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data.id", is(3)))
                .andExpect(jsonPath("data.userName", is("common")))
                .andExpect(jsonPath("data.name", is("Common")))
                .andExpect(jsonPath("data.email", is("common@votify.com.br")))
                .andExpect(jsonPath("data.role", is("CommonUser")));
    }

    @Test
    @Order(0)
    public void getSelfNotLogged() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/users/me"));
        MockMvcHelper.testUnsuccessfulResponse(resultActions, VotifyErrorCode.COMMON_UNAUTHORIZED);
    }

    @Test
    @Order(2)
    public void deleteSelfNotLogged() throws Exception {
        ResultActions resultActions = mockMvc.perform(delete("/users/me"));
        MockMvcHelper.testUnsuccessfulResponse(resultActions, VotifyErrorCode.COMMON_UNAUTHORIZED);
    }

    @Test
    @Order(2)
    public void deleteSelf() throws Exception {
        Cookie[] cookies = MockMvcHelper.login(
                mockMvc, objectMapper, "common@votify.com.br", "password123"
        );
        ResultActions resultActions = mockMvc.perform(delete("/users/me").cookie(cookies));
        MockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data", is(nullValue())));
    }


    @Test
    @Order(1)
    public void updatePassword_Success() throws Exception {
        Cookie[] cookies = MockMvcHelper.login(
                mockMvc, objectMapper, "moderator@votify.com.br", "moderator321"
        );
        UserUpdatePasswordRequestDTO requestDTO = new UserUpdatePasswordRequestDTO("moderator321", "newSecurePass123");

        ResultActions resultActions = mockMvc.perform(put("/users/me/password")
                .cookie(cookies)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)));

        MockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data", is(nullValue())));

        MockMvcHelper.loginExpectingError(
                mockMvc, objectMapper, "moderator@votify.com.br", "moderator321", VotifyErrorCode.LOGIN_UNAUTHORIZED
        );

        MockMvcHelper.login(mockMvc, objectMapper, "moderator@votify.com.br", "newSecurePass123");
    }

    @Test
    @Order(1)
    public void updatePassword_Fail_InvalidOldPassword() throws Exception {
        Cookie[] cookies = MockMvcHelper.login(
                mockMvc, objectMapper, "common@votify.com.br", "password123"
        );
        UserUpdatePasswordRequestDTO requestDTO = new UserUpdatePasswordRequestDTO("wrongOldPassword", "newPass");

        ResultActions resultActions = mockMvc.perform(put("/users/me/password")
                .cookie(cookies)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)));

        MockMvcHelper.testUnsuccessfulResponse(resultActions, VotifyErrorCode.INVALID_OLD_PASSWORD);
    }

    @Test
    @Order(1)
    public void updatePassword_Fail_NotLogged() throws Exception {
        UserUpdatePasswordRequestDTO requestDTO = new UserUpdatePasswordRequestDTO("anyPassword", "newPass");

        ResultActions resultActions = mockMvc.perform(put("/users/me/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)));

        MockMvcHelper.testUnsuccessfulResponse(resultActions, VotifyErrorCode.COMMON_UNAUTHORIZED);
    }

    @Test
    @Order(1)
    public void updateEmail_Success() throws Exception {
        Cookie[] cookies = MockMvcHelper.login(
                mockMvc, objectMapper, "admin@votify.com.br", "admin123"
        );
        String newEmail = "admin-new@votify.com.br";
        UserUpdateEmailRequestDTO requestDTO = new UserUpdateEmailRequestDTO(newEmail);

        ResultActions resultActions = mockMvc.perform(put("/users/me/email")
                .cookie(cookies)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)));

        MockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data.email", is(newEmail)));

        ResultActions checkResult = mockMvc.perform(get("/users/me").cookie(cookies));
        MockMvcHelper.testSuccessfulResponse(checkResult, HttpStatus.OK)
                .andExpect(jsonPath("data.email", is(newEmail)));

        UserUpdateEmailRequestDTO restoreDTO = new UserUpdateEmailRequestDTO("admin@votify.com.br");
        mockMvc.perform(put("/users/me/email")
                .cookie(cookies)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restoreDTO)));
    }

    @Test
    @Order(1)
    public void updateEmail_Fail_EmailExists() throws Exception {
        Cookie[] cookies = MockMvcHelper.login(
                mockMvc, objectMapper, "common@votify.com.br", "password123"
        );
        String existingEmail = "moderator@votify.com.br";
        UserUpdateEmailRequestDTO requestDTO = new UserUpdateEmailRequestDTO(existingEmail);

        ResultActions resultActions = mockMvc.perform(put("/users/me/email")
                .cookie(cookies)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)));

        MockMvcHelper.testUnsuccessfulResponse(resultActions, VotifyErrorCode.EMAIL_ALREADY_EXISTS);
    }

    @Test
    @Order(1)
    public void updateEmail_Fail_InvalidEmail() throws Exception {
        Cookie[] cookies = MockMvcHelper.login(
                mockMvc, objectMapper, "common@votify.com.br", "password123"
        );

        UserUpdateEmailRequestDTO requestDTOBlank = new UserUpdateEmailRequestDTO("   ");
        ResultActions resultActionsBlank = mockMvc.perform(put("/users/me/email")
                .cookie(cookies)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTOBlank)));
        MockMvcHelper.testUnsuccessfulResponse(resultActionsBlank, VotifyErrorCode.EMAIL_INVALID);

        UserUpdateEmailRequestDTO requestDTONull = new UserUpdateEmailRequestDTO(null);
        ResultActions resultActionsNull = mockMvc.perform(put("/users/me/email")
                .cookie(cookies)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTONull)));
        MockMvcHelper.testUnsuccessfulResponse(resultActionsNull, VotifyErrorCode.EMAIL_INVALID);
    }

    @Test
    @Order(1)
    public void updateEmail_Fail_NotLogged() throws Exception {
        UserUpdateEmailRequestDTO requestDTO = new UserUpdateEmailRequestDTO("new@email.com");

        ResultActions resultActions = mockMvc.perform(put("/users/me/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)));

        MockMvcHelper.testUnsuccessfulResponse(resultActions, VotifyErrorCode.COMMON_UNAUTHORIZED);
    }
}