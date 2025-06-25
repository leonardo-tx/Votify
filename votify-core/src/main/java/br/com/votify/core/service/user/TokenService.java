package br.com.votify.core.service.user;

import br.com.votify.core.model.user.AccessToken;
import br.com.votify.core.model.user.RefreshToken;
import br.com.votify.core.model.user.User;
import br.com.votify.core.properties.user.UserProperties;
import br.com.votify.core.repository.user.RefreshTokenRepository;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Service
public class TokenService {
    private static final SecretKey accessSecretKey = Jwts.SIG.HS256.key().build();
    private final SecretKey refreshSecretKey;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserProperties userProperties;

    public TokenService(RefreshTokenRepository refreshTokenRepository, UserProperties userProperties) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userProperties = userProperties;

        byte[] keyBytes = userProperties.getRefreshTokenSecret().getBytes(StandardCharsets.UTF_8);
        refreshSecretKey = new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    public AccessToken createAccessToken(RefreshToken refreshToken) throws VotifyException {
        return new AccessToken(refreshToken, userProperties, accessSecretKey, refreshSecretKey);
    }

    public AccessToken retrieveAccessToken(String accessToken) throws VotifyException {
        return new AccessToken(accessToken, accessSecretKey);
    }

    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken(user, userProperties, refreshSecretKey);
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken increaseRefreshTokenExpiration(String refreshToken) throws VotifyException {
        RefreshToken refreshTokenFromDatabase = refreshTokenRepository.findByCode(refreshToken).orElse(null);
        if (refreshTokenFromDatabase == null) {
            throw new VotifyException(VotifyErrorCode.REFRESH_TOKEN_EXPIRED);
        }
        refreshTokenFromDatabase.increaseExpiration(userProperties);
        return refreshTokenRepository.save(refreshTokenFromDatabase);
    }

    public void deleteRefreshToken(String token) {
        refreshTokenRepository.deleteByCode(token);
    }

    public void revokeAllRefreshTokens(User user) {
        refreshTokenRepository.deleteAllByUser(user);
    }
}
