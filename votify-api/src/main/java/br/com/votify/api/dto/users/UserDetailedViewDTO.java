package br.com.votify.api.dto.users;

import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.domain.entities.users.UserTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailedViewDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private UserTypeEnum role;

    public static UserDetailedViewDTO parse(User entity) {
        return new UserDetailedViewDTO(
            entity.getId(),
            entity.getFirstName(),
            entity.getLastName(),
            entity.getEmail(),
            entity.getRole()
        );
    }
}
