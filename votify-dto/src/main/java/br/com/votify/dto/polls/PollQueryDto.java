package br.com.votify.dto.polls;

import br.com.votify.core.domain.entities.poll.Poll;
import br.com.votify.core.domain.entities.vote.Vote;
import br.com.votify.core.domain.entities.vote.VoteOption;
import br.com.votify.dto.vote.VoteOptionViewDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PollQueryDto {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer choiceLimitPerUser;
    private Long responsibleId;
    private List<VoteOptionViewDTO> voteOptions;
    private int myChoices;

    public static PollQueryDto parse(Poll poll, List<Vote> myChoices) {
        PollQueryDto pollQueryDto = new PollQueryDto(
            poll.getId(),
            poll.getTitle(),
            poll.getDescription(),
            poll.getStartDate(),
            poll.getEndDate(),
            poll.getChoiceLimitPerUser(),
            poll.getResponsible().getId(),
            new ArrayList<>(),
            0
        );

        for (int i = 0; i < poll.getVoteOptions().size(); i++) {
            VoteOption voteOption = poll.getVoteOptions().get(i);
            for (Vote vote : myChoices) {
                if (Objects.equals(vote.getVoteOption().getId(), voteOption.getId())) {
                    pollQueryDto.myChoices = 1 << i;
                    break;
                }
            }
        }
        return pollQueryDto;
    }
}
