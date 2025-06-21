package br.com.votify.infra.mapping.user;

import br.com.votify.core.model.user.PasswordReset;
import br.com.votify.core.model.user.field.ConfirmationCode;
import br.com.votify.infra.persistence.user.PasswordResetEntity;
import br.com.votify.infra.persistence.user.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordResetMapperTest {
    private PasswordResetMapper passwordResetMapper;

    @BeforeEach
    void setupBeforeEach() {
        passwordResetMapper = new PasswordResetMapper();
    }

    @Test
    void testToModel() {
        UserEntity userEntity = mock(UserEntity.class);
        when(userEntity.getId()).thenReturn(2L);

        PasswordResetEntity passwordResetEntity = mock(PasswordResetEntity.class);
        when(passwordResetEntity.getCode()).thenReturn("code");
        when(passwordResetEntity.getUser()).thenReturn(userEntity);
        when(passwordResetEntity.getExpiration()).thenReturn(Instant.now());

        PasswordReset passwordReset = passwordResetMapper.toModel(passwordResetEntity);
        assertEquals(passwordResetEntity.getCode(), passwordReset.getCode().getValue());
        assertEquals(passwordResetEntity.getUser().getId(), passwordReset.getUserId());
        assertEquals(passwordResetEntity.getExpiration(), passwordReset.getExpiration());
    }

    @Test
    void testToEntity() {
        PasswordReset passwordReset = mock(PasswordReset.class);
        when(passwordReset.getCode()).thenReturn(new ConfirmationCode());
        when(passwordReset.getUserId()).thenReturn(2L);
        when(passwordReset.getExpiration()).thenReturn(Instant.now());

        PasswordResetEntity passwordResetEntity = passwordResetMapper.toEntity(passwordReset);
        assertEquals(passwordReset.getCode().getValue(), passwordResetEntity.getCode());
        assertEquals(passwordReset.getUserId(), passwordResetEntity.getUser().getId());
        assertEquals(passwordReset.getExpiration(), passwordResetEntity.getExpiration());
    }
}
