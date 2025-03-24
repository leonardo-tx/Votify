package br.com.votify.dto.polls;

import br.com.votify.core.domain.entities.polls.VoteOption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteOptionDetailedViewDTO {
    private String name;
    private int count;

    public static VoteOptionDetailedViewDTO parse(VoteOption voteOption) {
        return new VoteOptionDetailedViewDTO(voteOption.getName(), voteOption.getCount());
    }
}
