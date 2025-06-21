package br.com.votify.infra.mapping.poll;

import br.com.votify.core.model.poll.Vote;
import br.com.votify.infra.mapping.Mapper;
import br.com.votify.infra.persistence.poll.PollEntity;
import br.com.votify.infra.persistence.poll.VoteEntity;
import br.com.votify.infra.persistence.poll.VoteIdentifier;
import br.com.votify.infra.persistence.user.UserEntity;
import org.springframework.stereotype.Component;

@Component
public final class VoteMapper implements Mapper<Vote, VoteEntity> {
    @Override
    public Vote toModel(VoteEntity voteEntity) {
        return Vote.parseUnsafe(
                voteEntity.getOption(),
                voteEntity.getId().getPollId(),
                voteEntity.getId().getUserId()
        );
    }

    @Override
    public VoteEntity toEntity(Vote vote) {
        VoteIdentifier voteIdentifier = new VoteIdentifier(vote.getPollId(), vote.getUserId());
        return VoteEntity.builder()
                .id(voteIdentifier)
                .user(UserEntity.builder().id(vote.getUserId()).build())
                .poll(PollEntity.builder().id(vote.getPollId()).build())
                .option(vote.getOption())
                .build();
    }
}
