package br.com.votify.infra.repository.user;

import br.com.votify.core.model.user.EmailConfirmation;
import br.com.votify.core.model.user.field.Email;
import br.com.votify.core.repository.user.EmailConfirmationRepository;
import br.com.votify.infra.mapping.user.EmailConfirmationMapper;
import br.com.votify.infra.persistence.user.EmailConfirmationEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EmailConfirmationRepositoryImpl implements EmailConfirmationRepository {
    private final EmailConfirmationMapper mapper;
    private final EmailConfirmationEntityRepository repository;

    @Override
    public Optional<EmailConfirmation> findByUserEmail(Email email) {
        return repository.findByUserEmail(email.getValue())
                .map(mapper::toModel);
    }

    @Override
    public boolean existsByUserEmail(Email email) {
        return repository.existsByUserEmail(email.getValue());
    }

    @Override
    public List<EmailConfirmation> findAllExpired(Instant now) {
        List<EmailConfirmationEntity> entity = repository.findAllExpired(now);
        return entity.stream()
                .map(mapper::toModel)
                .toList();
    }

    @Override
    public void delete(EmailConfirmation emailConfirmation) {
        repository.deleteById(emailConfirmation.getCode().getValue());
    }

    @Override
    public EmailConfirmation save(EmailConfirmation emailConfirmation) {
        EmailConfirmationEntity entity = mapper.toEntity(emailConfirmation);
        EmailConfirmationEntity createdEntity = repository.save(entity);

        return mapper.toModel(createdEntity);
    }
}
