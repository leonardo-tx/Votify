package br.com.votify.infra.persistence.user;

import br.com.votify.core.model.user.UserRole;
import br.com.votify.core.model.user.field.Email;
import br.com.votify.core.model.user.field.Name;
import br.com.votify.core.model.user.field.UserName;
import br.com.votify.infra.persistence.poll.PollEntity;
import br.com.votify.infra.persistence.poll.VoteEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity(name = "User")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(
    name = "TB_USER",
    indexes = {
        @Index(columnList = "user_name", unique = true),
        @Index(columnList = "email", unique = true)
    }
)
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name", nullable = false, unique = true, length = UserName.MAX_LENGTH)
    private String userName;

    @Column(name = "name", nullable = false, length = Name.MAX_LENGTH)
    private String name;

    @Column(name = "email", unique = true, nullable = false, length = Email.MAX_LENGTH)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<RefreshTokenEntity> refreshTokens;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<VoteEntity> votes;

    @OneToMany(mappedBy = "responsible", fetch = FetchType.LAZY)
    private List<PollEntity> polls;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    private EmailConfirmationEntity emailConfirmation;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    private PasswordResetEntity passwordReset;
}
