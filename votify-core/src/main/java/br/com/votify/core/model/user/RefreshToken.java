package br.com.votify.core.model.user;

import br.com.votify.core.properties.user.UserProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.Getter;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Getter
public final class RefreshToken {
    private final String code;
    private Instant expiration;
    private final Long userId;

    public RefreshToken(User user, UserProperties userProperties, SecretKey secretKey) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("The user or it's id must not be null.");
        }
        if (userProperties == null) {
            throw new IllegalArgumentException("The user properties must not be null.");
        }
        if (secretKey == null) {
            throw new IllegalArgumentException("The secret key must not be null.");
        }

        Instant issuedAt = Instant.now();
        this.expiration = issuedAt.plusSeconds(userProperties.getRefreshTokenExpirationSeconds());
        this.userId = user.getId();
        this.code = Jwts.builder()
                .issuedAt(Date.from(issuedAt))
                .subject(user.getId().toString())
                .claim("userName", user.getUserName().getValue())
                .claim("name", user.getName().getValue())
                .claim("email", user.getEmail().getValue())
                .signWith(secretKey)
                .compact();
    }

    private RefreshToken(String code, Instant expiration, Long userId) {
        this.code = code;
        this.expiration = expiration;
        this.userId = userId;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiration);
    }

    public void increaseExpiration(UserProperties userProperties) {
        Instant now = Instant.now();
        this.expiration = now.plusSeconds(userProperties.getRefreshTokenExpirationSeconds());
    }

    public Claims getClaims(SecretKey secretKey) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(code)
                .getPayload();
    }

    public static RefreshToken parseUnsafe(String code, Instant expiration, Long userId) {
        return new RefreshToken(code, expiration, userId);
    }
}
