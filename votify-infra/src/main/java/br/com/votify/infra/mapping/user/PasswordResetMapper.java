package br.com.votify.infra.mapping.user;

import br.com.votify.core.model.user.PasswordReset;
import br.com.votify.core.model.user.field.ConfirmationCode;
import br.com.votify.infra.mapping.Mapper;
import br.com.votify.infra.persistence.user.PasswordResetEntity;
import br.com.votify.infra.persistence.user.UserEntity;
import org.springframework.stereotype.Component;

@Component
public final class PasswordResetMapper implements Mapper<PasswordReset, PasswordResetEntity> {
    @Override
    public PasswordReset toModel(PasswordResetEntity passwordResetEntity) {
        ConfirmationCode code = ConfirmationCode.parseUnsafe(passwordResetEntity.getCode());
        return PasswordReset.parseUnsafe(
                code,
                passwordResetEntity.getUser().getId(),
                passwordResetEntity.getExpiration()
        );
    }

    @Override
    public PasswordResetEntity toEntity(PasswordReset passwordReset) {
        return PasswordResetEntity.builder()
                .code(passwordReset.getCode().getValue())
                .expiration(passwordReset.getExpiration())
                .user(UserEntity.builder().id(passwordReset.getUserId()).build())
                .build();
    }
}
