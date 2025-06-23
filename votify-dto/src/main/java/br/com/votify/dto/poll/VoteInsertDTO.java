package br.com.votify.dto.poll;

import br.com.votify.core.model.poll.Poll;
import br.com.votify.core.model.poll.VoteRegister;
import br.com.votify.core.model.user.User;
import br.com.votify.core.utils.exceptions.VotifyException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteInsertDTO {
    private int value;

    public VoteRegister convertToEntity(User user, Poll poll) throws VotifyException {
        return new VoteRegister(value, user, poll);
    }
}
