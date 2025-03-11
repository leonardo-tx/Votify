package br.com.votify.api.controller.users;

import br.com.votify.api.configuration.SecurityConfig;
import br.com.votify.core.domain.entities.tokens.AuthTokens;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.service.ContextService;
import br.com.votify.core.service.UserService;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.dto.ApiResponse;
import br.com.votify.dto.users.UserDetailedViewDTO;
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
    private final ContextService contextService;
    private final SecurityConfig securityConfig;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<UserDetailedViewDTO>> get() throws VotifyException {
        User user = contextService.getUserOrThrow();
        UserDetailedViewDTO userDetailedViewDTO = UserDetailedViewDTO.parse(user);

        return ResponseEntity.ok(ApiResponse.success(userDetailedViewDTO));
    }

    @PostMapping("/regenerate-tokens")
    public ResponseEntity<ApiResponse<?>> regenerateTokens(HttpServletResponse response) throws VotifyException {
        AuthTokens authTokens = contextService.refreshTokens();

        Cookie refreshCookie = new Cookie("refresh_token", authTokens.getRefreshToken().getId());
        Cookie accessCookie = new Cookie("access_token", authTokens.getAccessToken());

        securityConfig.configureRefreshTokenCookie(refreshCookie);
        securityConfig.configureAccessTokenCookie(accessCookie);

        response.addCookie(refreshCookie);
        response.addCookie(accessCookie);

        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(null));
    }
    
    @DeleteMapping
    public ResponseEntity<ApiResponse<?>> deleteAccount(
            HttpServletResponse response
    ) throws VotifyException {
        User currentUser = contextService.getUserOrThrow();
        userService.deleteUser(currentUser.getId());
        
        Cookie refreshCookie = new Cookie("refresh_token", "");
        Cookie accessCookie = new Cookie("access_token", "");
        
        refreshCookie.setMaxAge(0);
        accessCookie.setMaxAge(0);
        
        securityConfig.configureRefreshTokenCookie(refreshCookie);
        securityConfig.configureAccessTokenCookie(accessCookie);
        
        response.addCookie(refreshCookie);
        response.addCookie(accessCookie);

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success(null));
    }
}
