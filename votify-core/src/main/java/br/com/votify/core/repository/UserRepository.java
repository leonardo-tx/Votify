package br.com.votify.core.repository;

import br.com.votify.core.domain.entities.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByUserName(String userName);
    Optional<User> findByEmail(String email);
    Optional<User> findByUserName(String userName);
}
