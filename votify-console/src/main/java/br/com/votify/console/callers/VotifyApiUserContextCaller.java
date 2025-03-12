package br.com.votify.console.callers;

import br.com.votify.dto.ApiResponse;
import br.com.votify.dto.users.UserDetailedViewDTO;
import lombok.AllArgsConstructor;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;

@AllArgsConstructor
public final class VotifyApiUserContextCaller {
    private static final String baseUrl = VotifyApiCaller.BASE_URL + "user";

    private final TestRestTemplate restTemplate;
    private final List<String> cookies;

    public ApiResponse<UserDetailedViewDTO> getUser() {
        HttpHeaders headers = new HttpHeaders();
        for (String cookie : cookies) {
            headers.add(HttpHeaders.COOKIE, cookie);
        }

        ResponseEntity<ApiResponse<UserDetailedViewDTO>> response = restTemplate.exchange(
            baseUrl,
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    public ApiResponse<?> regenerateTokens() {
        HttpHeaders headers = new HttpHeaders();
        for (String cookie : cookies) {
            headers.add(HttpHeaders.COOKIE, cookie);
        }

        ResponseEntity<ApiResponse<UserDetailedViewDTO>> response = restTemplate.exchange(
            baseUrl + "/regenerate-tokens",
            HttpMethod.POST,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    public ApiResponse<?> delete() {
        HttpHeaders headers = new HttpHeaders();
        for (String cookie : cookies) {
            headers.add(HttpHeaders.COOKIE, cookie);
        }

        ResponseEntity<ApiResponse<UserDetailedViewDTO>> response = restTemplate.exchange(
            baseUrl,
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }
}
