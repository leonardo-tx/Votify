package br.com.votify.core.service;

import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.repository.RefreshTokenRepository;
import br.com.votify.core.repository.UserRepository;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

@Service
@RequiredArgsConstructor
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserContextService {
    private final PasswordEncoderService passwordEncoderService;
    private final UserRepository userRepository;
    private final TokenService tokenService;

    public String login(String email, String password, String deviceId) throws VotifyException {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null || !passwordEncoderService.checkPassword(user, password)) {
            throw new VotifyException(VotifyErrorCode.LOGIN_UNAUTHORIZED);
        }
        return tokenService.createRefreshToken(user, deviceId);
    }
}
