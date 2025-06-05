package br.com.votify.core.repository.poll;

import br.com.votify.core.model.poll.Poll;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface PollRepository {
    Page<Poll> findAllByResponsibleId(Long userId, Pageable pageable);
    List<Poll> findAllByResponsibleId(Long userId);
    Page<Poll> findByTitleContainingIgnoreCase(String titleSearch, Instant now, Pageable pageable);
    Page<Poll> findAllByActives(Instant now, Pageable pageable);
    Poll save(Poll poll);
    Optional<Poll> findById(Long id);
    void delete(Poll poll);
}
