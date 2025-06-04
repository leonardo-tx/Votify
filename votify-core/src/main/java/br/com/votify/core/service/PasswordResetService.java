package br.com.votify.core.service;

import br.com.votify.core.domain.entities.password.PasswordResetProperties;
import br.com.votify.core.domain.entities.password.PasswordResetToken;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.repository.PasswordResetTokenRepository;
import br.com.votify.core.repository.UserRepository;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.core.utils.validators.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoderService passwordEncoderService;
    private final PasswordResetProperties passwordResetProperties;

    public String createPasswordResetRequest(String email) throws VotifyException {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new VotifyException(VotifyErrorCode.PASSWORD_RESET_EMAIL_NOT_FOUND);
        }

        User user = userOptional.get();
        Date now = new Date();

        List<PasswordResetToken> activeTokens =
            passwordResetTokenRepository.findByUserAndExpiryDateAfter(user, now);

        if (!activeTokens.isEmpty()) {
            throw new VotifyException(VotifyErrorCode.PASSWORD_RESET_REQUEST_EXISTS);
        }

        String code = generateRandomCode();
        Date expiryDate = Date.from(now.toInstant().plusSeconds(passwordResetProperties.getExpirationMinutes()));

        PasswordResetToken token = new PasswordResetToken(code, user, expiryDate);
        passwordResetTokenRepository.save(token);

        return code;
    }

    public void resetPassword(String code, String newPassword) throws VotifyException {
        UserValidator.validatePassword(newPassword);
        Optional<PasswordResetToken> tokenOptional = passwordResetTokenRepository.findByCode(code);

        if (tokenOptional.isEmpty() || tokenOptional.get().isExpired()) {
            throw new VotifyException(VotifyErrorCode.PASSWORD_RESET_CODE_INVALID);
        }

        PasswordResetToken token = tokenOptional.get();
        User user = token.getUser();

        String encryptedPassword = passwordEncoderService.encryptPassword(newPassword);
        user.setPassword(encryptedPassword);

        userRepository.save(user);
        passwordResetTokenRepository.delete(token);
    }

    private String generateRandomCode() {
        return UUID.randomUUID().toString().toUpperCase();
    }
}
