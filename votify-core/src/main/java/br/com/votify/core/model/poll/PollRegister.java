package br.com.votify.core.model.poll;

import br.com.votify.core.model.poll.field.Description;
import br.com.votify.core.model.poll.field.Title;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
public class PollRegister {
    public static final int MIN_VOTE_OPTIONS = 1;
    public static final int MAX_VOTE_OPTIONS = 5;

    private final Title title;
    private final Description description;
    private final Instant startDate;
    private final Instant endDate;
    private final boolean userRegistration;
    private final List<VoteOptionRegister> voteOptions;
    private final int choiceLimitPerUser;

    public PollRegister(
            String title,
            String description,
            Instant startDate,
            Instant endDate,
            boolean userRegistration,
            List<VoteOptionRegister> voteOptions,
            int choiceLimitPerUser
    ) throws VotifyException {
        Instant now = Instant.now();
        if (startDate == null) {
            startDate = now;
        }
        if (endDate == null) {
            throw new VotifyException(VotifyErrorCode.POLL_DATE_EMPTY);
        }
        if (endDate.isBefore(startDate) || startDate.isBefore(now)) {
            throw new VotifyException(VotifyErrorCode.POLL_DATE_INVALID);
        }
        if (voteOptions.size() < MIN_VOTE_OPTIONS || voteOptions.size() > MAX_VOTE_OPTIONS) {
            throw new VotifyException(
                    VotifyErrorCode.POLL_VOTE_OPTIONS_INVALID_LENGTH,
                    MIN_VOTE_OPTIONS,
                    MAX_VOTE_OPTIONS
            );
        }
        if (choiceLimitPerUser < MIN_VOTE_OPTIONS || choiceLimitPerUser > voteOptions.size()) {
            throw new VotifyException(VotifyErrorCode.POLL_INVALID_CHOICE_LIMIT_PER_USER);
        }

        this.title = new Title(title);
        this.description = new Description(description);
        this.startDate = startDate;
        this.endDate = endDate;
        this.userRegistration = userRegistration;
        this.voteOptions = voteOptions;
        this.choiceLimitPerUser = choiceLimitPerUser;
    }

    public int getVoteOptionsSize() {
        return voteOptions.size();
    }
}
