package br.com.votify.dto.polls;

import br.com.votify.core.domain.entities.polls.VoteOption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteOptionViewDTO {
    private String name;
    private int voteCount;

    public static VoteOptionViewDTO parse(VoteOption entity) {
        return new VoteOptionViewDTO(entity.getName(), entity.getCount());
    }

}
