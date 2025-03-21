package br.com.votify.dto.users;

import br.com.votify.dto.DTOInput;
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
    public CommonUser convertToEntity() {
        return new CommonUser(null, userName, name, email, password);
    }
}
