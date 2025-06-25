package br.com.votify.core.service.user;

import br.com.votify.core.model.user.PasswordReset;
import br.com.votify.core.model.user.User;
import br.com.votify.core.properties.user.UserProperties;
import br.com.votify.core.repository.user.PasswordResetRepository;
import br.com.votify.core.service.messaging.EmailSender;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.ResourceBundle;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private static final ResourceBundle messages = ResourceBundle.getBundle("messages");

    private final PasswordResetRepository passwordResetRepository;
    private final UserProperties userProperties;
    private final EmailSender emailSender;

    @Transactional
    public PasswordReset createPasswordResetRequest(User user) throws VotifyException {
        Optional<PasswordReset> active = passwordResetRepository.findByUser(user);
        if (active.isPresent()) {
            if (!active.get().isExpired()) throw new VotifyException(VotifyErrorCode.PASSWORD_RESET_REQUEST_EXISTS);
            passwordResetRepository.delete(active.get());
        }
        PasswordReset passwordReset = new PasswordReset(user, userProperties);
        PasswordReset savedPasswordReset = passwordResetRepository.save(passwordReset);

        String subject = messages.getString("message.password.reset.subject");
        String body = String.format(
                messages.getString("message.password.reset.body"),
                user.getName().getValue(),
                savedPasswordReset.getCode().encodeToUrlCode(),
                userProperties.getResetPasswordConfirmationExpirationMinutes()
        );
        emailSender.sendEmail(user, subject, body);
        return savedPasswordReset;
    }

    @Transactional
    public PasswordReset resetPassword(String code) throws VotifyException {
        Optional<PasswordReset> passwordResetOptional = passwordResetRepository.findByCode(code);
        if (passwordResetOptional.isEmpty() || passwordResetOptional.get().isExpired()) {
            throw new VotifyException(VotifyErrorCode.PASSWORD_RESET_CODE_INVALID);
        }
        PasswordReset passwordReset = passwordResetOptional.get();
        passwordResetRepository.delete(passwordReset);

        return passwordReset;
    }

    public void deleteFromUser(User user) {
        passwordResetRepository.deleteFromUser(user);
    }
}
