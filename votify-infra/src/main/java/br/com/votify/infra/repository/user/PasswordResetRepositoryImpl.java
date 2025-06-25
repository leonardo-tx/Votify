package br.com.votify.infra.repository.user;

import br.com.votify.core.model.user.PasswordReset;
import br.com.votify.core.model.user.User;
import br.com.votify.core.repository.user.PasswordResetRepository;
import br.com.votify.infra.mapping.user.PasswordResetMapper;
import br.com.votify.infra.persistence.user.PasswordResetEntity;
import br.com.votify.infra.persistence.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PasswordResetRepositoryImpl implements PasswordResetRepository {
    private final PasswordResetEntityRepository repository;
    private final PasswordResetMapper mapper;

    @Override
    public Optional<PasswordReset> findByCode(String code) {
        Optional<PasswordResetEntity> entityOptional = repository.findById(code);
        return entityOptional.map(mapper::toModel);
    }

    @Override
    public Optional<PasswordReset> findByUser(User user) {
        UserEntity userEntity = UserEntity.builder().id(user.getId()).build();
        Optional<PasswordResetEntity> entityOptional = repository.findByUser(userEntity);

        return entityOptional.map(mapper::toModel);
    }

    @Override
    public void delete(PasswordReset passwordReset) {
        PasswordResetEntity entity = mapper.toEntity(passwordReset);
        repository.delete(entity);
    }

    @Override
    public void deleteFromUser(User user) {
        repository.deleteByUserId(user.getId());
    }

    @Override
    public PasswordReset save(PasswordReset passwordReset) {
        PasswordResetEntity entity = mapper.toEntity(passwordReset);
        PasswordResetEntity createdEntity = repository.save(entity);

        return mapper.toModel(createdEntity);
    }
}
