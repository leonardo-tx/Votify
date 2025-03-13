package br.com.votify.api.controller.users;

import br.com.votify.dto.ApiResponse;
import br.com.votify.dto.users.PasswordResetConfirmDTO;
import br.com.votify.dto.users.PasswordResetRequestDTO;
import br.com.votify.dto.users.PasswordResetResponseDTO;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.core.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/password")
@RequiredArgsConstructor
public class PasswordResetController {
    private final PasswordResetService passwordResetService;

    @Value("${app.password-reset.expiration-minutes:15}")
    private int expirationMinutes;

    @PostMapping("/forgot")
    public ResponseEntity<ApiResponse<PasswordResetResponseDTO>> requestPasswordReset(
            @RequestBody PasswordResetRequestDTO requestDTO) throws VotifyException {

        String code = passwordResetService.createPasswordResetRequest(requestDTO.getEmail());
        PasswordResetResponseDTO responseDTO = new PasswordResetResponseDTO(code, expirationMinutes);
        return ResponseEntity.ok(ApiResponse.success(responseDTO));
    }

    @PostMapping("/reset")
    public ResponseEntity<ApiResponse<?>> resetPassword(
            @RequestBody PasswordResetConfirmDTO confirmDTO) throws VotifyException {

        passwordResetService.resetPassword(confirmDTO.getCode(), confirmDTO.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
