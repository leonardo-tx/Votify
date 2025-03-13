package br.com.votify.core.domain.entities.password;

import br.com.votify.core.domain.entities.users.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TB_PASSWORD_RESET_TOKEN")
public class PasswordResetToken {

    @Id
    private String code;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Date expiryDate;

    public boolean isExpired() {
        return new Date().after(expiryDate);
    }
}