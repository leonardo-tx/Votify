package br.com.votify.dto.vote;

import br.com.votify.core.domain.entities.vote.VoteOption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteOptionViewDTO {
    private String name;

    public static VoteOptionViewDTO parse(VoteOption entity) {
        return new VoteOptionViewDTO(entity.getName());
    }
}
