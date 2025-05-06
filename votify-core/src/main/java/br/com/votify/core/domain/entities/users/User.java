package br.com.votify.core.domain.entities.users;

import br.com.votify.core.domain.entities.polls.Poll;
import br.com.votify.core.domain.entities.polls.Vote;
import br.com.votify.core.domain.entities.polls.VoteOption;
import br.com.votify.core.domain.entities.tokens.EmailConfirmation;
import br.com.votify.core.domain.entities.tokens.RefreshToken;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(
        name = "TB_USER",
        indexes = {
                @Index(columnList = "userName", unique = true),
                @Index(columnList = "email", unique = true)
        }
)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "USER_TYPE", discriminatorType = DiscriminatorType.STRING)
public abstract class User implements Cloneable {
    public static final int USER_NAME_MIN_LENGTH = 3;
    public static final int USER_NAME_MAX_LENGTH = 40;
    public static final int EMAIL_MIN_LENGTH = 5;
    public static final int EMAIL_MAX_LENGTH = 254;
    public static final int NAME_MIN_LENGTH = 1;
    public static final int NAME_MAX_LENGTH = 50;
    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int PASSWORD_MAX_BYTES = 72;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = USER_NAME_MAX_LENGTH)
    private String userName;

    @Column(nullable = false, length = NAME_MAX_LENGTH)
    private String name;

    @Column(unique = true, nullable = false, length = EMAIL_MAX_LENGTH)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<RefreshToken> refreshTokens;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = false)
    private List<Vote> votes;

    @OneToMany(mappedBy = "responsible", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = false)
    private List<Poll> polls;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private EmailConfirmation emailConfirmation;

    public int getPermissions() {
        return PermissionFlags.NONE;
    }

    public boolean hasPermission(int permissionFlags) {
        return (getPermissions() & permissionFlags) != 0;
    }

    @Override
    public User clone() {
        try {
            return (User)super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @PreRemove
    public void preRemove() {
        for (int i = votes.size() - 1; i >= 0; i--) {
            Vote vote = votes.get(i);
            Poll poll = vote.getPoll();
            if (poll.hasEnded()) continue;

            for (VoteOption voteOption : poll.getVoteOptions()) {
                if (!voteOption.hasBeenVoted(vote)) continue;
                voteOption.decreaseCount();
            }
            votes.remove(i);
        }
        for (int i = polls.size() - 1; i >= 0; i--) {
            if (polls.get(i).hasEnded()) continue;
            polls.remove(i);
        }
    }
}
