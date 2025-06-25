package br.com.votify.core.repository.user;

import br.com.votify.core.model.user.PasswordReset;
import br.com.votify.core.model.user.User;

import java.util.Optional;

public interface PasswordResetRepository {
    Optional<PasswordReset> findByCode(String code);
    Optional<PasswordReset> findByUser(User user);
    void delete(PasswordReset passwordReset);
    void deleteFromUser(User user);
    PasswordReset save(PasswordReset passwordReset);
}