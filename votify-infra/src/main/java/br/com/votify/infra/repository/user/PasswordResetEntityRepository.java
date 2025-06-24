package br.com.votify.infra.repository.user;

import br.com.votify.infra.persistence.user.PasswordResetEntity;
import br.com.votify.infra.persistence.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetEntityRepository extends JpaRepository<PasswordResetEntity, String> {
    Optional<PasswordResetEntity> findByUser(UserEntity user);
}
