package br.com.votify.dto.poll;

import br.com.votify.core.model.poll.VoteOptionRegister;
import br.com.votify.core.utils.exceptions.VotifyException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteOptionInsertDTO {
    private String name;

    public VoteOptionRegister convertToEntity() throws VotifyException {
        return new VoteOptionRegister(name);
    }
}
