package br.com.votify.core.service;

import br.com.votify.core.domain.entities.tokens.EmailConfirmation;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.repository.EmailConfirmationRepository;
import br.com.votify.core.utils.EmailCodeGeneratorUtils;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailConfirmationService {

    private static final int emailConfirmationExpiration = 1;

    private final EmailConfirmationRepository repository;


    public void confirmEmail(String code, String email) throws VotifyException {
        EmailConfirmation entity = repository.findByUserEmail(email)
                .orElseThrow(() -> new VotifyException(VotifyErrorCode.USER_NOT_FOUND));

        if (entity.isEmailConfirmed()) {
            throw new VotifyException(VotifyErrorCode.EMAIL_ALREADY_CONFIRMED);
        }

        if (entity.getEmailConfirmationExpiration() == null
                || entity.getEmailConfirmationExpiration().isBefore(LocalDateTime.now())) {
            throw new VotifyException(VotifyErrorCode.EMAIL_CONFIRMATION_CODE_EXPIRED);
        }

        if (!Objects.equals(entity.getEmailConfirmationCode(), code)) {
            throw new VotifyException(VotifyErrorCode.EMAIL_CONFIRMATION_CODE_INVALID);
        }

        entity.setEmailConfirmed(true);
        entity.setEmailConfirmationExpiration(null);
        entity.setEmailConfirmationCode(null);

        repository.save(entity);
    }

    public Optional<EmailConfirmation> findByEmail(String email) {
        return repository.findByUserEmail(email);
    }

    public EmailConfirmation addUser(User createdUser) {
        String codeGenerated = EmailCodeGeneratorUtils.generateEmailConfirmationCode();
        EmailConfirmation entity = new EmailConfirmation(null, createdUser, codeGenerated, LocalDateTime.now().plusDays(emailConfirmationExpiration), false);
        return repository.save(entity);
    }
}
