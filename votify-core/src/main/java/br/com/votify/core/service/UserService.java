package br.com.votify.core.service;

import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.domain.entities.users.UserTypeEnum;
import br.com.votify.core.domain.entities.users.UserValidator;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.core.repository.UserRepository;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public User createUser(User user) throws VotifyException {
        UserValidator.validateFields(user);
        if (user.getRole() != UserTypeEnum.COMMON) {
            throw new VotifyException(VotifyErrorCode.ROLE_REGISTER_INVALID);
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new VotifyException(VotifyErrorCode.EMAIL_ALREADY_EXISTS);
        }
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);

        return userRepository.save(user);
    }

    public User updateUser(String id, User user) throws VotifyException {
        UserValidator.validateNonNullFields(user);

        User userFromDatabase = getUser(id);
        if (userFromDatabase == null) {
            throw new VotifyException(VotifyErrorCode.USER_NOT_FOUND);
        }
        if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
            throw new VotifyException(VotifyErrorCode.EMAIL_ALREADY_EXISTS);
        }

        userFromDatabase.updateFrom(user);
        return userRepository.save(userFromDatabase);
    }

    @Nullable
    public User getUser(String id) {
        return userRepository.findById(id).orElse(null);
    }

    public boolean checkPassword(User user, String givenPassword) {
        return passwordEncoder.matches(givenPassword, user.getPassword());
    }
}
