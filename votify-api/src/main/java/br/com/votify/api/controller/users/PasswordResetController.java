package br.com.votify.api.controller.users;

import br.com.votify.dto.ApiResponse;
import br.com.votify.dto.users.PasswordResetConfirmDTO;
import br.com.votify.dto.users.PasswordResetRequestDTO;
import br.com.votify.dto.users.PasswordResetResponseDTO;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.core.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
        return ApiResponse.success(responseDTO, HttpStatus.OK).createResponseEntity();
    }

    @PostMapping("/reset")
    public ResponseEntity<ApiResponse<Object>> resetPassword(
            @RequestBody PasswordResetConfirmDTO confirmDTO) throws VotifyException {

        passwordResetService.resetPassword(confirmDTO.getCode(), confirmDTO.getNewPassword());
        return ApiResponse.success(null, HttpStatus.OK).createResponseEntity();
    }
}
