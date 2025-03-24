package br.com.votify.dto.users;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PasswordResetConfirmDTO {
    private String code;
    private String newPassword;
}
