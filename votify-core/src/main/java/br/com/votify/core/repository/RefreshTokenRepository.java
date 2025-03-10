package br.com.votify.core.repository;

import br.com.votify.core.domain.entities.tokens.RefreshToken;
import br.com.votify.core.domain.entities.users.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    List<RefreshToken> findAllByUser(User user);
}
