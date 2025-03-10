package br.com.votify.core.domain.entities.tokens;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.token")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TokenProperties {
    private int accessTokenMaxAge;
    private int refreshTokenMaxAge;
    private String refreshTokenSecret;
}