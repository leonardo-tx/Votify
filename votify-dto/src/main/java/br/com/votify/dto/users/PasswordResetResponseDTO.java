package br.com.votify.dto.users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetResponseDTO {
    private String code;
    private int expirationMinutes;
    private String errorCode;

    public PasswordResetResponseDTO(String code, int expirationMinutes) {
        this.code = code;
        this.expirationMinutes = expirationMinutes;
    }
}