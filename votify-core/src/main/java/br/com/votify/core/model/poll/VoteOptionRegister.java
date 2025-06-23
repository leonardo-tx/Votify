package br.com.votify.core.model.poll;

import br.com.votify.core.model.poll.field.VoteOptionName;
import br.com.votify.core.utils.exceptions.VotifyException;
import lombok.Getter;

@Getter
public final class VoteOptionRegister {
    private final VoteOptionName name;

    public VoteOptionRegister(String name) throws VotifyException {
        this.name = new VoteOptionName(name);
    }
}
