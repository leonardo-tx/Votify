package br.com.votify.core.repository.user;

import br.com.votify.core.model.user.User;
import br.com.votify.core.model.user.field.Email;
import br.com.votify.core.model.user.field.UserName;

import java.util.Optional;

public interface UserRepository {
    boolean existsByEmail(Email email);
    boolean existsByUserName(UserName userName);
    Optional<User> findByEmail(Email email);
    Optional<User> findById(Long id);
    Optional<User> findByUserName(UserName userName);
    User save(User user);
    void delete(User user);
}
