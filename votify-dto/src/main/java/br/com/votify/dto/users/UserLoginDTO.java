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
public class UserLoginDTO implements DTOInput<User> {
    private String email;
    private String password;

    @Override
    public User convertToEntity() {
        return CommonUser.builder()
                .email(email)
                .password(password)
                .build();
    }
}
