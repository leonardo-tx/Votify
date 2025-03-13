package br.com.votify.core.pollTemp;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PollResponseDto {
    private Long id;
    private String question;
    private String description;
    private String userVote; // "no vote" ou a opção escolhida
}
