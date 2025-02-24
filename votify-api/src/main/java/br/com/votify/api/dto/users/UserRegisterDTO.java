package br.com.votify.api.dto.users;

import br.com.votify.api.dto.DTOInput;
import br.com.votify.core.domain.entities.users.CommonUser;
import br.com.votify.core.domain.entities.users.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public final class UserRegisterDTO implements DTOInput<User> {
    private String userName;
    private String name;
    private String email;
    private String password;

    @Override
    public User convertToEntity() {
        return new CommonUser(null, userName, name, email, password);
    }
}
