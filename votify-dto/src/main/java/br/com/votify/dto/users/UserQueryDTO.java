package br.com.votify.dto.users;

import br.com.votify.core.domain.entities.users.PermissionFlags;
import br.com.votify.core.domain.entities.users.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserQueryDTO {
    private Long id;
    private String name;
    private String userName;
    private String email;
    private String role;

    public static UserQueryDTO parse(User entity, User requester) {
        UserQueryDTO dto = new UserQueryDTO(
            entity.getId(),
            entity.getName(),
            entity.getUserName(),
            null,
            null
        );
        if (requester != null && requester.hasPermission(PermissionFlags.DETAILED_USER)) {
            dto.setEmail(entity.getEmail());
            dto.setRole(entity.getClass().getSimpleName());
        }
        return dto;
    }
}