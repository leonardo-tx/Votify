package br.com.votify.infra.mapping.user;

import br.com.votify.core.model.user.EmailConfirmation;
import br.com.votify.core.model.user.field.ConfirmationCode;
import br.com.votify.core.model.user.field.Email;
import br.com.votify.infra.mapping.Mapper;
import br.com.votify.infra.persistence.user.EmailConfirmationEntity;
import br.com.votify.infra.persistence.user.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class EmailConfirmationMapper implements Mapper<EmailConfirmation, EmailConfirmationEntity> {
    @Override
    public EmailConfirmation parseToModel(EmailConfirmationEntity emailConfirmationEntity) {
        String newEmail = emailConfirmationEntity.getNewEmail();
        return EmailConfirmation.parseUnsafe(
                ConfirmationCode.parseUnsafe(emailConfirmationEntity.getCode()),
                Email.parseUnsafe(newEmail),
                emailConfirmationEntity.getExpiration(),
                emailConfirmationEntity.getUser().getId()
        );
    }

    @Override
    public EmailConfirmationEntity parseToEntity(EmailConfirmation emailConfirmation) {
        String newEmail = emailConfirmation.getNewEmail().getValue();
        return EmailConfirmationEntity.builder()
                .code(emailConfirmation.getCode().getValue())
                .expiration(emailConfirmation.getExpiration())
                .user(UserEntity.builder().id(emailConfirmation.getUserId()).build())
                .newEmail(newEmail)
                .build();
    }
}
