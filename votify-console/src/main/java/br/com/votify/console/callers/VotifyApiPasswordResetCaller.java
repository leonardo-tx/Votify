package br.com.votify.console.callers;

import br.com.votify.dto.ApiResponse;
import br.com.votify.dto.users.PasswordResetConfirmDTO;
import br.com.votify.dto.users.PasswordResetRequestDTO;
import br.com.votify.dto.users.PasswordResetResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@AllArgsConstructor
public class VotifyApiPasswordResetCaller {
    private static final String baseUrl = VotifyApiCaller.BASE_URL + "password";

    private final TestRestTemplate restTemplate;

    public ApiResponse<PasswordResetResponseDTO> forgot(PasswordResetRequestDTO passwordResetRequestDTO) {
        ResponseEntity<ApiResponse<PasswordResetResponseDTO>> response = restTemplate.exchange(
            baseUrl + "/forgot",
            HttpMethod.POST,
            new HttpEntity<>(passwordResetRequestDTO),
            new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    public ApiResponse<?> reset(PasswordResetConfirmDTO passwordResetConfirmDTO) {
        ResponseEntity<ApiResponse<PasswordResetResponseDTO>> response = restTemplate.exchange(
            baseUrl + "/reset",
            HttpMethod.POST,
            new HttpEntity<>(passwordResetConfirmDTO),
            new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }
}
