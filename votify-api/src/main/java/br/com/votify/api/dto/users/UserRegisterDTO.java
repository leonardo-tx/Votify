package br.com.votify.api.dto.users;

import br.com.votify.api.dto.DTOInput;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.domain.entities.users.UserTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public final class UserRegisterDTO implements DTOInput<User> {
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    @Override
    public User convertToEntity() {
        return new User(null, firstName, lastName, email, UserTypeEnum.COMMON, password);
    }
}
