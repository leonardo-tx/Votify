package br.com.votify.console.callers;

import br.com.votify.dto.ApiResponse;
import br.com.votify.dto.users.UserDetailedViewDTO;
import br.com.votify.dto.users.UserLoginDTO;
import br.com.votify.dto.users.UserQueryDTO;
import br.com.votify.dto.users.UserRegisterDTO;
import jakarta.servlet.http.Cookie;
import lombok.AllArgsConstructor;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;

@AllArgsConstructor
public final class VotifyApiUsersCaller {
    private static final String baseUrl = VotifyApiCaller.BASE_URL + "users";

    private final TestRestTemplate restTemplate;
    private final List<String> cookies;

    public ApiResponse<UserDetailedViewDTO> register(UserRegisterDTO userRegisterDTO) {
        ResponseEntity<ApiResponse<UserDetailedViewDTO>> response = restTemplate.exchange(
            baseUrl,
            HttpMethod.POST,
            new HttpEntity<>(userRegisterDTO),
            new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    public ApiResponse<?> login(UserLoginDTO userLoginDTO) {
        ResponseEntity<ApiResponse<UserDetailedViewDTO>> response = restTemplate.exchange(
            baseUrl + "/login",
            HttpMethod.POST,
            new HttpEntity<>(userLoginDTO),
            new ParameterizedTypeReference<>() {}
        );
        ApiResponse<?> responseBody = response.getBody();
        if (responseBody.isSuccess()) {
            List<String> cookiesFromApi = response.getHeaders().get("Set-Cookie");
            cookies.clear();
            cookies.addAll(cookiesFromApi);
        }
        return responseBody;
    }

    public ApiResponse<UserQueryDTO> getUserById(String id) {
        HttpHeaders headers = new HttpHeaders();
        for (String cookie : cookies) {
            headers.add(HttpHeaders.COOKIE, cookie);
        }

        ResponseEntity<ApiResponse<UserQueryDTO>> response = restTemplate.exchange(
            baseUrl + "/{id}",
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<>() {},
            id
        );
        return response.getBody();
    }

    public ApiResponse<?> logout() {
        HttpHeaders headers = new HttpHeaders();
        for (String cookie : cookies) {
            headers.add(HttpHeaders.COOKIE, cookie);
        }

        ResponseEntity<ApiResponse<UserDetailedViewDTO>> response = restTemplate.exchange(
                baseUrl + "/logout",
                HttpMethod.POST,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {}
        );
        if (response.getBody().isSuccess()) {
            cookies.clear();
        }
        return response.getBody();
    }
}
