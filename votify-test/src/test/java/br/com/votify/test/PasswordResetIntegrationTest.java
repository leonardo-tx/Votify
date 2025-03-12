package br.com.votify.api.integration;

import br.com.votify.api.VotifyApiApplication;
import br.com.votify.dto.ApiResponse;
import br.com.votify.dto.users.*;
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

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = VotifyApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class PasswordResetIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String TEST_EMAIL = "testuser@example.com";
    private static final String TEST_PASSWORD = "TestPassword123";
    private static final String NEW_PASSWORD = "NewTestPassword123";
    private static String resetCode;

    @Test
    @Order(1)
    public void registerTestUser() {
        UserRegisterDTO registerDTO = new UserRegisterDTO("testuser", "Test", TEST_EMAIL, TEST_PASSWORD);
        ResponseEntity<ApiResponse<?>> response = restTemplate.exchange(
                "/users",
                HttpMethod.POST,
                new HttpEntity<>(registerDTO),
                new ParameterizedTypeReference<>() {}
        );
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    @Order(2)
    public void requestPasswordReset() {
        PasswordResetRequestDTO requestDTO = new PasswordResetRequestDTO(TEST_EMAIL);
        ResponseEntity<ApiResponse<PasswordResetResponseDTO>> response = restTemplate.exchange(
                "/password/forgot",
                HttpMethod.POST,
                new HttpEntity<>(requestDTO),
                new ParameterizedTypeReference<>() {}
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getData());
        resetCode = response.getBody().getData().getCode();
    }

    @Test
    @Order(3)
    public void requestPasswordResetDuplicate() {
        PasswordResetRequestDTO requestDTO = new PasswordResetRequestDTO(TEST_EMAIL);
        ResponseEntity<ApiResponse<PasswordResetResponseDTO>> response = restTemplate.exchange(
                "/password/forgot",
                HttpMethod.POST,
                new HttpEntity<>(requestDTO),
                new ParameterizedTypeReference<>() {}
        );
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("password.reset.request.exists", response.getBody().getErrorCode());
    }

    @Test
    @Order(4)
    public void resetPassword() {
        PasswordResetConfirmDTO confirmDTO = new PasswordResetConfirmDTO(resetCode, NEW_PASSWORD);
        ResponseEntity<ApiResponse<?>> response = restTemplate.exchange(
                "/password/reset",
                HttpMethod.POST,
                new HttpEntity<>(confirmDTO),
                new ParameterizedTypeReference<>() {}
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(5)
    public void loginWithNewPassword() {
        UserLoginDTO loginDTO = new UserLoginDTO(TEST_EMAIL, NEW_PASSWORD);
        ResponseEntity<ApiResponse<UserDetailedViewDTO>> response = restTemplate.exchange(
                "/users/login",
                HttpMethod.POST,
                new HttpEntity<>(loginDTO),
                new ParameterizedTypeReference<>() {}
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}