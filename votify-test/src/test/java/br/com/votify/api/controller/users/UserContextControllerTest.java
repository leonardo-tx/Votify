package br.com.votify.api.controller.users;

import br.com.votify.dto.ApiResponse;
import br.com.votify.dto.users.UserDetailedViewDTO;
import br.com.votify.dto.users.UserLoginDTO;
import br.com.votify.dto.users.UserRegisterDTO;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class UserContextControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

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
}