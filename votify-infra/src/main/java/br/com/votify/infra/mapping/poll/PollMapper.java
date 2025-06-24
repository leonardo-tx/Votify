package br.com.votify.infra.mapping.poll;

import br.com.votify.core.model.poll.Poll;
import br.com.votify.core.model.poll.field.Description;
import br.com.votify.core.model.poll.field.Title;
import br.com.votify.infra.mapping.Mapper;
import br.com.votify.infra.persistence.poll.PollEntity;
import br.com.votify.infra.persistence.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class PollMapper implements Mapper<Poll, PollEntity> {
    private final VoteOptionMapper voteOptionMapper;

    @Override
    public Poll toModel(PollEntity pollEntity) {
        return Poll.parseUnsafe(
                pollEntity.getId(),
                Title.parseUnsafe(pollEntity.getTitle()),
                Description.parseUnsafe(pollEntity.getDescription()),
                pollEntity.getStartDate(),
                pollEntity.getEndDate(),
                pollEntity.isUserRegistration(),
                pollEntity.getVoteOptions().stream().map(voteOptionMapper::toModel).toList(),
                pollEntity.getChoiceLimitPerUser(),
                pollEntity.getResponsible() == null ? null : pollEntity.getResponsible().getId()
        );
    }

    @Override
    public PollEntity toEntity(Poll poll) {
        PollEntity pollEntity = PollEntity.builder()
                .id(poll.getId())
                .title(poll.getTitle().getValue())
                .description(poll.getDescription().getValue())
                .startDate(poll.getStartDate())
                .endDate(poll.getEndDate())
                .userRegistration(poll.isUserRegistration())
                .choiceLimitPerUser(poll.getChoiceLimitPerUser())
                .responsible(
                        poll.getResponsibleId() != null
                                ? UserEntity.builder().id(poll.getResponsibleId()).build()
                                : null
                )
                .build();
        pollEntity.setVoteOptions(poll.getVoteOptions().stream()
                .map(voteOption -> voteOptionMapper.toEntity(voteOption, pollEntity)).toList()
        );
        return pollEntity;
    }
}
