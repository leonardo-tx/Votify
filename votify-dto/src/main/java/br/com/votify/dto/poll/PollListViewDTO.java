package br.com.votify.dto.poll;

import br.com.votify.core.model.poll.Poll;
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
    private boolean canceled;

    public static PollListViewDTO parse(Poll entity) {
        return new PollListViewDTO(
                entity.getId(),
                entity.getTitle().getValue(),
                entity.getDescription().getValue(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getResponsible() == null ? null : entity.getResponsible().getId(),
                entity.isCanceled()
        );
    }
}
