package br.com.votify.api.controller.users;

import br.com.votify.api.configuration.SecurityConfig;
import br.com.votify.core.domain.entities.users.CommonUser;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.service.ContextService;
import br.com.votify.core.service.UserService;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.dto.ApiResponse;
import br.com.votify.dto.users.UserDetailedViewDTO;
import br.com.votify.dto.users.UserLoginDTO;
import br.com.votify.dto.users.UserRegisterDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@WebAppConfiguration
@SpringJUnitConfig(SpringExtension.class)
public class UserControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Mock
    private UserService userService;

    @Mock
    private SecurityConfig securityConfig;

    @Mock
    private ContextService contextService;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private UserController userController;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new CommonUser(1L, "test-user", "Test User", "test@example.com", "password123");
    }

    @Test
    @Order(0)
    public void registerUser() {
        UserRegisterDTO dtoRegister = new UserRegisterDTO(
            "littledoge",
            "Byces",
            "123@gmail.com",
            "littledoge123"
        );
        ApiResponse<UserDetailedViewDTO> expectedApiResponse = ApiResponse.success(new UserDetailedViewDTO(
            1L,
            "littledoge",
            "Byces",
            "123@gmail.com"
        ));

        ResponseEntity<ApiResponse<UserDetailedViewDTO>> response = restTemplate.exchange(
            "/users",
                HttpMethod.POST,
                new HttpEntity<>(dtoRegister),
                new ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedApiResponse.toString(), response.getBody().toString());
    }

    @Test
    @Order(1)
    public void registerInvalidUser() {
        UserRegisterDTO dtoRegister = new UserRegisterDTO(
                "littlecat123",
                "Littlecat",
                "123@gmail.com",
                "ahsvdhgafvsdghasv"
        );
        ApiResponse<UserDetailedViewDTO> expectedApiResponse = ApiResponse.error(new VotifyException(
            VotifyErrorCode.EMAIL_ALREADY_EXISTS
        ));

        ResponseEntity<ApiResponse<UserDetailedViewDTO>> response = restTemplate.exchange(
            "/users",
            HttpMethod.POST,
            new HttpEntity<>(dtoRegister),
            new ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedApiResponse.toString(), response.getBody().toString());
    }

    @Test
    @Order(1)
    public void loginUser() {
        UserLoginDTO dtoLogin = new UserLoginDTO(
            "123@gmail.com",
            "littledoge123"
        );
        ApiResponse<?> expectedApiResponse = ApiResponse.success(null);

        ResponseEntity<ApiResponse<UserDetailedViewDTO>> response = restTemplate.exchange(
                "/users/login",
                HttpMethod.POST,
                new HttpEntity<>(dtoLogin),
                new ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedApiResponse.toString(), response.getBody().toString());

        List<String> cookies = response.getHeaders().get("Set-Cookie");

        assertNotNull(cookies);
        assertEquals(2, cookies.size());

        HashSet<String> hashSet = new HashSet<>();
        for (String cookie : cookies) {
            hashSet.add(cookie.split("=", 2)[0]);
        }
        assertTrue(hashSet.contains("access_token"));
        assertTrue(hashSet.contains("refresh_token"));
    }

    @Test
    void deleteAccount_ShouldDeleteUserAndClearCookies() throws VotifyException {
        // Arrange
        when(contextService.getUserOrThrow()).thenReturn(testUser);
        doNothing().when(userService).deleteUser(testUser.getId());

        // Act
        ResponseEntity<ApiResponse<?>> responseEntity = userController.deleteAccount(response);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        verify(userService).deleteUser(testUser.getId());
        verify(response, times(2)).addCookie(any(Cookie.class));
        verify(securityConfig, times(2)).configureAccessTokenCookie(any(Cookie.class));
    }

    @Test
    void deleteAccount_WhenUserNotFound_ShouldPropagateException() throws VotifyException {
        // Arrange
        when(contextService.getUserOrThrow()).thenReturn(testUser);
        doThrow(new VotifyException(VotifyErrorCode.USER_NOT_FOUND))
            .when(userService).deleteUser(testUser.getId());

        // Act & Assert
        assertThrows(VotifyException.class, () -> userController.deleteAccount(response));
        verify(response, never()).addCookie(any(Cookie.class));
    }

    @Test
    void deleteAccount_WhenUnauthorized_ShouldPropagateException() throws VotifyException {
        // Arrange
        when(contextService.getUserOrThrow())
            .thenThrow(new VotifyException(VotifyErrorCode.COMMON_UNAUTHORIZED));

        // Act & Assert
        assertThrows(VotifyException.class, () -> userController.deleteAccount(response));
        verify(userService, never()).deleteUser(any());
        verify(response, never()).addCookie(any(Cookie.class));
    }
}
