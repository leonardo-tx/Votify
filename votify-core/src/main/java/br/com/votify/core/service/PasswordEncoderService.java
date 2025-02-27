package br.com.votify.core.service;

import br.com.votify.core.domain.entities.users.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordEncoderService {
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public boolean checkPassword(User user, String givenPassword) {
        return passwordEncoder.matches(givenPassword, user.getPassword());
    }

    public String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }
}
