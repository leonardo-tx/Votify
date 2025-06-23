package br.com.votify.core.model.poll;

import br.com.votify.core.model.user.User;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import lombok.Getter;

@Getter
public class VoteRegister {
    private final int option;
    private final Long userId;
    private final Long pollId;

    public VoteRegister(int option, User user, Poll poll) throws VotifyException {
        if (user == null) {
            throw new VotifyException(VotifyErrorCode.COMMON_UNAUTHORIZED);
        }
        if (poll == null) {
            throw new VotifyException(VotifyErrorCode.POLL_NOT_FOUND);
        }
        if (option == 0) {
            throw new VotifyException(VotifyErrorCode.POLL_VOTE_EMPTY);
        }
        int max = (1 << poll.getVoteOptionsSize()) - 1;
        if ((option & max) != option || Integer.bitCount(option) > poll.getChoiceLimitPerUser()) {
            throw new VotifyException(VotifyErrorCode.POLL_VOTE_INVALID);
        }
        this.option = option;
        this.userId = user.getId();
        this.pollId = poll.getId();
    }
}
