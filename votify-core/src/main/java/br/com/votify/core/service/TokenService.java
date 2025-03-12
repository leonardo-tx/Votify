package br.com.votify.core.service;

import br.com.votify.core.domain.entities.tokens.RefreshToken;
import br.com.votify.core.domain.entities.tokens.TokenProperties;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.repository.RefreshTokenRepository;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.core.utils.validators.TokenValidator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class TokenService {
    private static final SecretKey accessSecretKey = Jwts.SIG.HS256.key().build();
    private final SecretKey refreshSecretKey;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProperties tokenProperties;

    public TokenService(RefreshTokenRepository refreshTokenRepository, TokenProperties tokenProperties) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenProperties = tokenProperties;

        byte[] keyBytes = tokenProperties.getRefreshTokenSecret().getBytes(StandardCharsets.UTF_8);
        refreshSecretKey = new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    public String createAccessToken(RefreshToken refreshToken) throws VotifyException {
        Claims claims = TokenValidator.validateRefreshToken(refreshToken, refreshSecretKey);
        if (!refreshTokenRepository.existsById(refreshToken.getId())) {
            throw new VotifyException(VotifyErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        Date issuedAt = Date.from(Instant.now());
        Date expiration = Date.from(issuedAt.toInstant().plusSeconds(60 * 15));

        return Jwts.builder()
            .issuedAt(issuedAt)
            .expiration(expiration)
            .subject(claims.getSubject())
            .claim("userName", claims.get("userName"))
            .claim("name", claims.get("name"))
            .claim("email", claims.get("email"))
            .signWith(accessSecretKey)
            .compact();
    }

    public RefreshToken createRefreshToken(User user) {
        Date issuedAt = Date.from(Instant.now());
        Date expiration = Date.from(issuedAt.toInstant().plusSeconds(3600 * 24 * 28));

        String token = Jwts.builder()
            .issuedAt(issuedAt)
            .subject(user.getId().toString())
            .claim("userName", user.getUserName())
            .claim("name", user.getName())
            .claim("email", user.getEmail())
            .signWith(refreshSecretKey)
            .compact();
        RefreshToken refreshToken = new RefreshToken(token, expiration, user);
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken increaseRefreshTokenExpiration(String refreshToken) throws VotifyException {
        RefreshToken refreshTokenFromDatabase = refreshTokenRepository.findById(refreshToken).orElse(null);
        if (refreshTokenFromDatabase == null) {
            throw new VotifyException(VotifyErrorCode.REFRESH_TOKEN_EXPIRED);
        }
        TokenValidator.validateRefreshToken(refreshTokenFromDatabase, refreshSecretKey);

        Date expiration = Date.from(Instant.now().plusSeconds(3600 * 24 * 28));
        refreshTokenFromDatabase.setExpiration(expiration);

        return refreshTokenRepository.save(refreshTokenFromDatabase);
    }

    public Long getUserIdFromAccessToken(String token) throws VotifyException {
        String idAsString = TokenValidator.validateAccessToken(token, accessSecretKey).getSubject();
        return Long.parseLong(idAsString);
    }
}
