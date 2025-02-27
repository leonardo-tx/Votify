package br.com.votify.core.domain.entities.tokens;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class AuthTokens {
    private String accessToken;
    private RefreshToken refreshToken;
}
