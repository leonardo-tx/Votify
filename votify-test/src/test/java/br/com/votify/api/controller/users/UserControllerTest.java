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
import br.com.votify.dto.users.UserQueryDTO;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ExtendWith(MockitoExtension.class)
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
        assertEquals(expectedApiResponse.isSuccess(), response.getBody().isSuccess());
        
        UserDetailedViewDTO expectedData = expectedApiResponse.getData();
        UserDetailedViewDTO actualData = response.getBody().getData();
        assertNotNull(actualData);
        assertEquals(expectedData.getId(), actualData.getId());
        assertEquals(expectedData.getUserName(), actualData.getUserName());
        assertEquals(expectedData.getName(), actualData.getName());
        assertEquals(expectedData.getEmail(), actualData.getEmail());
    }

    @Test
    @Order(1)
    public void registerInvalidUser() {
        UserRegisterDTO dtoRegister = new UserRegisterDTO(
            "littledoge",
            "Byces",
            "123@gmail.com",
            "littledoge123"
        );

        ResponseEntity<ApiResponse<UserDetailedViewDTO>> response = restTemplate.exchange(
            "/users",
                HttpMethod.POST,
                new HttpEntity<>(dtoRegister),
                new ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("user.name.already.exists", response.getBody().getErrorCode());
    }

    @Test
    @Order(2)
    public void loginUser() {
        UserLoginDTO dtoLogin = new UserLoginDTO("123@gmail.com", "littledoge123");
        ApiResponse<?> expectedApiResponse = ApiResponse.success(null);

        ResponseEntity<ApiResponse<?>> response = restTemplate.exchange(
            "/users/login",
                HttpMethod.POST,
                new HttpEntity<>(dtoLogin),
                new ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());

        HttpHeaders headers = response.getHeaders();
        List<String> cookies = headers.get(HttpHeaders.SET_COOKIE);
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
    @Order(3)
    public void getUserByIdAsCommonUser() {
        UserLoginDTO dtoLogin = new UserLoginDTO("123@gmail.com", "littledoge123");
        ResponseEntity<ApiResponse<UserDetailedViewDTO>> loginResponse = restTemplate.exchange(
            "/users/login",
            HttpMethod.POST,
            new HttpEntity<>(dtoLogin),
            new ParameterizedTypeReference<>() {}
        );
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        List<String> cookies = loginResponse.getHeaders().get("Set-Cookie");
        assertNotNull(cookies);

        HttpHeaders headers = new HttpHeaders();
        for (String cookie : cookies) {
            headers.add(HttpHeaders.COOKIE, cookie);
        }

        ResponseEntity<ApiResponse<UserQueryDTO>> response = restTemplate.exchange(
            "/users/{id}",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<>() {},
            1
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());

        UserQueryDTO userData = response.getBody().getData();
        assertEquals(1L, userData.getId());
        assertEquals("littledoge", userData.getUserName());
        assertEquals("Byces", userData.getName());
        assertNull(userData.getEmail());
        assertNull(userData.getRole());
    }


    @Test
    @Order(4)
    public void getUserByIdAsCommonUser_NotLoggedIn() {
        UserRegisterDTO commonUser2 = new UserRegisterDTO(
            "common2",
            "Leonardo 2",
            "leo@xyz.com",
            "justpass"
        );

        restTemplate.exchange(
            "/users",
            HttpMethod.POST,
            new HttpEntity<>(commonUser2),
            new ParameterizedTypeReference<ApiResponse<UserDetailedViewDTO>>() {}
        );

        ResponseEntity<ApiResponse<UserQueryDTO>> response = restTemplate.exchange(
            "/users/{id}",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<>() {},
            2
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());

        UserQueryDTO userData = response.getBody().getData();
        assertEquals(2L, userData.getId());
        assertEquals("common2", userData.getUserName());
        assertEquals("Leonardo 2", userData.getName());
        assertNull(userData.getEmail());
        assertNull(userData.getRole());
    }

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
