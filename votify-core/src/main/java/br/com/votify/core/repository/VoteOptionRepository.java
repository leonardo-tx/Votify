package br.com.votify.core.repository;

import br.com.votify.core.domain.entities.vote.VoteOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteOptionRepository extends JpaRepository<VoteOption, Long> {
}
