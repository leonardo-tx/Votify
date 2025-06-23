package br.com.votify.dto.polls;

import br.com.votify.core.domain.entities.polls.Poll;
import br.com.votify.core.domain.entities.polls.Vote;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PollQueryDTO {
    private Long id;
    private String title;
    private String description;
    private Instant startDate;
    private Instant endDate;
    private Integer choiceLimitPerUser;
    private Long responsibleId;
    private List<VoteOptionDetailedViewDTO> voteOptions;
    private int myChoices;
    private boolean userRegistration;


    public static PollQueryDTO parse(Poll poll, Vote vote) {
        List<VoteOptionDetailedViewDTO> optionsDto = poll.getVoteOptions()
                .stream()
                .map(VoteOptionDetailedViewDTO::parse)
                .toList();
        return new PollQueryDTO(
                poll.getId(),
                poll.getTitle(),
                poll.getDescription(),
                poll.getStartDate(),
                poll.getEndDate(),
                poll.getChoiceLimitPerUser(),
                poll.getResponsible().getId(),
                optionsDto,
                vote.getOption(),
                poll.isUserRegistration()

        );
    }
}