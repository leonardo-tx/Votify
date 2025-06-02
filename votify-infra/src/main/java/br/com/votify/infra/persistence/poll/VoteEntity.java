package br.com.votify.infra.persistence.poll;

import br.com.votify.infra.persistence.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "Vote")
@Getter
@Setter
@Builder
@Table(name = "TB_VOTE")
@NoArgsConstructor
@AllArgsConstructor
public class VoteEntity {
    @EmbeddedId
    private VoteIdentifier id;

    @Column(name = "option_value", nullable = false)
    private int option;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("pollId")
    @JoinColumn(name = "poll_id", foreignKey = @ForeignKey(name = "fk_poll_vote"))
    private PollEntity poll;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_user_vote"))
    private UserEntity user;
}
