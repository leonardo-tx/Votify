package br.com.votify.api.controller.rest.user;

import br.com.votify.api.configuration.SecurityConfig;
import br.com.votify.core.model.user.User;
import br.com.votify.core.model.user.UserRegister;
import br.com.votify.core.model.user.field.Email;
import br.com.votify.core.model.user.field.Password;
import br.com.votify.core.service.user.decorators.NeedsUserContext;
import br.com.votify.core.model.user.AuthTokens;
import br.com.votify.core.service.user.PasswordResetService;
import br.com.votify.core.service.user.UserService;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.dto.ApiResponse;
import br.com.votify.dto.user.*;
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
    private final SecurityConfig securityConfig;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDetailedViewDTO>> register(
            @RequestBody UserRegisterDTO userRegisterDTO
    ) throws VotifyException {
        UserRegister userRegister = userRegisterDTO.convertToEntity();
        User createdUser = userService.register(userRegister);
        UserDetailedViewDTO userDTO = UserDetailedViewDTO.parse(createdUser);

        return ApiResponse.success(userDTO, HttpStatus.CREATED).createResponseEntity();
    }

    @PostMapping("/login")
    @NeedsUserContext
    public ResponseEntity<ApiResponse<Object>> login(
            @RequestBody UserLoginDTO userLoginDTO,
            HttpServletResponse response
    ) throws VotifyException {
        AuthTokens authTokens = userService.login(
                new Email(userLoginDTO.getEmail()),
                new Password(userLoginDTO.getPassword())
        );
        Cookie refreshCookie = securityConfig.createRefreshTokenCookie(authTokens.getRefreshToken());
        Cookie accessCookie = securityConfig.createAccessTokenCookie(authTokens.getAccessToken());

        response.addCookie(refreshCookie);
        response.addCookie(accessCookie);

        return ApiResponse.success(null, HttpStatus.OK).createResponseEntity();
    }

    @PostMapping("/logout")
    @NeedsUserContext
    public ResponseEntity<ApiResponse<Object>> logout(HttpServletResponse response) {
        userService.logout();
        Cookie refreshCookie = securityConfig.createRefreshTokenCookie(null);
        Cookie accessCookie = securityConfig.createAccessTokenCookie(null);

        response.addCookie(refreshCookie);
        response.addCookie(accessCookie);

        return ApiResponse.success(null, HttpStatus.OK).createResponseEntity();
    }

    @PostMapping("/refresh-tokens")
    @NeedsUserContext
    public ResponseEntity<ApiResponse<Object>> refreshTokens(HttpServletResponse response) throws VotifyException {
        AuthTokens authTokens = userService.getContext().refreshTokens();

        Cookie refreshCookie = securityConfig.createRefreshTokenCookie(authTokens.getRefreshToken());
        Cookie accessCookie = securityConfig.createAccessTokenCookie(authTokens.getAccessToken());

        response.addCookie(refreshCookie);
        response.addCookie(accessCookie);

        return ApiResponse.success(null, HttpStatus.OK).createResponseEntity();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Object>> requestPasswordReset(
            @RequestBody PasswordResetRequestDTO requestDTO) throws VotifyException {
        Email email = new Email(requestDTO.getEmail());
        User user = userService.getUserByEmail(email);
        passwordResetService.createPasswordResetRequest(user);

        return ApiResponse.success(null, HttpStatus.OK).createResponseEntity();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Object>> resetPassword(
            @RequestBody PasswordResetConfirmDTO confirmDTO
    ) throws VotifyException {
        userService.resetPassword(
                confirmDTO.getCode(),
                new Password(confirmDTO.getNewPassword())
        );
        return ApiResponse.success(null, HttpStatus.OK).createResponseEntity();
    }

    @PostMapping("/confirm-email")
    @NeedsUserContext
    public ResponseEntity<ApiResponse<Object>> confirmEmail(
            @RequestBody EmailConfirmationRequestDTO emailConfirmationRequestDto,
            HttpServletResponse response
    ) throws VotifyException {
        boolean hasEmail = emailConfirmationRequestDto.getEmail() != null;
        userService.confirmEmail(
                emailConfirmationRequestDto.getCode(),
                hasEmail ? new Email(emailConfirmationRequestDto.getEmail()) : null
        );
        userService.logout();

        Cookie refreshCookie = securityConfig.createRefreshTokenCookie(null);
        Cookie accessCookie = securityConfig.createAccessTokenCookie(null);

        response.addCookie(refreshCookie);
        response.addCookie(accessCookie);

        return ApiResponse.success(null, HttpStatus.OK).createResponseEntity();
    }
}
