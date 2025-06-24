package br.com.votify.core.model.poll;

import lombok.Getter;

@Getter
public final class Vote {
    private final int option;
    private final Long pollId;
    private final Long userId;

    public Vote(VoteRegister voteRegister) {
        if (voteRegister == null) {
            throw new IllegalArgumentException("The vote register must not be null.");
        }
        this.option = voteRegister.getOption();
        this.pollId = voteRegister.getPollId();
        this.userId = voteRegister.getUserId();
    }

    private Vote(int option, Long pollId, Long userId) {
        this.option = option;
        this.pollId = pollId;
        this.userId = userId;
    }

    public static Vote parseUnsafe(int option, Long pollId, Long userId) {
        return new Vote(option, pollId, userId);
    }
}
