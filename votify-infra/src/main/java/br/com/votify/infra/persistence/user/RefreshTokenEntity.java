package br.com.votify.infra.persistence.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "RefreshToken")
@Table(name = "TB_REFRESH_TOKEN")
public class RefreshTokenEntity {
    @Id
    private String code;

    @Column(name = "expiration", nullable = false)
    private Instant expiration;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_refresh_token"))
    private UserEntity user;
}
