package br.com.votify.infra.mapping.user;

import br.com.votify.core.model.user.RefreshToken;
import br.com.votify.core.model.user.User;
import br.com.votify.infra.persistence.user.RefreshTokenEntity;
import br.com.votify.infra.persistence.user.UserEntity;
import br.com.votify.infra.repository.user.UserEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenMapperTest {
    private RefreshTokenMapper refreshTokenMapper;

    @BeforeEach
    void setupBeforeEach() {
        refreshTokenMapper = new RefreshTokenMapper();
    }

    @Test
    void testToModel() {
        UserEntity userEntity = mock(UserEntity.class);
        when(userEntity.getId()).thenReturn(2L);

        RefreshTokenEntity refreshTokenEntity = mock(RefreshTokenEntity.class);
        when(refreshTokenEntity.getCode()).thenReturn("code");
        when(refreshTokenEntity.getUser()).thenReturn(userEntity);
        when(refreshTokenEntity.getExpiration()).thenReturn(Instant.now());

        RefreshToken refreshToken = refreshTokenMapper.toModel(refreshTokenEntity);
        assertEquals(refreshTokenEntity.getCode(), refreshToken.getCode());
        assertEquals(refreshTokenEntity.getUser().getId(), refreshToken.getUserId());
        assertEquals(refreshTokenEntity.getExpiration(), refreshToken.getExpiration());
    }

    @Test
    void testToEntity() {
        RefreshToken refreshToken = mock(RefreshToken.class);
        when(refreshToken.getCode()).thenReturn("code");
        when(refreshToken.getUserId()).thenReturn(2L);
        when(refreshToken.getExpiration()).thenReturn(Instant.now());

        RefreshTokenEntity refreshTokenEntity = refreshTokenMapper.toEntity(refreshToken);
        assertEquals(refreshToken.getCode(), refreshTokenEntity.getCode());
        assertEquals(refreshToken.getUserId(), refreshTokenEntity.getUser().getId());
        assertEquals(refreshToken.getExpiration(), refreshTokenEntity.getExpiration());
    }
}
