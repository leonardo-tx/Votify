package br.com.votify.dto.users;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PasswordResetResponseDTO {
    private String code;
    private int expirationMinutes;
    private String errorCode;

    public PasswordResetResponseDTO(String code, int expirationMinutes) {
        this.code = code;
        this.expirationMinutes = expirationMinutes;
    }
}