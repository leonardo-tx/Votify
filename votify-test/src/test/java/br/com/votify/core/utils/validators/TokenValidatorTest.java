package br.com.votify.core.utils.validators;

import br.com.votify.core.domain.entities.tokens.RefreshToken;
import br.com.votify.core.domain.entities.users.CommonUser;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import io.jsonwebtoken.Jwts;
import net.jqwik.api.*;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class TokenValidatorTest {
    private static final SecretKey ACCESS_SECRET_KEY = Jwts.SIG.HS256.key().build();
    private static final SecretKey REFRESH_SECRET_KEY = Jwts.SIG.HS256.key().build();

    @Test
    public void nullAccessTokenTest() {
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> TokenValidator.validateAccessToken(null, ACCESS_SECRET_KEY)
        );
        assertEquals(VotifyErrorCode.ACCESS_TOKEN_INVALID, exception.getErrorCode());
    }

    @Property
    public void validateAccessTokenTest(@ForAll("validAccessTokens") String accessToken) {
        assertDoesNotThrow(() -> TokenValidator.validateAccessToken(accessToken, ACCESS_SECRET_KEY));
    }

    @Property
    public void accessTokensWithInvalidKeyTest(@ForAll("accessTokensWithInvalidKey") String accessToken) {
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> TokenValidator.validateAccessToken(accessToken, ACCESS_SECRET_KEY)
        );
        assertEquals(VotifyErrorCode.ACCESS_TOKEN_INVALID, exception.getErrorCode());
    }

    @Property
    public void accessTokensWithMissingFieldsTest(@ForAll("accessTokensWithMissingFields") String accessToken) {
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> TokenValidator.validateAccessToken(accessToken, ACCESS_SECRET_KEY)
        );
        assertEquals(VotifyErrorCode.ACCESS_TOKEN_INVALID, exception.getErrorCode());
    }

    @Property
    public void accessTokensExpiredTest(@ForAll("expiredAccessTokens") String accessToken) {
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> TokenValidator.validateAccessToken(accessToken, ACCESS_SECRET_KEY)
        );
        assertEquals(VotifyErrorCode.ACCESS_TOKEN_EXPIRED, exception.getErrorCode());
    }

    @Test
    public void nullRefreshTokenTest() {
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> TokenValidator.validateRefreshToken(null, REFRESH_SECRET_KEY)
        );
        assertEquals(VotifyErrorCode.REFRESH_TOKEN_INVALID, exception.getErrorCode());
    }

    @Property
    public void refreshTokensWithInvalidKeyTest(@ForAll("refreshTokensWithInvalidKey") RefreshToken refreshToken) {
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> TokenValidator.validateRefreshToken(refreshToken, REFRESH_SECRET_KEY)
        );
        assertEquals(VotifyErrorCode.REFRESH_TOKEN_INVALID, exception.getErrorCode());
    }

    @Property
    public void refreshTokensWithMissingFieldsTest(@ForAll("refreshTokensWithMissingFields") RefreshToken refreshToken) {
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> TokenValidator.validateRefreshToken(refreshToken, REFRESH_SECRET_KEY)
        );
        assertEquals(VotifyErrorCode.REFRESH_TOKEN_INVALID, exception.getErrorCode());
    }

    @Property
    public void refreshTokensExpiredTest(@ForAll("expiredRefreshTokens") RefreshToken refreshToken) {
        VotifyException exception = assertThrows(
            VotifyException.class,
            () -> TokenValidator.validateRefreshToken(refreshToken, REFRESH_SECRET_KEY)
        );
        assertEquals(VotifyErrorCode.REFRESH_TOKEN_EXPIRED, exception.getErrorCode());
    }

    @Property
    public void validateRefreshTokenTest(@ForAll("validRefreshTokens") RefreshToken refreshToken) {
        assertDoesNotThrow(() -> TokenValidator.validateRefreshToken(refreshToken, REFRESH_SECRET_KEY));
    }

    @Provide
    @SuppressWarnings("unused")
    private Arbitrary<String> validAccessTokens() {
        Random random = new Random();
        Arbitrary<String> strings = Arbitraries.strings();
        return strings.map((text) -> Jwts.builder()
            .issuedAt(Date.from(Instant.now()))
            .expiration(Date.from(Instant.now().plusSeconds(100)))
            .subject(Long.toString(random.nextLong()))
            .claim("userName", text)
            .claim("name", text)
            .claim("email", text)
            .signWith(ACCESS_SECRET_KEY)
            .compact()
        );
    }

    @Provide
    @SuppressWarnings("unused")
    private Arbitrary<String> accessTokensWithInvalidKey() {
        Random random = new Random();
        SecretKey secretKey = Jwts.SIG.HS256.key().build();
        Arbitrary<String> strings = Arbitraries.strings();
        return strings.map((text) -> Jwts.builder()
            .issuedAt(Date.from(Instant.now()))
            .expiration(Date.from(Instant.now().plusSeconds(100)))
            .subject(Long.toString(random.nextLong()))
            .claim("userName", text)
            .claim("name", text)
            .claim("email", text)
            .signWith(secretKey)
            .compact()
        );
    }

    @Provide
    @SuppressWarnings("unused")
    private Arbitrary<String> expiredAccessTokens() {
        Random random = new Random();
        Arbitrary<String> strings = Arbitraries.strings();
        return strings.map((text) -> Jwts.builder()
            .issuedAt(Date.from(Instant.now().minusSeconds(10)))
            .expiration(Date.from(Instant.now().minusSeconds(1)))
            .subject(Long.toString(random.nextLong()))
            .claim("userName", text)
            .claim("name", text)
            .claim("email", text)
            .signWith(ACCESS_SECRET_KEY)
            .compact()
        );
    }

    @Provide
    @SuppressWarnings("unused")
    private Arbitrary<String> accessTokensWithMissingFields() {
        Random random = new Random();
        Arbitrary<String> strings = Arbitraries.strings();
        return strings.map((text) -> Jwts.builder()
            .issuedAt(null)
            .expiration(random.nextBoolean() ? Date.from(Instant.now().plusSeconds(100)) : null)
            .subject(random.nextBoolean() ? Long.toString(random.nextLong()) : null)
            .claim("userName", random.nextBoolean() ? text : null)
            .claim("name", random.nextBoolean() ? text : null)
            .claim("email", random.nextBoolean() ? text : null)
            .signWith(ACCESS_SECRET_KEY)
            .compact()
        );
    }

    @Provide
    @SuppressWarnings("unused")
    private Arbitrary<RefreshToken> validRefreshTokens() {
        Random random = new Random();
        Arbitrary<String> strings = Arbitraries.strings();
        return strings.map((text) -> new RefreshToken(Jwts.builder()
            .issuedAt(Date.from(Instant.now()))
            .subject(Long.toString(random.nextLong()))
            .claim("userName", text)
            .claim("name", text)
            .claim("email", text)
            .signWith(REFRESH_SECRET_KEY)
            .compact(), Date.from(Instant.now().plusSeconds(100)), new CommonUser()));
    }

    @Provide
    @SuppressWarnings("unused")
    private Arbitrary<RefreshToken> refreshTokensWithInvalidKey() {
        Random random = new Random();
        SecretKey secretKey = Jwts.SIG.HS256.key().build();
        Arbitrary<String> strings = Arbitraries.strings();
        return strings.map((text) -> new RefreshToken(Jwts.builder()
            .issuedAt(Date.from(Instant.now()))
            .subject(Long.toString(random.nextLong()))
            .claim("userName", text)
            .claim("name", text)
            .claim("email", text)
            .signWith(secretKey)
            .compact(), Date.from(Instant.now().plusSeconds(100)), new CommonUser()));
    }

    @Provide
    @SuppressWarnings("unused")
    private Arbitrary<RefreshToken> expiredRefreshTokens() {
        Random random = new Random();
        Arbitrary<String> strings = Arbitraries.strings();
        return strings.map((text) -> new RefreshToken(Jwts.builder()
            .issuedAt(Date.from(Instant.now().minusSeconds(10)))
            .subject(Long.toString(random.nextLong()))
            .claim("userName", text)
            .claim("name", text)
            .claim("email", text)
            .signWith(REFRESH_SECRET_KEY)
            .compact(), Date.from(Instant.now().minusSeconds(1)), new CommonUser()));
    }

    @Provide
    @SuppressWarnings("unused")
    private Arbitrary<RefreshToken> refreshTokensWithMissingFields() {
        Random random = new Random();
        Arbitrary<String> strings = Arbitraries.strings();
        return strings.map((text) -> new RefreshToken(random.nextBoolean() ? Jwts.builder()
            .issuedAt(null)
            .subject(random.nextBoolean() ? Long.toString(random.nextLong()) : null)
            .claim("userName", random.nextBoolean() ? text : null)
            .claim("name", random.nextBoolean() ? text : null)
            .claim("email", random.nextBoolean() ? text : null)
            .signWith(REFRESH_SECRET_KEY)
            .compact() : null,
            random.nextBoolean() ? Date.from(Instant.now().plusSeconds(100)) : null,
            random.nextBoolean() ? new CommonUser() : null
        ));
    }
}
