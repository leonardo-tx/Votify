package br.com.votify.core.model.user;

import br.com.votify.core.model.user.field.Email;
import br.com.votify.core.model.user.field.Name;
import br.com.votify.core.model.user.field.Password;
import br.com.votify.core.model.user.field.UserName;
import br.com.votify.core.utils.exceptions.VotifyException;
import lombok.Getter;

@Getter
public final class UserRegister {
    private final UserName userName;
    private final Name name;
    private final Email email;
    private final Password password;

    public UserRegister(String userName, String name, String email, String password) throws VotifyException {
        this.userName = new UserName(userName);
        this.name = new Name(name);
        this.email = new Email(email);
        this.password = new Password(password);
    }
}
