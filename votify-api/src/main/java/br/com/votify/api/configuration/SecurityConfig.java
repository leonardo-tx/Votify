package br.com.votify.api.configuration;

import br.com.votify.core.model.user.AccessToken;
import br.com.votify.core.model.user.RefreshToken;
import br.com.votify.core.properties.user.UserProperties;
import jakarta.servlet.http.Cookie;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Getter
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserProperties userProperties;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    public Cookie createAccessTokenCookie(AccessToken accessToken) {
        String code = "";
        int maxAge = 0;

        if (accessToken != null) {
            code = accessToken.getCode();
            maxAge = userProperties.getAccessTokenExpirationSeconds();
        }
        Cookie cookie = getDefaultCookie(userProperties.getAccessTokenCookieName(), code);
        cookie.setMaxAge(maxAge);

        return cookie;
    }

    public Cookie createRefreshTokenCookie(RefreshToken refreshToken) {
        String code = "";
        int maxAge = 0;

        if (refreshToken != null) {
            code = refreshToken.getCode();
            maxAge = userProperties.getRefreshTokenExpirationSeconds();
        }
        Cookie cookie = getDefaultCookie(userProperties.getRefreshTokenCookieName(), code);
        cookie.setMaxAge(maxAge);

        return cookie;
    }

    private Cookie getDefaultCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(cookie.isHttpOnly());
        cookie.setSecure(cookie.getSecure());
        cookie.setPath("/");

        return cookie;
    }
}