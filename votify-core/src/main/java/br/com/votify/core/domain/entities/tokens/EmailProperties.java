package br.com.votify.core.domain.entities.tokens;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.email")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailProperties {
    private boolean confirmation;
}
