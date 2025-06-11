package br.com.votify.infra.mapping.poll;

import br.com.votify.core.model.poll.field.VoteOption;
import br.com.votify.infra.mapping.Mapper;
import br.com.votify.infra.persistence.poll.PollEntity;
import br.com.votify.infra.persistence.poll.VoteOptionEntity;
import br.com.votify.infra.persistence.poll.VoteOptionIdentifier;
import org.springframework.stereotype.Component;

@Component
public final class VoteOptionMapper implements Mapper<VoteOption, VoteOptionEntity> {
    @Override
    public VoteOption parseToModel(VoteOptionEntity voteOptionEntity) {
        return VoteOption.parseUnsafe(
                voteOptionEntity.getName(),
                voteOptionEntity.getCount(),
                voteOptionEntity.getId().getSequence(),
                voteOptionEntity.getPoll().getId()
        );
    }

    @Override
    public VoteOptionEntity parseToEntity(VoteOption voteOption) {
        VoteOptionIdentifier voteOptionIdentifier = VoteOptionIdentifier.builder()
                .pollId(voteOption.getPollId())
                .sequence(voteOption.getSequence())
                .build();
        return VoteOptionEntity.builder()
                .id(voteOptionIdentifier)
                .count(voteOption.getCount())
                .name(voteOption.getName())
                .poll(PollEntity.builder().id(voteOption.getPollId()).build())
                .build();
    }
}
