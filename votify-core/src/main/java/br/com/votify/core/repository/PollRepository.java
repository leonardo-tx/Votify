package br.com.votify.core.repository;

import br.com.votify.core.domain.entities.polls.Poll;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface PollRepository extends JpaRepository<Poll, Long> {
    @Query("""
        SELECT p
        FROM Poll p
        INNER JOIN p.responsible u
        WHERE u.id = :userId
        ORDER BY p.startDate DESC
        """)
    Page<Poll> findAllByResponsibleId(@Param("userId") Long userId, Pageable pageable);

    @Query("""
        SELECT p
        FROM Poll p
        WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :title, '%'))
        ORDER BY
            CASE
                WHEN p.startDate > :now THEN 1
                WHEN p.endDate > :now THEN 0
                ELSE 2
            END,
            p.endDate
        ASC
        """)
    Page<Poll> findByTitleContainingIgnoreCase(@Param("title") String title, @Param("now") Instant now, Pageable pageable);

    @Query("""
        SELECT p
        FROM Poll p
        WHERE p.startDate <= :now
        AND p.endDate > :now
        ORDER BY p.endDate ASC
        """)
    Page<Poll> findAllByActives(@Param("now") Instant now, Pageable pageable);
}
