package br.com.votify.core.service.user;

import br.com.votify.core.model.user.PasswordReset;
import br.com.votify.core.model.user.User;
import br.com.votify.core.properties.user.UserProperties;
import br.com.votify.core.model.user.field.Email;
import br.com.votify.core.model.user.field.Password;
import br.com.votify.core.repository.user.PasswordResetRepository;
import br.com.votify.core.repository.user.UserRepository;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetRepository passwordResetRepository;
    private final PasswordEncoderService passwordEncoderService;
    private final UserProperties userProperties;

    @Transactional
    public PasswordReset createPasswordResetRequest(Email email) throws VotifyException {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new VotifyException(VotifyErrorCode.PASSWORD_RESET_EMAIL_NOT_FOUND);
        }

        User user = userOptional.get();
        Optional<PasswordReset> active = passwordResetRepository.findByUser(user);
        if (active.isPresent()) {
            if (!active.get().isExpired()) throw new VotifyException(VotifyErrorCode.PASSWORD_RESET_REQUEST_EXISTS);
            passwordResetRepository.delete(active.get());
        }
        PasswordReset passwordReset = new PasswordReset(user, userProperties);
        return passwordResetRepository.save(passwordReset);
    }

    @Transactional
    public void resetPassword(String code, Password newPassword) throws VotifyException {
        Optional<PasswordReset> passwordResetOptional = passwordResetRepository.findByCode(code);
        if (passwordResetOptional.isEmpty() || passwordResetOptional.get().isExpired()) {
            throw new VotifyException(VotifyErrorCode.PASSWORD_RESET_CODE_INVALID);
        }
        PasswordReset passwordReset = passwordResetOptional.get();
        Optional<User> optionalUser = userRepository.findById(passwordReset.getUserId());
        if (optionalUser.isEmpty()) {
            throw new VotifyException(VotifyErrorCode.USER_NOT_FOUND);
        }
        User user = optionalUser.get();
        user.setPassword(passwordEncoderService, newPassword);

        userRepository.save(user);
        passwordResetRepository.delete(passwordReset);
    }
}
