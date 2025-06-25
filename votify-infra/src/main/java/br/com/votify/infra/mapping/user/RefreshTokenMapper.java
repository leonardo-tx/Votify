package br.com.votify.infra.mapping.user;

import br.com.votify.core.model.user.RefreshToken;
import br.com.votify.infra.mapping.Mapper;
import br.com.votify.infra.persistence.user.RefreshTokenEntity;
import br.com.votify.infra.persistence.user.UserEntity;
import org.springframework.stereotype.Component;

@Component
public final class RefreshTokenMapper implements Mapper<RefreshToken, RefreshTokenEntity> {
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
        return RefreshTokenEntity.builder()
                .code(refreshToken.getCode())
                .expiration(refreshToken.getExpiration())
                .user(UserEntity.builder().id(refreshToken.getUserId()).build())
                .build();
    }
}
