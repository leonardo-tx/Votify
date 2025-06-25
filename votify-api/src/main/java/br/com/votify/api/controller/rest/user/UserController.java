package br.com.votify.api.controller.rest.user;

import br.com.votify.api.configuration.SecurityConfig;
import br.com.votify.core.model.user.User;
import br.com.votify.core.model.user.field.Email;
import br.com.votify.core.model.user.field.Name;
import br.com.votify.core.model.user.field.Password;
import br.com.votify.core.model.user.field.UserName;
import br.com.votify.core.service.user.decorators.NeedsUserContext;
import br.com.votify.core.service.user.UserService;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.dto.ApiResponse;
import br.com.votify.dto.user.UserDetailedViewDTO;
import br.com.votify.dto.user.UserQueryDTO;
import br.com.votify.dto.user.UserUpdateInfoRequestDTO;
import br.com.votify.dto.user.UserUpdatePasswordRequestDTO;
import br.com.votify.dto.user.UserUpdateEmailRequestDTO;
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

    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<UserQueryDTO>> getUserByUsername(
        @PathVariable("username") String username
    ) throws VotifyException {
        User targetUser = userService.getUserByUserName(new UserName(username));
        UserQueryDTO dto = UserQueryDTO.parse(targetUser, null);
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
        userService.delete(userService.getContext().getUserOrThrow());
        Cookie refreshCookie = securityConfig.createRefreshTokenCookie(null);
        Cookie accessCookie = securityConfig.createAccessTokenCookie(null);

        response.addCookie(refreshCookie);
        response.addCookie(accessCookie);

        return ApiResponse.success(null, HttpStatus.OK).createResponseEntity();
    }

    @PutMapping("/me/info")
    public ResponseEntity<ApiResponse<UserDetailedViewDTO>> updateInfo(
        @RequestBody UserUpdateInfoRequestDTO requestDTO
    ) throws VotifyException {
        boolean hasNameUpdate = requestDTO.getName() != null && !requestDTO.getName().isBlank();
        boolean hasUserNameUpdate = requestDTO.getUserName() != null && !requestDTO.getUserName().isBlank();
        User updatedUser = userService.updateUserInfo(
                hasNameUpdate ? new Name(requestDTO.getName()) : null,
                hasUserNameUpdate ? new UserName(requestDTO.getUserName()) : null
        );
        UserDetailedViewDTO userDetailedViewDTO = UserDetailedViewDTO.parse(updatedUser);
        return ApiResponse.success(userDetailedViewDTO, HttpStatus.OK).createResponseEntity();
    }

    @PutMapping("/me/email")
    public ResponseEntity<ApiResponse<Object>> updateEmail(
        @RequestBody UserUpdateEmailRequestDTO requestDTO
    ) throws VotifyException {
        userService.updateUserEmail(new Email(requestDTO.getEmail()));
        return ApiResponse.success(null, HttpStatus.OK).createResponseEntity();
    }

    @PutMapping("/me/password")
    public ResponseEntity<ApiResponse<Object>> updatePassword(
        @RequestBody UserUpdatePasswordRequestDTO requestDTO,
        HttpServletResponse response
    ) throws VotifyException {
        userService.updateUserPassword(
                new Password(requestDTO.getOldPassword()),
                new Password(requestDTO.getNewPassword())
        );
        Cookie refreshCookie = securityConfig.createRefreshTokenCookie(null);
        Cookie accessCookie = securityConfig.createAccessTokenCookie(null);

        response.addCookie(refreshCookie);
        response.addCookie(accessCookie);

        return ApiResponse.success(null, HttpStatus.OK).createResponseEntity();
    }
}
