package br.com.votify.core.domain.entities.polls;

import br.com.votify.core.domain.entities.users.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "TB_VOTE")
@AllArgsConstructor
@NoArgsConstructor
public class Vote {
    @EmbeddedId
    private VoteIdentifier id;

    @Column(nullable = false)
    private int option;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("pollId")
    @JoinColumn(name = "poll_id", foreignKey = @ForeignKey(name = "fk_poll_vote"))
    private Poll poll;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_user_vote"))
    private User user;
}
