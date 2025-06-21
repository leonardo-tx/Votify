package br.com.votify.core.repository.user;

import br.com.votify.core.model.user.EmailConfirmation;
import br.com.votify.core.model.user.field.Email;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface EmailConfirmationRepository {
    Optional<EmailConfirmation> findByUserEmail(Email email);
    boolean existsByUserEmail(Email email);
    List<EmailConfirmation> findAllExpired(Instant now);
    void delete(EmailConfirmation emailConfirmation);
    EmailConfirmation save(EmailConfirmation emailConfirmation);
}
