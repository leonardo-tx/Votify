package br.com.votify.api.dto.users;

import br.com.votify.core.domain.entities.users.User;
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

    public static UserDetailedViewDTO parse(User entity) {
        return new UserDetailedViewDTO(
            entity.getId(),
            entity.getUserName(),
            entity.getName(),
            entity.getEmail()
        );
    }
}
