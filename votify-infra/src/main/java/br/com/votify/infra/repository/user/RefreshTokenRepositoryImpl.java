package br.com.votify.infra.repository.user;

import br.com.votify.core.model.user.RefreshToken;
import br.com.votify.core.model.user.User;
import br.com.votify.core.repository.user.RefreshTokenRepository;
import br.com.votify.infra.mapping.user.RefreshTokenMapper;
import br.com.votify.infra.persistence.user.RefreshTokenEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {
    private final RefreshTokenEntityRepository repository;
    private final RefreshTokenMapper mapper;

    @Override
    public void deleteAllByUser(User user) {
        repository.deleteAllByUserById(user.getId());
    }

    @Override
    public boolean existsByCode(String code) {
        return repository.existsById(code);
    }

    @Override
    public Optional<RefreshToken> findByCode(String code) {
        Optional<RefreshTokenEntity> entityOptional = repository.findById(code);
        return entityOptional.map(mapper::toModel);
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        RefreshTokenEntity entity = mapper.toEntity(refreshToken);
        RefreshTokenEntity createdEntity = repository.save(entity);

        return mapper.toModel(createdEntity);
    }

    @Override
    public void deleteByCode(String code) {
        repository.deleteById(code);
    }
}
