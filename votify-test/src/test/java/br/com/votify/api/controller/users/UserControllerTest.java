package br.com.votify.api.controller.users;

import br.com.votify.core.utils.exceptions.VotifyErrorCode;
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
}