package br.com.votify.core.model.poll;

import br.com.votify.core.model.poll.field.VoteOptionName;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import lombok.Getter;

@Getter
public final class VoteOption {
    public static final int MIN_SEQUENCE = PollRegister.MIN_VOTE_OPTIONS - 1;
    public static final int MAX_SEQUENCE = PollRegister.MAX_VOTE_OPTIONS - 1;

    private final VoteOptionName name;
    private int count;
    private final int sequence;
    private final Long pollId;

    public VoteOption(VoteOptionRegister voteOptionRegister, Poll poll, int sequence) throws VotifyException {
        if (voteOptionRegister == null) {
            throw new IllegalArgumentException("The vote option register must not be null.");
        }
        if (poll == null) {
            throw new IllegalArgumentException("The poll must not be null.");
        }
        if (sequence < MIN_SEQUENCE || sequence > MAX_SEQUENCE) {
            throw new VotifyException(
                    VotifyErrorCode.POLL_VOTE_OPTIONS_INVALID_LENGTH,
                    PollRegister.MIN_VOTE_OPTIONS,
                    PollRegister.MAX_VOTE_OPTIONS
            );
        }
        this.name = voteOptionRegister.getName();
        this.count = 0;
        this.sequence = sequence;
        this.pollId = poll.getId();
    }

    private VoteOption(VoteOptionName name, int count, int sequence, Long pollId) {
        this.name = name;
        this.count = count;
        this.sequence = sequence;
        this.pollId = pollId;
    }

    public void incrementCount() {
        count += 1;
    }

    public void decreaseCount() {
        count -= 1;
    }

    public boolean hasBeenVoted(Vote vote) {
        return (vote.getOption() & 1 << sequence) != 0;
    }

    public static VoteOption parseUnsafe(VoteOptionName name, int count, int sequence, Long pollId) {
        return new VoteOption(name, count, sequence, pollId);
    }
}