package br.com.votify.console.callers;

import br.com.votify.dto.ApiResponse;
import br.com.votify.dto.PageResponse;
import br.com.votify.dto.polls.PollInsertDTO;
import br.com.votify.dto.polls.PollListViewDTO;
import br.com.votify.dto.users.UserDetailedViewDTO;
import lombok.AllArgsConstructor;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;

@AllArgsConstructor
public class VotifyApiPollsCaller {
    private static final String baseUrl = VotifyApiCaller.BASE_URL + "polls";

    private final TestRestTemplate restTemplate;
    private final List<String> cookies;

    public ApiResponse<UserDetailedViewDTO> create(PollInsertDTO pollInsertDTO) {
        HttpHeaders headers = new HttpHeaders();
        for (String cookie : cookies) {
            headers.add(HttpHeaders.COOKIE, cookie);
        }

        ResponseEntity<ApiResponse<UserDetailedViewDTO>> response = restTemplate.exchange(
            baseUrl,
            HttpMethod.POST,
            new HttpEntity<>(pollInsertDTO, headers),
            new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    public ApiResponse<PageResponse<PollListViewDTO>> getUserPolls(String id) {
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<ApiResponse<PageResponse<PollListViewDTO>>> response = restTemplate.exchange(
                baseUrl + "/user/{userId}",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {},
                id
        );
        return response.getBody();
    }

    public ApiResponse<PageResponse<PollListViewDTO>> getMyPolls() {
        HttpHeaders headers = new HttpHeaders();
        for (String cookie : cookies) {
            headers.add(HttpHeaders.COOKIE, cookie);
        }
        ResponseEntity<ApiResponse<PageResponse<PollListViewDTO>>> response = restTemplate.exchange(
                baseUrl + "/my",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }
}
