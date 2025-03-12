package br.com.votify.dto.poll;

import br.com.votify.core.domain.entities.poll.Poll;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PollDetailedViewDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer choiceLimitPerUser;
    private String responsibleName;

    public static PollDetailedViewDTO parse(Poll entity) {
        return new PollDetailedViewDTO(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getChoiceLimitPerUser(),
                entity.getResponsible().getName()
        );
    }
}