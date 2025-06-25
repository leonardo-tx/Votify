package br.com.votify.dto.user;

import br.com.votify.core.model.user.UserRegister;
import br.com.votify.core.utils.exceptions.VotifyException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public final class UserRegisterDTO {
    private String userName;
    private String name;
    private String email;
    private String password;

    public UserRegister convertToEntity() throws VotifyException {
        return new UserRegister(userName, name, email, password);
    }
}
