package br.com.votify.core.service;

import br.com.votify.core.domain.entities.tokens.RefreshToken;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.repository.RefreshTokenRepository;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.core.utils.validators.TokenValidator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class TokenService {
    private static final SecretKey ACCESS_SECRET_KEY = Jwts.SIG.HS256.key().build();
    // TODO: Adicionar um Refresh Secret Key em arquivo, não pode ser gerado aleatoriamente!
    private static final SecretKey REFRESH_SECRET_KEY = Jwts.SIG.HS256.key().build();

    private final RefreshTokenRepository refreshTokenRepository;

    public String createAccessToken(RefreshToken refreshToken) throws VotifyException {
        Claims claims = TokenValidator.validateRefreshToken(refreshToken, REFRESH_SECRET_KEY);
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
            .signWith(ACCESS_SECRET_KEY)
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
            .signWith(REFRESH_SECRET_KEY)
            .compact();
        RefreshToken refreshToken = new RefreshToken(token, expiration, user);
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken increaseRefreshTokenExpiration(String refreshToken) throws VotifyException {
        RefreshToken refreshTokenFromDatabase = refreshTokenRepository.findById(refreshToken).orElse(null);
        if (refreshTokenFromDatabase == null) {
            throw new VotifyException(VotifyErrorCode.REFRESH_TOKEN_EXPIRED);
        }
        TokenValidator.validateRefreshToken(refreshTokenFromDatabase, REFRESH_SECRET_KEY);

        Date expiration = Date.from(Instant.now().plusSeconds(3600 * 24 * 28));
        refreshTokenFromDatabase.setExpiration(expiration);

        return refreshTokenRepository.save(refreshTokenFromDatabase);
    }

    public Claims getClaimsFromAccessToken(String token) throws VotifyException {
        return TokenValidator.validateAccessToken(token, ACCESS_SECRET_KEY);
    }
}
