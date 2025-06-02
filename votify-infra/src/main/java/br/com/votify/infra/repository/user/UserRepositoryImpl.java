package br.com.votify.infra.repository.user;

import br.com.votify.core.model.user.User;
import br.com.votify.core.model.user.field.Email;
import br.com.votify.core.model.user.field.UserName;
import br.com.votify.core.repository.user.UserRepository;
import br.com.votify.infra.mapping.user.UserMapper;
import br.com.votify.infra.persistence.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final UserEntityRepository repository;
    private final UserMapper userMapper;

    @Override
    public boolean existsByEmail(Email email) {
        return repository.existsByEmail(email.getValue());
    }

    @Override
    public boolean existsByUserName(UserName userName) {
        return repository.existsByUserName(userName.getValue());
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        Optional<UserEntity> entity = repository.findByEmail(email.getValue());
        return entity.map(userMapper::toModel);
    }

    @Override
    public Optional<User> findById(Long id) {
        Optional<UserEntity> entity = repository.findById(id);
        return entity.map(userMapper::toModel);
    }

    @Override
    public Optional<User> findByUserName(UserName userName) {
        Optional<UserEntity> entity = repository.findByUserName(userName.getValue());
        return entity.map(userMapper::toModel);
    }

    @Override
    public User save(User user) {
        UserEntity entity = userMapper.toEntity(user);
        UserEntity createdEntity = repository.save(entity);

        return userMapper.toModel(createdEntity);
    }

    @Override
    public void delete(User user) {
        repository.deleteById(user.getId());
    }
}
