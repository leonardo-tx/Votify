package br.com.votify.api.dto.users;

import br.com.votify.core.domain.entities.users.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSimpleViewDTO {
    private String id;
    private String firstName;
    private String lastName;

    public static UserSimpleViewDTO parse(User entity) {
        return new UserSimpleViewDTO(
            entity.getId(),
            entity.getFirstName(),
            entity.getLastName()
        );
    }
}
