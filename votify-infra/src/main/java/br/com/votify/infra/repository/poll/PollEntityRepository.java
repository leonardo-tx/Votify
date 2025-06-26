package br.com.votify.infra.repository.poll;

import br.com.votify.infra.persistence.poll.PollEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface PollEntityRepository extends JpaRepository<PollEntity, Long> {
    @Query("""
        SELECT p
        FROM Poll p
        INNER JOIN p.responsible u
        WHERE u.id = :userId
        ORDER BY p.startDate DESC
        """)
    Page<PollEntity> findAllByResponsibleId(@Param("userId") Long userId, Pageable pageable);

    @Query("""
        SELECT p
        FROM Poll p
        INNER JOIN p.responsible u
        WHERE u.id = :userId
        ORDER BY p.startDate DESC
        """)
    List<PollEntity> findAllByResponsibleId(@Param("userId") Long userId);

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
    Page<PollEntity> findByTitleContainingIgnoreCase(@Param("title") String title, @Param("now") Instant now, Pageable pageable);

    @Query("""
        SELECT p
        FROM Poll p
        WHERE p.startDate <= :now
        AND p.endDate > :now
        ORDER BY p.endDate ASC
        """)
    Page<PollEntity> findAllByActives(@Param("now") Instant now, Pageable pageable);
}
