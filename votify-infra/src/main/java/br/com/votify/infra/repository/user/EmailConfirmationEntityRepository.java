package br.com.votify.infra.repository.user;

import br.com.votify.infra.persistence.user.EmailConfirmationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface EmailConfirmationEntityRepository extends JpaRepository<EmailConfirmationEntity, String> {
    @Query("""
        SELECT ec
        FROM EmailConfirmation ec
        JOIN ec.user u
        WHERE u.email = :email
        """)
    Optional<EmailConfirmationEntity> findByUserEmail(@Param("email") String email);

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
        WHERE ec.expiration < :now
        """)
    List<EmailConfirmationEntity> findAllExpired(@Param("now") Instant now);

    @Modifying
    @Query("""
        DELETE FROM EmailConfirmation ec
        WHERE ec.user.id = :userId
        """)
    void deleteByUserId(@Param("userId") Long userId);

}
