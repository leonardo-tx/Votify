package br.com.votify.api.controller.users;

import br.com.votify.api.dto.ApiResponse;
import br.com.votify.api.dto.users.UserDetailedViewDTO;
import br.com.votify.api.dto.users.UserLoginDTO;
import br.com.votify.api.dto.users.UserRegisterDTO;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
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

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class UserControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

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
}
