package br.com.votify.core.repository;

import br.com.votify.core.domain.entities.polls.Vote;
import br.com.votify.core.domain.entities.polls.VoteIdentifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote, VoteIdentifier> {
}
