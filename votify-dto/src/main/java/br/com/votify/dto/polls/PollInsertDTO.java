package br.com.votify.dto.polls;

import br.com.votify.core.domain.entities.polls.Poll;
import br.com.votify.core.domain.entities.polls.VoteOption;
import br.com.votify.dto.DTOInput;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public final class PollInsertDTO implements DTOInput<Poll> {
    private String title;
    private String description;
    private Instant startDate;
    private Instant endDate;
    private boolean userRegistration;
    private Integer choiceLimitPerUser;
    private List<VoteOptionInsertDTO> voteOptions;

    @Override
    public Poll convertToEntity() {
        Poll poll = Poll.builder()
                .id(null)
                .title(title)
                .description(description)
                .startDate(startDate)
                .endDate(endDate)
                .userRegistration(userRegistration)
                .choiceLimitPerUser(choiceLimitPerUser)
                .archived(false)
                .voteOptions(new ArrayList<>())
                .votes(null)
                .responsible(null)
                .build();

        if (voteOptions != null) {
            for (VoteOptionInsertDTO voteOptionDTO : voteOptions) {
                VoteOption voteOption = voteOptionDTO.convertToEntity();
                voteOption.setPoll(poll);
                poll.getVoteOptions().add(voteOption);
            }
        }
        return poll;
    }
}