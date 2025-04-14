package br.com.votify.core.repository;

import br.com.votify.core.domain.entities.tokens.EmailConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EmailConfirmationRepository extends JpaRepository<EmailConfirmation, Long> {

    @Query("""
        SELECT ec
        FROM EmailConfirmation ec
        JOIN ec.user u
        WHERE u.email = :email
        """)
    Optional<EmailConfirmation> findByUserEmail(@Param("email") String email);

    @Query("""
        SELECT CASE
            WHEN EXISTS (
                SELECT 1 FROM EmailConfirmation ec
                WHERE ec.user.email = :email
            )
            THEN true ELSE false
        END
        """)
    boolean existsByUserEmail(@Param("email") String email);

    @Query("""
        SELECT ec
        FROM EmailConfirmation ec
        WHERE ec.emailConfirmationExpiration < :now
        """)
    List<EmailConfirmation> findAllExpirated(@Param("now") LocalDateTime now);
}
