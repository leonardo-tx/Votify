package br.com.votify.infra.repository.poll;

import br.com.votify.infra.persistence.poll.VoteEntity;
import br.com.votify.infra.persistence.poll.VoteIdentifier;
import br.com.votify.infra.persistence.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VoteEntityRepository extends JpaRepository<VoteEntity, VoteIdentifier> {
    List<VoteEntity> findAllByUser(UserEntity user);

    @Query("DELETE FROM Vote v WHERE v.user.id = :userId")
    @Modifying(clearAutomatically = true)
    void deleteAllByUserById(@Param("userId") Long id);
}
