package br.com.votify.core.service;

import br.com.votify.core.domain.entities.tokens.EmailConfirmation;
import br.com.votify.core.domain.entities.tokens.EmailConfirmationExpirationProperties;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.repository.EmailConfirmationRepository;
import br.com.votify.core.utils.EmailCodeGeneratorUtils;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EmailConfirmationService {
    private final EmailConfirmationRepository emailConfirmationRepository;
    private final EmailConfirmationExpirationProperties emailConfirmationExpirationProperties;

    public void confirmEmail(String code, String email) throws VotifyException {
        EmailConfirmation entity = emailConfirmationRepository.findByUserEmail(email)
                .orElseThrow(() -> new VotifyException(VotifyErrorCode.EMAIL_ALREADY_CONFIRMED));

        if (!Objects.equals(entity.getEmailConfirmationCode(), code)) {
            throw new VotifyException(VotifyErrorCode.EMAIL_CONFIRMATION_CODE_INVALID);
        }

        emailConfirmationRepository.delete(entity);
    }

    public boolean existsByEmail(String email) {
        return emailConfirmationRepository.existsByUserEmail(email);
    }

    public EmailConfirmation addUser(User createdUser) {
        String codeGenerated = EmailCodeGeneratorUtils.generateEmailConfirmationCode();
        EmailConfirmation entity = new EmailConfirmation(
                null,
                createdUser,
                codeGenerated,
                LocalDateTime.now().plusDays(emailConfirmationExpirationProperties.getExpirationMinutes())
        );
        return emailConfirmationRepository.save(entity);
    }

    public List<EmailConfirmation> findExpiredAccounts() {
        LocalDateTime now = LocalDateTime.now();
        return emailConfirmationRepository.findAllExpirated(now);
    }

    public void deleteById(Long id) {
        emailConfirmationRepository.deleteById(id);
    }
}
