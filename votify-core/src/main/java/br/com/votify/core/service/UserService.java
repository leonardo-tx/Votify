package br.com.votify.core.service;

import br.com.votify.core.domain.entities.tokens.AuthTokens;
import br.com.votify.core.domain.entities.tokens.RefreshToken;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.utils.validators.UserValidator;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.core.repository.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserService {
    @Getter
    private final ContextService context;
    private final PasswordEncoderService passwordEncoderService;
    private final UserRepository userRepository;
    private final TokenService tokenService;

    public User createUser(User user) throws VotifyException {
        UserValidator.validateFields(user);
        user.setId(null);

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new VotifyException(VotifyErrorCode.EMAIL_ALREADY_EXISTS);
        }
        if (userRepository.existsByUserName(user.getUserName())) {
            throw new VotifyException(VotifyErrorCode.USER_NAME_ALREADY_EXISTS);
        }
        String encryptedPassword = passwordEncoderService.encryptPassword(user.getPassword());
        user.setPassword(encryptedPassword);

        return userRepository.save(user);
    }

    public AuthTokens login(String email, String password) throws VotifyException {
        if (context.isAuthenticated()) {
            throw new VotifyException(VotifyErrorCode.LOGIN_ALREADY_LOGGED);
        }
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null || !passwordEncoderService.checkPassword(user, password)) {
            throw new VotifyException(VotifyErrorCode.LOGIN_UNAUTHORIZED);
        }
        RefreshToken refreshToken = tokenService.createRefreshToken(user);
        String accessToken = tokenService.createAccessToken(refreshToken);

        return new AuthTokens(accessToken, refreshToken);
    }

    public void logout() {
        String refreshToken = context.getCookieValueOrDefault("refresh_token", null);
        if (!context.isAuthenticated() || refreshToken == null) return;

        tokenService.deleteRefreshTokenById(refreshToken);
    }

    public User getUserById(long id) throws VotifyException {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            throw new VotifyException(VotifyErrorCode.USER_NOT_FOUND);
        }
        return user;
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Transactional
    public void deleteUser(Long userId) throws VotifyException {
        User user = getUserById(userId);
        
        User currentUser = context.getUserOrThrow();
        if (!currentUser.getId().equals(userId)) {
            throw new VotifyException(VotifyErrorCode.USER_DELETE_UNAUTHORIZED);
        }
        userRepository.delete(user);
    }
}
