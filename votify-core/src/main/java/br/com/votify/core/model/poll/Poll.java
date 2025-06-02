package br.com.votify.core.model.poll;

import br.com.votify.core.model.poll.field.Description;
import br.com.votify.core.model.poll.field.Title;
import br.com.votify.core.model.user.User;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public final class Poll {
    private final Long id;
    private final Title title;
    private final Description description;
    private Instant startDate;
    private Instant endDate;
    private final boolean userRegistration;
    private final List<VoteOption> voteOptions;
    private final int choiceLimitPerUser;
    private Long responsibleId;

    public Poll(PollRegister pollRegister, User responsible) throws VotifyException {
        if (pollRegister == null) {
            throw new IllegalArgumentException("The poll register must not be null.");
        }
        if (responsible == null || responsible.getId() == null) {
            throw new IllegalArgumentException("The responsible or it's id must not be null.");
        }
        this.id = null;
        this.title = pollRegister.getTitle();
        this.description = pollRegister.getDescription();
        this.startDate = pollRegister.getStartDate();
        this.endDate = pollRegister.getEndDate();
        this.userRegistration = pollRegister.isUserRegistration();
        this.voteOptions = new ArrayList<>();
        this.choiceLimitPerUser = pollRegister.getChoiceLimitPerUser();
        this.responsibleId = responsible.getId();

        for (int i = 0; i < pollRegister.getVoteOptionsSize(); i++) {
            VoteOptionRegister voteOptionRegister = pollRegister.getVoteOptions().get(i);
            VoteOption voteOption = new VoteOption(voteOptionRegister, this, i);
            this.voteOptions.add(voteOption);
        }
    }

    private Poll(
            Long id,
            Title title,
            Description description,
            Instant startDate,
            Instant endDate,
            boolean userRegistration,
            List<VoteOption> voteOptions,
            int choiceLimitPerUser,
            Long responsibleId
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.userRegistration = userRegistration;
        this.voteOptions = voteOptions;
        this.choiceLimitPerUser = choiceLimitPerUser;
        this.responsibleId = responsibleId;
    }

    public List<VoteOption> getVoteOptions() {
        return Collections.unmodifiableList(voteOptions);
    }

    public void cancel() throws VotifyException {
        Instant now = Instant.now();
        if (hasNotStarted()) {
            this.startDate = now;
        }
        if (hasEnded()) {
            throw new VotifyException(VotifyErrorCode.POLL_CANNOT_CANCEL_FINISHED);
        }
        this.endDate = now;
    }

    public Vote vote(VoteRegister voteRegister) throws VotifyException {
        if (voteRegister == null) {
            throw new IllegalArgumentException("The vote register must not be null.");
        }
        if (isOutOfDate()) {
            throw new VotifyException(VotifyErrorCode.POLL_VOTE_OUT_OF_DATE_INTERVAL);
        }
        Vote vote = new Vote(voteRegister);
        for (VoteOption voteOption : voteOptions) {
            if (voteOption.hasBeenVoted(vote)) {
                voteOption.incrementCount();
            }
        }
        return vote;
    }

    public void removeVote(Vote vote) {
        if (vote == null) {
            throw new IllegalArgumentException("The vote must not be null.");
        }
        if (isOutOfDate()) {
            throw new IllegalArgumentException("It's not possible to remove a vote on a out of date poll.");
        }
        for (VoteOption voteOption : voteOptions) {
            if (voteOption.hasBeenVoted(vote)) {
                voteOption.decreaseCount();
            }
        }
    }

    public void removeResponsible() {
        this.responsibleId = null;
    }

    public boolean isOutOfDate() {
        Instant now = Instant.now();
        return now.isBefore(startDate) || now.isAfter(endDate);
    }

    public boolean hasEnded() {
        Instant now = Instant.now();
        return now.isAfter(endDate);
    }

    public boolean hasNotStarted() {
        Instant now = Instant.now();
        return now.isBefore(startDate);
    }

    public int getVoteOptionsSize() {
        return voteOptions.size();
    }

    public static Poll parseUnsafe(
            Long id,
            Title title,
            Description description,
            Instant startDate,
            Instant endDate,
            boolean userRegistration,
            List<VoteOption> voteOptions,
            int choiceLimitPerUser,
            Long responsibleId
    ) {
        return new Poll(
                id,
                title,
                description,
                startDate,
                endDate,
                userRegistration,
                voteOptions,
                choiceLimitPerUser,
                responsibleId
        );
    }
}
