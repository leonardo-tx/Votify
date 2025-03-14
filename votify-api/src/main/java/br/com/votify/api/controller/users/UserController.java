package br.com.votify.api.controller.users;

import br.com.votify.api.configuration.SecurityConfig;
import br.com.votify.core.domain.entities.tokens.AuthTokens;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.core.service.UserService;
import br.com.votify.dto.ApiResponse;
import br.com.votify.dto.users.UserDetailedViewDTO;
import br.com.votify.dto.users.UserLoginDTO;
import br.com.votify.dto.users.UserQueryDTO;
import br.com.votify.dto.users.UserRegisterDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final SecurityConfig securityConfig;

    @PostMapping
    public ResponseEntity<ApiResponse<UserDetailedViewDTO>> registerUser(
        @RequestBody UserRegisterDTO userRegisterDTO
    ) throws VotifyException {
        User user = userRegisterDTO.convertToEntity();
        User createdUser = userService.createUser(user);
        UserDetailedViewDTO userDTO = UserDetailedViewDTO.parse(createdUser);

        return ApiResponse.success(userDTO, HttpStatus.CREATED).createResponseEntity();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserQueryDTO>> getUserById(
        @PathVariable("id") Long id
    ) throws VotifyException {
        Optional<User> requesterOpt = userService.getContext().getUserOptional();
        User targetUser = userService.getUserById(id);

        UserQueryDTO dto = requesterOpt
            .map(requester -> UserQueryDTO.parse(targetUser, requester))
            .orElseGet(() -> UserQueryDTO.parse(targetUser, null));

        return ApiResponse.success(dto, HttpStatus.OK).createResponseEntity();
    }

    @PostMapping("/login")
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
    public ResponseEntity<ApiResponse<Object>> logout(HttpServletResponse response) {
        userService.logout();

        Cookie refreshCookie = new Cookie("refresh_token", "");
        Cookie accessCookie = new Cookie("access_token", "");

        refreshCookie.setMaxAge(0);
        accessCookie.setMaxAge(0);
        refreshCookie.setPath("/");
        accessCookie.setPath("/");

        response.addCookie(refreshCookie);
        response.addCookie(accessCookie);

        return ApiResponse.success(null, HttpStatus.OK).createResponseEntity();
    }
}
