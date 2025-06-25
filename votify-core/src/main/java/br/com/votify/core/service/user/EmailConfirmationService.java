package br.com.votify.core.service.user;

import br.com.votify.core.model.user.EmailConfirmation;
import br.com.votify.core.model.user.User;
import br.com.votify.core.properties.user.UserProperties;
import br.com.votify.core.model.user.field.Email;
import br.com.votify.core.repository.user.EmailConfirmationRepository;
import br.com.votify.core.service.messaging.EmailSender;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

@Service
@RequiredArgsConstructor
public class EmailConfirmationService {
    private static final ResourceBundle messages = ResourceBundle.getBundle("messages");

    private final EmailConfirmationRepository emailConfirmationRepository;
    private final UserProperties userProperties;
    private final EmailSender emailSender;

    @Transactional
    public EmailConfirmation confirmEmail(String code, Email currentEmail) throws VotifyException {
        if (currentEmail == null) {
            throw new IllegalArgumentException("The current email must not be empty.");
        }
        EmailConfirmation emailConfirmation = emailConfirmationRepository.findByUserEmail(currentEmail)
                .orElseThrow(() -> new VotifyException(VotifyErrorCode.EMAIL_ALREADY_CONFIRMED));
        if (emailConfirmation.isExpired() || !emailConfirmation.getCode().matches(code)) {
            throw new VotifyException(VotifyErrorCode.EMAIL_CONFIRMATION_CODE_INVALID);
        }
        emailConfirmationRepository.delete(emailConfirmation);
        return emailConfirmation;
    }

    public boolean existsByEmail(Email email) {
        return emailConfirmationRepository.existsByUserEmail(email);
    }

    @Transactional
    public EmailConfirmation addEmailConfirmation(User user, Email newEmail) throws VotifyException {
        Optional<EmailConfirmation> active = emailConfirmationRepository.findByUserEmail(user.getEmail());
        if (active.isPresent()) {
            if (!active.get().isExpired()) throw new VotifyException(VotifyErrorCode.PENDING_EMAIL_CONFIRMATION);
            emailConfirmationRepository.delete(active.get());
        }
        EmailConfirmation emailConfirmation = new EmailConfirmation(newEmail, user, userProperties);
        EmailConfirmation savedEmailConfirmation = emailConfirmationRepository.save(emailConfirmation);

        String subject = messages.getString("message.email.confirmation.subject");
        String bodyKey = savedEmailConfirmation.getNewEmail() == null
                ? "message.email.confirmation.new.account.body"
                : "message.email.confirmation.existing.account.body";
        String body = String.format(
                messages.getString(bodyKey),
                user.getName().getValue(),
                user.getEmail().getValue(),
                savedEmailConfirmation.getCode().encodeToUrlCode(),
                userProperties.getEmailConfirmationExpirationMinutes()
        );
        emailSender.sendEmail(user, subject, body);
        return savedEmailConfirmation;
    }

    public List<EmailConfirmation> findExpiredAccounts() {
        Instant now = Instant.now();
        return emailConfirmationRepository.findAllExpired(now);
    }

    public void delete(EmailConfirmation emailConfirmation) {
        emailConfirmationRepository.delete(emailConfirmation);
    }

    public void deleteFromUser(User user) {
        emailConfirmationRepository.deleteFromUser(user);
    }
}
