package br.com.votify.core.service.user;

import br.com.votify.core.model.user.User;
import br.com.votify.core.model.user.field.Password;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordEncoderService {
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    public boolean checkPassword(User user, Password givenPassword) {
        return passwordEncoder.matches(givenPassword.getValue(), user.getEncryptedPassword());
    }

    public String encryptPassword(Password password) {
        return passwordEncoder.encode(password.getValue());
    }
}
