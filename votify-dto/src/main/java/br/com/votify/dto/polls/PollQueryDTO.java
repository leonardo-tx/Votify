package br.com.votify.dto.polls;

import br.com.votify.core.domain.entities.polls.Poll;
import br.com.votify.core.domain.entities.polls.Vote;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PollQueryDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer choiceLimitPerUser;
    private Long responsibleId;
    private List<VoteOptionViewDTO> voteOptions;
    private int myChoices;

    public static PollQueryDTO parse(Poll poll, Vote vote) {
        List<VoteOptionViewDTO> optionsDto = poll.getVoteOptions()
                .stream()
                .map(VoteOptionViewDTO::parse)
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
                vote.getOption()
        );
    }
}