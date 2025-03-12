package br.com.votify.dto.vote;

import br.com.votify.core.domain.entities.vote.VoteOption;
import br.com.votify.dto.DTOInput;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteOptionInsertDTO implements DTOInput<VoteOption> {
    private String name;

    @Override
    public VoteOption convertToEntity() {
        return new VoteOption(null, name, null);
    }
}
