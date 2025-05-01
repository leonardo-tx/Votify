package br.com.votify.api.controller.users;

import br.com.votify.api.configuration.SecurityConfig;
import br.com.votify.core.decorators.NeedsUserContext;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.service.EmailConfirmationService;
import br.com.votify.core.service.UserService;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.dto.ApiResponse;
import br.com.votify.dto.users.UserDetailedViewDTO;
import br.com.votify.dto.users.UserQueryDTO;
import br.com.votify.dto.users.UserUpdateInfoRequestDTO;
import br.com.votify.dto.users.UserUpdatePasswordRequestDTO;
import br.com.votify.dto.users.UserUpdateEmailRequestDTO;
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
@NeedsUserContext
public class UserController {
    private final UserService userService;
    private final EmailConfirmationService emailConfirmationService;
    private final SecurityConfig securityConfig;

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

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDetailedViewDTO>> getSelf() throws VotifyException {
        User user = userService.getContext().getUserOrThrow();
        UserDetailedViewDTO userDetailedViewDTO = UserDetailedViewDTO.parse(user);

        return ApiResponse.success(userDetailedViewDTO, HttpStatus.OK).createResponseEntity();
    }

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Object>> deleteSelf(
        HttpServletResponse response
    ) throws VotifyException {
        userService.getContext().deleteUser();

        Cookie refreshCookie = new Cookie("refresh_token", "");
        Cookie accessCookie = new Cookie("access_token", "");

        refreshCookie.setMaxAge(0);
        accessCookie.setMaxAge(0);

        response.addCookie(refreshCookie);
        response.addCookie(accessCookie);

        return ApiResponse.success(null, HttpStatus.OK).createResponseEntity();
    }

    @PutMapping("/me/info")
    public ResponseEntity<ApiResponse<UserDetailedViewDTO>> updateInfo(
        @RequestBody UserUpdateInfoRequestDTO requestDTO
    ) throws VotifyException {
        User updatedUser = userService.updateUserInfo(
            requestDTO.getName(),
            requestDTO.getUserName()
        );

        UserDetailedViewDTO userDetailedViewDTO = UserDetailedViewDTO.parse(updatedUser);
        return ApiResponse.success(userDetailedViewDTO, HttpStatus.OK).createResponseEntity();
    }

    @PutMapping("/me/email")
    public ResponseEntity<ApiResponse<String>> updateEmail(
        @RequestBody UserUpdateEmailRequestDTO requestDTO
    ) throws VotifyException {
        User updatedUser = userService.updateUserEmail(requestDTO.getEmail());
        String code = updatedUser.getEmailConfirmation().getEmailConfirmationCode();

        return ApiResponse.success(code, HttpStatus.OK).createResponseEntity();
    }

    @PutMapping("/me/password")
    public ResponseEntity<ApiResponse<Object>> updatePassword(
        @RequestBody UserUpdatePasswordRequestDTO requestDTO,
        HttpServletResponse response
    ) throws VotifyException {
        userService.updateUserPassword(requestDTO.getOldPassword(), requestDTO.getNewPassword());

        Cookie refreshCookie = new Cookie("refresh_token", "");
        Cookie accessCookie = new Cookie("access_token", "");

        refreshCookie.setMaxAge(0);
        accessCookie.setMaxAge(0);

        response.addCookie(refreshCookie);
        response.addCookie(accessCookie);

        return ApiResponse.success(null, HttpStatus.OK).createResponseEntity();
    }
}
