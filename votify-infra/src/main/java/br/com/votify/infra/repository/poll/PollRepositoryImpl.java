package br.com.votify.infra.repository.poll;

import br.com.votify.core.model.poll.Poll;
import br.com.votify.core.model.user.User;
import br.com.votify.core.repository.poll.PollRepository;
import br.com.votify.infra.mapping.poll.PollMapper;
import br.com.votify.infra.persistence.poll.PollEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PollRepositoryImpl implements PollRepository {
    private final PollMapper mapper;
    private final PollEntityRepository repository;

    @Override
    public Page<Poll> findAllByResponsible(User user, Pageable pageable) {
        Page<PollEntity> entityPage = repository.findAllByResponsibleId(user.getId(), pageable);
        return entityPage.map(mapper::toModel);
    }

    @Override
    public List<Poll> findAllByResponsible(User user) {
        List<PollEntity> entities = repository.findAllByResponsibleId(user.getId());
        return entities.stream().map(mapper::toModel).toList();
    }

    @Override
    public Page<Poll> findByTitleContainingIgnoreCase(String titleSearch, Instant now, Pageable pageable) {
        Page<PollEntity> entityPage = repository.findByTitleContainingIgnoreCase(
                titleSearch,
                now,
                pageable
        );
        return entityPage.map(mapper::toModel);
    }

    @Override
    public Page<Poll> findAllByActives(Instant now, Pageable pageable) {
        Page<PollEntity> entityPage = repository.findAllByActives(now, pageable);
        return entityPage.map(mapper::toModel);
    }

    @Override
    public Poll save(Poll poll) {
        PollEntity entity = mapper.toEntity(poll);
        PollEntity createdEntity = repository.save(entity);

        return mapper.toModel(createdEntity);
    }

    @Override
    public Optional<Poll> findById(Long id) {
        Optional<PollEntity> entityOptional = repository.findById(id);
        return entityOptional.map(mapper::toModel);
    }

    @Override
    public void delete(Poll poll) {
        repository.deleteById(poll.getId());
    }
}
