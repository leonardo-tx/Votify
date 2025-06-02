package br.com.votify.infra.mapping.user;

import br.com.votify.core.model.user.User;
import br.com.votify.core.model.user.field.Email;
import br.com.votify.core.model.user.field.Name;
import br.com.votify.core.model.user.field.UserName;
import br.com.votify.infra.mapping.Mapper;
import br.com.votify.infra.persistence.user.EmailConfirmationEntity;
import br.com.votify.infra.persistence.user.UserEntity;
import org.springframework.stereotype.Component;

@Component
public final class UserMapper implements Mapper<User, UserEntity> {
    @Override
    public User toModel(UserEntity userEntity) {
        EmailConfirmationEntity emailConfirmation = userEntity.getEmailConfirmation();
        boolean active = emailConfirmation == null || emailConfirmation.getNewEmail() != null;
        return User.parseUnsafe(
                userEntity.getId(),
                Email.parseUnsafe(userEntity.getEmail()),
                UserName.parseUnsafe(userEntity.getUserName()),
                Name.parseUnsafe(userEntity.getName()),
                userEntity.getPassword(),
                userEntity.getRole(),
                active
        );
    }

    @Override
    public UserEntity toEntity(User user) {
        return UserEntity.builder()
            .id(user.getId())
            .userName(user.getUserName().getValue())
            .name(user.getName().getValue())
            .email(user.getEmail().getValue())
            .password(user.getEncryptedPassword())
            .role(user.getRole())
            .build();
    }
}
