package br.com.votify.core.domain.entities.poll;

import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.domain.entities.vote.VoteOption;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "TB_POLL")
@AllArgsConstructor
@NoArgsConstructor
public class Poll {
    public static final int TITLE_MIN_LENGTH = 5;
    public static final int TITLE_MAX_LENGTH = 50;
    public static final int DESCRIPTION_MIN_LENGTH = 0;
    public static final int DESCRIPTION_MAX_LENGTH = 512;
    public static final int VOTE_OPTIONS_MIN = 1;
    public static final int VOTE_OPTIONS_MAX= 5;
    public static final int PAGE_SIZE_LIMIT = 10;

    public Poll(
        String title,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        boolean userRegistration,
        List<VoteOption> voteOptions,
        Integer choiceLimitPerUser
    ) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.userRegistration = userRegistration;
        this.voteOptions = voteOptions;
        this.choiceLimitPerUser = choiceLimitPerUser;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = TITLE_MAX_LENGTH)
    private String title;

    @Column(name = "description", nullable = false, length = DESCRIPTION_MAX_LENGTH)
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "user_registration", nullable = false)
    @ColumnDefault("0")
    private boolean userRegistration = false;

    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL)
    private List<VoteOption> voteOptions;

    @Column(name = "choice_limit_per_user", nullable = false)
    private Integer choiceLimitPerUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "responsible_id",
        foreignKey = @ForeignKey(name = "fk_responsible_poll")
    )
    private User responsible;
}
