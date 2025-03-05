package br.com.votify.core.domain.entities.vote;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "TB_VOTE")
@AllArgsConstructor
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vote_option_id",
            foreignKey = @ForeignKey(name = "fk_vote_option_vote"),
            nullable = false)
    private VoteOption voteOption;

    @Column(name = "encrypted_user_id", nullable = false)
    private String encryptedUserId;

    @Column(name = "anonymous_voter_hash", nullable = false, unique = true)
    private String anonymousVoterHash;
}
