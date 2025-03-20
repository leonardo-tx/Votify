package br.com.votify.core.pollTemp;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.Id;



@Data
@Entity
@Table(name = "votes")
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Exemplo: opção escolhida
    private String chosenOption;

    // Caso vocês não tenham uma entidade User, apenas use userId
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "poll_id")
    private Poll poll;
}