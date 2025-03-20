package br.com.votify.core.domain.entities.password;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.password-reset")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetProperties {
    private int expirationMinutes;
}
