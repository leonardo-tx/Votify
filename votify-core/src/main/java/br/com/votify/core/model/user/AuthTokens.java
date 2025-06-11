package br.com.votify.core.model.user;

import lombok.*;

@Getter
public final class AuthTokens {
    private final AccessToken accessToken;
    private final RefreshToken refreshToken;

    public AuthTokens(AccessToken accessToken, RefreshToken refreshToken) {
        if (accessToken == null) {
            throw new IllegalArgumentException("The access token must not be null.");
        }
        if (refreshToken == null) {
            throw new IllegalArgumentException("The refresh token must not be null.");
        }
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
