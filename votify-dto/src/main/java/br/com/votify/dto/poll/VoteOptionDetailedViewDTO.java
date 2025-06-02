package br.com.votify.dto.poll;

import br.com.votify.core.model.poll.VoteOption;
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
        return new VoteOptionDetailedViewDTO(voteOption.getName().getValue(), voteOption.getCount());
    }
}
