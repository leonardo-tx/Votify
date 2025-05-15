package br.com.votify.core.service;

import br.com.votify.core.domain.entities.tokens.EmailConfirmation;
import br.com.votify.core.domain.entities.tokens.EmailConfirmationExpirationProperties;
import br.com.votify.core.domain.entities.tokens.EmailProperties;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.repository.EmailConfirmationRepository;
import br.com.votify.core.repository.UserRepository;
import br.com.votify.core.utils.EmailCodeGeneratorUtils;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailConfirmationService {
    private final UserRepository userRepository;
    private final ContextService contextService;
    private final EmailConfirmationRepository emailConfirmationRepository;
    private final EmailConfirmationExpirationProperties emailConfirmationExpirationProperties;
    private final EmailService emailService;
    private final Environment environment;

    @Value("${spring.mail.username:}")
    private String senderEmail;

    private boolean isDevOrTestEnvironment() {
        String[] profiles = environment.getActiveProfiles();
        return profiles.length == 0 || Arrays.stream(profiles)
                .anyMatch(p -> p.contains("dev") || p.contains("test"));
    }

    private boolean isProdEnvironment() {
        String[] profiles = environment.getActiveProfiles();
        return profiles.length > 0 && Arrays.stream(profiles)
                .anyMatch(p -> p.contains("prod"));
    }

    @Transactional
    public void confirmEmail(String code, String currentEmail) throws VotifyException {
        if (currentEmail == null) {
            User user = contextService.getUserOrThrow();
            currentEmail = user.getEmail();
        }
        EmailConfirmation emailConfirmation = emailConfirmationRepository.findByUserEmail(currentEmail)
                .orElseThrow(() -> new VotifyException(VotifyErrorCode.EMAIL_ALREADY_CONFIRMED));

        if (!Objects.equals(emailConfirmation.getEmailConfirmationCode(), code)) {
            throw new VotifyException(VotifyErrorCode.EMAIL_CONFIRMATION_CODE_INVALID);
        }

        emailConfirmationRepository.delete(emailConfirmation);
        if (emailConfirmation.getNewEmail() == null) return;

        User user = emailConfirmation.getUser();
        user.setEmailConfirmation(null);
        user.setEmail(emailConfirmation.getNewEmail());

        userRepository.save(user);
    }

    public boolean existsByEmail(String email) {
        return emailConfirmationRepository.existsByUserEmail(email);
    }

    public Optional<EmailConfirmation> addUser(User createdUser, String newEmail) throws VotifyException {
        if (createdUser.getEmailConfirmation() != null) {
            throw new VotifyException(VotifyErrorCode.PENDING_EMAIL_CONFIRMATION);
        }

        String codeGenerated = EmailCodeGeneratorUtils.generateEmailConfirmationCode();
        LocalDateTime now = LocalDateTime.now();
        EmailConfirmation emailConfirmation = new EmailConfirmation(
                null,
                createdUser,
                newEmail,
                codeGenerated,
                now.plusMinutes(emailConfirmationExpirationProperties.getExpirationMinutes())
        );
        
        EmailConfirmation savedConfirmation = emailConfirmationRepository.save(emailConfirmation);

        boolean emailConfigured = senderEmail != null && !senderEmail.trim().isEmpty();

        if (isProdEnvironment() && emailConfigured) {
            emailService.sendEmailConfirmation(createdUser.getEmail(), codeGenerated);

        } else if (isDevOrTestEnvironment() && emailConfigured) {
            try {
                confirmEmail(codeGenerated, createdUser.getEmail());
            } catch (VotifyException e) {
                throw new VotifyException(VotifyErrorCode.EMAIL_CONFIRMATION_CODE_INVALID);
            }
        }
        return savedConfirmation;
    }

    public List<EmailConfirmation> findExpiredAccounts() {
        LocalDateTime now = LocalDateTime.now();
        return emailConfirmationRepository.findAllExpirated(now);
    }

    public void deleteById(Long id) {
        emailConfirmationRepository.deleteById(id);
    }
}
