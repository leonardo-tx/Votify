package br.com.votify.dto.users;

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
    private String role;
    private String confirmationCode;

    public static UserDetailedViewDTO parse(User entity, String confirmationCode) {
        return new UserDetailedViewDTO(
            entity.getId(),
            entity.getUserName(),
            entity.getName(),
            entity.getEmail(),
            entity.getClass().getSimpleName(),
            confirmationCode
        );
    }

    public static UserDetailedViewDTO parse(User entity) {
        return new UserDetailedViewDTO(
                entity.getId(),
                entity.getUserName(),
                entity.getName(),
                entity.getEmail(),
                entity.getClass().getSimpleName(),
                null
        );
    }
}
