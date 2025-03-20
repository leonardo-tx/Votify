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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(
        name = "vote_option_id",
        foreignKey = @ForeignKey(name = "fk_vote_option_vote"),
        nullable = false
    )
    private VoteOption voteOption;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "user_id",
        foreignKey = @ForeignKey(name = "fk_user_vote")
    )
    private User user;
}
