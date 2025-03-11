package br.com.votify.test;

import br.com.votify.dto.ApiResponse;
import br.com.votify.dto.users.UserDetailedViewDTO;
import br.com.votify.dto.users.UserLoginDTO;
import br.com.votify.dto.users.UserRegisterDTO;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.List;

public class AuthHelper {
    public static List<String> login(TestRestTemplate restTemplate, UserLoginDTO dto) {
        ResponseEntity<ApiResponse<UserDetailedViewDTO>> response = restTemplate.exchange(
            "/users/login",
            HttpMethod.POST,
            new HttpEntity<>(dto),
            new ParameterizedTypeReference<>() {}
        );
        return response.getHeaders().get("Set-Cookie");
    }

    public static void register(TestRestTemplate restTemplate, UserRegisterDTO dto) {
        restTemplate.exchange(
            "/users",
            HttpMethod.POST,
            new HttpEntity<>(dto),
            new ParameterizedTypeReference<>() {}
        );
    }
}
