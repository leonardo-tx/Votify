package br.com.votify.api.controller.users;

import br.com.votify.api.dto.users.UserDTO;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.domain.entities.users.UserTypeEnum;
import br.com.votify.core.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // GET para retornar todos os usuários
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserDTO> userDTOs = users.stream()
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getDocument(),
                        user.getEmail(),
                        user.getRole().toString()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    // Buscar usuário por ID
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable("id") Integer id) {
        User user = userService.getUser(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        UserDTO dto = new UserDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getDocument(),
                user.getEmail(),
                user.getRole().toString()
        );
        return ResponseEntity.ok(dto);
    }

    // Criar um novo usuário
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        // Converter DTO para entidade
        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setDocument(userDTO.getDocument());
        user.setEmail(userDTO.getEmail());
        // Converte a role de String para UserTypeEnum
        user.setRole(UserTypeEnum.valueOf(userDTO.getRole().toUpperCase()));

        User savedUser = userService.createUser(user);
        UserDTO savedDTO = new UserDTO(
                savedUser.getId(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getDocument(),
                savedUser.getEmail(),
                savedUser.getRole().toString()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDTO);
    }

    // (Opcional) Endpoints para atualizar e deletar usuários podem ser adicionados
}
