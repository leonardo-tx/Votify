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
@Table(name = "tb_email_confirmation")
@AllArgsConstructor
@NoArgsConstructor
public class EmailConfirmation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "email_confirmation_code")
    private String emailConfirmationCode;

    @Column(name = "email_confirmation_expiration")
    private LocalDateTime emailConfirmationExpiration;

    @Column(name = "email_confirmed", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private boolean emailConfirmed = false;
}
