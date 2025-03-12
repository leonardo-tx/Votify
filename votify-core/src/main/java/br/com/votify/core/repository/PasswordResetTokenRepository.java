package br.com.votify.core.repository;

import br.com.votify.core.domain.entities.password.PasswordResetToken;
import br.com.votify.core.domain.entities.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, String> {
    Optional<PasswordResetToken> findByCode(String code);
    List<PasswordResetToken> findByUser(User user);
    List<PasswordResetToken> findByUserAndExpiryDateAfter(User user, Date date);
}