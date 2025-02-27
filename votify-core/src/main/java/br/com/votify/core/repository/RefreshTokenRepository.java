package br.com.votify.core.repository;

import br.com.votify.core.domain.entities.tokens.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String>  {
}
