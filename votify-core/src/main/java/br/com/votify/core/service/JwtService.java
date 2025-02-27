package br.com.votify.core.service;

import br.com.votify.core.domain.entities.users.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {
    private static String SECRET_KEY = "AKJSDHKJAHDSJAJDDA";

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    private String createToken(User user) {
        Date issuedAt = Date.from(Instant.now());
        Date expiration = Date.from(issuedAt.toInstant().plusSeconds(60 * 15));

        Jwts.builder()
            .issuedAt(issuedAt)
            .expiration(expiration)
            .subject(user.getId().toString())
            .claim("userName", user.getUserName())
            .claim("name", user.getName())
            .claim("email", user.getEmail())
            .signWith(SignatureAlgorithm.ES384, SECRET_KEY)

    }
}
