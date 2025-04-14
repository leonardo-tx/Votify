package br.com.votify.core.domain.entities.tokens;

import br.com.votify.core.domain.entities.users.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "TB_EMAIL_CONFIRMATION")
@AllArgsConstructor
@NoArgsConstructor
public class EmailConfirmation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_user_email_confirmation"), nullable = false, unique = true)
    private User user;

    @Column(name = "new_email", nullable = true)
    private String newEmail;

    @Column(name = "email_confirmation_code", nullable = false)
    private String emailConfirmationCode;

    @Column(name = "email_confirmation_expiration", nullable = false)
    private LocalDateTime emailConfirmationExpiration;
}
