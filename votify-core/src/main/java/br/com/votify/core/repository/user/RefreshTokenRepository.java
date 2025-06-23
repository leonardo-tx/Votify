package br.com.votify.core.repository.user;

import br.com.votify.core.model.user.RefreshToken;
import br.com.votify.core.model.user.User;

import java.util.Optional;

public interface RefreshTokenRepository {
    void deleteAllByUser(User user);
    boolean existsByCode(String code);
    Optional<RefreshToken> findByCode(String code);
    RefreshToken save(RefreshToken refreshToken);
    void deleteByCode(String code);
}
