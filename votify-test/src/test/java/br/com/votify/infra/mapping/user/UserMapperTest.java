package br.com.votify.infra.mapping.user;

import br.com.votify.core.model.user.User;
import br.com.votify.core.model.user.UserRole;
import br.com.votify.core.model.user.field.Email;
import br.com.votify.core.model.user.field.Name;
import br.com.votify.core.model.user.field.UserName;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.infra.persistence.user.EmailConfirmationEntity;
import br.com.votify.infra.persistence.user.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {
    private UserMapper userMapper;

    @BeforeEach
    void setupBeforeEach() {
        userMapper = new UserMapper();
    }

    @Test
    void testToModelWithNullEmailConfirmation() {
        UserEntity userEntity = mock(UserEntity.class);
        when(userEntity.getId()).thenReturn(2L);
        when(userEntity.getEmail()).thenReturn("mail@mail.com");
        when(userEntity.getUserName()).thenReturn("username");
        when(userEntity.getName()).thenReturn("name");
        when(userEntity.getPassword()).thenReturn("encrypted_password");
        when(userEntity.getRole()).thenReturn(UserRole.COMMON);
        when(userEntity.getEmailConfirmation()).thenReturn(null);

        User user = userMapper.toModel(userEntity);
        assertEquals(userEntity.getId(), user.getId());
        assertEquals(userEntity.getEmail(), user.getEmail().getValue());
        assertEquals(userEntity.getUserName(), user.getUserName().getValue());
        assertEquals(userEntity.getName(), user.getName().getValue());
        assertEquals(userEntity.getPassword(), user.getEncryptedPassword());
        assertEquals(userEntity.getRole(), user.getRole());
        assertTrue(user.isActive());
    }

    @Test
    void testToModelWithNewEmail() {
        EmailConfirmationEntity emailConfirmation = mock(EmailConfirmationEntity.class);
        when(emailConfirmation.getNewEmail()).thenReturn("newmail@mail.com");

        UserEntity userEntity = mock(UserEntity.class);
        when(userEntity.getId()).thenReturn(2L);
        when(userEntity.getEmail()).thenReturn("mail@mail.com");
        when(userEntity.getUserName()).thenReturn("username");
        when(userEntity.getName()).thenReturn("name");
        when(userEntity.getPassword()).thenReturn("encrypted_password");
        when(userEntity.getRole()).thenReturn(UserRole.COMMON);
        when(userEntity.getEmailConfirmation()).thenReturn(emailConfirmation);

        User user = userMapper.toModel(userEntity);
        assertEquals(userEntity.getId(), user.getId());
        assertEquals(userEntity.getEmail(), user.getEmail().getValue());
        assertEquals(userEntity.getUserName(), user.getUserName().getValue());
        assertEquals(userEntity.getName(), user.getName().getValue());
        assertEquals(userEntity.getPassword(), user.getEncryptedPassword());
        assertEquals(userEntity.getRole(), user.getRole());
        assertTrue(user.isActive());
    }

    @Test
    void testToModelWithNewAccount() {
        EmailConfirmationEntity emailConfirmation = mock(EmailConfirmationEntity.class);
        when(emailConfirmation.getNewEmail()).thenReturn(null);

        UserEntity userEntity = mock(UserEntity.class);
        when(userEntity.getId()).thenReturn(2L);
        when(userEntity.getEmail()).thenReturn("mail@mail.com");
        when(userEntity.getUserName()).thenReturn("username");
        when(userEntity.getName()).thenReturn("name");
        when(userEntity.getPassword()).thenReturn("encrypted_password");
        when(userEntity.getRole()).thenReturn(UserRole.COMMON);
        when(userEntity.getEmailConfirmation()).thenReturn(emailConfirmation);

        User user = userMapper.toModel(userEntity);
        assertEquals(userEntity.getId(), user.getId());
        assertEquals(userEntity.getEmail(), user.getEmail().getValue());
        assertEquals(userEntity.getUserName(), user.getUserName().getValue());
        assertEquals(userEntity.getName(), user.getName().getValue());
        assertEquals(userEntity.getPassword(), user.getEncryptedPassword());
        assertEquals(userEntity.getRole(), user.getRole());
        assertFalse(user.isActive());
    }

    @Test
    void testToEntity() throws VotifyException {
        User user = mock(User.class);
        when(user.getId()).thenReturn(2L);
        when(user.getEmail()).thenReturn(new Email("mail@mail.com"));
        when(user.getUserName()).thenReturn(new UserName("username"));
        when(user.getName()).thenReturn(new Name("name"));
        when(user.getEncryptedPassword()).thenReturn("encrypted_password");
        when(user.getRole()).thenReturn(UserRole.COMMON);

        UserEntity userEntity = userMapper.toEntity(user);
        assertEquals(user.getId(), userEntity.getId());
        assertEquals(user.getEmail().getValue(), userEntity.getEmail());
        assertEquals(user.getUserName().getValue(), userEntity.getUserName());
        assertEquals(user.getName().getValue(), userEntity.getName());
        assertEquals(user.getEncryptedPassword(), userEntity.getPassword());
        assertEquals(user.getRole(), userEntity.getRole());
    }
}
