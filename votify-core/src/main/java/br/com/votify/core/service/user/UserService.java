package br.com.votify.core.service.user;

import br.com.votify.core.model.user.*;
import br.com.votify.core.model.user.field.Email;
import br.com.votify.core.model.user.field.Name;
import br.com.votify.core.model.user.field.Password;
import br.com.votify.core.model.user.field.UserName;
import br.com.votify.core.properties.user.UserProperties;
import br.com.votify.core.service.poll.PollService;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.core.repository.user.UserRepository;
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
    private final PollService pollService;
    private final EmailConfirmationService emailConfirmationService;
    private final PasswordResetService passwordResetService;
    private final TokenService tokenService;
    private final UserProperties userProperties;

    @Transactional
    public User register(UserRegister userRegister) throws VotifyException {
        if (userRepository.existsByEmail(userRegister.getEmail())) {
            throw new VotifyException(VotifyErrorCode.EMAIL_ALREADY_EXISTS);
        }
        if (userRepository.existsByUserName(userRegister.getUserName())) {
            throw new VotifyException(VotifyErrorCode.USER_NAME_ALREADY_EXISTS);
        }
        User user = new User(passwordEncoderService, userRegister);
        User createdUser = userRepository.save(user);

        emailConfirmationService.addEmailConfirmation(createdUser, null);
        return User.parseUnsafe(
                createdUser.getId(),
                createdUser.getEmail(),
                createdUser.getUserName(),
                createdUser.getName(),
                createdUser.getEncryptedPassword(),
                createdUser.getRole(),
                false
        );
    }

    public AuthTokens login(Email email, Password password) throws VotifyException {
        if (context.isAuthenticated()) {
            throw new VotifyException(VotifyErrorCode.LOGIN_ALREADY_LOGGED);
        }
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null || !passwordEncoderService.checkPassword(user, password)) {
            throw new VotifyException(VotifyErrorCode.LOGIN_UNAUTHORIZED);
        }
        if (!user.isActive()) {
            throw new VotifyException(VotifyErrorCode.PENDING_EMAIL_CONFIRMATION);
        }
        RefreshToken refreshToken = tokenService.createRefreshToken(user);
        AccessToken accessToken = tokenService.createAccessToken(refreshToken);

        return new AuthTokens(accessToken, refreshToken);
    }

    @Transactional
    public void resetPassword(String code, Password newPassword) throws VotifyException {
        PasswordReset passwordReset = passwordResetService.resetPassword(code);
        Optional<User> optionalUser = userRepository.findById(passwordReset.getUserId());
        if (optionalUser.isEmpty()) {
            throw new VotifyException(VotifyErrorCode.USER_NOT_FOUND);
        }
        User user = optionalUser.get();
        user.setPassword(passwordEncoderService, newPassword);

        userRepository.save(user);
    }

    public void logout() {
        String refreshToken = context.getCookieValueOrDefault(
                userProperties.getRefreshTokenCookieName(),
                null
        );
        if (!context.isAuthenticated() || refreshToken == null) return;

        tokenService.deleteRefreshToken(refreshToken);
    }

    @Transactional
    public void confirmEmail(String code, Email currentEmail) throws VotifyException {
        User user = null;
        if (currentEmail == null) {
            user = context.getUserOrThrow();
            currentEmail = user.getEmail();
        }
        EmailConfirmation emailConfirmation = emailConfirmationService.confirmEmail(code, currentEmail);
        if (emailConfirmation.getNewEmail() == null) return;
        if (user == null) {
            user = userRepository.findById(emailConfirmation.getUserId())
                    .orElseThrow(() -> new VotifyException(VotifyErrorCode.USER_NOT_FOUND));
        }
        user.setEmail(emailConfirmation.getNewEmail());
        userRepository.save(user);
    }

    @Transactional
    public void delete(User user) {
        tokenService.revokeAllRefreshTokens(user);
        pollService.deletePollInfoFromUser(user);
        passwordResetService.deleteFromUser(user);
        emailConfirmationService.deleteFromUser(user);
        userRepository.delete(user);
    }

    @Transactional
    public User updateUserInfo(Name name, UserName userName) throws VotifyException {
        User user = context.getUserOrThrow();
        if (name != null) {
            user.setName(name);
        }
        if (userName != null) {
            if (!user.getUserName().equals(userName) && userRepository.existsByUserName(userName)) {
                throw new VotifyException(VotifyErrorCode.USER_NAME_ALREADY_EXISTS);
            }
            user.setUserName(userName);
        }
        return userRepository.save(user);
    }

    public void updateUserPassword(Password oldPassword, Password newPassword) throws VotifyException {
        User user = context.getUserOrThrow();
        if (oldPassword == null || newPassword == null) {
            throw new VotifyException(VotifyErrorCode.PASSWORD_EMPTY);
        }
        if (!passwordEncoderService.checkPassword(user, oldPassword)) {
            throw new VotifyException(VotifyErrorCode.INVALID_OLD_PASSWORD);
        }
        user.setPassword(passwordEncoderService, newPassword);
        userRepository.save(user);
    }

    public void updateUserEmail(Email email) throws VotifyException {
        User user = context.getUserOrThrow();

        if (email == null) {
            throw new VotifyException(VotifyErrorCode.EMAIL_EMPTY);
        }
        if (user.getEmail().equals(email)) {
            return;
        }
        if (userRepository.existsByEmail(email)) {
            throw new VotifyException(VotifyErrorCode.EMAIL_ALREADY_EXISTS);
        }
        emailConfirmationService.addEmailConfirmation(user, email);
    }

    public User getUserById(long id) throws VotifyException {
        return userRepository.findById(id)
                .orElseThrow(() -> new VotifyException(VotifyErrorCode.USER_NOT_FOUND));
    }

    public User getUserByUserName(UserName userName) throws VotifyException {
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new VotifyException(VotifyErrorCode.USER_NOT_FOUND));
    }

    public User getUserByEmail(Email email) throws VotifyException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new VotifyException(VotifyErrorCode.USER_NOT_FOUND));
    }
}
