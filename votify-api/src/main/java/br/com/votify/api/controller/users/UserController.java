package br.com.votify.api.controller.users;

import br.com.votify.api.dto.ApiResponse;
import br.com.votify.api.dto.users.UserDetailedViewDTO;
import br.com.votify.api.dto.users.UserRegisterDTO;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.core.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // TODO: Terminar a parte do CRUD
    @PostMapping
    public ResponseEntity<ApiResponse<UserDetailedViewDTO>> registerUser(@RequestBody UserRegisterDTO userRegisterDTO) {
        try {
            User user = userRegisterDTO.convertToEntity();
            user = userService.createUser(user);
            UserDetailedViewDTO userDto = UserDetailedViewDTO.parse(user);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(userDto));
        } catch (VotifyException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e));
        }
    }

}
