package br.com.votify.dto.poll;

import br.com.votify.core.model.poll.PollRegister;
import br.com.votify.core.model.poll.VoteOptionRegister;
import br.com.votify.core.utils.exceptions.VotifyException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public final class PollInsertDTO {
    private String title;
    private String description;
    private Instant startDate;
    private Instant endDate;
    private boolean userRegistration;
    private Integer choiceLimitPerUser;
    private List<VoteOptionInsertDTO> voteOptions;

    public PollRegister convertToEntity() throws VotifyException {
        List<VoteOptionRegister> voteOptionRegisters = new ArrayList<>();
        for (VoteOptionInsertDTO voteOption : voteOptions) {
            voteOptionRegisters.add(voteOption.convertToEntity());
        }
        return new PollRegister(
                title,
                description,
                startDate,
                endDate,
                userRegistration,
                voteOptionRegisters,
                choiceLimitPerUser
        );
    }
}