package br.com.votify.core.service;

import br.com.votify.core.domain.entities.tokens.RefreshToken;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.repository.RefreshTokenRepository;
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

    public String createAccessToken(User user) {
        Date issuedAt = Date.from(Instant.now());
        Date expiration = Date.from(issuedAt.toInstant().plusSeconds(60 * 15));

        return Jwts.builder()
            .issuedAt(issuedAt)
            .expiration(expiration)
            .subject(user.getId().toString())
            .claim("userName", user.getUserName())
            .claim("name", user.getName())
            .claim("email", user.getEmail())
            .signWith(ACCESS_SECRET_KEY)
            .compact();
    }

    public String createRefreshToken(User user, String deviceId) {
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
        RefreshToken refreshToken = new RefreshToken(token, expiration, user, deviceId);
        refreshTokenRepository.save(refreshToken);

        return token;
    }
}
