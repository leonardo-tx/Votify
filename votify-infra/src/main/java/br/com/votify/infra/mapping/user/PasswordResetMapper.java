package br.com.votify.infra.mapping.user;

import br.com.votify.core.model.user.PasswordReset;
import br.com.votify.core.model.user.field.ConfirmationCode;
import br.com.votify.core.repository.user.UserRepository;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.infra.mapping.Mapper;
import br.com.votify.infra.persistence.user.PasswordResetEntity;
import br.com.votify.infra.persistence.user.UserEntity;
import br.com.votify.infra.repository.user.UserEntityRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class PasswordResetMapper implements Mapper<PasswordReset, PasswordResetEntity> {
    private final UserEntityRepository userEntityRepository;

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
        System.out.println(passwordReset.getUserId());
        UserEntity userEntity = userEntityRepository.getReferenceById(passwordReset.getUserId());
        System.out.println("UserEntity ID: " + userEntity.getId());
        return PasswordResetEntity.builder()
                .code(passwordReset.getCode().getValue())
                .expiration(passwordReset.getExpiration())
                .user(userEntity)
                .build();
    }
}
