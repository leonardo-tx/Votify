package br.com.votify.api.configuration;

import br.com.votify.core.properties.user.UserProperties;
import jakarta.servlet.http.Cookie;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class SecurityConfig {
    @Autowired
    private UserProperties userProperties;

    public void configureAccessTokenCookie(Cookie cookie) {
        configureCookie(cookie);
        cookie.setMaxAge(userProperties.getAccessTokenExpirationSeconds());
    }

    public void configureRefreshTokenCookie(Cookie cookie) {
        configureCookie(cookie);
        cookie.setMaxAge(userProperties.getRefreshTokenExpirationSeconds());
    }

    private void configureCookie(Cookie cookie) {
        cookie.setHttpOnly(userProperties.isCookieHttpOnly());
        cookie.setSecure(userProperties.isCookieSecure());
        cookie.setPath(userProperties.getCookiePath());
    }
}