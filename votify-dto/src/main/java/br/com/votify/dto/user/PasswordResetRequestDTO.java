package br.com.votify.dto.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PasswordResetRequestDTO {
    private String email;
}
