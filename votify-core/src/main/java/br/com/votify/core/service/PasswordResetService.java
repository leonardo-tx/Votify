package br.com.votify.core.service;

import br.com.votify.core.domain.entities.password.PasswordResetToken;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.repository.PasswordResetTokenRepository;
import br.com.votify.core.repository.UserRepository;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoderService passwordEncoderService;
    private final MessageSource messageSource;  // Adicionado para internacionalização (i18n)

    @Value("${app.password-reset.expiration-minutes:15}")
    private int expirationMinutes;

    public String createPasswordResetRequest(String email) throws VotifyException {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new VotifyException(VotifyErrorCode.PASSWORD_RESET_EMAIL_NOT_FOUND);

        }

        User user = userOptional.get();
        Date now = new Date();

        // Verificar se já existe um token válido
        List<PasswordResetToken> activeTokens =
                passwordResetTokenRepository.findByUserAndExpiryDateAfter(user, now);

        if (!activeTokens.isEmpty()) {
            throw new VotifyException(VotifyErrorCode.PASSWORD_RESET_REQUEST_EXISTS);
        }

        String code = generateRandomCode();
        Date expiryDate = new Date(now.getTime() + TimeUnit.MINUTES.toMillis(expirationMinutes));

        // Salvar o token
        PasswordResetToken token = new PasswordResetToken(code, user, expiryDate);
        passwordResetTokenRepository.save(token);

        return code;
    }

    public void resetPassword(String code, String newPassword) throws VotifyException {
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
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    private String getMessage(String key) {
        return messageSource.getMessage(key, null, Locale.getDefault());
    }
}
