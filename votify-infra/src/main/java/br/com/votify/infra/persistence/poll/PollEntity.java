package br.com.votify.infra.persistence.poll;

import br.com.votify.core.model.poll.field.Description;
import br.com.votify.core.model.poll.field.Title;
import br.com.votify.infra.persistence.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Entity(name = "Poll")
@Getter
@Setter
@Table(name = "TB_POLL")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PollEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = Title.MAX_LENGTH)
    private String title;

    @Column(name = "description", nullable = false, length = Description.MAX_LENGTH)
    private String description;

    @Column(name = "start_date", nullable = false)
    private Instant startDate;

    @Column(name = "end_date", nullable = false)
    private Instant endDate;

    @Column(name = "user_registration", nullable = false)
    private boolean userRegistration;

    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL)
    private List<VoteOptionEntity> voteOptions;

    @OneToMany(mappedBy = "poll", cascade = CascadeType.REMOVE)
    private List<VoteEntity> votes;

    @Column(name = "choice_limit_per_user", nullable = false)
    private Integer choiceLimitPerUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "responsible_id",
            foreignKey = @ForeignKey(name = "fk_responsible_poll")
    )
    private UserEntity responsible;
}
