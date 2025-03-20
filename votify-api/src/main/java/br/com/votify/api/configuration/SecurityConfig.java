package br.com.votify.api.configuration;

import br.com.votify.core.domain.entities.cookies.CookieProperties;
import br.com.votify.core.domain.entities.password.PasswordResetProperties;
import br.com.votify.core.domain.entities.tokens.TokenProperties;
import jakarta.servlet.http.Cookie;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class SecurityConfig {
    @Autowired
    private TokenProperties tokenProperties;

    @Autowired
    private CookieProperties cookieProperties;

    @Autowired
    private PasswordResetProperties passwordResetProperties;

    public void configureAccessTokenCookie(Cookie cookie) {
        configureCookie(cookie);
        cookie.setMaxAge(tokenProperties.getAccessTokenMaxAge());
    }

    public void configureRefreshTokenCookie(Cookie cookie) {
        configureCookie(cookie);
        cookie.setMaxAge(tokenProperties.getRefreshTokenMaxAge());
    }

    private void configureCookie(Cookie cookie) {
        cookie.setHttpOnly(cookieProperties.isHttpOnly());
        cookie.setSecure(cookieProperties.isSecure());
        cookie.setPath(cookieProperties.getPath());
    }
}