package br.com.votify.infra.repository.user;

import br.com.votify.core.model.user.EmailConfirmation;
import br.com.votify.core.model.user.User;
import br.com.votify.core.model.user.UserRole;
import br.com.votify.core.model.user.field.ConfirmationCode;
import br.com.votify.core.model.user.field.Email;
import br.com.votify.core.properties.user.UserProperties;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.infra.persistence.user.EmailConfirmationEntity;
import br.com.votify.infra.persistence.user.UserEntity;
import br.com.votify.test.suites.RepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EmailConfirmationRepositoryImplTest extends RepositoryTest {
    @Autowired
    private EmailConfirmationRepositoryImpl emailConfirmationRepository;

    @Autowired
    private EmailConfirmationEntityRepository entityRepository;

    @Autowired
    private UserEntityRepository userEntityRepository;

    private UserEntity testUser;
    private EmailConfirmationEntity testConfirmation;

    @BeforeEach
    void setupBeforeEach() {
        testUser = UserEntity.builder()
                .email("test@example.com")
                .name("Test User")
                .userName("testuser")
                .password("encrypted_password")
                .role(UserRole.COMMON)
                .build();
        userEntityRepository.save(testUser);

        testConfirmation = EmailConfirmationEntity.builder()
                .code("ABC123")
                .user(testUser)
                .newEmail("new@example.com")
                .expiration(Instant.now().plus(Duration.ofHours(1)))
                .build();
        entityRepository.save(testConfirmation);
    }

    @Test
    void findByUserEmailShouldReturnConfirmationWhenFound() throws VotifyException {
        Email email = new Email("test@example.com");
        Optional<EmailConfirmation> result = emailConfirmationRepository.findByUserEmail(email);

        assertTrue(result.isPresent());
        assertEquals("ABC123", result.get().getCode().getValue());
        assertEquals(testUser.getId(), result.get().getUserId());
    }

    @Test
    void findByUserEmailShouldReturnEmptyWhenNotFound() throws VotifyException {
        Email email = new Email("nonexistent@example.com");
        Optional<EmailConfirmation> result = emailConfirmationRepository.findByUserEmail(email);

        assertFalse(result.isPresent());
    }

    @Test
    void existsByUserEmailShouldReturnTrueWhenExists() throws VotifyException {
        Email email = new Email("test@example.com");
        boolean result = emailConfirmationRepository.existsByUserEmail(email);

        assertTrue(result);
    }

    @Test
    void existsByUserEmailShouldReturnFalseWhenNotExists() throws VotifyException {
        Email email = new Email("nonexistent@example.com");
        boolean result = emailConfirmationRepository.existsByUserEmail(email);

        assertFalse(result);
    }

    @Test
    void findAllExpiredShouldReturnExpiredConfirmations() {
        UserEntity expiredUser = UserEntity.builder()
                .email("expired@example.com")
                .name("Expired User")
                .userName("expireduser")
                .password("encrypted_password")
                .role(UserRole.COMMON)
                .build();
        userEntityRepository.save(expiredUser);

        EmailConfirmationEntity expiredConfirmation = EmailConfirmationEntity.builder()
                .code("EXPIREDDDD123")
                .user(expiredUser)
                .newEmail("expired@example.com")
                .expiration(Instant.now().minus(Duration.ofHours(1)))
                .build();
        entityRepository.save(expiredConfirmation);

        List<EmailConfirmation> result = emailConfirmationRepository.findAllExpired(Instant.now());

        assertEquals(1, result.size());
        assertEquals("EXPIREDDDD123", result.get(0).getCode().getValue());
    }

    @Test
    void findAllExpiredShouldReturnEmptyListWhenNoExpiredConfirmations() {
        List<EmailConfirmation> result = emailConfirmationRepository.findAllExpired(Instant.now());
        assertTrue(result.isEmpty());
    }

    @Test
    void deleteShouldRemoveConfirmation() throws VotifyException {
        ConfirmationCode code = mock(ConfirmationCode.class);
        when(code.getValue()).thenReturn(testConfirmation.getCode());

        EmailConfirmation confirmation = mock(EmailConfirmation.class);
        when(confirmation.getCode()).thenReturn(code);

        assertTrue(entityRepository.existsById("ABC123"));
        emailConfirmationRepository.delete(confirmation);
        assertFalse(entityRepository.existsById("ABC123"));
    }

    @Test
    void saveShouldPersistAndReturnConfirmation() throws VotifyException {
        UserProperties userProperties = mock(UserProperties.class);
        when(userProperties.getEmailConfirmationExpirationMinutes()).thenReturn(30);

        UserEntity newUser = UserEntity.builder()
                .email("new@example.com")
                .name("New User")
                .userName("newuser")
                .password("encrypted_password")
                .role(UserRole.COMMON)
                .build();
        userEntityRepository.save(newUser);

        User user = mock(User.class);
        when(user.getId()).thenReturn(2L);

        EmailConfirmation newConfirmation = new EmailConfirmation(
                new Email("newemail@example.com"),
                user,
                userProperties
        );

        EmailConfirmation savedConfirmation = emailConfirmationRepository.save(newConfirmation);

        assertNotNull(savedConfirmation);
        assertEquals(newConfirmation.getCode().getValue(), savedConfirmation.getCode().getValue());
        assertEquals(newConfirmation.getUserId(), savedConfirmation.getUserId());
        assertTrue(entityRepository.existsById(savedConfirmation.getCode().getValue()));
    }
}
