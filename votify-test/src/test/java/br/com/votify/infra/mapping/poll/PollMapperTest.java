package br.com.votify.infra.mapping.poll;

import br.com.votify.core.model.poll.Poll;
import br.com.votify.core.model.poll.VoteOption;
import br.com.votify.core.model.poll.field.Description;
import br.com.votify.core.model.poll.field.Title;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.infra.persistence.poll.PollEntity;
import br.com.votify.infra.persistence.poll.VoteOptionEntity;
import br.com.votify.infra.persistence.user.UserEntity;
import jakarta.transaction.NotSupportedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PollMapperTest {
    @Mock
    private VoteOptionMapper voteOptionMapper;

    @InjectMocks
    private PollMapper pollMapper;

    @Test
    void testToModelWithUser() {
        UserEntity userEntity = mock(UserEntity.class);
        when(userEntity.getId()).thenReturn(1L);

        List<VoteOptionEntity> voteOptionEntities = List.of(mock(VoteOptionEntity.class));

        PollEntity pollEntity = mock(PollEntity.class);
        when(pollEntity.getId()).thenReturn(2L);
        when(pollEntity.getTitle()).thenReturn("Title");
        when(pollEntity.getDescription()).thenReturn("");
        when(pollEntity.getStartDate()).thenReturn(Instant.now());
        when(pollEntity.getEndDate()).thenReturn(Instant.now());
        when(pollEntity.isUserRegistration()).thenReturn(true);
        when(pollEntity.getVoteOptions()).thenReturn(voteOptionEntities);
        when(pollEntity.getChoiceLimitPerUser()).thenReturn(1);
        when(pollEntity.getResponsible()).thenReturn(userEntity);

        when(voteOptionMapper.toModel(any(VoteOptionEntity.class))).thenReturn(mock(VoteOption.class));

        Poll poll = pollMapper.toModel(pollEntity);
        assertEquals(pollEntity.getId(), poll.getId());
        assertEquals(pollEntity.getTitle(), poll.getTitle().getValue());
        assertEquals(pollEntity.getDescription(), poll.getDescription().getValue());
        assertEquals(pollEntity.getStartDate(), poll.getStartDate());
        assertEquals(pollEntity.getEndDate(), poll.getEndDate());
        assertEquals(pollEntity.isUserRegistration(), poll.isUserRegistration());
        assertEquals(pollEntity.getVoteOptions().size(), poll.getVoteOptions().size());
        assertEquals(pollEntity.getChoiceLimitPerUser(), poll.getChoiceLimitPerUser());
        assertEquals(pollEntity.getResponsible().getId(), poll.getResponsibleId());
    }

    @Test
    void testToModelWithoutUser() {
        List<VoteOptionEntity> voteOptionEntities = List.of(mock(VoteOptionEntity.class));

        PollEntity pollEntity = mock(PollEntity.class);
        when(pollEntity.getId()).thenReturn(2L);
        when(pollEntity.getTitle()).thenReturn("Title");
        when(pollEntity.getDescription()).thenReturn("");
        when(pollEntity.getStartDate()).thenReturn(Instant.now());
        when(pollEntity.getEndDate()).thenReturn(Instant.now());
        when(pollEntity.isUserRegistration()).thenReturn(true);
        when(pollEntity.getVoteOptions()).thenReturn(voteOptionEntities);
        when(pollEntity.getChoiceLimitPerUser()).thenReturn(1);
        when(pollEntity.getResponsible()).thenReturn(null);

        when(voteOptionMapper.toModel(any(VoteOptionEntity.class))).thenReturn(mock(VoteOption.class));

        Poll poll = pollMapper.toModel(pollEntity);
        assertEquals(pollEntity.getId(), poll.getId());
        assertEquals(pollEntity.getTitle(), poll.getTitle().getValue());
        assertEquals(pollEntity.getDescription(), poll.getDescription().getValue());
        assertEquals(pollEntity.getStartDate(), poll.getStartDate());
        assertEquals(pollEntity.getEndDate(), poll.getEndDate());
        assertEquals(pollEntity.isUserRegistration(), poll.isUserRegistration());
        assertEquals(pollEntity.getVoteOptions().size(), poll.getVoteOptions().size());
        assertEquals(pollEntity.getChoiceLimitPerUser(), poll.getChoiceLimitPerUser());
        assertNull(poll.getResponsibleId());
    }

    @Test
    void testToEntityWithUser() throws VotifyException {
        List<VoteOption> voteOptions = List.of(mock(VoteOption.class));

        Poll poll = mock(Poll.class);
        when(poll.getId()).thenReturn(2L);
        when(poll.getTitle()).thenReturn(new Title("Title"));
        when(poll.getDescription()).thenReturn(new Description(""));
        when(poll.getStartDate()).thenReturn(Instant.now());
        when(poll.getEndDate()).thenReturn(Instant.now());
        when(poll.isUserRegistration()).thenReturn(true);
        when(poll.getVoteOptions()).thenReturn(voteOptions);
        when(poll.getChoiceLimitPerUser()).thenReturn(1);
        when(poll.getResponsibleId()).thenReturn(1L);

        when(voteOptionMapper.toEntity(any(VoteOption.class), any(PollEntity.class))).thenReturn(mock(VoteOptionEntity.class));

        PollEntity pollEntity = pollMapper.toEntity(poll);
        assertEquals(poll.getId(), pollEntity.getId());
        assertEquals(poll.getTitle().getValue(), pollEntity.getTitle());
        assertEquals(poll.getDescription().getValue(), pollEntity.getDescription());
        assertEquals(poll.getStartDate(), pollEntity.getStartDate());
        assertEquals(poll.getEndDate(), pollEntity.getEndDate());
        assertEquals(poll.isUserRegistration(), pollEntity.isUserRegistration());
        assertEquals(poll.getVoteOptions().size(), pollEntity.getVoteOptions().size());
        assertEquals(poll.getChoiceLimitPerUser(), pollEntity.getChoiceLimitPerUser());
        assertEquals(poll.getResponsibleId(), pollEntity.getResponsible().getId());
    }

    @Test
    void testToEntityWithoutUser() throws VotifyException {
        List<VoteOption> voteOptions = List.of(mock(VoteOption.class));

        Poll poll = mock(Poll.class);
        when(poll.getId()).thenReturn(2L);
        when(poll.getTitle()).thenReturn(new Title("Title"));
        when(poll.getDescription()).thenReturn(new Description(""));
        when(poll.getStartDate()).thenReturn(Instant.now());
        when(poll.getEndDate()).thenReturn(Instant.now());
        when(poll.isUserRegistration()).thenReturn(true);
        when(poll.getVoteOptions()).thenReturn(voteOptions);
        when(poll.getChoiceLimitPerUser()).thenReturn(1);
        when(poll.getResponsibleId()).thenReturn(null);

        when(voteOptionMapper.toEntity(any(VoteOption.class), any(PollEntity.class))).thenReturn(mock(VoteOptionEntity.class));

        PollEntity pollEntity = pollMapper.toEntity(poll);
        assertEquals(poll.getId(), pollEntity.getId());
        assertEquals(poll.getTitle().getValue(), pollEntity.getTitle());
        assertEquals(poll.getDescription().getValue(), pollEntity.getDescription());
        assertEquals(poll.getStartDate(), pollEntity.getStartDate());
        assertEquals(poll.getEndDate(), pollEntity.getEndDate());
        assertEquals(poll.isUserRegistration(), pollEntity.isUserRegistration());
        assertEquals(poll.getVoteOptions().size(), pollEntity.getVoteOptions().size());
        assertEquals(poll.getChoiceLimitPerUser(), pollEntity.getChoiceLimitPerUser());
        assertNull(pollEntity.getResponsible());
    }
}
