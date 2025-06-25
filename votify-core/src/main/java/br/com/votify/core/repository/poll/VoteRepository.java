package br.com.votify.core.repository.poll;

import br.com.votify.core.model.poll.Poll;
import br.com.votify.core.model.poll.Vote;
import br.com.votify.core.model.user.User;

import java.util.List;
import java.util.Optional;

public interface VoteRepository {
    boolean exists(Vote vote);
    Vote save(Vote vote);
    Optional<Vote> findByPollAndUser(Poll poll, User user);
    List<Vote> findAllFromUser(User user);
    void deleteAllByUser(User user);
}
