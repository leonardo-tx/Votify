package br.com.votify.dto.users;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PasswordResetRequestDTO {
    private String email;
}
