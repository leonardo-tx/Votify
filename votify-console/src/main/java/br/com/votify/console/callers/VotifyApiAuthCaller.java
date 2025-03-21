package br.com.votify.console.callers;

import br.com.votify.dto.ApiResponse;
import br.com.votify.dto.users.*;
import lombok.AllArgsConstructor;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;

@AllArgsConstructor
public class VotifyApiAuthCaller {
    private static final String baseUrl = VotifyApiCaller.BASE_URL + "auth";

    private final TestRestTemplate restTemplate;
    private final List<String> cookies;

    public ApiResponse<UserDetailedViewDTO> register(UserRegisterDTO userRegisterDTO) {
        ResponseEntity<ApiResponse<UserDetailedViewDTO>> response = restTemplate.exchange(
                baseUrl + "/register",
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

    public ApiResponse<PasswordResetResponseDTO> forgotPassword(PasswordResetRequestDTO passwordResetRequestDTO) {
        ResponseEntity<ApiResponse<PasswordResetResponseDTO>> response = restTemplate.exchange(
                baseUrl + "/forgot-password",
                HttpMethod.POST,
                new HttpEntity<>(passwordResetRequestDTO),
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    public ApiResponse<?> resetPassword(PasswordResetConfirmDTO passwordResetConfirmDTO) {
        ResponseEntity<ApiResponse<PasswordResetResponseDTO>> response = restTemplate.exchange(
                baseUrl + "/reset-password",
                HttpMethod.POST,
                new HttpEntity<>(passwordResetConfirmDTO),
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    public ApiResponse<?> refreshTokens() {
        HttpHeaders headers = new HttpHeaders();
        for (String cookie : cookies) {
            headers.add(HttpHeaders.COOKIE, cookie);
        }

        ResponseEntity<ApiResponse<UserDetailedViewDTO>> response = restTemplate.exchange(
                baseUrl + "/refresh-tokens",
                HttpMethod.POST,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }
}
