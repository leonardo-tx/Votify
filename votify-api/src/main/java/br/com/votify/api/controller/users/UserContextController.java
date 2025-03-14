package br.com.votify.api.controller.users;

import br.com.votify.api.configuration.SecurityConfig;
import br.com.votify.core.domain.entities.tokens.AuthTokens;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.service.ContextService;
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

    @GetMapping
    public ResponseEntity<ApiResponse<UserDetailedViewDTO>> get() throws VotifyException {
        User user = contextService.getUserOrThrow();
        UserDetailedViewDTO userDetailedViewDTO = UserDetailedViewDTO.parse(user);

        return ApiResponse.success(userDetailedViewDTO, HttpStatus.OK).createResponseEntity();
    }

    @PostMapping("/regenerate-tokens")
    public ResponseEntity<ApiResponse<Object>> regenerateTokens(HttpServletResponse response) throws VotifyException {
        AuthTokens authTokens = contextService.refreshTokens();

        Cookie refreshCookie = new Cookie("refresh_token", authTokens.getRefreshToken().getId());
        Cookie accessCookie = new Cookie("access_token", authTokens.getAccessToken());

        securityConfig.configureRefreshTokenCookie(refreshCookie);
        securityConfig.configureAccessTokenCookie(accessCookie);

        response.addCookie(refreshCookie);
        response.addCookie(accessCookie);

        return ApiResponse.success(null, HttpStatus.OK).createResponseEntity();
    }
    
    @DeleteMapping
    public ResponseEntity<ApiResponse<Object>> deleteAccount(
        HttpServletResponse response
    ) throws VotifyException {
        contextService.deleteUser();
        
        Cookie refreshCookie = new Cookie("refresh_token", "");
        Cookie accessCookie = new Cookie("access_token", "");
        
        refreshCookie.setMaxAge(0);
        accessCookie.setMaxAge(0);
        
        response.addCookie(refreshCookie);
        response.addCookie(accessCookie);

        return ApiResponse.success(null, HttpStatus.OK).createResponseEntity();
    }
}
