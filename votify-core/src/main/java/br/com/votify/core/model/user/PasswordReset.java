package br.com.votify.core.model.user;

import br.com.votify.core.model.user.field.ConfirmationCode;
import br.com.votify.core.properties.user.UserProperties;
import lombok.Getter;

import java.time.Duration;
import java.time.Instant;

@Getter
public class PasswordReset {
    private final ConfirmationCode code;
    private final Long userId;
    private final Instant expiration;

    public PasswordReset(User user, UserProperties userProperties) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("The user or it's id must not be null.");
        }
        if (userProperties == null) {
            throw new IllegalArgumentException("The user properties must not be null.");
        }
        this.userId = user.getId();
        this.code = new ConfirmationCode();

        Duration durationInMinutes = Duration.ofMinutes(userProperties.getResetPasswordConfirmationExpirationMinutes());
        this.expiration = Instant.now().plus(durationInMinutes);
    }

    private PasswordReset(ConfirmationCode code, Long userId, Instant expiration) {
        this.code = code;
        this.userId = userId;
        this.expiration = expiration;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiration);
    }

    public static PasswordReset parseUnsafe(ConfirmationCode code, Long userId, Instant expiration) {
        return new PasswordReset(code, userId, expiration);
    }
}