package br.com.votify.core.domain.entities.polls;

import br.com.votify.core.domain.entities.users.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import java.util.ArrayList;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "TB_POLL")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Poll {
    public static final int TITLE_MIN_LENGTH = 5;
    public static final int TITLE_MAX_LENGTH = 50;
    public static final int DESCRIPTION_MIN_LENGTH = 0;
    public static final int DESCRIPTION_MAX_LENGTH = 512;
    public static final int VOTE_OPTIONS_MIN = 1;
    public static final int VOTE_OPTIONS_MAX= 5;
    public static final int PAGE_SIZE_LIMIT = 10;

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
    private List<VoteOption> voteOptions = new ArrayList<>();

    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL)
    private List<Vote> votes = new ArrayList<>();

    @Column(name = "choice_limit_per_user", nullable = false)
    private Integer choiceLimitPerUser;

    @Column(name = "canceled", nullable = false)
    private boolean canceled = false;

    @Column(name = "archived", nullable = false)
    private boolean archived = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "responsible_id",
        foreignKey = @ForeignKey(name = "fk_responsible_poll")
    )
    private User responsible;
}
