package br.com.votify.api.controller.users;

import br.com.votify.dto.ApiResponse;
import br.com.votify.dto.users.UserDetailedViewDTO;
import br.com.votify.dto.users.UserLoginDTO;
import br.com.votify.dto.users.UserRegisterDTO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class UserContextControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    private boolean setupCompleted = false;
    private List<String> cookies = new ArrayList<>();

    @BeforeEach
    public void setupBeforeEach() {
        if (setupCompleted) return;

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
        ResponseEntity<ApiResponse<?>> loginResponse = restTemplate.exchange(
                "/users/login",
                HttpMethod.POST,
                new HttpEntity<>(dtoLogin),
                new ParameterizedTypeReference<>() {}
        );
        this.cookies = loginResponse.getHeaders().get("Set-Cookie");
        this.setupCompleted = true;
    }

    @Test
    @Order(0)
    public void get() {
        ApiResponse<UserDetailedViewDTO> expectedApiResponse = ApiResponse.success(new UserDetailedViewDTO(
                1L,
                "littledoge",
                "Byces",
                "123@gmail.com"
        ));

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
        assertTrue(response.getBody().isSuccess());
        
        UserDetailedViewDTO expectedData = expectedApiResponse.getData();
        UserDetailedViewDTO actualData = response.getBody().getData();
        assertNotNull(actualData);
        assertEquals(expectedData.getId(), actualData.getId());
        assertEquals(expectedData.getUserName(), actualData.getUserName());
        assertEquals(expectedData.getName(), actualData.getName());
        assertEquals(expectedData.getEmail(), actualData.getEmail());
        assertNull(response.getBody().getErrorCode());
        assertNull(response.getBody().getErrorMessage());
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

        List<String> cookies = loginResponse.getHeaders().get("Set-Cookie");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", cookies.get(0));
        headers.add("Cookie", cookies.get(1));
        ResponseEntity<ApiResponse<?>> response = restTemplate.exchange(
                "/user/regenerate-tokens",
                HttpMethod.POST,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertNull(response.getBody().getData());
        assertNull(response.getBody().getErrorCode());
        assertNull(response.getBody().getErrorMessage());
    }

    @Test
    @Order(2)
    void deleteAccount() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", cookies.get(0));
        headers.add("Cookie", cookies.get(1));
        ResponseEntity<ApiResponse<?>> response = restTemplate.exchange(
            "/user",
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertNull(response.getBody().getData());
        assertNull(response.getBody().getErrorCode());
        assertNull(response.getBody().getErrorMessage());

        List<String> cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
        assertNotNull(cookies);
        assertEquals(2, cookies.size());

        HashSet<String> hashSet = new HashSet<>();
        for (String cookie : cookies) {
            String[] split = cookie.split("=", 2);
            hashSet.add(split[0]);

            assertEquals("", split[1].split(";")[0]);
        }
        assertTrue(hashSet.contains("access_token"));
        assertTrue(hashSet.contains("refresh_token"));
    }
}