package br.com.votify.console.callers;

import org.springframework.boot.test.web.client.TestRestTemplate;

import java.util.ArrayList;
import java.util.List;

public final class VotifyApiCaller {
    public static final String BASE_URL = "http://localhost:8081/";

    private static final TestRestTemplate restTemplate = new TestRestTemplate();
    private static final List<String> cookies = new ArrayList<>();

    public static final VotifyApiUsersCaller USERS =
        new VotifyApiUsersCaller(restTemplate, cookies);
    public static final VotifyApiUserContextCaller CONTEXT =
        new VotifyApiUserContextCaller(restTemplate, cookies);
    public static final VotifyApiPollsCaller POLLS =
        new VotifyApiPollsCaller(restTemplate, cookies);
    public static final VotifyApiPasswordResetCaller PASSWORD =
        new VotifyApiPasswordResetCaller(restTemplate);
}
