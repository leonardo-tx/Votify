package br.com.votify.api.configuration;

import jakarta.servlet.http.Cookie;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {
    public void configureAccessTokenCookie(Cookie cookie) {
        configureCookie(cookie);
        cookie.setMaxAge(60 * 15);
    }

    public void configureRefreshTokenCookie(Cookie cookie) {
        configureCookie(cookie);
        cookie.setMaxAge(3600 * 24 * 28);
    }

    private void configureCookie(Cookie cookie) {
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
    }
}