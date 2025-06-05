package br.com.votify.infra.persistence.poll;

import br.com.votify.core.model.poll.field.VoteOption;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "VoteOption")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TB_VOTE_OPTION")
public class VoteOptionEntity {
    @EmbeddedId
    private VoteOptionIdentifier id;

    @Column(name = "name", nullable = false, length = VoteOption.NAME_MAX_LENGTH)
    private String name;

    @Column(nullable = false)
    private int count;

    @ManyToOne
    @MapsId("pollId")
    @JoinColumn(name = "poll_id", nullable = false)
    private PollEntity poll;
}
