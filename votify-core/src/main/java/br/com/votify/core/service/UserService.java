package br.com.votify.core.service;

import br.com.votify.core.domain.entities.tokens.AuthTokens;
import br.com.votify.core.domain.entities.tokens.EmailConfirmation;
import br.com.votify.core.domain.entities.tokens.RefreshToken;
import br.com.votify.core.domain.entities.users.CommonUser;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.utils.validators.UserValidator;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.core.repository.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    @Getter
    private final ContextService context;
    private final PasswordEncoderService passwordEncoderService;
    private final UserRepository userRepository;
    private final EmailConfirmationService emailConfirmationService;
    private final TokenService tokenService;

    @Transactional
    public User register(CommonUser user) throws VotifyException {
        UserValidator.validateFields(user);
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new VotifyException(VotifyErrorCode.EMAIL_ALREADY_EXISTS);
        }
        if (userRepository.existsByUserName(user.getUserName())) {
            throw new VotifyException(VotifyErrorCode.USER_NAME_ALREADY_EXISTS);
        }
        String encryptedPassword = passwordEncoderService.encryptPassword(user.getPassword());
        user.setPassword(encryptedPassword);
        user.setId(null);

        User createdUser = userRepository.save(user);
        EmailConfirmation emailConfirmation = emailConfirmationService.addUser(createdUser, null);

        createdUser.setEmailConfirmation(emailConfirmation);
        return createdUser;
    }

    public AuthTokens login(String email, String password) throws VotifyException {
        if (context.isAuthenticated()) {
            throw new VotifyException(VotifyErrorCode.LOGIN_ALREADY_LOGGED);
        }

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null || !passwordEncoderService.checkPassword(user, password)) {
            throw new VotifyException(VotifyErrorCode.LOGIN_UNAUTHORIZED);
        }
        //if (user.getEmailConfirmation() != null && user.getEmailConfirmation().getNewEmail() == null) {
            //throw new VotifyException(VotifyErrorCode.PENDING_EMAIL_CONFIRMATION);
        //}
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
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new VotifyException(VotifyErrorCode.USER_NOT_FOUND);
        }
        return optionalUser.get();
    }

    @Transactional
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    @Transactional
    public User updateUserInfo(String name, String userName) throws VotifyException {
        User user = context.getUserOrThrow();

        if (name != null && !name.isBlank()) {
            UserValidator.validateName(name);
            user.setName(name);
        }
        if (userName != null && !userName.isBlank()) {
            UserValidator.validateUserName(userName);
            if (!user.getUserName().equals(userName) && userRepository.existsByUserName(userName)) {
                throw new VotifyException(VotifyErrorCode.USER_NAME_ALREADY_EXISTS);
            }
            user.setUserName(userName);
        }
        return userRepository.save(user);
    }

    public void updateUserPassword(String oldPassword, String newPassword) throws VotifyException {
        User user = context.getUserOrThrow();

        if (!passwordEncoderService.checkPassword(user, oldPassword)) {
            throw new VotifyException(VotifyErrorCode.INVALID_OLD_PASSWORD);
        }

        UserValidator.validatePassword(newPassword);
        String encryptedNewPassword = passwordEncoderService.encryptPassword(newPassword);
        user.setPassword(encryptedNewPassword);

        userRepository.save(user);
    }

    public User updateUserEmail(String email) throws VotifyException {
        User user = context.getUserOrThrow();
        if (user.getEmail().equals(email)) {
            return user;
        }

        UserValidator.validateEmail(email);
        if (userRepository.existsByEmail(email)) {
            throw new VotifyException(VotifyErrorCode.EMAIL_ALREADY_EXISTS);
        }

        EmailConfirmation emailConfirmation = emailConfirmationService.addUser(user, email);
        user.setEmailConfirmation(emailConfirmation);

        return user;
    }
}
