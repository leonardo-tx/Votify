package br.com.votify.core.properties.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public final class UserProperties {
    private boolean cookieSecure;
    private boolean cookieHttpOnly;
    private int emailConfirmationExpirationMinutes;
    private int accessTokenExpirationSeconds;
    private int refreshTokenExpirationSeconds;
    private int resetPasswordConfirmationExpirationMinutes;
    private String refreshTokenCookieName;
    private String accessTokenCookieName;
    private String refreshTokenSecret;
}
