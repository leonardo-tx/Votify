package br.com.votify.core.service;

import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.domain.entities.users.UserValidator;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public User createUser(User user) throws VotifyException {
        UserValidator.validateFields(user);
        user.setId(null);

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new VotifyException(VotifyErrorCode.EMAIL_ALREADY_EXISTS);
        }
        if (userRepository.existsByUserName(user.getUserName())) {
            throw new VotifyException(VotifyErrorCode.USER_NAME_ALREADY_EXISTS);
        }
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);

        return userRepository.save(user);
    }

    public User getUserById(long id) throws VotifyException {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new VotifyException(VotifyErrorCode.USER_NOT_FOUND);
        }
        return user;
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public boolean checkPassword(User user, String givenPassword) {
        return passwordEncoder.matches(givenPassword, user.getPassword());
    }
}
