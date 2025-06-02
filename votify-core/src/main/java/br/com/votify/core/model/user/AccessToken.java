package br.com.votify.core.model.user;

import br.com.votify.core.properties.user.UserProperties;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.Getter;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Getter
public final class AccessToken {
    private final String code;
    private final Long userId;

    public AccessToken(
            RefreshToken refreshToken,
            UserProperties userProperties,
            SecretKey accessSecretKey,
            SecretKey refreshSecretKey
    ) throws VotifyException {
        Claims claims = refreshToken.getClaims(refreshSecretKey);
        if (refreshToken.isExpired()) {
            throw new VotifyException(VotifyErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        Instant issuedAt = Instant.now();
        Instant expiration = issuedAt.plusSeconds(userProperties.getAccessTokenExpirationSeconds());
        this.userId = refreshToken.getUserId();
        this.code = Jwts.builder()
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiration))
                .subject(claims.getSubject())
                .claim("userName", claims.get("userName"))
                .claim("name", claims.get("name"))
                .claim("email", claims.get("email"))
                .signWith(accessSecretKey)
                .compact();
    }

    public AccessToken(String token, SecretKey secretKey) throws VotifyException {
        if (token == null) {
            throw new VotifyException(VotifyErrorCode.ACCESS_TOKEN_INVALID);
        }
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            if (claims.getExpiration() == null) {
                throw new VotifyException(VotifyErrorCode.ACCESS_TOKEN_INVALID);
            }
            validateCommonClaims(claims);
            this.code = token;
            this.userId = Long.parseLong(claims.getSubject());
        } catch (ExpiredJwtException e) {
            throw new VotifyException(VotifyErrorCode.ACCESS_TOKEN_EXPIRED);
        } catch (JwtException e) {
            throw new VotifyException(VotifyErrorCode.ACCESS_TOKEN_INVALID);
        }
    }

    private static void validateCommonClaims(Claims claims) throws VotifyException {
        try {
            Long.parseLong(claims.getSubject());
            if (claims.get("userName") == null ||
                    claims.get("name") == null ||
                    claims.get("email") == null ||
                    claims.getIssuedAt() == null) {
                throw new VotifyException(VotifyErrorCode.ACCESS_TOKEN_INVALID);
            }
        } catch (VotifyException e) {
            throw e;
        } catch (Exception e) {
            throw new VotifyException(VotifyErrorCode.ACCESS_TOKEN_INVALID);
        }
    }
}
