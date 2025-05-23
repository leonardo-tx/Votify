package br.com.votify.dto.polls;

import br.com.votify.core.domain.entities.polls.VoteOption;
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
        return new VoteOption(null, name, 0, null);
    }
}
