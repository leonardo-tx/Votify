package br.com.votify.core.repository;

import br.com.votify.core.domain.entities.polls.Poll;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    
    @Query("""
        SELECT p
        FROM Poll p
        INNER JOIN p.responsible u
        WHERE u.id = :userId
        ORDER BY p.startDate DESC
        """)
    Page<Poll> findAllByResponsibleId(@Param("userId") Long userId, Pageable pageable);
}
