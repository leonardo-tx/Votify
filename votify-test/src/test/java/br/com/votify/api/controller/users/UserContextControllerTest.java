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
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.junit.jupiter.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ExtendWith(MockitoExtension.class)
public class UserContextControllerTest {
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
    private UserContextController userContextController;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new CommonUser(1L, "test-user", "Test User", "test@example.com", "password123");
    }

    @Test
    @Order(0)
    public void get() {
        UserRegisterDTO dtoRegister = new UserRegisterDTO(
            "littledoge",
            "Byces",
            "123@gmail.com",
            "littledoge123"
        );
        UserLoginDTO dtoLogin = new UserLoginDTO(
            "123@gmail.com",
            "littledoge123"
        );

        restTemplate.exchange(
            "/users",
            HttpMethod.POST,
            new HttpEntity<>(dtoRegister),
            new ParameterizedTypeReference<>() {}
        );

        ResponseEntity<ApiResponse<UserDetailedViewDTO>> loginResponse = restTemplate.exchange(
            "/users/login",
            HttpMethod.POST,
            new HttpEntity<>(dtoLogin),
            new ParameterizedTypeReference<>() {}
        );

        ApiResponse<UserDetailedViewDTO> expectedApiResponse = ApiResponse.success(new UserDetailedViewDTO(
            1L,
            "littledoge",
            "Byces",
            "123@gmail.com"
        ));

        List<String> cookies = loginResponse.getHeaders().get("Set-Cookie");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", cookies.get(0));
        headers.add("Cookie", cookies.get(1));

        ResponseEntity<ApiResponse<UserDetailedViewDTO>> response = restTemplate.exchange(
            "/user",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedApiResponse.toString(), response.getBody().toString());
    }

    @Test
    @Order(1)
    public void regenerateTokens() {
        UserLoginDTO dtoLogin = new UserLoginDTO(
            "123@gmail.com",
            "littledoge123"
        );

        ResponseEntity<ApiResponse<UserDetailedViewDTO>> loginResponse = restTemplate.exchange(
            "/users/login",
            HttpMethod.POST,
            new HttpEntity<>(dtoLogin),
            new ParameterizedTypeReference<>() {}
        );

        ApiResponse<?> expectedApiResponse = ApiResponse.success(null);

        List<String> cookies = loginResponse.getHeaders().get("Set-Cookie");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", cookies.get(0));

        ResponseEntity<ApiResponse<?>> response = restTemplate.exchange(
            "/user/regenerate-tokens",
            HttpMethod.POST,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedApiResponse.toString(), response.getBody().toString());
    }

    @Test
    void deleteAccount_ShouldDeleteUserAndClearCookies() throws VotifyException {
        // Arrange
        when(contextService.getUserOrThrow()).thenReturn(testUser);
        doNothing().when(userService).deleteUser(testUser.getId());

        // Act
        ResponseEntity<ApiResponse<?>> responseEntity = userContextController.deleteAccount(response);

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
        assertThrows(VotifyException.class, () -> userContextController.deleteAccount(response));
        verify(response, never()).addCookie(any(Cookie.class));
    }

    @Test
    void deleteAccount_WhenUnauthorized_ShouldPropagateException() throws VotifyException {
        // Arrange
        when(contextService.getUserOrThrow())
            .thenThrow(new VotifyException(VotifyErrorCode.COMMON_UNAUTHORIZED));

        // Act & Assert
        assertThrows(VotifyException.class, () -> userContextController.deleteAccount(response));
        verify(userService, never()).deleteUser(any());
        verify(response, never()).addCookie(any(Cookie.class));
    }
}
