package br.com.votify.infra.mapping.user;

import br.com.votify.core.model.user.RefreshToken;
import br.com.votify.infra.mapping.Mapper;
import br.com.votify.infra.persistence.user.RefreshTokenEntity;
import br.com.votify.infra.persistence.user.UserEntity;
import br.com.votify.infra.repository.user.UserEntityRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class RefreshTokenMapper implements Mapper<RefreshToken, RefreshTokenEntity> {
    private final UserEntityRepository userEntityRepository;

    @Override
    public RefreshToken toModel(RefreshTokenEntity refreshTokenEntity) {
        return RefreshToken.parseUnsafe(
                refreshTokenEntity.getCode(),
                refreshTokenEntity.getExpiration(),
                refreshTokenEntity.getUser().getId()
        );
    }

    @Override
    public RefreshTokenEntity toEntity(RefreshToken refreshToken) {
        UserEntity userEntity = userEntityRepository.getReferenceById(refreshToken.getUserId());
        return RefreshTokenEntity.builder()
                .code(refreshToken.getCode())
                .expiration(refreshToken.getExpiration())
                .user(userEntity)
                .build();
    }
}
