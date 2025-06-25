package br.com.votify.infra.repository.user;

import br.com.votify.core.model.user.User;
import br.com.votify.core.model.user.UserRole;
import br.com.votify.core.model.user.field.Email;
import br.com.votify.core.model.user.field.Name;
import br.com.votify.core.model.user.field.UserName;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.infra.persistence.user.UserEntity;
import br.com.votify.test.suites.RepositoryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserRepositoryImplTest extends RepositoryTest {
    @Autowired
    private UserRepositoryImpl userRepository;

    @Autowired
    private UserEntityRepository entityRepository;

    @BeforeEach
    void setupBeforeEach() {
        UserEntity entity = UserEntity.builder()
                .email("test@example.com")
                .name("Teste")
                .userName("testuser")
                .password("encrypted_password")
                .role(UserRole.COMMON)
                .build();
        entityRepository.save(entity);
    }

    @Test
    void existsByEmailShouldReturnTrueWhenEmailExists() throws VotifyException {
        Email email = new Email("test@example.com");
        boolean result = userRepository.existsByEmail(email);

        assertTrue(result);
    }

    @Test
    void existsByUserNameShouldReturnFalseWhenUserNameDoesNotExist() throws VotifyException {
        UserName userName = new UserName("nonexistent");
        boolean result = userRepository.existsByUserName(userName);

        assertFalse(result);
    }

    @Test
    void findByUserNameShouldReturnFalseWhenUserNameDoesNotExist() throws VotifyException {
        UserName userName = new UserName("nonexistent");
        Optional<User> userOptional = userRepository.findByUserName(userName);

        assertFalse(userOptional.isPresent());
    }

    @Test
    void existsByUserName() throws VotifyException {
        UserName userName = new UserName("testuser");
        boolean result = userRepository.existsByUserName(userName);

        assertTrue(result);
    }

    @Test
    void findByUserName() throws VotifyException {
        UserName userName = new UserName("testuser");
        Optional<User> userOptional = userRepository.findByUserName(userName);

        assertTrue(userOptional.isPresent());
    }

    @Test
    void findByEmailShouldReturnUserWhenFound() throws VotifyException {
        Email email = new Email("test@example.com");
        Optional<User> result = userRepository.findByEmail(email);

        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
    }

    @Test
    void findByIdShouldReturnEmptyWhenNotFound() {
        Optional<User> result = userRepository.findById(999L);
        assertTrue(result.isEmpty());
    }

    @Test
    void saveShouldPersistAndReturnUser() {
        User user = User.parseUnsafe(
                null,
                Email.parseUnsafe("new@example.com"),
                UserName.parseUnsafe("newuser"),
                Name.parseUnsafe("User"),
                "encrypted_password",
                UserRole.COMMON,
                false
        );
        User savedUser = userRepository.save(user);

        assertEquals(50L, savedUser.getId());
        assertEquals(50, entityRepository.count());
    }

    @Test
    void deleteShouldRemoveUserFromDatabase() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(50L);

        userRepository.delete(user);
        assertEquals(49, entityRepository.count());
    }
}
