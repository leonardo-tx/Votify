package br.com.votify.core.utils.validators;

import br.com.votify.core.domain.entities.tokens.RefreshToken;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.time.Instant;

public final class TokenValidator {
    public static Claims validateAccessToken(String token, SecretKey secretKey) throws VotifyException {
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
            validateCommonClaims(claims, VotifyErrorCode.ACCESS_TOKEN_INVALID);
            return claims;
        } catch (ExpiredJwtException e) {
            throw new VotifyException(VotifyErrorCode.ACCESS_TOKEN_EXPIRED);
        } catch (JwtException e) {
            throw new VotifyException(VotifyErrorCode.ACCESS_TOKEN_INVALID);
        }
    }

    public static Claims validateRefreshToken(RefreshToken refreshToken, SecretKey secretKey) throws VotifyException {
        if (refreshToken == null ||
            refreshToken.getId() == null ||
            refreshToken.getUser() == null ||
            refreshToken.getExpiration() == null) {
            throw new VotifyException(VotifyErrorCode.REFRESH_TOKEN_INVALID);
        }
        if (Instant.now().isAfter(refreshToken.getExpiration().toInstant())) {
            throw new VotifyException(VotifyErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        try {
            Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(refreshToken.getId())
                .getPayload();
            validateCommonClaims(claims, VotifyErrorCode.REFRESH_TOKEN_INVALID);
            return claims;
        } catch (JwtException e) {
            throw new VotifyException(VotifyErrorCode.REFRESH_TOKEN_INVALID);
        }
    }

    private static void validateCommonClaims(Claims claims, VotifyErrorCode errorCode) throws VotifyException {
        try {
            Long.parseLong(claims.getSubject());
            if (claims.get("userName") == null ||
                claims.get("name") == null ||
                claims.get("email") == null ||
                claims.getIssuedAt() == null) {
                throw new VotifyException(errorCode);
            }
        } catch (VotifyException e) {
            throw e;
        } catch (Exception e) {
            throw new VotifyException(errorCode);
        }
    }
}
