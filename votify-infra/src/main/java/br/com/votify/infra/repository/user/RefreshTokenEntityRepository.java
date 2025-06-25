package br.com.votify.infra.repository.user;

import br.com.votify.infra.persistence.user.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RefreshTokenEntityRepository extends JpaRepository<RefreshTokenEntity, String> {
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.user.id = :userId")
    void deleteAllByUserById(@Param("userId") Long id);
}
