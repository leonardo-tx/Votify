package br.com.votify.dto.polls;

import br.com.votify.core.domain.entities.polls.Poll;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PollListViewDTO {
    private Long id;
    private String title;
    private String description;
    private Instant startDate;
    private Instant endDate;
    private Long responsibleId;

    public static PollListViewDTO parse(Poll entity) {
        return new PollListViewDTO(
            entity.getId(),
            entity.getTitle(),
            entity.getDescription(),
            entity.getStartDate(),
            entity.getEndDate(),
                entity.getResponsible() == null ? null : entity.getResponsible().getId()
        );
    }
} 