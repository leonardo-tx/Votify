package br.com.votify.infra.mapping.poll;

import br.com.votify.core.model.poll.VoteOption;
import br.com.votify.core.model.poll.field.VoteOptionName;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.infra.persistence.poll.VoteOptionEntity;
import br.com.votify.infra.persistence.poll.VoteOptionIdentifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VoteOptionMapperTest {
    private VoteOptionMapper voteOptionMapper;

    @BeforeEach
    void setupBeforeEach() {
        voteOptionMapper = new VoteOptionMapper();
    }

    @Test
    void testToModel() {
        VoteOptionEntity voteOptionEntity = mock(VoteOptionEntity.class);
        when(voteOptionEntity.getName()).thenReturn("Option");
        when(voteOptionEntity.getId()).thenReturn(new VoteOptionIdentifier(1L, 2));
        when(voteOptionEntity.getCount()).thenReturn(5);

        VoteOption voteOption = voteOptionMapper.toModel(voteOptionEntity);
        assertEquals(voteOptionEntity.getName(), voteOption.getName().getValue());
        assertEquals(voteOptionEntity.getId().getSequence(), voteOption.getSequence());
        assertEquals(voteOptionEntity.getId().getPollId(), voteOption.getPollId());
        assertEquals(voteOptionEntity.getCount(), voteOption.getCount());
    }

    @Test
    void testToEntity() throws VotifyException {
        VoteOption voteOption = mock(VoteOption.class);
        when(voteOption.getName()).thenReturn(new VoteOptionName("Option"));
        when(voteOption.getSequence()).thenReturn(2);
        when(voteOption.getPollId()).thenReturn(1L);
        when(voteOption.getCount()).thenReturn(5);

        VoteOptionEntity voteOptionEntity = voteOptionMapper.toEntity(voteOption);
        assertEquals(voteOption.getName().getValue(), voteOptionEntity.getName());
        assertEquals(voteOptionEntity.getId().getSequence(), voteOption.getSequence());
        assertEquals(voteOption.getPollId(), voteOptionEntity.getId().getPollId());
        assertEquals(voteOption.getCount(), voteOptionEntity.getCount());
        assertEquals(voteOption.getPollId(), voteOptionEntity.getPoll().getId());
    }
}
