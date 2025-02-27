package br.com.votify.api.controller.users;

import br.com.votify.api.configuration.SecurityConfig;
import br.com.votify.api.dto.ApiResponse;
import br.com.votify.api.dto.users.UserDetailedViewDTO;
import br.com.votify.api.dto.users.UserLoginDTO;
import br.com.votify.core.domain.entities.tokens.AuthTokens;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.service.TokenService;
import br.com.votify.core.service.UserService;
import br.com.votify.core.utils.exceptions.VotifyException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserContextController {
    private UserService userService;
    private TokenService tokenService;
    private SecurityConfig securityConfig;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(
        @RequestBody UserLoginDTO userLoginDTO,
        HttpServletResponse response
    ) throws VotifyException {
        AuthTokens authTokens = userService.login(
            userLoginDTO.getEmail(),
            userLoginDTO.getPassword()
        );
        Cookie refreshCookie = new Cookie("refresh_token", authTokens.getRefreshToken().getId());
        Cookie accessCookie = new Cookie("access_token", authTokens.getAccessToken());

        securityConfig.configureRefreshTokenCookie(refreshCookie);
        securityConfig.configureAccessTokenCookie(accessCookie);

        response.addCookie(refreshCookie);
        response.addCookie(accessCookie);

        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<UserDetailedViewDTO>> get() throws VotifyException {
        User user = userService.context.getUserOrThrow();
        UserDetailedViewDTO userDetailedViewDTO = UserDetailedViewDTO.parse(user);

        return ResponseEntity.ok(ApiResponse.success(userDetailedViewDTO));
    }

    @PostMapping("/access-token")
    public ResponseEntity<ApiResponse<?>> regenerateAccessToken(HttpServletResponse response) throws VotifyException {
        AuthTokens authTokens = userService.refreshTokens();

        Cookie refreshCookie = new Cookie("refresh_token", authTokens.getRefreshToken().getId());
        Cookie accessCookie = new Cookie("access_token", authTokens.getAccessToken());

        securityConfig.configureRefreshTokenCookie(refreshCookie);
        securityConfig.configureAccessTokenCookie(accessCookie);

        response.addCookie(refreshCookie);
        response.addCookie(accessCookie);

        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(null));
    }
}
