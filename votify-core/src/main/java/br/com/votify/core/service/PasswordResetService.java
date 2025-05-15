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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Arrays;
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
    private final EmailService emailService;
    private final Environment environment;

    @Value("${spring.mail.username:}")
    private String senderEmail;

    private boolean isDevOrTestEnvironment() {
        String[] profiles = environment.getActiveProfiles();
        return profiles.length == 0 || Arrays.stream(profiles)
                .anyMatch(p -> p.contains("dev") || p.contains("test"));
    }

    public Optional<String> createPasswordResetRequest(String email) throws VotifyException {
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
        long expirationSeconds = passwordResetProperties.getExpirationMinutes() * 60L;
        Date expiryDate = Date.from(now.toInstant().plusSeconds(expirationSeconds));

        PasswordResetToken token = new PasswordResetToken(code, user, expiryDate);
        passwordResetTokenRepository.save(token);

        boolean emailConfigured = senderEmail != null && !senderEmail.trim().isEmpty();

        if (isDevOrTestEnvironment()) {
            System.out.println("DEV/TEST: Password Reset Code for " + user.getEmail() + " is: " + code);
            return Optional.empty();
        } else {
            if (emailConfigured) {
                try {
                    emailService.sendPasswordResetEmail(user.getEmail(), code);
                    return Optional.empty();
                } catch (Exception e) {
                    System.err.println("ERROR: Failed to send password reset email for " + user.getEmail() +
                                       " in environment [" + Arrays.toString(environment.getActiveProfiles()) + "]. " +
                                       "Returning code as fallback. Error: " + e.getClass().getName() + ": " + e.getMessage());
                    return Optional.of(code);
                }
            } else {
                System.err.println("ERROR: Email service not configured in environment [" +
                                   Arrays.toString(environment.getActiveProfiles()) +
                                   "] for password reset. Returning code directly as fallback. THIS SHOULD BE ADDRESSED.");
                return Optional.of(code);
            }
        }
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
