package br.com.votify.dto.polls;

import br.com.votify.core.domain.entities.polls.Poll;
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
    private boolean userRegistration;
    private Integer choiceLimitPerUser;
    private Long responsibleId;

    public static PollDetailedViewDTO parse(Poll entity) {
        return new PollDetailedViewDTO(
            entity.getId(),
            entity.getTitle(),
            entity.getDescription(),
            entity.getStartDate(),
            entity.getEndDate(),
            entity.isUserRegistration(),
            entity.getChoiceLimitPerUser(),
            entity.getResponsible().getId()
        );
    }
}