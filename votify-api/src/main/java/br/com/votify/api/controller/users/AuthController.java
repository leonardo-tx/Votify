package br.com.votify.api.controller.users;

import br.com.votify.api.configuration.SecurityConfig;
import br.com.votify.core.decorators.NeedsUserContext;
import br.com.votify.core.domain.entities.tokens.AuthTokens;
import br.com.votify.core.domain.entities.users.CommonUser;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.service.EmailConfirmationService;
import br.com.votify.core.service.PasswordResetService;
import br.com.votify.core.service.UserService;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.dto.ApiResponse;
import br.com.votify.dto.users.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final PasswordResetService passwordResetService;
    private final EmailConfirmationService emailConfirmationService;
    private final SecurityConfig securityConfig;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDetailedViewDTO>> register(
            @RequestBody UserRegisterDTO userRegisterDTO
    ) throws VotifyException {
        CommonUser user = userRegisterDTO.convertToEntity();
        User createdUser = userService.register(user);
        UserDetailedViewDTO userDTO = UserDetailedViewDTO.parse(
                createdUser,
                createdUser.getEmailConfirmation().getEmailConfirmationCode()
        );
        return ApiResponse.success(userDTO, HttpStatus.CREATED).createResponseEntity();
    }

    @PostMapping("/login")
    @NeedsUserContext
    public ResponseEntity<ApiResponse<Object>> login(
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

        return ApiResponse.success(null, HttpStatus.OK).createResponseEntity();
    }

    @PostMapping("/logout")
    @NeedsUserContext
    public ResponseEntity<ApiResponse<Object>> logout(HttpServletResponse response) {
        userService.logout();

        Cookie refreshCookie = new Cookie("refresh_token", "");
        Cookie accessCookie = new Cookie("access_token", "");

        refreshCookie.setMaxAge(0);
        accessCookie.setMaxAge(0);
        refreshCookie.setPath(securityConfig.getCookieProperties().getPath());
        accessCookie.setPath(securityConfig.getCookieProperties().getPath());

        response.addCookie(refreshCookie);
        response.addCookie(accessCookie);

        return ApiResponse.success(null, HttpStatus.OK).createResponseEntity();
    }

    @PostMapping("/refresh-tokens")
    @NeedsUserContext
    public ResponseEntity<ApiResponse<Object>> refreshTokens(HttpServletResponse response) throws VotifyException {
        AuthTokens authTokens = userService.getContext().refreshTokens();

        Cookie refreshCookie = new Cookie("refresh_token", authTokens.getRefreshToken().getId());
        Cookie accessCookie = new Cookie("access_token", authTokens.getAccessToken());

        securityConfig.configureRefreshTokenCookie(refreshCookie);
        securityConfig.configureAccessTokenCookie(accessCookie);

        response.addCookie(refreshCookie);
        response.addCookie(accessCookie);

        return ApiResponse.success(null, HttpStatus.OK).createResponseEntity();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<PasswordResetResponseDTO>> requestPasswordReset(
            @RequestBody PasswordResetRequestDTO requestDTO) throws VotifyException {

        String code = passwordResetService.createPasswordResetRequest(requestDTO.getEmail());
        PasswordResetResponseDTO responseDTO = new PasswordResetResponseDTO(
            code,
            securityConfig.getPasswordResetProperties().getExpirationMinutes()
        );
        return ApiResponse.success(responseDTO, HttpStatus.OK).createResponseEntity();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Object>> resetPassword(
            @RequestBody PasswordResetConfirmDTO confirmDTO) throws VotifyException {

        passwordResetService.resetPassword(confirmDTO.getCode(), confirmDTO.getNewPassword());
        return ApiResponse.success(null, HttpStatus.OK).createResponseEntity();
    }

    @PostMapping("/confirm-email")
    @NeedsUserContext
    public ResponseEntity<ApiResponse<Object>> confirmEmail(@RequestBody EmailConfirmationRequestDTO emailConfirmationRequestDto) throws VotifyException {
        emailConfirmationService.confirmEmail(
                emailConfirmationRequestDto.getCode(),
                emailConfirmationRequestDto.getEmail()
        );
        return ApiResponse.success(null, HttpStatus.OK).createResponseEntity();
    }
}
