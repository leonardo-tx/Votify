package br.com.votify.infra.mapping.user;

import br.com.votify.core.model.user.EmailConfirmation;
import br.com.votify.core.model.user.field.ConfirmationCode;
import br.com.votify.core.model.user.field.Email;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.infra.persistence.user.EmailConfirmationEntity;
import br.com.votify.infra.persistence.user.UserEntity;
import br.com.votify.infra.repository.user.UserEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailConfirmationMapperTest {
    @Mock
    private UserEntityRepository userEntityRepository;

    @InjectMocks
    private EmailConfirmationMapper emailConfirmationMapper;

    @Test
    void testToModelWithoutNewEmail() {
        UserEntity userEntity = mock(UserEntity.class);
        when(userEntity.getId()).thenReturn(2L);

        EmailConfirmationEntity emailConfirmationEntity = mock(EmailConfirmationEntity.class);
        when(emailConfirmationEntity.getCode()).thenReturn("code");
        when(emailConfirmationEntity.getUser()).thenReturn(userEntity);
        when(emailConfirmationEntity.getNewEmail()).thenReturn(null);
        when(emailConfirmationEntity.getExpiration()).thenReturn(Instant.now());

        EmailConfirmation emailConfirmation = emailConfirmationMapper.toModel(emailConfirmationEntity);
        assertEquals(emailConfirmationEntity.getCode(), emailConfirmation.getCode().getValue());
        assertEquals(emailConfirmationEntity.getUser().getId(), emailConfirmation.getUserId());
        assertEquals(emailConfirmationEntity.getExpiration(), emailConfirmation.getExpiration());
        assertNull(emailConfirmation.getNewEmail());
    }

    @Test
    void testToModelWithNewEmail() {
        UserEntity userEntity = mock(UserEntity.class);
        when(userEntity.getId()).thenReturn(2L);

        EmailConfirmationEntity emailConfirmationEntity = mock(EmailConfirmationEntity.class);
        when(emailConfirmationEntity.getCode()).thenReturn("code");
        when(emailConfirmationEntity.getUser()).thenReturn(userEntity);
        when(emailConfirmationEntity.getNewEmail()).thenReturn("mail@mail.com");
        when(emailConfirmationEntity.getExpiration()).thenReturn(Instant.now());

        EmailConfirmation emailConfirmation = emailConfirmationMapper.toModel(emailConfirmationEntity);
        assertEquals(emailConfirmationEntity.getCode(), emailConfirmation.getCode().getValue());
        assertEquals(emailConfirmationEntity.getUser().getId(), emailConfirmation.getUserId());
        assertEquals(emailConfirmationEntity.getExpiration(), emailConfirmation.getExpiration());
        assertEquals(emailConfirmationEntity.getNewEmail(), emailConfirmation.getNewEmail().getValue());
    }

    @Test
    void testToEntityWithoutNewEmail() {
        UserEntity userEntity = mock(UserEntity.class);
        EmailConfirmation emailConfirmation = mock(EmailConfirmation.class);
        when(emailConfirmation.getCode()).thenReturn(new ConfirmationCode());
        when(emailConfirmation.getUserId()).thenReturn(2L);
        when(emailConfirmation.getNewEmail()).thenReturn(null);
        when(emailConfirmation.getExpiration()).thenReturn(Instant.now());
        when(userEntityRepository.getReferenceById(2L)).thenReturn(userEntity);

        EmailConfirmationEntity emailConfirmationEntity = emailConfirmationMapper.toEntity(emailConfirmation);
        assertEquals(emailConfirmation.getCode().getValue(), emailConfirmationEntity.getCode());
        assertEquals(userEntity, emailConfirmationEntity.getUser());
        assertEquals(emailConfirmation.getExpiration(), emailConfirmationEntity.getExpiration());
        assertNull(emailConfirmationEntity.getNewEmail());
    }

    @Test
    void testToEntityWithNewEmail() throws VotifyException {
        UserEntity userEntity = mock(UserEntity.class);
        EmailConfirmation emailConfirmation = mock(EmailConfirmation.class);
        when(emailConfirmation.getCode()).thenReturn(new ConfirmationCode());
        when(emailConfirmation.getUserId()).thenReturn(2L);
        when(emailConfirmation.getNewEmail()).thenReturn(new Email("mail@mail.com"));
        when(emailConfirmation.getExpiration()).thenReturn(Instant.now());
        when(userEntityRepository.getReferenceById(2L)).thenReturn(userEntity);

        EmailConfirmationEntity emailConfirmationEntity = emailConfirmationMapper.toEntity(emailConfirmation);
        assertEquals(emailConfirmation.getCode().getValue(), emailConfirmationEntity.getCode());
        assertEquals(userEntity, emailConfirmationEntity.getUser());
        assertEquals(emailConfirmation.getExpiration(), emailConfirmationEntity.getExpiration());
        assertEquals(emailConfirmation.getNewEmail().getValue(), emailConfirmationEntity.getNewEmail());
    }
}
