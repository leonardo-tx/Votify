package br.com.votify.core.repository;

import br.com.votify.core.domain.entities.tokens.EmailConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmailConfirmationRepository extends JpaRepository<EmailConfirmation, Long> {

    @Query("SELECT ec " +
            "FROM EmailConfirmation ec " +
            "JOIN ec.user u " +
            "WHERE u.email = :email")
    Optional<EmailConfirmation> findByUserEmail(@Param("email") String email);
}
