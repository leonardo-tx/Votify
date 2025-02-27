package br.com.votify.core.service;

import br.com.votify.core.domain.entities.tokens.AuthTokens;
import br.com.votify.core.domain.entities.tokens.RefreshToken;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.repository.UserRepository;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

@Service
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ContextService {
    private final TokenService tokenService;
    private final Map<String, String> cookies;
    private final User user;

    public ContextService(
        UserRepository userRepository,
        TokenService tokenService,
        HttpServletRequest httpServletRequest
    ) throws VotifyException {
        this.tokenService = tokenService;
        this.cookies = new HashMap<>();
        if (httpServletRequest.getCookies() == null) {
            this.user = null;
            return;
        }

        for (Cookie cookie : httpServletRequest.getCookies()) {
            this.cookies.put(cookie.getName(), cookie.getValue());
        }

        String accessToken = cookies.getOrDefault("access_token", null);
        if (accessToken == null) {
            this.user = null;
            return;
        }
        long id = tokenService.getUserIdFromAccessToken(accessToken);
        this.user = userRepository.findById(id).orElse(null);
    }

    @NonNull
    public User getUserOrThrow() throws VotifyException {
        throwIfNotAuthenticated();
        return user;
    }

    public AuthTokens refreshTokens() throws VotifyException {
        String refreshToken = getCookieValue("refresh_token");
        if (refreshToken == null) {
            throw new VotifyException(VotifyErrorCode.REFRESH_TOKEN_EXPIRED);
        }
        RefreshToken updatedRefreshToken = tokenService.increaseRefreshTokenExpiration(refreshToken);
        String newAccessToken = tokenService.createAccessToken(updatedRefreshToken);

        return new AuthTokens(newAccessToken, updatedRefreshToken);
    }

    public void throwIfNotAuthenticated() throws VotifyException {
        if (!isAuthenticated()) {
            throw new VotifyException(VotifyErrorCode.COMMON_UNAUTHORIZED);
        }
    }

    public boolean isAuthenticated() {
        return user != null;
    }

    public String getCookieValue(String key) {
        return cookies.get(key);
    }
}
