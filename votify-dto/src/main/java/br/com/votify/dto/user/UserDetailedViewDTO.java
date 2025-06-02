package br.com.votify.dto.user;

import br.com.votify.core.model.user.User;
import br.com.votify.core.model.user.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailedViewDTO {
    private Long id;
    private String userName;
    private String name;
    private String email;
    private UserRole role;

    public static UserDetailedViewDTO parse(User entity) {
        return new UserDetailedViewDTO(
            entity.getId(),
            entity.getUserName().getValue(),
            entity.getName().getValue(),
            entity.getEmail().getValue(),
            entity.getRole()
        );
    }
}
