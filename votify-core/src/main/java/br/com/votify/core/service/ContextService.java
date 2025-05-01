package br.com.votify.core.service;

import br.com.votify.core.decorators.NeedsUserContext;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ContextService {
    private final TokenService tokenService;
    private final Map<String, String> cookies;
    private final User user;
    private final UserRepository userRepository;

    public ContextService(
        UserRepository userRepository,
        TokenService tokenService,
        HttpServletRequest request
    ) throws VotifyException {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.cookies = new HashMap<>();

        HandlerMethod handlerMethod = (HandlerMethod)request.getAttribute(
                HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE
        );
        boolean hasMethodAnnotation = handlerMethod.getMethod().isAnnotationPresent(NeedsUserContext.class);
        boolean hasClassAnnotation = handlerMethod.getBeanType().isAnnotationPresent(NeedsUserContext.class);

        if ((!hasMethodAnnotation && !hasClassAnnotation) || request.getCookies() == null) {
            this.user = null;
            return;
        }

        for (Cookie cookie : request.getCookies()) {
            this.cookies.put(cookie.getName(), cookie.getValue());
        }

        String accessToken = cookies.getOrDefault("access_token", null);
        if (accessToken == null) {
            this.user = null;
            return;
        }

        long id;
        try {
            id = tokenService.getUserIdFromAccessToken(accessToken);
        } catch (VotifyException e) {
            this.user = null;
            return;
        }
        Optional<User> userOptional = userRepository.findById(id);
        this.user = userOptional.map(User::clone).orElse(null);
    }

    public Optional<User> getUserOptional() {
        return Optional.ofNullable(user);
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

    public String getCookieValueOrDefault(String key, String defaultValue) {
        return cookies.getOrDefault(key, defaultValue);
    }

    @Transactional
    public void deleteUser() throws VotifyException {
        User currentUser = getUserOrThrow();
        userRepository.delete(currentUser);
    }
}
