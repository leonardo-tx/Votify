package br.com.votify.core.repository;

import br.com.votify.core.domain.entities.polls.Vote;
import br.com.votify.core.domain.entities.polls.VoteIdentifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote, VoteIdentifier> {

    @Query("""
        SELECT v
        FROM Vote v
        WHERE v.poll.id = :pollId
        """)
    Page<Vote> findByPollId(@Param("pollId") Long pollId , Pageable pageable);
}
