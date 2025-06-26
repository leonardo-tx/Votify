package br.com.votify.infra.repository.poll;

import br.com.votify.core.model.poll.Poll;
import br.com.votify.core.model.poll.Vote;
import br.com.votify.core.model.user.User;
import br.com.votify.core.repository.poll.VoteRepository;
import br.com.votify.infra.mapping.poll.VoteMapper;
import br.com.votify.infra.persistence.poll.VoteEntity;
import br.com.votify.infra.persistence.poll.VoteIdentifier;
import br.com.votify.infra.persistence.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class VoteRepositoryImpl implements VoteRepository {
    private final VoteEntityRepository repository;
    private final VoteMapper mapper;

    @Override
    public boolean exists(Vote vote) {
        VoteIdentifier voteIdentifier = new VoteIdentifier(vote.getPollId(), vote.getUserId());
        return repository.existsById(voteIdentifier);
    }

    @Override
    public Vote save(Vote vote) {
        VoteEntity entity = mapper.toEntity(vote);
        VoteEntity createdEntity = repository.save(entity);

        return mapper.toModel(createdEntity);
    }

    @Override
    public Optional<Vote> findByPollAndUser(Poll poll, User user) {
        VoteIdentifier voteIdentifier = new VoteIdentifier(poll.getId(), user.getId());
        Optional<VoteEntity> optionalEntity = repository.findById(voteIdentifier);

        return optionalEntity.map(mapper::toModel);
    }

    @Override
    public List<Vote> findAllFromUser(User user) {
        List<VoteEntity> entities = repository.findAllByUser(UserEntity.builder().id(user.getId()).build());
        return entities.stream().map(mapper::toModel).toList();
    }

    @Override
    public void deleteAllByUser(User user) {
        repository.deleteAllByUserById(user.getId());
    }
}
