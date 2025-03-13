package br.com.votify.dto.poll;

import br.com.votify.core.domain.entities.poll.Poll;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PollListViewDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String responsibleName;

    public static PollListViewDTO parse(Poll entity) {
        return new PollListViewDTO(
            entity.getId(),
            entity.getTitle(),
            entity.getDescription(),
            entity.getStartDate(),
            entity.getEndDate(),
            entity.getResponsible().getName()
        );
    }
} 