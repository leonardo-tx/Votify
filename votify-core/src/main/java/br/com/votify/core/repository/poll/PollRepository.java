package br.com.votify.core.repository.poll;

import br.com.votify.core.model.poll.Poll;
import br.com.votify.core.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface PollRepository {
    Page<Poll> findAllByResponsible(User user, Pageable pageable);
    List<Poll> findAllByResponsible(User user);
    Page<Poll> findByTitleContainingIgnoreCase(String titleSearch, Instant now, Pageable pageable);
    Page<Poll> findAllByActives(Instant now, Pageable pageable);
    Poll save(Poll poll);
    Optional<Poll> findById(Long id);
    void delete(Poll poll);
}
