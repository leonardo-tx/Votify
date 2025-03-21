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
}