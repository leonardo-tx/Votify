package br.com.votify.core.service;

import br.com.votify.core.domain.entities.tokens.EmailConfirmation;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.repository.EmailConfirmationRepository;
import br.com.votify.core.repository.UserRepository;
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

    private final UserRepository userRepository;
    private final EmailConfirmationRepository repository;

    public String generateEmailConfirmationCode(String email) throws VotifyException {
        var entity = repository.findByUserEmail(email)
                .orElseThrow(() -> new VotifyException(VotifyErrorCode.USER_NOT_FOUND));

        if (entity.isEmailConfirmed()) {
            throw new VotifyException(VotifyErrorCode.EMAIL_ALREADY_CONFIRMED);
        }

        if (entity.getEmailConfirmationExpiration() == null || entity.getEmailConfirmationExpiration().isBefore(LocalDateTime.now())) {
            String codeGenerated = EmailCodeGeneratorUtils.generateEmailConfirmationCode();
            entity.setEmailConfirmationExpiration(LocalDateTime.now().plusDays(emailConfirmationExpiration));
            entity.setEmailConfirmationCode(codeGenerated);
            repository.save(entity);
            return codeGenerated;
        }

        return entity.getEmailConfirmationCode();
    }

    public void confirmEmail(String code, String email) throws VotifyException {
        var entity = repository.findByUserEmail(email)
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

    public void addUser(User createdUser) {
        var entity = new EmailConfirmation(null, createdUser, null, null, false);
        repository.save(entity);
    }
}
