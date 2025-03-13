package br.com.votify.dto.poll;

import br.com.votify.core.domain.entities.poll.Poll;
import br.com.votify.core.domain.entities.vote.VoteOption;
import br.com.votify.dto.DTOInput;
import br.com.votify.dto.vote.VoteOptionInsertDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public final class PollInsertDTO implements DTOInput<Poll> {
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean userRegistration;
    private Integer choiceLimitPerUser;
    private List<VoteOptionInsertDTO> voteOptions;

    @Override
    public Poll convertToEntity() {
        Poll poll = new Poll(
            title,
            description,
            startDate,
            endDate,
            userRegistration,
            new ArrayList<>(),
            choiceLimitPerUser
        );
        if (voteOptions == null) {
            return poll;
        }
        for (VoteOptionInsertDTO voteOptionDTO : voteOptions) {
            VoteOption voteOption = voteOptionDTO.convertToEntity();
            voteOption.setPoll(poll);

            poll.getVoteOptions().add(voteOption);
        }
        return poll;
    }
}