package br.com.votify.api.controller.rest.user;

import br.com.votify.core.model.user.PermissionFlags;
import br.com.votify.core.model.user.User;
import br.com.votify.core.model.user.UserRole;
import br.com.votify.core.model.user.field.Email;
import br.com.votify.core.model.user.field.Name;
import br.com.votify.core.model.user.field.Password;
import br.com.votify.core.model.user.field.UserName;
import br.com.votify.core.service.user.ContextService;
import br.com.votify.core.service.user.UserService;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.dto.user.UserUpdateEmailRequestDTO;
import br.com.votify.dto.user.UserUpdateInfoRequestDTO;
import br.com.votify.dto.user.UserUpdatePasswordRequestDTO;
import br.com.votify.test.suites.ControllerTest;
import org.junit.jupiter.api.*;
import org.springframework.http.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class UserControllerTest extends ControllerTest {
    @MockitoBean
    private UserService userService;

    @MockitoBean
    private ContextService contextService;

    @BeforeEach
    void setupBeforeEach() {
        when(userService.getContext()).thenReturn(contextService);
    }

    @Test
    void getUserByIdAsGuest() throws Exception {
        User user = User.parseUnsafe(
                2L,
                new Email("moderator@votify.com.br"),
                new UserName("moderator"),
                new Name("Moderator"),
                "encrypted_password",
                UserRole.MODERATOR,
                true
        );

        when(contextService.getUserOptional()).thenReturn(Optional.empty());
        when(userService.getUserById(2)).thenReturn(user);

        ResultActions resultActions = mockMvc.perform(get("/api/users/{id}", 2));
        mockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data.id", is(2)))
                .andExpect(jsonPath("data.userName", is("moderator")))
                .andExpect(jsonPath("data.name", is("Moderator")))
                .andExpect(jsonPath("data.email", is(nullValue())))
                .andExpect(jsonPath("data.role", is(nullValue())));
    }

    @Test
    void getUserByIdWithoutDetailedUserPermission() throws Exception {
        User user = User.parseUnsafe(
                1L,
                new Email("admin@votify.com.br"),
                new UserName("admin"),
                new Name("Administrator"),
                "encrypted_password",
                UserRole.ADMIN,
                true
        );
        User authenticatedUser = mock(User.class);
        when(authenticatedUser.hasPermission(PermissionFlags.DETAILED_USER)).thenReturn(false);

        when(contextService.getUserOptional()).thenReturn(Optional.of(authenticatedUser));
        when(userService.getUserById(1)).thenReturn(user);

        ResultActions resultActions = mockMvc.perform(get("/api/users/{id}", 1));
        mockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data.id", is(1)))
                .andExpect(jsonPath("data.userName", is("admin")))
                .andExpect(jsonPath("data.name", is("Administrator")))
                .andExpect(jsonPath("data.email", is(nullValue())))
                .andExpect(jsonPath("data.role", is(nullValue())));
    }

    @Test
    void getUserByIdWithDetailedUserPermission() throws Exception {
        User user = User.parseUnsafe(
                1L,
                new Email("admin@votify.com.br"),
                new UserName("admin"),
                new Name("Administrator"),
                "encrypted_password",
                UserRole.ADMIN,
                true
        );
        User authenticatedUser = mock(User.class);
        when(authenticatedUser.hasPermission(PermissionFlags.DETAILED_USER)).thenReturn(true);

        when(contextService.getUserOptional()).thenReturn(Optional.of(authenticatedUser));
        when(userService.getUserById(1)).thenReturn(user);

        ResultActions resultActions = mockMvc.perform(get("/api/users/{id}", 1));
        mockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data.id", is(1)))
                .andExpect(jsonPath("data.userName", is("admin")))
                .andExpect(jsonPath("data.name", is("Administrator")))
                .andExpect(jsonPath("data.email", is("admin@votify.com.br")))
                .andExpect(jsonPath("data.role", is("ADMIN")));
    }

    @Test
    void getSelf() throws Exception {
        User authenticatedUser = User.parseUnsafe(
                3L,
                new Email("common@votify.com.br"),
                new UserName("common"),
                new Name("Common"),
                "encrypted_password",
                UserRole.COMMON,
                true
        );
        when(contextService.getUserOrThrow()).thenReturn(authenticatedUser);

        ResultActions resultActions = mockMvc.perform(get("/api/users/me"));
        mockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data.id", is(3)))
                .andExpect(jsonPath("data.userName", is("common")))
                .andExpect(jsonPath("data.name", is("Common")))
                .andExpect(jsonPath("data.email", is("common@votify.com.br")))
                .andExpect(jsonPath("data.role", is("COMMON")));
    }

    @Test
    void getSelfNotLogged() throws Exception {
        when(contextService.getUserOrThrow()).thenThrow(new VotifyException(VotifyErrorCode.COMMON_UNAUTHORIZED));

        ResultActions resultActions = mockMvc.perform(get("/api/users/me"));
        mockMvcHelper.testUnsuccessfulResponse(resultActions, VotifyErrorCode.COMMON_UNAUTHORIZED);
    }
    
    @Test
    void updatePassword_Success() throws Exception {
        UserUpdatePasswordRequestDTO requestDTO = new UserUpdatePasswordRequestDTO(
                "moderator321",
                "newSecurePass123"
        );
        ResultActions resultActions = mockMvc.perform(put("/api/users/me/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)));

        mockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data", is(nullValue())));
        mockMvcHelper.validateCookie(resultActions, userProperties.getAccessTokenCookieName(), "", 0);
        mockMvcHelper.validateCookie(resultActions, userProperties.getRefreshTokenCookieName(), "", 0);

        verify(userService).updateUserPassword(
                eq(new Password("moderator321")),
                eq(new Password("newSecurePass123"))
        );
    }

    @Test
    void updatePassword_Fail_InvalidOldPassword() throws Exception {
        UserUpdatePasswordRequestDTO requestDTO = new UserUpdatePasswordRequestDTO(
                "wrongOldPassword",
                "newPassword"
        );
        doThrow(new VotifyException(VotifyErrorCode.INVALID_OLD_PASSWORD))
                .when(userService)
                .updateUserPassword(
                        eq(new Password("wrongOldPassword")),
                        eq(new Password("newPassword"))
                );

        ResultActions resultActions = mockMvc.perform(put("/api/users/me/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)));

        mockMvcHelper.testUnsuccessfulResponse(resultActions, VotifyErrorCode.INVALID_OLD_PASSWORD);
    }

    @Test
    void updatePassword_Fail_NotLogged() throws Exception {
        UserUpdatePasswordRequestDTO requestDTO = new UserUpdatePasswordRequestDTO(
                "anyPassword",
                "newPassword"
        );
        doThrow(new VotifyException(VotifyErrorCode.COMMON_UNAUTHORIZED))
                .when(userService)
                .updateUserPassword(
                        eq(new Password("anyPassword")),
                        eq(new Password("newPassword"))
                );

        ResultActions resultActions = mockMvc.perform(put("/api/users/me/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)));

        mockMvcHelper.testUnsuccessfulResponse(resultActions, VotifyErrorCode.COMMON_UNAUTHORIZED);
    }

    @Test
    void updateEmail_Success() throws Exception {
        String newEmail = "admin-new@votify.com.br";
        UserUpdateEmailRequestDTO requestDTO = new UserUpdateEmailRequestDTO(newEmail);

        ResultActions resultActions = mockMvc.perform(put("/api/users/me/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)));

        mockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data", is(nullValue())));

        verify(userService).updateUserEmail(eq(new Email(newEmail)));
    }

    @Test
    void updateEmail_Fail_EmailExists() throws Exception {
        String existingEmail = "moderator@votify.com.br";
        UserUpdateEmailRequestDTO requestDTO = new UserUpdateEmailRequestDTO(existingEmail);
        doThrow(new VotifyException(VotifyErrorCode.EMAIL_ALREADY_EXISTS))
                .when(userService)
                .updateUserEmail(eq(new Email(existingEmail)));

        ResultActions resultActions = mockMvc.perform(put("/api/users/me/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)));

        mockMvcHelper.testUnsuccessfulResponse(resultActions, VotifyErrorCode.EMAIL_ALREADY_EXISTS);
    }

    @Test
    void updateEmail_Fail_InvalidEmail() throws Exception {
        UserUpdateEmailRequestDTO requestDTOBlank = new UserUpdateEmailRequestDTO("   ");
        ResultActions resultActionsBlank = mockMvc.perform(put("/api/users/me/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTOBlank)));
        mockMvcHelper.testUnsuccessfulResponse(
                resultActionsBlank,
                VotifyErrorCode.EMAIL_INVALID_LENGTH,
                Email.MIN_LENGTH,
                Email.MAX_LENGTH
        );

        UserUpdateEmailRequestDTO requestDTONull = new UserUpdateEmailRequestDTO(null);
        ResultActions resultActionsNull = mockMvc.perform(put("/api/users/me/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTONull)));
        mockMvcHelper.testUnsuccessfulResponse(resultActionsNull, VotifyErrorCode.EMAIL_EMPTY);

        verifyNoInteractions(userService);
    }

    @Test
    void updateEmail_Fail_NotLogged() throws Exception {
        UserUpdateEmailRequestDTO requestDTO = new UserUpdateEmailRequestDTO("new@email.com");
        doThrow(new VotifyException(VotifyErrorCode.COMMON_UNAUTHORIZED))
                .when(userService)
                .updateUserEmail(eq(new Email("new@email.com")));

        ResultActions resultActions = mockMvc.perform(put("/api/users/me/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)));
        mockMvcHelper.testUnsuccessfulResponse(resultActions, VotifyErrorCode.COMMON_UNAUTHORIZED);
    }

    @Test
    void updateEmailDuplicated() throws Exception {
        String newEmail = "admin-newwww@votify.com.br";
        UserUpdateEmailRequestDTO requestDTO = new UserUpdateEmailRequestDTO(newEmail);
        doThrow(new VotifyException(VotifyErrorCode.PENDING_EMAIL_CONFIRMATION))
                .when(userService)
                .updateUserEmail(eq(new Email(newEmail)));

        ResultActions resultActions = mockMvc.perform(put("/api/users/me/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)));

        mockMvcHelper.testUnsuccessfulResponse(resultActions, VotifyErrorCode.PENDING_EMAIL_CONFIRMATION);
    }

    @Test
    void updateInfoName() throws Exception {
        User updatedUser = User.parseUnsafe(
                2L,
                new Email("moderator@votify.com.br"),
                new UserName("moderator"),
                new Name("Mod"),
                "encrypted_password",
                UserRole.MODERATOR,
                true
        );

        UserUpdateInfoRequestDTO userUpdateInfoRequestDTO = new UserUpdateInfoRequestDTO("Mod", null);
        when(userService.updateUserInfo(eq(new Name("Mod")), isNull())).thenReturn(updatedUser);

        ResultActions resultActions = mockMvc.perform(put("/api/users/me/info")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userUpdateInfoRequestDTO)));
        mockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data.id", is(2)))
                .andExpect(jsonPath("data.userName", is("moderator")))
                .andExpect(jsonPath("data.name", is("Mod")))
                .andExpect(jsonPath("data.email", is("moderator@votify.com.br")))
                .andExpect(jsonPath("data.role", is("MODERATOR")));
    }

    @Test
    void updateInfoUserName() throws Exception {
        User updatedUser = User.parseUnsafe(
                2L,
                new Email("moderator@votify.com.br"),
                new UserName("cool-username"),
                new Name("Mod"),
                "encrypted_password",
                UserRole.MODERATOR,
                true
        );

        UserUpdateInfoRequestDTO userUpdateInfoRequestDTO = new UserUpdateInfoRequestDTO(
                "",
                "cool-username"
        );
        when(userService.updateUserInfo(isNull(), eq(new UserName("cool-username")))).thenReturn(updatedUser);
        ResultActions resultActions = mockMvc.perform(put("/api/users/me/info")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userUpdateInfoRequestDTO)));
        mockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data.id", is(2)))
                .andExpect(jsonPath("data.userName", is("cool-username")))
                .andExpect(jsonPath("data.name", is("Mod")))
                .andExpect(jsonPath("data.email", is("moderator@votify.com.br")))
                .andExpect(jsonPath("data.role", is("MODERATOR")));
    }

    @Test
    void deleteSelfNotLogged() throws Exception {
        when(contextService.getUserOrThrow()).thenThrow(new VotifyException(VotifyErrorCode.COMMON_UNAUTHORIZED));

        ResultActions resultActions = mockMvc.perform(delete("/api/users/me"));
        mockMvcHelper.testUnsuccessfulResponse(resultActions, VotifyErrorCode.COMMON_UNAUTHORIZED);
    }

    @Test
    void deleteSelf() throws Exception {
        User user = mock(User.class);
        when(contextService.getUserOrThrow()).thenReturn(user);

        ResultActions resultActions = mockMvc.perform(delete("/api/users/me"));
        mockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data", is(nullValue())));
        mockMvcHelper.validateCookie(resultActions, userProperties.getAccessTokenCookieName(), "", 0);
        mockMvcHelper.validateCookie(resultActions, userProperties.getRefreshTokenCookieName(), "", 0);

        verify(userService).delete(user);
    }
}