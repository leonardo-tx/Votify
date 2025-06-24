package br.com.votify.infra.mapping.poll;

import br.com.votify.core.model.poll.VoteOption;
import br.com.votify.core.model.poll.field.VoteOptionName;
import br.com.votify.infra.mapping.Mapper;
import br.com.votify.infra.persistence.poll.PollEntity;
import br.com.votify.infra.persistence.poll.VoteOptionEntity;
import br.com.votify.infra.persistence.poll.VoteOptionIdentifier;
import jakarta.transaction.NotSupportedException;
import org.springframework.stereotype.Component;

@Component
public final class VoteOptionMapper implements Mapper<VoteOption, VoteOptionEntity> {
    @Override
    public VoteOption toModel(VoteOptionEntity voteOptionEntity) {
        return VoteOption.parseUnsafe(
                VoteOptionName.parseUnsafe(voteOptionEntity.getName()),
                voteOptionEntity.getCount(),
                voteOptionEntity.getId().getSequence(),
                voteOptionEntity.getId().getPollId()
        );
    }

    @Override
    public VoteOptionEntity toEntity(VoteOption voteOption) throws NotSupportedException {
        throw new NotSupportedException("The default method toEntity is not supported.");
    }

    public VoteOptionEntity toEntity(VoteOption voteOption, PollEntity pollEntity) {
        VoteOptionIdentifier voteOptionIdentifier = VoteOptionIdentifier.builder()
                .pollId(voteOption.getPollId())
                .sequence(voteOption.getSequence())
                .build();
        return VoteOptionEntity.builder()
                .id(voteOptionIdentifier)
                .count(voteOption.getCount())
                .name(voteOption.getName().getValue())
                .poll(pollEntity)
                .build();
    }
}
