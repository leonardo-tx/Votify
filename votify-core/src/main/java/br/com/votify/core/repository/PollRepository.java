package br.com.votify.core.repository;

import br.com.votify.core.domain.entities.poll.Poll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PollRepository extends JpaRepository<Poll, Long> {

    @Query("""
        SELECT COUNT(p) > 0
        FROM Poll p
        INNER JOIN p.responsible u
        WHERE u.id = :userId
        AND p.title = :title
        """)
    boolean existsByTitleAndResponsibleId(@Param("title") String title, @Param("userId") Long userId);
}
