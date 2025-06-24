package br.com.votify.core.model.user;

import br.com.votify.core.model.user.field.ConfirmationCode;
import br.com.votify.core.model.user.field.Email;
import br.com.votify.core.properties.user.UserProperties;
import lombok.Getter;

import java.time.Duration;
import java.time.Instant;

@Getter
public final class EmailConfirmation {
    private final ConfirmationCode code;
    private final Email newEmail;
    private final Instant expiration;
    private final Long userId;

    public EmailConfirmation(Email newEmail, User user, UserProperties userProperties) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("The user or it's id must not be null.");
        }
        if (userProperties == null) {
            throw new IllegalArgumentException("The user properties must not be null.");
        }
        this.code = new ConfirmationCode();
        this.newEmail = newEmail;
        this.userId = user.getId();

        Duration durationInMinutes = Duration.ofMinutes(userProperties.getEmailConfirmationExpirationMinutes());
        this.expiration = Instant.now().plus(durationInMinutes);
    }

    private EmailConfirmation(ConfirmationCode code, Email newEmail, Instant expiration, Long userId) {
        this.code = code;
        this.newEmail = newEmail;
        this.expiration = expiration;
        this.userId = userId;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiration);
    }

    public static EmailConfirmation parseUnsafe(
            ConfirmationCode code,
            Email newEmail,
            Instant expiration,
            Long userId
    ) {
        return new EmailConfirmation(code, newEmail, expiration, userId);
    }
}
