package br.com.votify.dto.polls;

import br.com.votify.core.domain.entities.polls.Vote;
import br.com.votify.dto.DTOInput;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteInsertDTO implements DTOInput<Vote> {
    private int value;

    @Override
    public Vote convertToEntity() {
        return new Vote(null, value, null, null);
    }
}
