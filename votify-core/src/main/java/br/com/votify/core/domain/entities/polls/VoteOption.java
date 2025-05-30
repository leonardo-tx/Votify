package br.com.votify.core.domain.entities.polls;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "TB_VOTE_OPTION")
@AllArgsConstructor
@NoArgsConstructor
public class VoteOption {
    public static final int NAME_MIN_LENGTH = 3;
    public static final int NAME_MAX_LENGTH = 30;

    @EmbeddedId
    private VoteOptionIdentifier id;

    @Column(name = "name", nullable = false, length = NAME_MAX_LENGTH)
    private String name;

    @Column(nullable = false)
    private int count;

    @ManyToOne
    @MapsId("pollId")
    @JoinColumn(name = "poll_id", nullable = false)
    private Poll poll;

    public void incrementCount() {
        count += 1;
    }

    public void decreaseCount() {
        count -= 1;
    }

    public boolean hasBeenVoted(Vote vote) {
        return (vote.getOption() & 1 << id.getSequence()) != 0;
    }
}