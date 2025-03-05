package br.com.votify.core.domain.entities.vote;

import br.com.votify.core.domain.entities.poll.Poll;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "TB_VOTE_OPTION")
@AllArgsConstructor
public class VoteOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 30)
    private String name;

    @ManyToOne
    @JoinColumn(name = "poll_id", nullable = false)
    private Poll poll;
}