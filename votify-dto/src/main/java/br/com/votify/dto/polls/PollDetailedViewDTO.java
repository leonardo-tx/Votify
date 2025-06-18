package br.com.votify.dto.polls;

import br.com.votify.core.domain.entities.polls.Poll;
import br.com.votify.core.domain.entities.polls.VoteOption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PollDetailedViewDTO {
    private Long id;
    private String title;
    private String description;
    private Instant startDate;
    private Instant endDate;
    private boolean userRegistration;
    private Integer choiceLimitPerUser;
    private Long responsibleId;
    private int votedOption;
    private List<VoteOptionDetailedViewDTO> voteOptions;

    public static PollDetailedViewDTO parse(Poll entity, int votedOption) {
        List<VoteOptionDetailedViewDTO> voteOptions = new ArrayList<>();
        for (VoteOption voteOption : entity.getVoteOptions()) {
            voteOptions.add(VoteOptionDetailedViewDTO.parse(voteOption));
        }

        return new PollDetailedViewDTO(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.isUserRegistration(),
                entity.getChoiceLimitPerUser(),
                entity.getResponsible() == null ? null : entity.getResponsible().getId(),
                votedOption,
                voteOptions
        );
    }
}