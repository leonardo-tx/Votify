package br.com.votify.infra.mapping.user;

import br.com.votify.core.model.user.EmailConfirmation;
import br.com.votify.core.model.user.field.ConfirmationCode;
import br.com.votify.core.model.user.field.Email;
import br.com.votify.infra.mapping.Mapper;
import br.com.votify.infra.persistence.user.EmailConfirmationEntity;
import br.com.votify.infra.persistence.user.UserEntity;
import br.com.votify.infra.repository.user.UserEntityRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailConfirmationMapper implements Mapper<EmailConfirmation, EmailConfirmationEntity> {
    private final UserEntityRepository userEntityRepository;

    @Override
    public EmailConfirmation toModel(EmailConfirmationEntity emailConfirmationEntity) {
        String newEmail = emailConfirmationEntity.getNewEmail();
        return EmailConfirmation.parseUnsafe(
                ConfirmationCode.parseUnsafe(emailConfirmationEntity.getCode()),
                newEmail == null ? null : Email.parseUnsafe(newEmail),
                emailConfirmationEntity.getExpiration(),
                emailConfirmationEntity.getUser().getId()
        );
    }

    @Override
    public EmailConfirmationEntity toEntity(EmailConfirmation emailConfirmation) {
        UserEntity userEntity = userEntityRepository.getReferenceById(emailConfirmation.getUserId());
        Email newEmail = emailConfirmation.getNewEmail();
        return EmailConfirmationEntity.builder()
                .code(emailConfirmation.getCode().getValue())
                .expiration(emailConfirmation.getExpiration())
                .user(userEntity)
                .newEmail(newEmail == null ? null : newEmail.getValue())
                .build();
    }
}
