package br.com.votify.infra.mapping.poll;

import br.com.votify.core.model.poll.Vote;
import br.com.votify.infra.persistence.poll.VoteEntity;
import br.com.votify.infra.persistence.poll.VoteIdentifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VoteMapperTest {
    private VoteMapper voteMapper;

    @BeforeEach
    void setupBeforeEach() {
        voteMapper = new VoteMapper();
    }

    @Test
    void testToModel() {
        VoteEntity voteEntity = mock(VoteEntity.class);
        when(voteEntity.getId()).thenReturn(new VoteIdentifier(2L, 4L));
        when(voteEntity.getOption()).thenReturn(2);

        Vote vote = voteMapper.toModel(voteEntity);
        assertEquals(voteEntity.getId().getPollId(), vote.getPollId());
        assertEquals(voteEntity.getId().getUserId(), vote.getUserId());
        assertEquals(voteEntity.getOption(), vote.getOption());
    }

    @Test
    void testToEntity() {
        Vote vote = mock(Vote.class);
        when(vote.getPollId()).thenReturn(2L);
        when(vote.getUserId()).thenReturn(4L);
        when(vote.getOption()).thenReturn(2);

        VoteEntity voteEntity = voteMapper.toEntity(vote);
        assertEquals(vote.getPollId(), voteEntity.getId().getPollId());
        assertEquals(vote.getUserId(), voteEntity.getId().getUserId());
        assertEquals(vote.getOption(), voteEntity.getOption());
        assertEquals(vote.getPollId(), voteEntity.getPoll().getId());
        assertEquals(vote.getUserId(), voteEntity.getUser().getId());
    }
}
