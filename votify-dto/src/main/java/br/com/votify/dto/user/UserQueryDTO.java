package br.com.votify.dto.user;

import br.com.votify.core.model.user.PermissionFlags;
import br.com.votify.core.model.user.User;
import br.com.votify.core.model.user.UserRole;
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
    private UserRole role;

    public static UserQueryDTO parse(User entity, User requester) {
        UserQueryDTO dto = new UserQueryDTO(
            entity.getId(),
            entity.getName().getValue(),
            entity.getUserName().getValue(),
            null,
            null
        );
        if (requester != null && requester.hasPermission(PermissionFlags.DETAILED_USER)) {
            dto.setEmail(entity.getEmail().getValue());
            dto.setRole(entity.getRole());
        }
        return dto;
    }
}